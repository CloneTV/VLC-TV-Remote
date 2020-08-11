package ru.ps.vlcatv.utils.playlist;

import androidx.annotation.Keep;
import android.content.ContentValues;
import android.util.Log;

import androidx.databinding.Observable;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.databinding.ObservableLong;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

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
import ru.ps.vlcatv.utils.reflect.annotation.IUniqueReflect;

@Keep
@IBaseTableReflect(
        IThisTableName = "TableGroups",
        IParentTable = "TableGroups",
        IParentKey = "idx",
        IParentSyncDelete = IBaseTableReflect.CASCADE,
        IParentSyncUpdate = IBaseTableReflect.DEFAULT,
        IIndexUnique = {
                @IUniqueReflect(IUnique = {"nfo"})
        },
        IIndexOne = { "id_parent" }
)
public class PlayListGroup extends ReflectAttribute implements PlayListObjectInterface {

    private boolean changeTitleOnce = false;
    private PlayList playListRoot = null;
    public ObservableBoolean isChange = new ObservableBoolean(false);

    PlayListGroup() {}
    public PlayListGroup(PlayList pl, String s, long id, int grpId, boolean isFakeNfo) {
        playListRoot = pl;
        PlayListUtils.setIdsSkip(new PlayListGroupIds(PlayListConstant.IDS_VLC, id), ids);
        PlayListUtils.setIdsSkip(new PlayListGroupIds(PlayListConstant.IDS_GRP_ID, grpId), ids);
        setTitle(s);
        if (isFakeNfo)
            setNfoFake(s);
        description.addOnPropertyChangedCallback(
                new Observable.OnPropertyChangedCallback() {
                    @Override
                    public void onPropertyChanged(Observable sender, int propertyId) {
                        reloadBindingData();
                    }
                }
        );
        dbIndex = -1;
        dbParent = -1;
    }

    public long selectedItemId = -1;

    @IFieldReflect("crc")
    public long crc = -1;
    @IFieldReflect("type")
    public int type = -1;
    @IFieldReflect("nfo")
    public String nfo = "";

    @IFieldReflect("title")
    public ObservableField<String> title = new ObservableField<>("");
    @IFieldReflect("desc")
    public ObservableField<String> description = new ObservableField<>("");
    @IFieldReflect("poster")
    public ObservableField<String> poster = new ObservableField<>("");
    @IFieldReflect("trailer")
    public ObservableField<String> trailer = new ObservableField<>("");
    @IFieldReflect("date")
    public ObservableField<String> date = new ObservableField<>("");
    @IFieldReflect("rating")
    public ObservableField<String> rating = new ObservableField<>("");

    @IFieldReflect("season")
    public ObservableInt season = new ObservableInt(0);
    @IFieldReflect("episodes")
    public ObservableInt episodes = new ObservableInt(0);
    @IFieldReflect("total_time")
    public ObservableLong totalTime = new ObservableLong(0);

    @IArrayReflect(value = "ids", SkipRecursion = false)
    public List<PlayListGroupIds> ids = new ArrayList<>();

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

    ///

    @IArrayReflect(value = "groups", SkipRecursion = true)
    public List<PlayListGroup> groups = new ArrayList<>();
    public PlayListGroup parent = null;

    @IArrayReflect(value = "items", SkipRecursion = true)
    public List<PlayListItem> items = new ArrayList<>();

    ///

    @Override
    public void setDuration(int d) {}
    @Override
    public void setPosition(int d) {}

    @Override
    public void reloadBindingData() {
        isChange.set(!isChange.get());
    }

    @Override
    public int visibleLastViewDate(ObservableBoolean b) {
        return 0;
    }

    @Override
    public int visibleLastViewLayout(ObservableBoolean b) {
        return 0;
    }

    @Override
    public int visibleProgress(ObservableBoolean b) {
        return 0;
    }

    @Override
    public int visibleWatchedImage(ObservableBoolean b) {
        return 0;
    }

    @Override
    public boolean visibleWatchedColor(ObservableBoolean b) {
        return false;
    }

    @Override
    public int visibleWatched(ObservableBoolean b) {
        return 0;
    }

    @Override
    public int visibleDate(ObservableBoolean b) {
        return 0;
    }

    @Override
    public int visibleDateField(ObservableBoolean b) {
        return 0;
    }

    @Override
    public int visibleRatingField(ObservableBoolean b) {
        return 0;
    }

    @Override
    public int visibleDurationField(ObservableBoolean b) {
        return 0;
    }

    @Override
    public int visibleUriField(ObservableBoolean b) {
        return 0;
    }

    @Override
    public int visibleMediaLayout(ObservableBoolean b) {
        return 0;
    }

    @Override
    public int visibleMediaImage(ObservableBoolean b) {
        return 0;
    }

    @Override
    public int visibleAwards(ObservableBoolean b) {
        return 0;
    }

    @Override
    public int visibleIsEmpty(ObservableBoolean b) {
        return 0;
    }

    @Override
    public long getVlcId() {
        return PlayListUtils.getIdsLong(PlayListConstant.IDS_VLC, ids);
    }

    @Override
    public String getImdbId() {
        if (season.get() == 0)
            return null;
        return PlayListUtils.getIdsString(PlayListConstant.IDS_IMDB, ids);
    }

    @Override
    public String getKpoId() {
        return PlayListUtils.getIdsString(PlayListConstant.IDS_KPO, ids);
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
    public String getDescription(ObservableBoolean b) {
        if ((parent != null) && (season.get() > 0))
            return parent.description.get();
        else if (!Text.isempty(description.get()))
            return description.get();
        else
            return "";
    }
    @Override
    public String getTotalSeasonViewTime(ObservableBoolean b) {
        long total =  totalTime.get();
        boolean isMinutes = (total > 60);
        long m = (isMinutes) ? (total / 60) : 0;
        boolean isHours = ((isMinutes) && (m > 60));
        long h = (isHours) ? (m / 60) : 0;
        if (isHours)
            m = (m - (h * 60));

        if ((m > 0) && (h > 0))
            return String.format(
                    Locale.getDefault(),
                    playListRoot
                            .getParseInterface()
                            .getStringFormat(PlayListParseInterface.ID_FMT_GROUP_TOTAL1),
                    h, m
            );
        else if (m > 0)
            return String.format(
                    Locale.getDefault(),
                    playListRoot
                            .getParseInterface()
                            .getStringFormat(PlayListParseInterface.ID_FMT_GROUP_TOTAL2),
                    m
            );
        else if (h > 0)
            return String.format(
                    Locale.getDefault(),
                    playListRoot
                            .getParseInterface()
                            .getStringFormat(PlayListParseInterface.ID_FMT_GROUP_TOTAL3),
                    h
            );
        else
            return "";
    }

    @Override
    public String getLastViewDate(ObservableBoolean b) {
        return null;
    }

    @Override
    public String getSeasonEpisode(ObservableBoolean b) {
        return null;
    }

    @Override
    public void setTitle(String s) {
        if (Text.isempty(s))
            return;
        if ((Text.isempty(title.get())) ||
            ((!Text.isempty(title.get())) && (!s.equals(title.get()))))
            title.set(s);
    }
    @Override
    public void setNfoFake(String s) {
        if ((!Text.isempty(s)) && (Text.isempty(nfo))) {
            int pos = s.lastIndexOf('/');
            if (pos > 0) {
                nfo = String.format(
                        Locale.getDefault(),
                        "%s/index.none",
                        s.substring(0, pos)
                ).replace(' ', '_');
            } else {
                nfo = String.format(
                        Locale.getDefault(),
                        "file:///%s/index.none",
                        s
                ).replace(' ', '_');
            }
        }
    }
    @Override
    public void setNfo(String s) {
        if ((!Text.isempty(s)) && (Text.isempty(nfo))) {
            int pos = s.lastIndexOf('/');
            if (pos > 0) {
                s = s.substring(0, pos);
                pos = s.lastIndexOf('/');
                if (pos > 0) {
                    nfo = String.format(
                            Locale.getDefault(),
                            "%s/tvshow.nfo",
                            s.substring(0, pos)
                    );
                }
            }
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
        if ((s > 0) && (s > season.get()))
            season.set(s);
        if (e > 0) {
            episodes.set(episodes.get() + 1);
        }
    }
    @Override
    public void setSeason(int s) {
        if (s <= 0)
            return;
        if (changeTitleOnce)
            return;

        changeTitleOnce = true;
        if (parent != null) {
            type = PlayListConstant.TYPE_SEASON;
            parent.type = PlayListConstant.TYPE_SHOWS;
            if (!Text.isempty(parent.title.get()))
                setTitle(
                        String.format(
                                Locale.getDefault(),
                                "%s / %d",
                                parent.title.get(), s
                        )
                );
            season.set(s);
        }
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
    public void copy(PlayListGroup plg) {

        if (plg == null)
            return;

        if ((plg.dbIndex > 0) && (plg.dbIndex != dbIndex))
            dbIndex = plg.dbIndex;
        if ((plg.dbParent > 0) && (plg.dbParent != dbParent))
            dbParent = plg.dbParent;
        if ((Text.isempty(nfo)) && (!Text.isempty(plg.nfo)))
            nfo = plg.nfo;

        if (!Text.isempty(plg.title.get()))
            title.set(plg.title.get());
        if (!Text.isempty(plg.description.get()))
            description.set(plg.description.get());
        if (!Text.isempty(plg.poster.get()))
            poster.set(plg.poster.get());
        if (!Text.isempty(plg.trailer.get()))
            trailer.set(plg.trailer.get());
        if (!Text.isempty(plg.date.get()))
            date.set(plg.date.get());
        if (!Text.isempty(plg.rating.get()))
            rating.set(plg.rating.get());
        if (plg.season.get() > 0)
            season.set(plg.season.get());
        if (plg.episodes.get() > 0)
            episodes.set(plg.episodes.get());
        if (plg.totalTime.get() > 0)
            totalTime.set(plg.totalTime.get());
        if (plg.type > 0)
            type = plg.type;

        if ((plg.producerIdx != null) && (plg.producerIdx.size() > 0))
            producerIdx = plg.producerIdx;
        if ((plg.studiosIdx != null) && (plg.studiosIdx.size() > 0))
            studiosIdx = plg.studiosIdx;
        if ((plg.actorIdx != null) && (plg.actorIdx.size() > 0))
            actorIdx = plg.actorIdx;
        if ((plg.genreIdx != null) && (plg.genreIdx.size() > 0))
            genreIdx = plg.genreIdx;
        if ((plg.countryIdx != null) && (plg.countryIdx.size() > 0))
            countryIdx = plg.countryIdx;
        if ((plg.tagIdx != null) && (plg.tagIdx.size() > 0))
            tagIdx = plg.tagIdx;

        PlayListUtils.setIdsSkip(plg.ids, ids, PlayListGroupIds.class);
    }

    @Override
    public void copy(ParseObject pa) {
        if (pa == null)
            return;

        switch (pa.itemType) {
            case PlayListConstant.TYPE_FOLDER_SHOWS:
            case PlayListConstant.TYPE_FOLDER_MOVIE:
            case PlayListConstant.TYPE_FOLDER:
            case PlayListConstant.TYPE_SHOWS: {
                copyNFO(pa);
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

        if ((type == PlayListConstant.TYPE_NONE) || (type == PlayListConstant.TYPE_NODE))
            type = pa.playListType;

        if (pa.itemPremiered != null) {
            try {
                SimpleDateFormat fmt = new SimpleDateFormat("yyyy", Locale.getDefault());
                date.set(fmt.format(pa.itemPremiered));
            } catch (Exception ignore) {}
        }
        if (pa.imageList.size() > 0) {
            if (Text.isempty(poster.get()))
                setImage(PlayListUtils.getListRandom(pa.imageList));
        }
        if (pa.idList.size() > 0)
            PlayListUtils.setIdsSkip(pa.idList, ids, PlayListGroupIds.class);
        if (!Text.isempty(pa.itemTitle)) {
            if (Text.isempty(title.get())) {
                title.set(pa.itemTitle);
            } else {
                String s = description.get();
                if (Text.isempty(s))
                    description.set(s);
                else
                    description.set(s + ", " + pa.itemTitle);
            }
        }
    }
    private void copyOMDB(ParseObject pa) {

        if ((!Text.isempty(pa.itemDescription)) && (Text.isempty(description.get())))
            setDescription(pa.itemDescription);
        if ((!Text.isempty(pa.itemTitle)) && (Text.isempty(title.get())))
            title.set(pa.itemTitle);
        if ((pa.imageList.size() > 0) && (Text.isempty(poster.get())))
            setImage(pa.imageList.get(0).valId);

        boolean b = (pa.itemSeason > 0);
        if (b)
            setSeason(pa.itemSeason);

        switch (type) {
            case PlayListConstant.TYPE_NONE:
            case PlayListConstant.TYPE_NODE:
            case PlayListConstant.TYPE_ONLINE: {
                if (b) {
                    if (groups.size() > 0)
                        type = PlayListConstant.TYPE_SHOWS;
                    else
                        type = PlayListConstant.TYPE_SEASON;
                } else {
                    type = PlayListConstant.TYPE_FOLDER_MOVIE;
                }
                break;
            }
        }

        if (pa.ratingList.size() > 0)
            rating.set(pa.getRating());
        PlayListUtils.setIdsSkip(pa.idList, ids, PlayListGroupIds.class);

        if (playListRoot != null) {
            actorIdx = PlayListUtils.setNewActors(pa.actorList, playListRoot.actors);
            genreIdx = PlayListUtils.setNewIndexList(pa.genreList, playListRoot.genres, PlayListGenres.class);
            producerIdx = PlayListUtils.setNewIndexList(pa.producerList, playListRoot.producers, PlayListProducers.class);
            studiosIdx = PlayListUtils.setNewIndexList(pa.studioList, playListRoot.studios, PlayListStudios.class);
            countryIdx = PlayListUtils.setNewIndexList(pa.countryList, playListRoot.country, PlayListCountry.class);
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

    private void copyNFO(ParseObject pa) {
        crc = pa.itemCrc;

        if (!Text.isempty(pa.itemTitle))
            title.set(
                    pa.itemTitle
            );
        else if (((playListRoot != null) &&
                 (playListRoot.isRandomTitle) &&
                 (pa.titleList.size() > 0)) ||
                (Text.isempty(title.get())))
            title.set(
                    PlayListUtils.getListRandom(pa.titleList)
            );

        if (pa.imageList.size() > 0)
            poster.set(
                    PlayListUtils.getListRandom(pa.imageList)
            );

        if (pa.trailerList.size() > 0)
            trailer.set(
                    PlayListUtils.getListRandom(pa.trailerList)
            );

        if (pa.imageList.size() > 0) {

            List<String> imgList = new ArrayList<>();
            for (ParseContainer pc : pa.imageList) {

                if (pc.intId < 0) {
                    imgList.add(pc.valId);

                } else if (groups.size() > (pc.intId)) {

                    PlayListGroup grp = groups.get(pc.intId);

                    if (!Text.isempty(pc.valId)) {
                        if (pc.typeId.equals("poster"))
                            grp.poster.set(pc.valId);
                        else if (((pc.typeId.equals("thumb")) ||
                                  (pc.typeId.equals("fanart")) ||
                                  (pc.typeId.equals("banner"))) &&
                                (Text.isempty(grp.poster.get())))
                            grp.poster.set(pc.valId);
                    }
                    grp.title.set(
                            String.format(
                                    Locale.getDefault(),
                                    "%s / %d",
                                    title.get(),
                                    (pc.intId + 1)
                            )
                    );
                }
            }

            if ((playListRoot != null) && (playListRoot.isRandomImage))
                poster.set(PlayListUtils.getStringRandom(imgList));
            else if (imgList.size() > 0)
                poster.set(imgList.get(0));
        }

        PlayListUtils.setIdsSkip(pa.idList, ids, PlayListGroupIds.class);

        if (playListRoot != null) {

            actorIdx = PlayListUtils.setNewActors(pa.actorList, playListRoot.actors);
            genreIdx = PlayListUtils.setNewIndexList(pa.genreList, playListRoot.genres, PlayListGenres.class);
            producerIdx = PlayListUtils.setNewIndexList(pa.producerList, playListRoot.producers, PlayListProducers.class);
            studiosIdx = PlayListUtils.setNewIndexList(pa.studioList, playListRoot.studios, PlayListStudios.class);
            countryIdx = PlayListUtils.setNewIndexList(pa.countryList, playListRoot.country, PlayListCountry.class);
            tagIdx = PlayListUtils.setNewIndexList(pa.tagList, playListRoot.tags, PlayListTags.class);
        }

        if (!Text.isempty(pa.itemDescription))
            setDescription(pa.itemDescription);

        switch (type) {
            case PlayListConstant.TYPE_FOLDER:
            case PlayListConstant.TYPE_NODE:
            case PlayListConstant.TYPE_NONE: {
                type = pa.itemType;
                break;
            }
        }

        if (pa.itemPremiered != null) {
            try {
                SimpleDateFormat fmt = new SimpleDateFormat("yyyy", Locale.getDefault());
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
        return ((Text.isempty(title.get())) ||
                (Text.isempty(poster.get())) ||
                (Text.isempty(description.get())));
    }
    @Override
    public boolean isEmpty() {
        return ((groups.size() == 0) &&
                (items.size() == 0));
    }
    @Override
    public void updateFromDB() {
        updateFromDB(0);
    }
    @Override
    public void updateFromDB(int type) {
        try {
            if (!Text.isempty(nfo)) {
                if (playListRoot != null) {
                    DbManager dbm = playListRoot.getDbManager();
                    if (dbm != null) {
                        try {
                            ContentValues cv = new ContentValues();
                            cv.put("nfo", nfo);
                            PlayListGroup pg = new PlayListGroup();
                            pg.fromDb(dbm, cv, true);
                            copy(pg);

                        } catch (Exception ignore) {}
                    }
                }
            }
            switch (type) {
                case PlayListConstant.TYPE_MOVIE:
                    type = PlayListConstant.TYPE_FOLDER_MOVIE;
                    break;
                case PlayListConstant.TYPE_SEASON:
                case PlayListConstant.TYPE_SERIES:
                    type = PlayListConstant.TYPE_FOLDER_SHOWS;
                    break;
                case PlayListConstant.TYPE_AUDIO:
                    type = PlayListConstant.TYPE_FOLDER_AUDIO;
                    break;
                case PlayListConstant.TYPE_NONE:
                case PlayListConstant.TYPE_NODE:
                    type = PlayListConstant.TYPE_FOLDER;
                    break;
                case 0:
                    break;
            }
        } catch (Exception ignore) {}
    }

    @Override
    public void updateFromNFO() {
        try {
            if ((Text.isempty(nfo)) ||
                (nfo.endsWith(".none")))
                return;

            PlayListParseInterface pif;
            if ((playListRoot == null) || ((pif = playListRoot.getParseInterface()) == null))
                return;

            JSONObject obj = (JSONObject) pif.downloadNFO(nfo);
            if (obj == null)
                return;
            ParseObject po = new ParseObject(obj);
            switch (po.itemType) {
                case PlayListConstant.TYPE_FOLDER:
                case PlayListConstant.TYPE_FOLDER_AUDIO:
                case PlayListConstant.TYPE_FOLDER_MOVIE:
                case PlayListConstant.TYPE_FOLDER_SHOWS:
                case PlayListConstant.TYPE_NODE:
                case PlayListConstant.TYPE_SEASON:
                case PlayListConstant.TYPE_SHOWS:
                    break;
                default:
                    return;
            }
            if (crc == po.itemCrc)
                return;
            copy(po);

        } catch (Exception ignore) {}
    }

    @Override
    public void updateTRAILER() {
        try {
            PlayListParseInterface pif;
            if ((items.size() == 0) ||
                (season.get() == 0) ||
                (playListRoot == null) || ((pif = playListRoot.getParseInterface()) == null))
                return;

            String uri = trailer.get();
            if (Text.isempty(uri)) {
                List<PlayListItemTrailers> trailers = items.get(0).trailers;
                if (trailers.size() == 0)
                    return;
                uri = PlayListUtils.getListRandom(trailers);
            }
            String s = pif.checkTRAILER(uri);
            if (!Text.isempty(s)) {
                if (BuildConfig.DEBUG) Log.e("- GROUP Local trailer found: " + title.get(), "" + s);
                trailer.set(s);
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("- update GROUP Trailer exception: ", e.getMessage(), e);
        }
        reloadBindingData();
    }

    @Override
    public void updateFromMOVEDB() {
        try {
            PlayListParseInterface pif;
            if ((season.get() == 0) ||
                (playListRoot == null) || ((pif = playListRoot.getParseInterface()) == null))
                return;

            String imdb;
            if ((imdb = getImdbId()) == null) {
                String s = title.get();
                if (Text.isempty(s))
                    return;
                int pos = s.lastIndexOf('/');
                if (pos > 0)
                    s = s.substring(0, pos);
                copy(
                        PlayListUtils.parseObject(
                                (JSONObject) pif.downloadIMDB(
                                        s,
                                        PlayListParseInterface.ID_FMT_SEASON
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

}
