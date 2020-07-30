package ru.ps.vlcatv.utils.playlist.parse;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.Keep;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import ru.ps.vlcatv.utils.BuildConfig;
import ru.ps.vlcatv.utils.Text;
import ru.ps.vlcatv.utils.json.JSONArray;
import ru.ps.vlcatv.utils.json.JSONObject;
import ru.ps.vlcatv.utils.playlist.PlayList;
import ru.ps.vlcatv.utils.playlist.PlayListActors;
import ru.ps.vlcatv.utils.playlist.PlayListConstant;
import ru.ps.vlcatv.utils.playlist.PlayListEpgDefault;
import ru.ps.vlcatv.utils.playlist.PlayListEpgDefaultItem;
import ru.ps.vlcatv.utils.playlist.PlayListItemIds;
import ru.ps.vlcatv.utils.playlist.PlayListItemStatistic;
import ru.ps.vlcatv.utils.reflect.ReflectAttribute;
import ru.ps.vlcatv.utils.reflect.annotation.IArrayReflect;
import ru.ps.vlcatv.utils.reflect.annotation.IFieldReflect;
import ru.ps.vlcatv.utils.reflect.annotation.IObjectReflect;

@Keep
public class ParseObject extends ReflectAttribute {

    private static final String[] m38uRadio = new String[] {
            "radio",
            "радио"
    };
    public ParseObject() {}
    public ParseObject(JSONObject obj) {
        merge(obj);
    }
    public ParseObject(String[] str, PlayListEpgDefault epgList) {
        merge(str, epgList);
    }
    public ParseObject(String[] str) {
        merge(str, null);
    }

    @IFieldReflect("crc")
    public long itemCrc = -1;

    @IFieldReflect("id_group")
    public long itemGroup = -1;

    @IFieldReflect("id_item_group")
    public long itemGroupId = -1;

    @IFieldReflect("vlc_id")
    public int vlcId = -1;

    /// ToDo: compatible to ViewCard version only
    @IFieldReflect("title")
    public String itemTitle = null;

    @IFieldReflect("desc")
    public String itemDescription = null;

    @IFieldReflect("uri")
    public String itemUri = null;

    @IFieldReflect("nfo")
    public String itemNfoUri = null;

    @IFieldReflect("premiered")
    public Date itemPremiered = null;

    @IFieldReflect("season")
    public int itemSeason = -1;

    @IFieldReflect("episode")
    public int itemEpisode = -1;

    @IFieldReflect("duration")
    public int itemDuration = 0;

    @IFieldReflect("dimension")
    public String itemDimension = null;

    @IFieldReflect("type")
    public int itemType = PlayListConstant.TYPE_NONE;

    @IFieldReflect("type")
    public int playListType = PlayList.IDX_EMPTY;

    @IArrayReflect(value = "alt_titles", SkipRecursion = false)
    public List<String> titleList = new ArrayList<>();

    @IArrayReflect(value = "genres", SkipRecursion = false)
    public List<String> genreList = new ArrayList<>();

    @IArrayReflect(value = "trailers", SkipRecursion = false)
    public List<String> trailerList = new ArrayList<>();

    @IArrayReflect(value = "producers", SkipRecursion = false)
    public List<String> producerList = new ArrayList<>();

    @IArrayReflect(value = "studios", SkipRecursion = false)
    public List<String> studioList = new ArrayList<>();

    @IArrayReflect(value = "user_note", SkipRecursion = false)
    public List<String> userNoteList = new ArrayList<>();

    @IArrayReflect(value = "tag_search", SkipRecursion = false)
    public List<String> tagList = new ArrayList<>();

    @IArrayReflect(value = "country", SkipRecursion = false)
    public List<String> countryList = new ArrayList<>();

    ///

    // 1 Unique
    @IObjectReflect("stat")
    public PlayListItemStatistic itemStat = new PlayListItemStatistic();

    // 2 Unique
    @IArrayReflect(value = "images", SkipRecursion = false)
    public List<ParseContainer> imageList = new ArrayList<>();

    // 3 Unique
    @IArrayReflect(value = "ratings", SkipRecursion = false)
    public List<ParseContainer> ratingList = new ArrayList<>();

    // 4 Unique
    @IArrayReflect(value = "actors", SkipRecursion = false)
    public List<PlayListActors> actorList = new ArrayList<>();

    // 5 Unique
    @IArrayReflect(value = "ids", SkipRecursion = false)
    public List<PlayListItemIds> idList = new ArrayList<>();

    ///

    public ParseContainer findFromParseContainer(String s, List<ParseContainer> list) {
        if (Text.isempty(s))
            return null;
        for (ParseContainer pc : list)
            if (pc.typeId.equals(s))
                return pc;

        return null;
    }
    public void setDescription(String s) {
        if (!Text.isempty(s))
            itemDescription = s;
    }
    public void setSeasonEpisode(int s, int e) {
        itemSeason = s;
        itemEpisode = e;
    }
    public void addProducer(String s) {
        if ((!Text.isempty(s)) && (!producerList.contains(s)))
            producerList.add(s);
    }
    public void addTrailer(String s) {
        try {
            if (Text.isempty(s))
                return;

            Matcher m;
            Pattern pattern;
            String trailerParse = null;
            pattern = Pattern.compile("^plugin://plugin.video.youtube.*&videoid=(.*)&.*$");
            m = pattern.matcher(s);
            if (m.matches()) {
                trailerParse = String.format(
                        Locale.getDefault(),
                        "http://youtube.com/watch?v=%s",
                        m.group(1)
                );
            } else {
                pattern = Pattern.compile("^plugin://.*(http?.*)$");
                m = pattern.matcher(s);
                if (m.matches()) {
                    try {
                        trailerParse = URLDecoder.decode(m.group(1), "UTF-8");
                    } catch (UnsupportedEncodingException ignore) {
                        return;
                    }
                }
            }
            if (Text.isempty(trailerParse))
                trailerParse = s;

            if (!trailerList.contains(trailerParse))
                trailerList.add(trailerParse);

        } catch (Exception ignore) {}
    }
    public void addTitle(String s) {
        if (Text.isempty(s))
            return;
        if (!titleList.contains(s)) {
            titleList.add(s);
            if (Text.isempty(itemTitle))
                itemTitle = s;
        }
    }
    public void setPremiered1(String s) {
        if (Text.isempty(s))
            return;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            itemPremiered = format.parse(s);
        } catch (Exception ignore) {}
    }
    public void setPremiered2(String s) {
        if (Text.isempty(s))
            return;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy", Locale.getDefault());
            itemPremiered = format.parse(s);
        } catch (Exception ignore) {}
    }
    public void setPremiered3(String s) {
        if (Text.isempty(s))
            return;
        try {
            SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            itemPremiered = format.parse(s);
        } catch (Exception ignore) {}
    }

    public void setItemType(int type) {
        itemType = type;
    }
    public void setDuration(int val) {
        if (val > 0)
            itemStat.totalProgress.set(val);
    }
    public void setWatched(boolean b) {
        itemStat.watched.set(b);
    }
    public void setPlayCount(long l) {
        itemStat.playCount.set(l);
    }
    public void addImage(String t, String s, int season) {
        if (!Text.isempty(s)) {
            ParseContainer ii = new ParseContainer();
            ii.typeId = t;
            ii.valId = s;
            ii.intId = season;
            addImage_(ii);
        }
    }
    public void addIds(String s, String v) {
        if (!Text.isempty(s)) {
            PlayListItemIds ids = new PlayListItemIds();
            ids.typeId = s;
            ids.valId = v;
            addIds_(ids);
        }
    }
    public void addActor(String n, String r, String p, String t) {
        if (!Text.isempty(n)) {
            PlayListActors ia = new PlayListActors();
            ia.Name = n;
            ia.Role = r;
            ia.profileUri = p;
            ia.thumbUri = t;
            addActor_(ia);
        }
    }
    public void addRating(String type, int val) {
        ParseContainer ir = new ParseContainer();
        ir.typeId = type;
        ir.valId = Integer.toString(val);
        addRating_(ir);
    }
    public void addRating(String type, double val) {
        ParseContainer ir = new ParseContainer();
        ir.typeId = type;
        ir.valId = Double.toString(val);
        addRating_(ir);
    }
    public void addRating(String type, String val) {
        if (Text.isempty(val))
            return;
        ParseContainer ir = new ParseContainer();
        ir.typeId = type;
        ir.valId = val;
        addRating_(ir);
    }
    public String getRating(String type) {
        for (ParseContainer pc : ratingList)
            if (type.equals(pc.typeId))
                return pc.valId;

        return null;
    }

    ///

    private void addRating_(ParseContainer val) {
        if (!ratingList.contains(val))
            ratingList.add(val);
    }
    private void addActor_(PlayListActors val) {
        if (!actorList.contains(val))
            actorList.add(val);
    }
    private void addIds_(PlayListItemIds val) {
        if (!idList.contains(val))
            idList.add(val);
    }
    private void addImage_(ParseContainer val) {
        if (!imageList.contains(val))
            imageList.add(val);
    }
    private void addToList_(List<String> list, Object obj) {
        if ((obj == null) || (list == null))
            return;
        String s = obj.toString();
        if (Text.isempty(s))
            return;
        if (!list.contains(s))
            list.add(s);
    }

    public int merge(JSONObject obj) {

        if (obj == null)
            return PlayListConstant.TYPE_NONE;

        try {
            byte[] bytes = obj.toString().getBytes();
            Checksum crc = new CRC32();
            crc.update(bytes, 0, bytes.length);
            itemCrc = crc.getValue();
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("- calculate crc", Text.requireString(e.getLocalizedMessage()), e);
            return PlayListConstant.TYPE_NONE;
        }

        try {

            JSONArray arr = null;
            JSONObject ele = null;

            String str;
            ///////////////////////////////////////////////////////////////////////////////
            /// IMDB source compatible
            if ((str = obj.optString("searchType",null)) != null) {

                if (Text.isempty(str))
                    return PlayListConstant.TYPE_NONE;

                switch (str) {
                    case "Movie":
                        playListType = PlayListConstant.TYPE_MOVIE;
                        break;
                    case "Series":
                        playListType = PlayListConstant.TYPE_SEASON;
                        break;
                    case "Episode":
                        playListType = PlayListConstant.TYPE_SERIES;
                        break;
                    default:
                        return PlayListConstant.TYPE_NONE;
                }

                setItemType(PlayListConstant.TYPE_SOURCE_IMDB);
                arr = new JSONArray(obj.optString("results", ""));
                if (arr.length() == 0)
                    return PlayListConstant.TYPE_NONE;
                ele = arr.getJSONObject(0);
                if (ele == null)
                    return PlayListConstant.TYPE_NONE;

                addIds(
                        PlayListConstant.IDS_IMDB,
                        ele.optString("id", null)
                );
                addImage("poster", ele.optString("image", null), -1);
                addTitle(ele.optString("title", null));

                str = ele.optString("description", null);
                if ((!Text.isempty(str)) && (!str.equals("N/A"))) {
                    try {
                        Pattern[] p = new Pattern[] {
                                Pattern.compile(
                                        "\\((\\d+)\\) aka (.*)$",
                                        Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
                                ),
                                Pattern.compile(
                                        "\\((\\d+)\\)",
                                        Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE
                                )
                        };
                        Matcher m = p[0].matcher(str);
                        if (m.find()) {
                            setPremiered2(m.group(0));
                            str = m.group(1);
                            if (!Text.isempty(str)) {
                                addTitle(str.replace("\"", "").trim());
                            }
                        } else {
                            m = p[1].matcher(str);
                            if (m.find()) {
                                setPremiered1(m.group(0));
                            } else {
                                setDescription(str);
                            }
                        }
                    } catch (Exception ignore) {}
                }
                return itemType;

            ///////////////////////////////////////////////////////////////////////////////
            /// OMDB source compatible
            } else if ((str = obj.optString("Response",null)) != null) {

                if (!str.equals("True"))
                    return PlayListConstant.TYPE_NONE;
                if ((str = obj.optString("Error",null)) != null) {
                    if (BuildConfig.DEBUG) Log.e("-- OMDB Media Item error: ", str);
                    return PlayListConstant.TYPE_NONE;
                }

                setItemType(PlayListConstant.TYPE_SOURCE_OMDB);

                setSeasonEpisode(
                        obj.optInt("Season", -1),
                        obj.optInt("Episode", -1)
                );
                itemTitle = obj.optString("Title", "");
                itemDescription = obj.optString("Plot", "");
                 if (itemDescription.equals("N/A"))
                     itemDescription = "";
                addImage("poster", obj.optString("Poster", null), -1);
                addRating(PlayListConstant.IDS_RATING, obj.optString("imdbRating",null));
                addRating(PlayListConstant.IDS_VOTES, obj.optString("imdbVotes",null));
                addRating(PlayListConstant.IDS_META, obj.optString("Metascore",null));
                addRating(PlayListConstant.IDS_RATED, obj.optString("Rated",null));

                if ((str = obj.optString("Awards", null)) != null)
                    if ((!Text.isempty(str)) && (!str.equals("N/A")))
                        addRating(PlayListConstant.IDS_AWARDS, str);

                if (((arr = obj.optJSONArray("Ratings")) != null) && (arr.length() > 0))
                    for (int i = 0; i < arr.length(); i++) {
                        if ((ele = arr.optJSONObject(i)) != null)
                            addRating(
                                    ele.optString("Source", ""),
                                    ele.optString("Value", "")
                            );
                    }
                addIds(
                        PlayListConstant.IDS_IMDB,
                        obj.optString("imdbID", null)
                );
                addIds(
                        PlayListConstant.IDS_SERIES_ID,
                        obj.optString("seriesID", null)
                );
                addProducer(obj.optString("Director", null));
                addToList_(countryList, obj.optString("Country"));

                if ((str = obj.optString("Writer", null)) != null) {
                    String[] items = str.split(",");
                    for (String item : items)
                        addProducer(item.trim());
                }
                if ((str = obj.optString("Genre", null)) != null) {
                    String[] items = str.split(",");
                    for (String item : items)
                        addToList_(genreList, item.trim());
                }
                if ((str = obj.optString("Actors", null)) != null) {
                    String[] items = str.split(",");
                    for (String item : items)
                        addActor(item.trim(), "", "", "");
                }
                if ((str = obj.optString("Runtime", null)) != null) {
                    String[] items = str.split(" ");
                    try {
                        if (items.length > 0)
                            itemDuration = Integer.parseInt(items[0].trim());
                    } catch (Exception ignore) {}
                }
                if ((str = obj.optString("Released", null)) != null)
                    setPremiered3(str);

            ///////////////////////////////////////////////////////////////////////////////
            /// SHOW/SEASON NFO source compatible
            } else if ((ele = obj.optJSONObject("tvshow")) != null) {

                setItemType(PlayListConstant.TYPE_SHOWS);
                arr = ele.optJSONArray("thumb");
                if (arr != null)
                    for (int k = 0; k < arr.length(); k++) {
                        JSONObject j = arr.optJSONObject(k);
                        if (j != null)
                            addImage(
                                j.optString("aspect", null),
                                j.optString("content", null),
                                j.optInt("season", -1)
                        );
                    }
                JSONObject jobj = ele.optJSONObject("fanart");
                if (jobj != null)
                    addImage(
                            "fanart",
                            jobj.optString("thumb", null),
                            -1
                    );

            ///////////////////////////////////////////////////////////////////////////////
            /// SERIES NFO source compatible
            } else if ((ele = obj.optJSONObject("episodedetails")) != null) {

                setItemType(PlayListConstant.TYPE_SERIES);
                addImage("thumb", ele.optString("thumb", null), -1);
                setSeasonEpisode(
                        ele.optInt("season", -1),
                        ele.optInt("episode", -1)
                );

            ///////////////////////////////////////////////////////////////////////////////
            /// MOVIE NFO source compatible
            } else if ((ele = obj.optJSONObject("movie")) != null) {

                setItemType(PlayListConstant.TYPE_MOVIE);

                JSONObject jobj = ele.optJSONObject("thumb");
                if (jobj != null)
                    addImage("thumb", jobj.optString("content", null), -1);

                jobj = ele.optJSONObject("ratings");
                if (jobj != null) {
                    jobj = jobj.optJSONObject("rating");
                    if (jobj != null) {
                        addRating("rating", jobj.optDouble("value", 0.0));
                        addRating("votes", jobj.optDouble("votes", 0.0));
                        addRating("userrating", jobj.optDouble("userrating", 0.0));
                    }
                }

                arr = ele.optJSONArray("producer");
                if (arr != null)
                    for (int k = 0; k < arr.length(); k++) {
                        JSONObject j = arr.optJSONObject(k);
                        if (j != null)
                            addProducer(j.optString("name", null));
                    }

                arr = ele.optJSONArray("country");
                if (arr != null)
                    for (int k = 0; k < arr.length(); k++)
                        addToList_(countryList, arr.opt(k));

                arr = ele.optJSONArray("genre");
                if (arr != null)
                    for (int k = 0; k < arr.length(); k++)
                        addToList_(genreList, arr.opt(k));

                jobj = ele.optJSONObject("fanart");
                if (jobj != null)
                    addImage("thumb", jobj.optString("thumb", null), -1);
                //
            }

            ///////////////////////////////////////////////////////////////////////////////
            /// All NFO source compatible
            if (ele != null) {

                arr = ele.optJSONArray("studio");
                if (arr != null)
                    for (int k = 0; k < arr.length(); k++)
                        addToList_(studioList, arr.opt(k));

                arr = ele.optJSONArray("credits");
                if (arr != null)
                    for (int k = 0; k < arr.length(); k++)
                        addToList_(producerList, arr.opt(k));

                arr = ele.optJSONArray("actor");
                if (arr != null)
                    for (int k = 0; k < arr.length(); k++) {
                        JSONObject j = arr.optJSONObject(k);
                        if (j != null)
                            addActor(
                                j.optString("name", null),
                                j.optString("role", null),
                                j.optString("profile", null),
                                j.optString("thumb", null)
                            );
                    }

                arr = ele.optJSONArray("user_note");
                if (arr != null)
                    for (int k = 0; k < arr.length(); k++)
                        addToList_(userNoteList, arr.opt(k));

                arr = ele.optJSONArray("tag");
                if (arr != null)
                    for (int k = 0; k < arr.length(); k++)
                        addToList_(tagList, arr.opt(k));

                arr = ele.optJSONArray("uniqueid");
                if (arr != null)
                    for (int k = 0; k < arr.length(); k++) {
                        JSONObject j = arr.optJSONObject(k);
                        if (j != null)
                            addIds(
                                j.optString("type", null),
                                j.optString("content", null)
                            );
                    }

                JSONObject jobj = ele.optJSONObject("fileinfo");
                if (jobj != null) {
                    jobj = jobj.optJSONObject("streamdetails");
                    if (jobj != null) {
                        jobj = jobj.optJSONObject("video");
                        if (jobj != null) {

                            String codec = jobj.optString("codec", "");
                            int w = jobj.optInt("width", 0);
                            int h = jobj.optInt("height", 0);
                            double r = jobj.optDouble("aspect", 0.0);
                            if ((!Text.isempty(codec)) && (w > 0) && (h > 0) && (Double.compare(r, 0.0) != 0))
                                itemDimension = String.format(
                                        Locale.getDefault(),
                                        "%s %dx%d (%f)",
                                        codec, w, h, r
                                );
                            else if ((!Text.isempty(codec)) && (w > 0) && (h > 0))
                                itemDimension = String.format(
                                        Locale.getDefault(),
                                        "%s %dx%d",
                                        codec, w, h
                                );
                            else if ((w > 0) && (h > 0) && (Double.compare(r, 0.0) != 0))
                                itemDimension = String.format(
                                        Locale.getDefault(),
                                        "%dx%d (%f)",
                                        w, h, r
                                );
                        }
                    }
                }

                addRating("votes", ele.optInt("votes", 0));
                addRating("rating", ele.optDouble("rating", 0.0));
                addRating("userrating", ele.optInt("userrating", 0));
                addRating("top250", ele.optInt("top250", 0));
                setWatched(ele.optBoolean("watched", false));
                addTrailer(ele.optString("trailer", null));
                addTitle(ele.optString("title", null));
                addTitle(ele.optString("showtitle", null));
                addTitle(ele.optString("originaltitle", null));
                addProducer(ele.optString("director", null));
                setPlayCount(ele.optLong("playcount", 0L));
                setDescription(ele.optString("plot", null));

                String s = ele.optString("premiered", null);
                if (Text.isempty(s)) {
                    s = ele.optString("year", null);
                    if (!Text.isempty(s)) {
                        setPremiered1(String.format(Locale.getDefault(), "%s-01-01", s));
                    }
                } else {
                    setPremiered1(s);
                }
                setDuration(ele.optInt("runtime", 0));
                ///
            }

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("- merge parse", Text.requireString(e.getLocalizedMessage()), e);
        }
        return itemType;
    }

    public int merge(String[] array, PlayListEpgDefault epgList) {

        itemType = PlayListConstant.TYPE_NONE;
        try {

            try {
                StringBuilder sb = new StringBuilder();
                for (String s : array) {
                    if (Text.isempty(s))
                        break;
                    sb.append(s.trim());
                }
                byte[] bytes = sb.toString().getBytes();
                Checksum crc = new CRC32();
                crc.update(bytes, 0, bytes.length);
                itemCrc = crc.getValue();
            } catch (Exception e) {
                if (BuildConfig.DEBUG) Log.e("- calculate crc", Text.requireString(e.getLocalizedMessage()), e);
                return PlayListConstant.TYPE_NONE;
            }

            for (String s : array) {
                if (Text.isempty(s))
                    break;
                if (s.charAt(0) == '#') {
                    if (s.startsWith("#EXTINF")) {
                        String lc, name;
                        int pos = s.indexOf(',');
                        if (pos > 0) {
                            name = s.substring(pos + 1, s.length())
                                    .replace('_', ' ')
                                    .trim();
                            try {
                                Pattern p = Pattern.compile(
                                        "\\((?:.*)\\)",
                                        Pattern.CASE_INSENSITIVE |
                                                Pattern.UNICODE_CASE
                                );
                                Matcher m = p.matcher(name);
                                if (m.find())
                                    name = m.replaceAll("").trim();
                            } catch (Exception ignore) {}

                            lc = name.toLowerCase();
                            int off = 0;
                            for (int i = (lc.length() - 1), n = 0; ((i > 0) && (i > lc.length() - 5)); i--, n++) {
                                if (n == 0) {
                                    if ((lc.charAt(i) == 'd') || (lc.charAt(i) == 'k')) {
                                        off = 1;
                                    } else {
                                        off = 0;
                                        break;
                                    }
                                } else if (n == 1) {
                                    if ((off == 1) &&
                                        ((lc.charAt(i) == '8') ||
                                         (lc.charAt(i) == '4') ||
                                         (lc.charAt(i) == 's') ||
                                         (lc.charAt(i) == 'h'))) {
                                        off = 2;
                                    } else {
                                        off = 0;
                                        break;
                                    }
                                } else if (n == 3) {
                                    if ((off == 2) &&
                                        ((lc.charAt(i) == 'u') ||
                                         (lc.charAt(i) == 'f') ||
                                         (lc.charAt(i) == ' '))) {
                                        off = 3;
                                    }
                                } else if (n == 4) {
                                    if ((off == 3) && (lc.charAt(i) == ' ')) {
                                        off = 4;
                                    }
                                } else if (n == 5) {
                                    if ((off == 4) && (lc.charAt(i) == 'k')) {
                                        off = 5;
                                    }
                                } else if (n == 6) {
                                    if ((off == 5) &&
                                        ((lc.charAt(i) == '4') || (lc.charAt(i) == '8'))) {
                                        off = 6;
                                    }
                                } else if (n == 7) {
                                    if ((off == 6) && (lc.charAt(i) == ' ')) {
                                        off = 7;
                                    }
                                    break;
                                }
                            }
                            if (off > 0)
                                name = name.substring(0, (name.length() - off)).trim();

                        } else {
                            return PlayListConstant.TYPE_NONE;
                        }

                        boolean isImageAdded = false;
                        PlayListEpgDefaultItem epgItem = null;
                        playListType = PlayList.IDX_ONLINE_TV;
                        name = name.replace('_', ' ').trim().toUpperCase();

                        if (!epgList.isEmpty())
                            epgItem = epgList.find(name);

                        if (epgItem != null) {

                            try {
                                addTitle(epgItem.getName(name));
                                if (!Text.isempty(epgItem.epgId))
                                    addIds(PlayListConstant.IDS_EPG, epgItem.epgId);
                                if (!Text.isempty(epgItem.posterDefault)) {
                                    addImage("poster", epgItem.posterDefault, -1);
                                    isImageAdded = true;
                                }
                            } catch (Exception ignore) {}

                        } else {
                            addTitle(name);
                        }

                        lc = name.toLowerCase();

                        if (!isImageAdded) {

                            final String s1;
                            final String s2 = lc.replace(' ', '_')
                                    .replace("\"", "")
                                    .replace('.', '_')
                                    .replace(':', '_')
                                    .replace(';', '_')
                                    .replace('-', '_')
                                    .replace('+', '_')
                                    .replace('%', '_')
                                    .replace('!', '_')
                                    .replace('/', '_')
                                    .replace('\\', '_');

                            int i;
                            for (i = (s2.length() - 1); i > 0; i--)
                                if (s2.charAt(i) != '_')
                                    break;

                            if (i < (s2.length() - 1))
                                s1 = s2.substring(0, i + 1);
                            else
                                s1 = s2;

                            /*
                            Log.d("--mp3 name",
                                    String.format(
                                            Locale.getDefault(),
                                            "%s.webp",
                                            s1
                                    ));
                            */

                            addImage(
                                    "poster",
                                    String.format(
                                            Locale.getDefault(),
                                            "file:///android_asset/logo/%s.webp",
                                            s1
                                    ),
                                    -1
                            );
                        }
                        for (String r : m38uRadio) {
                            if (lc.contains(r)) {
                                playListType = PlayList.IDX_ONLINE_RADIO;
                                break;
                            }
                        }

                    } else if (s.startsWith("#EXTVLCOPT")) {
                        int pos = s.indexOf(':');
                        if (pos > 0) {
                            String  ids = null,
                                    val = null,
                                    name = s.substring(pos + 1, s.length());

                            if ((pos = name.indexOf('=')) > 0) {
                                ids = name.substring(0, pos).trim();
                                val = name.substring(pos + 1, name.length()).trim();
                                addIds(ids, val);
                            }
                        }
                    }

                } else if (s.startsWith("https://smarttvnews.ru")) {
                    itemType = PlayListConstant.TYPE_NONE;
                    return itemType;

                } else if (s.startsWith("http")) {
                    itemUri = s.trim();
                    itemType = PlayListConstant.TYPE_ONLINE;
                    if (itemUri.toLowerCase().contains(m38uRadio[0])) {
                        playListType = PlayList.IDX_ONLINE_RADIO;
                    }
                }
            }

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("- merge m3u8 parse", Text.requireString(e.getLocalizedMessage()), e);
        }
        return itemType;
    }

    public String getRating() {
        if (ratingList.size() > 0) {
            try {

                int i = 0;
                String rating1 = null, rating2 = null, rating4 = null;

                try {
                    rating1 = getRating("rating");
                    if (!Text.isempty(rating1)) {
                        double d = Double.parseDouble(rating1);
                        i += (Double.compare(d, 0.0) == 0) ? 0 : 1;
                    }
                } catch (Exception ignored) {}
                try {
                    rating2 = getRating("userrating");
                    if (!Text.isempty(rating2)) {
                        int n = Integer.parseInt(rating2);
                        i += (n > 0) ? 2 : 0;
                    }
                } catch (Exception ignored) {
                }
                try {
                    rating4 = getRating("votes");
                    if (!Text.isempty(rating4)) {
                        int n = Integer.parseInt(rating4);
                        i += (n > 0) ? 4 : 0;
                    }
                } catch (Exception ignored) {
                }

                switch (i) {
                    case 1: {
                        return rating1;
                    }
                    case 2: {
                        return rating2;
                    }
                    case 3: {
                        return String.format(
                                Locale.getDefault(),
                                "%s | %s",
                                rating1, rating2
                        );
                    }
                    case 4: {
                        return rating4;
                    }
                    case 5: {
                        return String.format(
                                Locale.getDefault(),
                                "%s | %s",
                                rating1, rating4
                        );
                    }
                    case 6: {
                        return String.format(
                                Locale.getDefault(),
                                "%s | %s",
                                rating2, rating4
                        );
                    }
                    case 7: {
                        return String.format(
                                Locale.getDefault(),
                                "%s | %s | %s",
                                rating1, rating2, rating4
                        );
                    }
                }
            } catch (Exception ignore) {}
        }
        return "";
    }
}
