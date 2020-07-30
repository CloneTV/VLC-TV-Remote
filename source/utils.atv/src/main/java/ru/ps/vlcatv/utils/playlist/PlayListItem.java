package ru.ps.vlcatv.utils.playlist;

import androidx.annotation.Keep;

import android.content.ContentValues;
import android.os.Parcel;
import android.util.Log;
import android.view.View;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.ps.vlcatv.utils.BuildConfig;
import ru.ps.vlcatv.utils.Text;
import ru.ps.vlcatv.utils.db.DbManager;
import ru.ps.vlcatv.utils.json.JSONObject;
import ru.ps.vlcatv.utils.playlist.parse.ParseContainer;
import ru.ps.vlcatv.utils.playlist.parse.ParseObject;
import ru.ps.vlcatv.utils.playlist.parse.PlayListParseInterface;
import ru.ps.vlcatv.utils.reflect.ReflectAttribute;
import ru.ps.vlcatv.utils.reflect.annotation.IArrayReflect;
import ru.ps.vlcatv.utils.reflect.annotation.IBaseTableReflect;
import ru.ps.vlcatv.utils.reflect.annotation.IFieldReflect;
import ru.ps.vlcatv.utils.reflect.annotation.IObjectReflect;
import ru.ps.vlcatv.utils.reflect.annotation.IUniqueReflect;

@Keep
@IBaseTableReflect(
        IThisTableName = "TableItems",
        IParentTable = "TableGroups",
        IParentKey = "idx",
        IParentSyncDelete = IBaseTableReflect.CASCADE,
        IParentSyncUpdate = IBaseTableReflect.DEFAULT,
        IIndexUnique = {
                @IUniqueReflect(IUnique = {"id_parent", "uri"}),
                @IUniqueReflect(IUnique = {"uri"})
        },
        IIndexOne = { "id_parent", "title" }
)
public final class PlayListItem extends ReflectAttribute implements PlayListObjectInterface {

    public ObservableBoolean isChange = new ObservableBoolean(false);
    private PlayList playListRoot = null;

    public PlayListItem() {}
    public PlayListItem(PlayList pl, String s, String u, long id, int d, int grpId, int grpIdx) {
        playListRoot = pl;
        setUri(u);
        parseUri(u, s);
        setDuration(d);
        PlayListUtils.setIdsSkip(new PlayListItemIds(PlayListConstant.IDS_VLC, id), ids);
        PlayListUtils.setIdsSkip(new PlayListItemIds(PlayListConstant.IDS_GRP_ID, grpId), ids);
        PlayListUtils.setIdsSkip(new PlayListItemIds(PlayListConstant.IDS_GRP_IDX, grpIdx), ids);
    }
    public PlayListItem(PlayList pl, PlayListFavorite fav) {
        playListRoot = pl;
        copy(fav);
    }
    public PlayListItem(PlayList pl, ParseObject po) {
        playListRoot = pl;
        copy(po);
    }

    /// Parcelable

    @Override
    public int describeContents() {
        return 0;
    }
    public static final Creator<PlayListItem> CREATOR = new Creator<PlayListItem>() {
        @Override
        public PlayListItem createFromParcel(Parcel in) {
            return new PlayListItem(in);
        }

        @Override
        public PlayListItem[] newArray(int size) {
            return new PlayListItem[size];
        }
    };
    protected PlayListItem(Parcel parcel) {
        super(parcel);
        fromParcelable(parcel);
    }
    public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);
        toParcelable(parcel);
    }

    @IFieldReflect("crc")
    public long crc = -1;
    @IFieldReflect("title")
    public final ObservableField<String> title = new ObservableField<>("");
    @IFieldReflect("desc")
    public final ObservableField<String> description = new ObservableField<>("");
    @IFieldReflect("poster")
    public final ObservableField<String> poster = new ObservableField<>("");
    @IFieldReflect("trailer")
    public final ObservableField<String> trailer = new ObservableField<>("");
    public final ObservableField<String> trailerInfo = new ObservableField<>("");
    @IFieldReflect("date")
    public final ObservableField<String> date = new ObservableField<>("");
    @IFieldReflect("rating")
    public final ObservableField<String> rating = new ObservableField<>("");
    @IFieldReflect("awards")
    public final ObservableField<String> awards = new ObservableField<>("");
    @IFieldReflect("dimension")
    public ObservableField<String> dimension = new ObservableField<>("");

    @IFieldReflect("uri")
    public String uri = null;
    @IFieldReflect("nfo")
    public String nfo = null;

    @IFieldReflect("season")
    public final ObservableInt season = new ObservableInt(-1);
    @IFieldReflect("episode")
    public final ObservableInt episode = new ObservableInt(-1);

    @IFieldReflect("type")
    public int type = -1;

    @IFieldReflect("type_online")
    public int onlineType = -1;

    @IObjectReflect("statistic")
    public PlayListItemStatistic stat = new PlayListItemStatistic();
    public ObservableField<String> duration = new ObservableField<>();

    @IArrayReflect(value = "ids", SkipRecursion = false)
    public List<PlayListItemIds> ids = new ArrayList<>();

    @IArrayReflect(value = "titles", SkipRecursion = true)
    public List<PlayListItemTitles> titles = new ArrayList<>();

    @IArrayReflect(value = "images", SkipRecursion = true)
    public List<PlayListItemImages> images = new ArrayList<>();

    @IArrayReflect(value = "trailers", SkipRecursion = true)
    public List<PlayListItemTrailers> trailers = new ArrayList<>();

    @IArrayReflect(value = "actors", SkipRecursion = false)
    public List<PlayListActorsIndex> actorIdx = new ArrayList<>();

    @IArrayReflect(value = "producers", SkipRecursion = false)
    public List<PlayListProducersIndex> producerIdx = new ArrayList<>();

    @IArrayReflect(value = "genres", SkipRecursion = false)
    public List<PlayListGenresIndex> genreIdx = new ArrayList<>();

    @IArrayReflect(value = "tags", SkipRecursion = false)
    public List<PlayListTagsIndex> tagIdx = new ArrayList<>();

    @IArrayReflect(value = "studios", SkipRecursion = false)
    public List<PlayListStudiosIndex> studiosIdx = new ArrayList<>();

    @IArrayReflect(value = "country", SkipRecursion = false)
    public List<PlayListCountryIndex> countryIdx = new ArrayList<>();

    @IArrayReflect(value = "urls", SkipRecursion = false)
    public List<PlayListItemUrls> urls = new ArrayList<>();

    ///

    @Override
    public void setDuration() {
        int pos = stat.totalProgress.get();
        if (pos <= 0)
            return;
        stat.isMinutes = (pos > 60);
        if (stat.lastProgress.get() <= 0)
            stat.lastProgress.set(5);
        setStatData();
    }
    @Override
    public void setDuration(int d) {
        stat.totalProgress.set(d);
        stat.isMinutes = (d > 60);
        setStatData();
    }
    @Override
    public void setPosition(int d) {
        stat.lastProgress.set(d);
        setStatData();
    }
    private void setStatData() {

        if (stat.totalProgress.get() <= 0) {
            duration.set("");
            return;
        }

        String tmType = null;
        int total  = ((stat.isMinutes) ? (stat.totalProgress.get() / 60) : Math.max(stat.totalProgress.get(), 0)),
            last   = ((stat.lastProgress.get() > 0) ?
                      ((stat.isMinutes) ? (stat.lastProgress.get() / 60) : Math.max(stat.lastProgress.get(), 0)) : 0),
            remain = ((stat.lastProgress.get() > 0) ?
                      ((stat.isMinutes) ? ((stat.totalProgress.get() - stat.lastProgress.get()) / 60) :
                              Math.max((stat.totalProgress.get() - stat.lastProgress.get()), 0)) : 0);

        if (playListRoot != null) {
            PlayListParseInterface pif = playListRoot.getParseInterface();
            if (pif != null)
                tmType = pif.getStringFormat(
                        (stat.isMinutes) ?
                                PlayListParseInterface.ID_FMT_TIME_MIN :
                                PlayListParseInterface.ID_FMT_TIME_SEC,
                        ((remain > 0) ? remain : ((last > 0) ? last : total))
                );
        }
        if (Text.isempty(tmType)) {
            if (stat.isMinutes)
                tmType = "min.";
            else
                tmType = "sec.";
        }
        if (remain > 0) {
            duration.set(String.format(
                    Locale.getDefault(),
                    "%d/%d/%d %s",
                    total, last, remain, tmType
            ));
        }
        else if (last > 0) {
            duration.set(String.format(
                    Locale.getDefault(),
                    "%d/%d %s",
                    total, last, tmType
            ));
        } else {
            duration.set(String.format(
                    Locale.getDefault(),
                    "%d %s",
                    total, tmType
            ));
        }
    }

    @Override
    public void reloadBindingData() {
        isChange.set(!isChange.get());
    }
    @Override
    public String getDescription(ObservableBoolean b) {
        return description.get();
    }
    @Override
    public String getSeasonEpisode(ObservableBoolean b) {
        if ((season.get() <= 0) && (episode.get() <= 0))
            return "";

        try {
            String s = null,
                   e = null;

            if ((playListRoot != null) && (Text.isempty(date.get()))) {
                PlayListParseInterface pif = playListRoot.getParseInterface();
                if (pif != null) {
                    s = pif.getStringFormat(PlayListParseInterface.ID_FMT_SEASON);
                    e = pif.getStringFormat(PlayListParseInterface.ID_FMT_EPISODE);
                }
            }
            if ((Text.isempty(s)) || (Text.isempty(e)))
                return String.format(
                        Locale.getDefault(),
                        "%d/%d",
                        season.get(),
                        episode.get()
                );
            else
                return String.format(
                        Locale.getDefault(),
                        "%s %d, %s %d",
                        s, season.get(),
                        e, episode.get()
                );

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("- getSeasonEpisode: ", e.getMessage(), e);
            return "";
        }
    }
    @Override
    public String getLastViewDate(ObservableBoolean b) {
        if (stat.lastView == null)
            return "";
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            return fmt.format(stat.lastView);
        } catch (Exception ignore) {}
        return "";
    }
    @Override
    public int visibleLastViewDate(ObservableBoolean b) {
        return (stat.lastView == null) ? View.GONE : View.VISIBLE;
    }
    @Override
    public int visibleLastViewLayout(ObservableBoolean b) {
        return ((stat.lastView == null) && (!stat.watched.get())) ? View.GONE : View.VISIBLE;
    }
    @Override
    public int visibleProgress(ObservableBoolean b) {
        return ((stat.lastProgress.get() > 0) ? View.VISIBLE : View.GONE);
    }
    @Override
    public int visibleWatchedImage(ObservableBoolean b) {
        return ((stat.watched.get()) ? View.GONE : View.VISIBLE);
    }
    @Override
    public boolean visibleWatchedColor(ObservableBoolean b) {
        return stat.watched.get();
    }
    @Override
    public int visibleWatched(ObservableBoolean b) {
        return ((stat.watched.get()) ? View.VISIBLE : View.GONE);
    }
    @Override
    public int visibleDate(ObservableBoolean b) {
        return ((Text.isempty(date.get())) ? View.GONE : View.VISIBLE);
    }
    @Override
    public int visibleDateField(ObservableBoolean b) {
        return ((Text.isempty(date.get())) ? View.GONE :
                ((Text.isempty(rating.get())) ? View.VISIBLE : View.GONE));
    }
    @Override
    public int visibleRatingField(ObservableBoolean b) {
        return ((Text.isempty(rating.get())) ? View.GONE : View.VISIBLE);
    }
    @Override
    public int visibleDurationField(ObservableBoolean b) {
        return ((Text.isempty(duration.get())) ? View.GONE : View.VISIBLE);
    }
    @Override
    public int visibleUriField(ObservableBoolean b) {
        return (((!Text.isempty(uri)) && (type == PlayListConstant.TYPE_ONLINE)) ? View.VISIBLE : View.GONE);
    }
    @Override
    public int visibleMediaLayout(ObservableBoolean b) {
        return (((Text.isempty(poster.get())) && (Text.isempty(trailer.get()))) ? View.GONE : View.VISIBLE);
    }
    @Override
    public int visibleMediaImage(ObservableBoolean b) {
        return ((Text.isempty(poster.get())) ? View.GONE : View.VISIBLE);
    }
    @Override
    public int visibleAwards(ObservableBoolean b) {
        return (Text.isempty(awards.get()) ? View.GONE : View.VISIBLE);
    }
    @Override
    public int visibleIsEmpty(ObservableBoolean b) {
        return (isDataEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public long getVlcId() {
        return PlayListUtils.getIdsLong(PlayListConstant.IDS_VLC, ids);
    }
    @Override
    public String getImdbId() {
        return PlayListUtils.getIdsString(PlayListConstant.IDS_IMDB, ids);
    }
    @Override
    public long getDbIndex() {
        return dbIndex;
    }
    @Override
    public long getDbParent() {
        return dbParent;
    }
    @Override
    public void setTitle(String s) {
        if (!Text.isempty(s)) {

            title.set(s.replace('.', ' ')
                       .replace('_', ' ')
            );

        } else if (!Text.isempty(uri)) {
            int pos1 = uri.lastIndexOf('/'),
                pos2 = uri.lastIndexOf('.');

            String text;
            if ((pos1 > 0) && (pos2 > 0))
                text = uri.substring(pos1, pos2);
            else if (pos1 > 0)
                text = uri.substring(0, pos1);
            else if (pos2 > 0)
                text = uri.substring(0, pos2);
            else
                text = uri;

            title.set(text.replace('.', ' ')
                    .replace('/', ' ')
                    .replace('_', ' ')
            );
        } else {
            title.set(
                    String.format(
                            Locale.getDefault(),
                            "edit this id:  %s",
                            PlayListUtils.getIdsString(PlayListConstant.IDS_VLC, ids)
                    )
            );
        }
    }

    private boolean checkTitle(String s) {
        final String[] cmp = new String[] {
                "webrip",
                "webdlrip",
                "newstudio",
                "octopus"
        };
        String check = s.toLowerCase();
        for (String c : cmp)
            if (check.contains(c))
                return false;
        return true;
    }

    private void parseUri(String s, String t) {

        int pos;
        String name;
        try {
            name = URLDecoder.decode(s, "UTF-8");
        } catch (UnsupportedEncodingException ignore) {
            name = s;
        }
        if ((pos = name.lastIndexOf('/')) > 0)
            name = name.substring(pos + 1, name.length()).trim();
        else
            name = name.trim();

        uriParse[] parsersEpisode = new uriParse[] {
                new uriParse(
                        ".*[.\\s][sS](\\d{2})[eE](\\d{2})[.\\s]([a-zA-Z0-9._-]+)[.\\s].*",
                        new Integer[] { 3, 1, 2 }
                ),
                new uriParse(
                        "(.*?)[.\\s][sS](\\d{2})[eE](\\d{2}).*",
                        new Integer[] { 1, 2, 3 }
                ),
                new uriParse(
                        "^(0?[0-9]|1[0-9]|2[0-9])[xX](\\d{2})\\s-\\s.*?[(](.*?)[)].*",
                        new Integer[] { 3, 1, 2 }
                ),
                new uriParse(
                        "^(0?[0-9]|1[0-9]|2[0-9])[xX](\\d{2})\\s-\\s(.*?)[\\.].*",
                        new Integer[] { 3, 1, 2 }
                )
        };
        for (uriParse p : parsersEpisode) {
            try {
                String[] out = p.parse(name);
                if (out != null) {
                    setSeason(
                            Integer.parseInt(out[uriParse.IDX_SEASON]),
                            Integer.parseInt(out[uriParse.IDX_EPISODE])
                    );

                    if (!checkTitle(out[uriParse.IDX_TITLE]))
                        continue;
                    setTitle(
                            String.format(
                                    Locale.getDefault(),
                                    "%s %d/%d",
                                    out[uriParse.IDX_TITLE], season.get(), episode.get()
                            )
                    );
                    return;
                }
            } catch (Exception ignore) {}
        }
        uriParse[] parsersFilm = new uriParse[] {
                new uriParse(
                        "(.*?)[.\\s](\\d{4}).*",
                        new Integer[] { 1, 2 }
                ),
                new uriParse(
                        "(.*?)[(](\\d{4})[)].*",
                        new Integer[] { 1, 2 }
                )
        };
        for (uriParse p : parsersFilm) {
            try {
                String[] out = p.parse(name);
                if (out != null) {
                    date.set(out[uriParse.IDX_YEAR]);
                    if (!checkTitle(out[uriParse.IDX_TITLE]))
                        continue;
                    setTitle(out[uriParse.IDX_TITLE]);
                    return;
                }
            } catch (Exception ignore) {}
        }
        setTitle(t);
    }

    @Override
    public void setUri(String s) {
        if (Text.isempty(s))
            return;

        uri = s;
        int pos1 = s.lastIndexOf('.');
        if (pos1 > 0) {
            nfo = String.format(
                    Locale.getDefault(),
                    "%s.nfo",
                    s.substring(0, pos1)
            );
            String ss = s.substring(pos1 + 1, s.length());
            if (!Text.isempty(ss)) {
                switch (ss) {
                    case "au":
                    case "snd":
                    case "pcm":
                    case "aif":
                    case "aifc":
                    case "aiff":
                    case "vorbis":
                    case "wav":
                    case "mp3":
                    case "ogg": {
                        type = PlayListConstant.TYPE_AUDIO;
                        nfo = null;
                        break;
                    }
                    case "grp3":
                    case "mp4":
                    case "mkv":
                    case "mpeg":
                    case "3gp":
                    case "3g2":
                    case "3gpp":
                    case "3gpp2":
                    case "webm":
                    case "avi": {
                        type = PlayListConstant.TYPE_VIDEO;
                        break;
                    }
                    case "m3u8": {
                        type = PlayListConstant.TYPE_ONLINE;
                        nfo = null;
                        break;
                    }
                    default: {
                        if (s.startsWith("http")) {
                            type = PlayListConstant.TYPE_ONLINE;
                            nfo = null;
                        } else {
                            type = PlayListConstant.TYPE_LEAF;
                        }
                        break;
                    }
                }
            }
        } else {
            nfo = null;
        }
    }
    @Override
    public void setImage(String s) {
        if (Text.isempty(s))
            return;
        poster.set(s);
    }
    @Override
    public void setTrailer(String s) {
        if (Text.isempty(s))
            return;
        trailer.set(s);
    }
    @Override
    public void setSeason(int s, int e) {
        if (s > 0)
            season.set(s);
        if (e > 0)
            episode.set(e);
        if ((season.get() > 0) && (episode.get() > 0))
            type = PlayListConstant.TYPE_SERIES;
    }
    @Override
    public void setDescription(String s) {
        if (Text.isempty(s))
            return;
        if ((!Text.isempty(description.get())) && (s.equals(description.get())))
            return;
        description.set(s);
    }

    @Override
    public void copy(PlayListFavorite fav) {
        if (fav == null)
            return;
        type = fav.itemType;
        setTitle(fav.itemTitle);
        setDescription(fav.itemDesc);
        setImage(fav.itemImage);
        setUri(fav.itemUrl);
        long id = ((fav.itemVlcId > 0L) ? fav.itemVlcId : 0L);
        PlayListUtils.setIdsSkip(new PlayListItemIds(PlayListConstant.IDS_VLC, id), ids);
        PlayListUtils.setIdsSkip(new PlayListItemIds(PlayListConstant.IDS_GRP_ID, PlayList.IDX_ONLINE_FAV), ids);
        if (!Text.isempty(fav.itemEpg))
            PlayListUtils.setIdsSkip(new PlayListItemIds(PlayListConstant.IDS_EPG, fav.itemEpg), ids);
    }

    @Override
    public void copy(PlayListItem item) {
        if (item == null)
            return;

        if ((item.dbIndex > 0) && (item.dbIndex != dbIndex))
            dbIndex = item.dbIndex;
        if ((item.dbParent > 0) && (item.dbParent != dbParent))
            dbParent = item.dbParent;
        if ((Text.isempty(nfo)) && (!Text.isempty(item.nfo)))
            nfo = item.nfo;
        if ((Text.isempty(uri)) && (!Text.isempty(item.uri)))
            uri = item.uri;

        if (!Text.isempty(item.description.get()))
            description.set(item.description.get());
        if (!Text.isempty(item.awards.get()))
            awards.set(item.awards.get());
        if (!Text.isempty(item.date.get()))
            date.set(item.date.get());
        if (!Text.isempty(item.rating.get()))
            rating.set(item.rating.get());
        if (item.season.get() > 0)
            season.set(item.season.get());
        if (item.episode.get() > 0)
            episode.set(item.episode.get());
        if (item.type > 0)
            type = item.type;

        if (item.stat != null) {
            if (item.stat.totalProgress.get() > 0)
                setDuration(item.stat.totalProgress.get());
            if (item.stat.lastProgress.get() > 0)
                setPosition(item.stat.lastProgress.get());
            if (!stat.watched.get())
                stat.watched.set(item.stat.watched.get());
            if (item.stat.playCount.get() > stat.playCount.get())
                stat.playCount.set(item.stat.playCount.get());
            if (item.stat.lastView != null) {
                if ((stat.lastView == null) ||
                    (stat.lastView.getTime() < item.stat.lastView.getTime()))
                    stat.lastView = item.stat.lastView;
            }
        }

        if (!Text.isempty(title.get()))
            PlayListUtils.setItemListSkip(new PlayListItemTitles(title.get()), titles);

        PlayListUtils.setIdsSkip(item.ids, ids, PlayListItemIds.class);
        PlayListUtils.setItemListSkip(item.titles, titles);
        PlayListUtils.setItemListSkip(item.images, images);
        PlayListUtils.setItemListSkip(item.trailers, trailers);

        if (!Text.isempty(item.title.get()))
            title.set(item.title.get());

        if (titles.size() > 0) {
            if (((playListRoot != null) && (playListRoot.isRandomTitle)) || (Text.isempty(item.title.get())))
                title.set(
                        PlayListUtils.getListRandom(titles)
                );
        } else if (!Text.isempty(item.title.get())) {
            title.set(item.title.get());
        }

        if (images.size() > 0) {
            if (((playListRoot != null) && (playListRoot.isRandomImage)) || (Text.isempty(item.poster.get())))
                poster.set(
                        PlayListUtils.getListRandom(images)
                );
        } else if (!Text.isempty(item.poster.get())) {
            poster.set(item.poster.get());
        }

        if (trailers.size() > 0) {
            if (((playListRoot != null) && (playListRoot.isRandomTrailer)) || (Text.isempty(item.trailer.get())))
                trailer.set(
                        PlayListUtils.getListRandom(trailers)
                );
        } else if (!Text.isempty(item.trailer.get())) {
            trailer.set(item.trailer.get());
        }

        if ((item.producerIdx != null) && (item.producerIdx.size() > 0))
            producerIdx = item.producerIdx;
        if ((item.actorIdx != null) && (item.actorIdx.size() > 0))
            actorIdx = item.actorIdx;
    }

    @Override
    public void copy(ParseObject pa) {
        if (pa == null)
            return;

        switch (pa.itemType) {
            case PlayListConstant.TYPE_MOVIE:
            case PlayListConstant.TYPE_SERIES: {
                copyNFO(pa);
                break;
            }
            case PlayListConstant.TYPE_ONLINE: {
                copyM3U8(pa);
                break;
            }
            case PlayListConstant.TYPE_SOURCE_OMDB: {
                copyOMDB(pa);
                break;
            }
            case PlayListConstant.TYPE_SOURCE_IMDB: {
                copyIMDB(pa);
                break;
            }
            default:
                break;
        }
    }

    private void copyIMDB(ParseObject pa) {

        if (type == PlayListConstant.TYPE_ONLINE)
            onlineType = pa.playListType;

        if (pa.itemPremiered != null) {
            try {
                SimpleDateFormat fmt = new SimpleDateFormat("yyyy", Locale.getDefault());
                date.set(fmt.format(pa.itemPremiered));
            } catch (Exception ignore) {}
        }
        if (pa.imageList.size() > 0) {
            for (ParseContainer c : pa.imageList)
                PlayListUtils.setItemListSkip(new PlayListItemImages(c.valId), images);
            if (type == PlayListConstant.TYPE_ONLINE) {
                setImage(PlayListUtils.getListRandom(images));
            } else {
                if (Text.isempty(poster.get()))
                    setImage(PlayListUtils.getListRandom(images));
            }
        }
        if (pa.idList.size() > 0)
            PlayListUtils.setIdsSkip(pa.idList, ids, PlayListItemIds.class);
        if (!Text.isempty(pa.itemTitle))
            PlayListUtils.setItemListSkip(new PlayListItemTitles(pa.itemTitle), titles);
    }
    private void copyOMDB(ParseObject pa) {

        if ((!Text.isempty(pa.itemDescription)) && (Text.isempty(description.get())))
            setDescription(pa.itemDescription);
        if (!Text.isempty(pa.itemTitle)) {
            if ((Text.isempty(title.get())) || (title.get().toLowerCase().startsWith("эпизод")))
                    title.set(pa.itemTitle);
            PlayListUtils.setItemListSkip(new PlayListItemTitles(pa.itemTitle), titles);
        }
        if (pa.imageList.size() > 0) {
            for (ParseContainer c : pa.imageList)
                PlayListUtils.setItemListSkip(new PlayListItemImages(c.valId), images);
            if (type == PlayListConstant.TYPE_ONLINE) {
                setImage(PlayListUtils.getListRandom(images));
            } else {
                if (Text.isempty(poster.get()))
                    setImage(PlayListUtils.getListRandom(images));
            }
        }

        boolean b = ((pa.itemSeason > 0) || (pa.itemEpisode > 0));
        if (b)
            setSeason(pa.itemSeason, pa.itemEpisode);

        switch (type) {
            case PlayListConstant.TYPE_ONLINE: {
                if (b)
                    onlineType = PlayListConstant.TYPE_SERIES;
                break;
            }
            case PlayListConstant.TYPE_LEAF: {
                if (b)
                    type = PlayListConstant.TYPE_SERIES;
                else
                    type = PlayListConstant.TYPE_MOVIE;
                break;
            }
        }

        if (pa.ratingList.size() > 0) {
            ParseContainer pc = pa.findFromParseContainer(PlayListConstant.IDS_AWARDS, pa.ratingList);
            if (pc != null)
                awards.set(pc.valId);
            rating.set(pa.getRating());
        }
        PlayListUtils.setIdsSkip(pa.idList, ids, PlayListItemIds.class);

        if (playListRoot != null) {
            if (pa.actorList.size() > 0) {
                List<PlayListActorsIndex> actor_idx;
                actor_idx = PlayListUtils.setNewActors(pa.actorList, playListRoot.actors);
                if (actor_idx.size() > 0)
                    actorIdx.addAll(actor_idx);
            }
            if (pa.genreList.size() > 0) {
                List<PlayListGenresIndex> genre_idx;
                genre_idx = PlayListUtils.setNewIndexList(pa.genreList, playListRoot.genres, PlayListGenres.class);
                if (genre_idx.size() > 0)
                    genreIdx.addAll(genre_idx);
            }
            if (pa.producerList.size() > 0) {
                List<PlayListProducersIndex> producer_idx;
                producer_idx = PlayListUtils.setNewIndexList(pa.producerList, playListRoot.producers, PlayListProducers.class);
                if (producer_idx.size() > 0)
                    producerIdx.addAll(producer_idx);
            }
            if (pa.studioList.size() > 0) {
                List<PlayListStudiosIndex> studios_idx;
                studios_idx = PlayListUtils.setNewIndexList(pa.studioList, playListRoot.studios, PlayListStudios.class);
                if (studios_idx.size() > 0)
                    studiosIdx.addAll(studios_idx);
            }
            if (pa.countryList.size() > 0) {
                List<PlayListCountryIndex> country_idx;
                country_idx = PlayListUtils.setNewIndexList(pa.countryList, playListRoot.country, PlayListCountry.class);
                if (country_idx.size() > 0)
                    countryIdx.addAll(country_idx);
            }
            if (pa.tagList.size() > 0) {
                List<PlayListTagsIndex> tag_idx;
                tag_idx = PlayListUtils.setNewIndexList(pa.tagList, playListRoot.tags, PlayListTags.class);
                if (tag_idx.size() > 0)
                    tagIdx.addAll(tag_idx);
            }
        }

        if ((Text.isempty(date.get())) && (pa.itemPremiered != null)) {
            try {
                SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                date.set(fmt.format(
                        Objects.requireNonNull(pa.itemPremiered))
                );
            } catch (Exception ignore) {}
        }
    }

    private void copyM3U8(ParseObject pa) {
        crc = pa.itemCrc;
        uri = pa.itemUri;
        type = pa.itemType;

        duration.set("");
        urls.add(new PlayListItemUrls(pa.itemUri));

        if (!Text.isempty(pa.itemTitle))
            title.set(
                    pa.itemTitle
            );
        else if (pa.titleList.size() > 0)
            title.set(
                    pa.titleList.get(0)
            );

        if (pa.imageList.size() > 0)
            for (ParseContainer pc : pa.imageList) {
                poster.set(pc.valId);
                break;
            }
        if (pa.idList.size() > 0)
            PlayListUtils.setIdsSkip(pa.idList, ids, PlayListItemIds.class);
        PlayListUtils.setIdsSkip(new PlayListItemIds(PlayListConstant.IDS_VLC, 0), ids);
    }

    private void copyNFO(ParseObject pa) {

        if (pa.itemCrc > 0)
            crc = pa.itemCrc;

        if (pa.titleList.size() > 0)
            for (String s : pa.titleList)
                PlayListUtils.setItemListSkip(new PlayListItemTitles(s), titles);

        if (!Text.isempty(title.get()))
            PlayListUtils.setItemListSkip(new PlayListItemTitles(title.get()), titles);

        if (pa.trailerList.size() > 0)
            for (String s : pa.trailerList)
                PlayListUtils.setItemListSkip(new PlayListItemTrailers(s), trailers);

        if (pa.imageList.size() > 0)
            for (ParseContainer c : pa.imageList)
                PlayListUtils.setItemListSkip(new PlayListItemImages(c.valId), images);

        PlayListUtils.setIdsSkip(pa.idList, ids, PlayListItemIds.class);

        if (playListRoot != null) {

            actorIdx = PlayListUtils.setNewActors(pa.actorList, playListRoot.actors);
            genreIdx = PlayListUtils.setNewIndexList(pa.genreList, playListRoot.genres, PlayListGenres.class);
            producerIdx = PlayListUtils.setNewIndexList(pa.producerList, playListRoot.producers, PlayListProducers.class);
            studiosIdx = PlayListUtils.setNewIndexList(pa.studioList, playListRoot.studios, PlayListStudios.class);
            countryIdx = PlayListUtils.setNewIndexList(pa.countryList, playListRoot.country, PlayListCountry.class);
            tagIdx = PlayListUtils.setNewIndexList(pa.tagList, playListRoot.tags, PlayListTags.class);

            if (!Text.isempty(pa.itemTitle))
                title.set(
                        pa.itemTitle
                );
            else if (playListRoot.isRandomTitle)
                title.set(
                        PlayListUtils.getListRandom(titles)
                );

            if ((playListRoot.isRandomTrailer) || (Text.isempty(trailer.get())))
                setTrailer(PlayListUtils.getListRandom(trailers));
            if ((playListRoot.isRandomImage) || (Text.isempty(poster.get())))
                setImage(PlayListUtils.getListRandom(images));
        }

        if (!Text.isempty(pa.itemDescription))
            setDescription(pa.itemDescription);
        if ((pa.itemSeason > 0) || (pa.itemEpisode > 0))
            setSeason(pa.itemSeason, pa.itemEpisode);
        if (!Text.isempty(pa.itemDimension))
            dimension.set(pa.itemDimension);

        switch (type) {
            case PlayListConstant.TYPE_LEAF:
            case PlayListConstant.TYPE_NODE:
            case PlayListConstant.TYPE_NONE: {
                type = pa.itemType;
                break;
            }
        }
        if (pa.itemPremiered != null) {
            try {
                SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                date.set(fmt.format(
                        Objects.requireNonNull(pa.itemPremiered))
                );
            } catch (Exception ignore) {}
        }
        if (pa.ratingList.size() > 0)
            rating.set(pa.getRating());
    }

    @Override
    public boolean isDataEmpty() {
        switch (type) {
            case PlayListConstant.TYPE_ONLINE:
            case PlayListConstant.TYPE_AUDIO:
            case PlayListConstant.TYPE_FOLDER: return false;
            case PlayListConstant.TYPE_NONE: return true;
        }
        return ((Text.isempty(title.get())) ||
                (Text.isempty(poster.get())) ||
                (Text.isempty(description.get())));
    }
    @Override
    public boolean isEmpty() {
        return ((Text.isempty(uri)) ||
                (Text.isempty(title.get())));
    }

    @Override
    public void updateFromDB() {
        try {
            if (!Text.isempty(uri)) {
                if (playListRoot != null) {
                    DbManager dbm = playListRoot.getDbManager();
                    if (dbm != null) {
                        try {
                            ContentValues cv = new ContentValues();
                            cv.put("uri", uri);
                            PlayListItem item = new PlayListItem();
                            item.fromDb(dbm, cv, true);
                            copy(item);

                        } catch (Exception e) {
                            if (BuildConfig.DEBUG) Log.e("- update From Db update exception (1): ", Text.requireString(e.getLocalizedMessage()), e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("- update From Db update exception (2): ", Text.requireString(e.getLocalizedMessage()), e);
        }
        reloadBindingData();
    }

    @Override
    public void updateFromNFO() {
        try {
            if ((Text.isempty(nfo)) ||
                (nfo.endsWith(".none")))
                return;

            if (BuildConfig.DEBUG) Log.e("- From Nfo: " + title.get(), "" + nfo);

            PlayListParseInterface pif;
            if ((playListRoot == null) || ((pif = playListRoot.getParseInterface()) == null))
                return;

            ParseObject po = PlayListUtils.parseObject((JSONObject) pif.downloadNFO(nfo));
            if (po == null)
                return;
            switch (po.itemType) {
                case PlayListConstant.TYPE_LEAF:
                case PlayListConstant.TYPE_MOVIE:
                case PlayListConstant.TYPE_SERIES:
                case PlayListConstant.TYPE_VIDEO:
                    break;
                default:
                    return;
            }
            if (crc == po.itemCrc) {
                return;
            }
            copy(po);

        } catch (Exception ignore) {}
        reloadBindingData();
    }

    @Override
    public void updateFromMOVEDB() {
        try {
            PlayListParseInterface pif;
            if ((playListRoot == null) || ((pif = playListRoot.getParseInterface()) == null))
                return;

            String imdb;
            if ((imdb = getImdbId()) == null) {
                copy(
                        PlayListUtils.parseObject(
                                (JSONObject) pif.downloadIMDB(
                                        title.get(),
                                        ((episode.get() > 0) ?
                                                PlayListParseInterface.ID_FMT_EPISODE :
                                                PlayListParseInterface.ID_FMT_MOVIE
                                        )
                                )
                        )
                );
                imdb = getImdbId();
            }
            if (!Text.isempty(imdb))
                copy(
                        PlayListUtils.parseObject(
                                (JSONObject) pif.downloadOMDB(imdb)
                        )
                );

        } catch (Exception ignore) {}
        reloadBindingData();
    }

    @Override
    public void updateTRAILER() {
        try {
            PlayListParseInterface pif;
            if ((playListRoot == null) || ((pif = playListRoot.getParseInterface()) == null))
                return;

            String s = pif.checkTRAILER(uri);
            if (!Text.isempty(s)) {
                if (BuildConfig.DEBUG) Log.e("- ITEM Local trailer found: " + title.get(), "" + s);
                if (!Text.isempty(trailer.get()))
                    PlayListUtils.setItemListSkip(new PlayListItemTrailers(trailer.get()), trailers);
                PlayListUtils.setItemListSkip(new PlayListItemTrailers(s), trailers);
                trailer.set(s);
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("- update ITEM Trailer exception: ", Text.requireString(e.getLocalizedMessage()), e);
        }
        reloadBindingData();
    }

    ///

    static protected final class uriParse {
        public static final int IDX_TITLE = 0;
        public static final int IDX_YEAR = 1;
        public static final int IDX_SEASON = 1;
        public static final int IDX_EPISODE = 2;
        Pattern pattern;
        Integer[] list;

        uriParse(String p, Integer[] idx) {
            pattern = Pattern.compile(p);
            list = idx;
        }
        public String[] parse(String s) {
            try {
                Matcher m = pattern.matcher(s);
                if (!m.matches())
                    return null;

                String[] out = new String[list.length];
                for (int i = 0; i < list.length; i++)
                    out[i] = m.group(list[i]);
                return out;

            } catch (Exception ignore) { return null; }
        }
    }
}
