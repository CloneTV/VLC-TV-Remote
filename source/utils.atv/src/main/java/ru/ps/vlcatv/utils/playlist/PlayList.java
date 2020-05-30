package ru.ps.vlcatv.utils.playlist;

import android.content.ContentValues;
import android.content.Context;

import androidx.annotation.Keep;
import androidx.databinding.ObservableField;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import ru.ps.vlcatv.utils.BuildConfig;
import ru.ps.vlcatv.utils.Log;
import ru.ps.vlcatv.utils.Text;
import ru.ps.vlcatv.utils.db.ConstantDataDb;
import ru.ps.vlcatv.utils.db.DbManager;
import ru.ps.vlcatv.utils.json.JSONArray;
import ru.ps.vlcatv.utils.json.JSONObject;
import ru.ps.vlcatv.utils.playlist.parse.ParseM3UList;
import ru.ps.vlcatv.utils.playlist.parse.PlayListParseInterface;
import ru.ps.vlcatv.utils.reflect.ReflectAttribute;
import ru.ps.vlcatv.utils.reflect.annotation.IArrayReflect;
import ru.ps.vlcatv.utils.reflect.annotation.IBaseTableReflect;
import ru.ps.vlcatv.utils.reflect.annotation.IFieldReflect;
import ru.ps.vlcatv.utils.reflect.annotation.IUniqueReflect;

@Keep
@IBaseTableReflect(
        IThisTableName = "TablePlayList",
        IIndexUnique = {
                @IUniqueReflect(IUnique = {"unique_root"})
        }
)
public class PlayList extends ReflectAttribute {

        private static final String TAG = PlayList.class.getSimpleName();
        private static final String PL_EXCEPT1 = "not property init before run";
        public static final int IDX_EMPTY = -1;
        public static final int IDX_ROOT_EMPTY = 0;
        public static final int IDX_HISTORY = 1;                // not saved to db!
        public static final int IDX_ONLINE_FAV = 2;             // not saved to db!
        public static final int IDX_ONLINE_TV = 3;
        public static final int IDX_ONLINE_RADIO = 4;
        public static final int IDX_ONLINE_FILMS = 5;

        private Executor mExecutor = null;
        private PlayListParseInterface pif = null;
        private DbManager dbMgr = null;
        private PlayStatusInterface playStatus = null;
        public AtomicBoolean isUpdateDb = new AtomicBoolean(false);
        public AtomicBoolean isCompleteDb = new AtomicBoolean(false);

        public boolean isRandomTitle = false;
        public boolean isRandomImage = false;
        public boolean isRandomTrailer = false;

        @IFieldReflect("unique_root")
        String uniqueId = TAG;
        @IFieldReflect("updates")
        public ObservableField<Date> updateDate = new ObservableField<>();
        @IFieldReflect("creates")
        public ObservableField<Date> createDate = new ObservableField<>();

        @IArrayReflect(value = "actors", SkipRecursion = false)
        public List<PlayListActors> actors = new ArrayList<>();
        @IArrayReflect(value = "genres", SkipRecursion = false)
        public List<PlayListGenres> genres = new ArrayList<>();
        @IArrayReflect(value = "tags", SkipRecursion = false)
        public List<PlayListTags> tags = new ArrayList<>();
        @IArrayReflect(value = "studios", SkipRecursion = false)
        public List<PlayListStudios> studios = new ArrayList<>();
        @IArrayReflect(value = "producers", SkipRecursion = false)
        public List<PlayListProducers> producers = new ArrayList<>();
        @IArrayReflect(value = "country", SkipRecursion = false)
        public List<PlayListCountry> country = new ArrayList<>();

        @IArrayReflect(value = "history", SkipRecursion = false)
        public List<PlayListHistoryIndex> history = new ArrayList<>();
        @IArrayReflect(value = "favorites", SkipRecursion = false)
        public List<PlayListFavorite> favorites = new ArrayList<>();

        @IArrayReflect(value = "groups", SkipRecursion = true)
        public List<PlayListGroup> groups = new ArrayList<>();

        @IArrayReflect(value = "items_edit", SkipRecursion = true)
        public List<PlayListItemEdit> itemsEdit = new ArrayList<>();
        public PlayListItem currentPlay = null;

        ////

        public PlayList() {
                dbIndex = 1;
        }

        public PlayList(Context context, PlayListParseInterface ifc, PlayStatusInterface psi) {
                setInstance(new DbManager(context), ifc, psi);
                dbMgr.open(ConstantDataDb.BaseVersion, this.getClass());
                dbIndex = 1;
                /* init(); */
        }
        public boolean isBusy() {
                return (isUpdateDb.get() || !isCompleteDb.get());
        }
        public boolean isEmpty() {
                return (groups.size() == 0);
        }

        private Executor getExecutor() {
                if (mExecutor == null)
                        mExecutor =Executors.newSingleThreadExecutor();
                return mExecutor;
        }
        public void setInstance(DbManager dbm, PlayListParseInterface ifc, PlayStatusInterface psi) {
                dbMgr =  dbm;
                pif = ifc;
                if (psi != null)
                        psi.setPlayList(this);
                playStatus = psi;
        }
        public int getOffset() {
                return IDX_ONLINE_FILMS + 1;
        }

        /// find

        public PlayListItem findItemByVlcId(long id) {
                for (PlayListGroup grp : groups) {
                        PlayListItem pli;
                        if ((pli = findItemByVlcId__(grp, id)) != null)
                                return pli;
                }
                return null;
        }
        public PlayListItem findItemByUrl(String url) {
                for (PlayListGroup grp : groups) {
                        PlayListItem pli;
                        if ((pli = findItemByUrl__(grp, url)) != null)
                                return pli;
                }
                return null;
        }
        public PlayListItem findItemByTitle(String title, PlayListGroup grp) {
                for (PlayListItem item : grp.items) {
                        if ((!Text.isempty(item.title.get())) && (item.title.get().equals(title)))
                                return item;
                }
                return null;
        }
        public PlayListItem findItemByDbId(long id) {
                for (PlayListGroup grp : groups) {
                        PlayListItem pli;
                        if ((pli = findItemByDbId__(grp, id)) != null)
                                return pli;
                }
                return null;
        }
        public PlayListGroup findGroupByVlcId(long id) {
                for (PlayListGroup grp : groups) {
                        PlayListGroup gr;
                        if ((gr = findGroupByVlcId__(grp, id)) != null)
                                return gr;
                }
                return null;
        }
        public PlayListGroup findGroupByItemVlcId(long id, int offset) {
                if (offset >= groups.size())
                        return null;
                for (int i = offset; i < groups.size(); i++) {
                        PlayListGroup gr;
                        if ((gr = findGroupByItemVlcId_(groups.get(i), id)) != null)
                                return gr;
                }
                return null;
        }

        ///

        public void saveItem(PlayListItem item) {
                if (BuildConfig.DEBUG) Log.d("- SAVE Media Item ="  + item.type, " title=" + item.title.get());
                try {
                        if ((item.dbIndex <= 0) || (item.dbParent <= 0)) {
                                item.reloadBindingData();
                                ContentValues cv = new ContentValues();
                                cv.put("uri", item.uri);
                                item.toDb(dbMgr, item.dbParent, cv);
                        } else {
                                item.toDb(dbMgr);
                        }

                } catch (Exception e) {
                        if (BuildConfig.DEBUG) Log.e(TAG, Text.requireString(e.getLocalizedMessage()), e);
                }
        }
        public void load(final JSONObject obj) {
                if (obj == null)
                        return;
                if ((groups.size() > IDX_ONLINE_FILMS) && (isCompleteDb.get())) {
                        pif.loadStage2();
                        return;
                }
                try {
                        new Thread(initPlayListRunnable(obj)).start();
                } catch (Exception e) {
                        if (BuildConfig.DEBUG) Log.e(TAG, Text.requireString(e.getLocalizedMessage()), e);
                }
        }
        public DbManager getDbManager() {
                return dbMgr;
        }
        public PlayListParseInterface getParseInterface() {
                return pif;
        }
        public void setPlayStatus(PlayStatusInterface s) {
                synchronized (playStatus) {
                        playStatus = s;
                }
        }
        public PlayStatusInterface getPlayStatus() {
                return playStatus;
        }
        public void setNewStatus(final JSONObject obj) {
                if (playStatus != null)
                        getExecutor().execute(new Runnable() {
                                @Override
                                public void run() {
                                        playStatus.setNewStatus(obj);
                                }
                        });
        }

        ///
        /// public method

        public void addHistory(PlayListItem item) {
                if ((item == null) || (dbMgr == null))
                        return;

                getExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                                if (BuildConfig.DEBUG) Log.d("- ADD History ="  + item.type, " title=" + item.title.get());
                                switch (item.type) {
                                        case PlayListConstant.TYPE_MOVIE:
                                        case PlayListConstant.TYPE_SERIES:
                                        case PlayListConstant.TYPE_VIDEO: {

                                                long hiId = 0, vlcId = item.getVlcId();
                                                if (history.size() > 0)
                                                        hiId = history.get(history.size() - 1).historyVlcId;

                                                if (hiId != vlcId) {
                                                        history.add(
                                                                new PlayListHistoryIndex(
                                                                        vlcId,
                                                                        item.stat.lastProgress.get()
                                                                )
                                                        );
                                                        if (groups.size() > 0) {
                                                                final PlayListGroup grp = groups.get(IDX_HISTORY);
                                                                grp.items.add(0, item);
                                                                synchronized (grp) {
                                                                        grp.notify();
                                                                }
                                                        }
                                                        if (!isUpdateDb.get()) {
                                                                isUpdateDb.set(true);
                                                                try {
                                                                        toDb(dbMgr, -1, true);
                                                                        if (BuildConfig.DEBUG) Log.d("- SAVE MAIN PLAYLIST TABLE TO DB", " OK");
                                                                } catch (Exception ignore) {}
                                                                isUpdateDb.set(false);
                                                        }
                                                        if (pif != null)
                                                                pif.loadStage1();
                                                        if (BuildConfig.DEBUG) Log.d("- ADD ITEM TO HISTORY", " VLC ID=" + vlcId);
                                                }
                                                break;
                                        }
                                }
                                saveItem(item);
                        }
                });
        }
        public void AddFavorites(PlayListFavorite fav) { // remote 184 Green button
                boolean b = false;
                for (PlayListFavorite f : favorites)
                        if (f.itemUrl.equals(fav.itemUrl)) {
                                b = true;
                                break;
                        }
                if (!b) {
                        favorites.add(fav);
                        loadFavorites_();
                        getExecutor().execute(new Runnable() {
                                @Override
                                public void run() {
                                        if (!isUpdateDb.get()) {
                                                isUpdateDb.set(true);
                                                toDb(dbMgr, -1, true);
                                                isUpdateDb.set(false);
                                        }
                                }
                        });
                }
        }
        public void RemoveFavorites(String url) { // remote 183 RED button
                boolean b = false;
                for (PlayListFavorite f : favorites)
                        if (f.itemUrl.equals(url)) {
                                favorites.remove(f);
                                loadFavorites_();
                                getExecutor().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                                if (!isUpdateDb.get()) {
                                                        isUpdateDb.set(true);
                                                        toDb(dbMgr, -1, true);
                                                        isUpdateDb.set(false);
                                                }
                                        }
                                });
                                break;
                        }
        }

        public void Clear() {
                getExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                                isUpdateDb.set(true);
                                Clear_();
                                isUpdateDb.set(false);
                                isCompleteDb.set(false);
                        }
                });
        }
        public void saveToDb() {
                if (!isUpdateDb.get()) {
                        isUpdateDb.set(true);
                        saveToDb_();
                        isUpdateDb.set(false);
                }
        }
        public void closeDb() {
                getExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                                if (!isUpdateDb.get()) {
                                        isUpdateDb.set(true);
                                        closeDb_();
                                        isUpdateDb.set(false);
                                }
                        }
                });
        }

        public void updateAll() {
                isUpdateDb.set(true);
                updateAll_();
                saveToDb_();
                isUpdateDb.set(false);
        }
        /*
        public void createFromVlc(final JSONObject obj) {
                getExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                                createFromVlc_(obj);
                                toDb(dbMgr);
                        }
                });
        }
        public void updateFromDb() {
                getExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                                updateFromDb_();
                        }
                });
        }
        public void updateFromNfo() {
                getExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                                updateFromNfo_();
                                pif.updateDataEnd();
                                toDb(dbMgr);
                                pif.saveDataEnd();
                        }
                });
        }
        public void updateTrailers() {
                getExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                                updateTrailers_();
                                pif.updateDataEnd();
                                toDb(dbMgr);
                                pif.saveDataEnd();
                        }
                });
        }
        public void updateFromOmdb() {
                getExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                                updateFromOmdb_();
                                pif.updateDataEnd();
                                toDb(dbMgr);
                                pif.saveDataEnd();
                        }
                });
        }
        */

        ///
        /// VLC play list private method

        private void Clear_() {
                if (currentPlay != null) {
                        saveItem(currentPlay);
                        toDb(dbMgr, -1, true);
                        synchronized(currentPlay) {
                                currentPlay = null;
                        }
                        groups.clear();
                }
        }
        private void saveToDb_() {
                if (dbMgr != null) {

                        toDb(dbMgr, -1, true);
                        if (groups.size() <= (IDX_ONLINE_FILMS + 1))
                                return;

                        if (BuildConfig.DEBUG) Log.d("- SAVE PlayList to DB", "- BEGIN = " + new Date().toString());

                        for (int i = IDX_ONLINE_FAV + 1; i < groups.size(); i++)
                                groups.get(i).toDb(dbMgr);

                        if (BuildConfig.DEBUG) Log.d("- SAVE PlayList to DB", "- END = " + new Date().toString());
                }
        }
        private void closeDb_() {
                if (dbMgr != null)
                        dbMgr.close();
                if (pif != null)
                        pif.close();
        }
        private void createFromVlc_(JSONObject obj) {
                if (obj == null)
                        return;
                if ((dbMgr == null) || (pif == null))
                        throw new RuntimeException(PL_EXCEPT1);

                JSONArray root = jArray_(obj);
                if (root == null)
                        return;

                if (BuildConfig.DEBUG) Log.d("- CREATE PlayList from VLC", "- BEGIN = " + new Date().toString());

                for (int i = 0; i < root.length(); i++) {

                        JSONObject ele = root.optJSONObject(i);
                        if (ele == null)
                                continue;

                        String strName = jName_(ele);
                        if (Text.isempty(strName))
                                continue;
                        PlayListGroup plg = new PlayListGroup(
                                this, strName, jId_(ele), (groups.size() + 1), true
                        );
                        groups.add(plg);
                        createFromVlc__(plg, jArray_(ele));
                }
                for (PlayListGroup plg : groups)
                        updateFromVlc__(plg);

                createDate.set(new Date(System.currentTimeMillis()));
                if (BuildConfig.DEBUG) Log.d("- CREATE PlayList from VLC", "- END = " + new Date().toString());
        }
        private void updateAll_() {
                if ((dbMgr == null) || (pif == null))
                        throw new RuntimeException(PL_EXCEPT1);

                if (groups.size() <= IDX_ONLINE_FILMS + 1)
                        return;

                if (BuildConfig.DEBUG) Log.d("- UPDATE PlayList All", "- BEGIN = " + new Date().toString());

                for (int i = IDX_HISTORY + 1; i < groups.size(); i++)
                        updateAll__(groups.get(i));

                if (BuildConfig.DEBUG) Log.d("- UPDATE PlayList All", "- END = " + new Date().toString());
        }
        private void updateFromDb_() {
                if ((dbMgr == null) || (pif == null))
                        throw new RuntimeException(PL_EXCEPT1);

                if (groups.size() <= IDX_ONLINE_FILMS + 1)
                        return;

                if (BuildConfig.DEBUG) Log.d("- UPDATE PlayList from DB", "- BEGIN = " + new Date().toString());

                for (int i = IDX_HISTORY + 1; i < groups.size(); i++)
                        updateFromDb__(groups.get(i));

                if (BuildConfig.DEBUG) Log.d("- UPDATE PlayList from DB", "- END = " + new Date().toString());
        }
        private void updateFromNfo_() {
                if ((dbMgr == null) || (pif == null))
                        throw new RuntimeException(PL_EXCEPT1);

                if (groups.size() <= IDX_ONLINE_FILMS + 1)
                        return;

                if (BuildConfig.DEBUG) Log.d("- UPDATE PlayList from NFO", "- BEGIN = " + new Date().toString());

                for (int i = IDX_ONLINE_FILMS + 1; i < groups.size(); i++)
                        updateFromNfo__(groups.get(i));

                if (BuildConfig.DEBUG) Log.d("- UPDATE PlayList from NFO", "- END = " + new Date().toString());
        }
        /*
        private void updateTrailers_() {
                if ((dbMgr == null) || (pif == null))
                        throw new RuntimeException(PL_EXCEPT1);

                if (groups.size() <= IDX_ONLINE_FILM + 1)
                        return;

                if (BuildConfig.DEBUG) Log.d("- UPDATE PlayList Trailers", "- BEGIN = " + new Date().toString());

                for (int i = IDX_ONLINE_FILM + 1; i < groups.size(); i++)
                        updateTrailers__(groups.get(i));

                if (BuildConfig.DEBUG) Log.d("- UPDATE PlayList Trailers", "- END = " + new Date().toString());
        }
        private void updateFromOmdb_() {
                if ((dbMgr == null) || (pif == null))
                        throw new RuntimeException(PL_EXCEPT1);

                if (groups.size() <= IDX_ONLINE_FILM + 1)
                        return;

                if (BuildConfig.DEBUG) Log.d("- UPDATE PlayList from OMDB", "- BEGIN + " + new Date().toString());

                for (int i = IDX_ONLINE_FILM + 1; i < groups.size(); i++)
                        updateFromOmdb__(groups.get(i));

                if (BuildConfig.DEBUG) Log.d("- UPDATE PlayList from OMDB", "- END " + new Date().toString());
        }
        */

        /// Load

        private void loadOnline_() {
                if (pif == null)
                        return;

                if (BuildConfig.DEBUG) Log.d("- UPDATE PlayList from Online", "- BEGIN");
                {
                        final String s1 = pif.downloadM3u8(null, IDX_ONLINE_TV);
                        if (!Text.isempty(s1)) {
                                ParseM3UList.parseM3u8(this, s1, IDX_ONLINE_TV);
                        } else {
                                for (String uri : PlayListConstant.TV_ONLINE_ARRAY) {
                                        final String s2 = pif.downloadM3u8(uri, IDX_ONLINE_TV);
                                        if (!Text.isempty(s2))
                                                ParseM3UList.parseM3u8(this, s2, IDX_ONLINE_TV);
                                }
                        }
                }
                //
                {
                        final String s1 = pif.downloadM3u8(null, IDX_ONLINE_RADIO);
                        if (!Text.isempty(s1))
                                ParseM3UList.parseM3u8(this, s1, IDX_ONLINE_RADIO);
                        else
                                ParseM3UList.parseM3u8(this, PlayListConstant.RADIO_ONLINE, IDX_ONLINE_RADIO);
                }
                //
                {
                        final String s1 = pif.downloadM3u8(null, IDX_ONLINE_FILMS);
                        if (!Text.isempty(s1))
                                ParseM3UList.parseM3u8(this, s1, IDX_ONLINE_FILMS);
                        else
                                ParseM3UList.parseM3u8(this, PlayListConstant.FILMS_ONLINE, IDX_ONLINE_FILMS);
                }
                if (BuildConfig.DEBUG) Log.d("- UPDATE PlayList from Online", "- END");
        }
        private void loadFavorites_() {
                try {
                        if (favorites.size() == 0)
                                return;
                        PlayListGroup grp = groups.get(IDX_ONLINE_FAV);
                        if (grp == null)
                                return;

                        if (BuildConfig.DEBUG) Log.d("- UPDATE PlayList from Favorites", "- BEGIN");

                        grp.items.clear();
                        for (PlayListFavorite fav : favorites) {
                                PlayListItem item = new PlayListItem(PlayList.this, fav);
                                if (!item.isEmpty())
                                        grp.items.add(item);
                        }
                        if (BuildConfig.DEBUG) Log.d("- UPDATE PlayList from Favorites", "- END");

                } catch (Exception e) {
                        if (BuildConfig.DEBUG) Log.e(TAG, Text.requireString(e.getLocalizedMessage()), e);
                }
        }
        private void loadHistory_() {
                if ((history.size() == 0) || (groups.size() <= IDX_HISTORY))
                        return;

                try {
                        PlayListGroup grp = groups.get(IDX_HISTORY);
                        if (grp == null)
                                return;

                        if (BuildConfig.DEBUG) Log.d("- UPDATE PlayList from History", "- BEGIN");

                        grp.items.clear();
                        int i = (history.size() - 1),
                            hm = pif.getHistoryMax();

                        for (; ((i >= 0) && (hm >= 0)); i--, hm--) {
                                PlayListHistoryIndex hi = history.get(i);
                                if (hi == null)
                                        continue;

                                PlayListItem item;
                                if ((item = findItemByVlcId(hi.historyVlcId)) != null)
                                        grp.items.add(item);
                        }
                        if (BuildConfig.DEBUG) Log.d("- UPDATE PlayList from History", "- END");
                } catch (Exception e) {
                        if (BuildConfig.DEBUG) Log.e(TAG, Text.requireString(e.getLocalizedMessage()), e);
                }
        }

        /// FIND

        private PlayListItem findItemByVlcId__(PlayListGroup grp, long id) {

                for (PlayListItem pli : grp.items)
                        if (pli.getVlcId() == id)
                                return pli;

                for (PlayListGroup gr : grp.groups) {
                        PlayListItem pli;
                        if ((pli = findItemByVlcId__(gr, id)) != null)
                                return pli;
                }
                return null;
        }
        private PlayListItem findItemByDbId__(PlayListGroup grp, long id) {

                for (PlayListItem pli : grp.items)
                        if (pli.dbIndex == id)
                                return pli;

                for (PlayListGroup gr : grp.groups) {
                        PlayListItem pli;
                        if ((pli = findItemByVlcId__(gr, id)) != null)
                                return pli;
                }
                return null;
        }
        private PlayListItem findItemByUrl__(PlayListGroup grp, String url) {
                for (PlayListItem pli : grp.items)
                        if (pli.uri.equals(url))
                                return pli;

                for (PlayListGroup gr : grp.groups) {
                        PlayListItem pli;
                        if ((pli = findItemByUrl__(gr, url)) != null)
                                return pli;
                }
                return null;
        }
        private PlayListGroup findGroupByVlcId__(PlayListGroup grp, long id) {

                if (grp.getVlcId() == id)
                        return grp;

                for (PlayListGroup gr : grp.groups) {
                        if (gr.getVlcId() == id)
                                return gr;
                        PlayListGroup grs;
                        if ((grs = findGroupByVlcId__(gr, id)) != null)
                                return grs;
                }
                return null;
        }
        private PlayListGroup findGroupByItemVlcId_(PlayListGroup grp, long id) {
                if (grp == null)
                        return null;

                if (grp.items.size() > 0)
                   for (PlayListItem item : grp.items)
                           if (item.getVlcId() == id)
                                   return grp;

                for (PlayListGroup gr : grp.groups) {
                        PlayListGroup grr;
                        if ((grr = findGroupByItemVlcId_(gr, id)) != null)
                                return grr;
                }
                return null;
        }

        /// UPDATE

        private void updateFromVlc__(PlayListGroup plg) {
                if (plg == null)
                        return;

                for (PlayListGroup ccg : plg.groups)
                        updateFromVlc__(ccg);

                try {
                        if ((plg.items.size() > 0) && (plg.season.get() > 0)) {
                                plg.episodes.set(plg.items.size());

                                if (plg.parent != null) {
                                        long timeTotal = 0;
                                        int episodesTotal = 0;
                                        PlayListGroup parent = plg.parent;

                                        for (PlayListGroup grp : parent.groups) {
                                                long timeSeason = 0;
                                                for (PlayListItem pli : grp.items)
                                                        timeSeason += pli.stat.totalProgress.get();

                                                if (timeSeason > 0)
                                                        grp.totalTime.set(timeSeason);
                                                grp.episodes.set(grp.items.size());
                                                episodesTotal += grp.items.size();
                                                timeTotal += timeSeason;
                                        }
                                        parent.totalTime.set(timeTotal);
                                        parent.episodes.set(episodesTotal);
                                        parent.season.set(parent.groups.size());
                                }
                        }
                } catch (Exception ignore) {}
        }
        private void createFromVlc__(PlayListGroup plg, JSONArray array) {
                if ((plg == null) || (array == null))
                        return;

                int grpIdx = 0,
                        grpId = PlayListUtils.getIdsInt(PlayListConstant.IDS_GRP_ID, plg.ids);

                for (int i = 0; i < array.length(); i++) {
                        try {
                                JSONObject ele = array.optJSONObject(i);
                                String strName = jName_(ele);
                                switch (jType(ele)) {
                                        case PlayListConstant.TYPE_NODE: {
                                                PlayListGroup ccg = new PlayListGroup(
                                                        this, strName, jId_(ele), grpId, false
                                                );
                                                ccg.parent = plg;
                                                plg.groups.add(ccg);
                                                createFromVlc__(ccg, jArray_(ele));
                                                break;
                                        }
                                        case PlayListConstant.TYPE_LEAF: {
                                                PlayListItem pli = new PlayListItem(
                                                        this, strName,
                                                        jUri_(ele), jId_(ele), jDuration_(ele),
                                                        grpId, grpIdx++
                                                );
                                                if (i == 0) {
                                                        plg.setSeason(pli.season.get());
                                                        plg.setNfoFake(pli.uri);
                                                        if (plg.parent != null)
                                                                plg.parent.setNfo(pli.uri);
                                                }
                                                plg.items.add(pli);
                                                break;
                                        }
                                }
                        } catch (Exception ignore) {}
                }
        }
        private void updateAll__(PlayListGroup grp) {
                if (grp == null)
                        return;

                try {
                        grp.updateFromNfo();
                } catch (Exception ignore) {}
                try {
                        grp.updateFromOmdb();
                } catch (Exception ignore) {}
                /*
                try {
                        grp.updateTrailer();
                } catch (Exception ignore) {}
                 */

                if (grp.items.size() > 0)
                        for (PlayListItem item : grp.items) {
                                try {
                                        if (item.isDataEmpty())
                                                item.updateFromNfo();
                                } catch (Exception ignore) {}
                                try {
                                        item.updateFromOmdb();
                                } catch (Exception ignore) {}
                                try {
                                        item.updateTrailer();
                                } catch (Exception ignore) {}
                        }

                if (grp.groups.size() > 0)
                        for (PlayListGroup gp : grp.groups)
                                updateAll__(gp);
        }
        private void updateFromNfo__(PlayListGroup grp) {
                if (grp == null)
                        return;

                try {
                        grp.updateFromNfo();
                } catch (Exception e) {
                        if (BuildConfig.DEBUG) Log.e(TAG, Text.requireString(e.getLocalizedMessage()), e);
                }
                if (grp.items.size() > 0)
                        for (PlayListItem item : grp.items) {
                                if (item.isDataEmpty())
                                        try {
                                                item.updateFromNfo();
                                        } catch (Exception e) {
                                                if (BuildConfig.DEBUG) Log.e(TAG, Text.requireString(e.getLocalizedMessage()), e);
                                        }
                        }

                if (grp.groups.size() > 0)
                        for (PlayListGroup gp : grp.groups)
                                updateFromNfo__(gp);
        }
        /*
        private void updateTrailers__(PlayListGroup grp) {
                if (grp == null)
                        return;

                try {
                        grp.updateTrailer();
                } catch (Exception e) {
                        if (BuildConfig.DEBUG) Log.e(TAG, Text.requireString(e.getLocalizedMessage()), e);
                }
                if (grp.items.size() > 0)
                        for (PlayListItem item : grp.items) {
                                try {
                                        item.updateTrailer();
                                } catch (Exception e) {
                                        if (BuildConfig.DEBUG) Log.e(TAG, Text.requireString(e.getLocalizedMessage()), e);
                                }
                        }

                if (grp.groups.size() > 0)
                        for (PlayListGroup gp : grp.groups)
                                updateTrailers__(gp);
        }
        private void updateFromOmdb__(PlayListGroup grp) {
                if (grp == null)
                        return;

                try {
                        grp.updateFromOmdb();
                } catch (Exception e) {
                        if (BuildConfig.DEBUG) Log.e(TAG, Text.requireString(e.getLocalizedMessage()), e);
                }

                if (grp.items.size() > 0)
                        for (PlayListItem item : grp.items) {
                                try {
                                        Thread.sleep(500);
                                        item.updateFromOmdb();
                                } catch (Exception e) {
                                        if (BuildConfig.DEBUG) Log.e(TAG, Text.requireString(e.getLocalizedMessage()), e);
                                }
                        }

                if (grp.groups.size() > 0)
                        for (PlayListGroup gp : grp.groups)
                                updateFromOmdb__(gp);
        }
        */
        private void updateFromDb__(PlayListGroup grp) {
                if (grp == null)
                        return;

                try {
                        grp.updateFromDb();
                } catch (Exception e) {
                        if (BuildConfig.DEBUG) Log.e(TAG, Text.requireString(e.getLocalizedMessage()), e);
                }
                if (grp.items.size() > 0) {
                        boolean isStart = true;
                        for (PlayListItem item : grp.items) {
                                try {
                                        if (isStart) {
                                                isStart = false;

                                                if (grp.parent != null) {
                                                        if (Text.isempty(grp.parent.nfo))
                                                                grp.parent.setNfo(item.uri);
                                                        grp.parent.updateFromDb(item.type);
                                                }
                                                if (Text.isempty(grp.nfo)) {
                                                        grp.setNfoFake(item.uri);
                                                        grp.updateFromDb();
                                                }
                                        } // END isStart (update group)
                                        item.updateFromDb();
                                } catch (Exception e) {
                                        if (BuildConfig.DEBUG) Log.e(TAG, Text.requireString(e.getLocalizedMessage()), e);
                                }
                        }
                }
                if (grp.groups.size() > 0)
                        for (PlayListGroup gp : grp.groups)
                                updateFromDb__(gp);
        }
        private int jType(JSONObject obj) {
                String s = obj.optString("type", null);
                switch (s) {
                        case "node": return PlayListConstant.TYPE_NODE;
                        case "leaf": return PlayListConstant.TYPE_LEAF;
                        default: return PlayListConstant.TYPE_NONE;
                }

        }
        private String jName_(JSONObject obj) {
                return obj.optString("name", null);
        }
        private String jUri_(JSONObject obj) {
                return obj.optString("uri", null);
        }
        private long jId_(JSONObject obj) {
                return obj.optLong("id", -1);
        }
        private int jDuration_(JSONObject obj) {
                return obj.optInt("duration", -1);
        }
        private JSONArray jArray_(JSONObject obj) {
                return obj.optJSONArray("children");
        }
        private void initPlayList() {

                if ((dbMgr != null) && (!dbMgr.isEmpty())) {
                        try {
                                fromDb(dbMgr, -1, 1, true);
                        } catch (Exception e) {
                                if (BuildConfig.DEBUG) Log.e(TAG, Text.requireString(e.getLocalizedMessage()), e);
                        }
                }

                ///

                if (groups.size() > 0)
                        groups.clear();

                PlayListGroup grp = new PlayListGroup(
                        this,
                        "-",
                        IDX_ROOT_EMPTY + 100000L,
                        IDX_ROOT_EMPTY + 1,
                        true
                );
                groups.add(grp);
                grp = new PlayListGroup(
                        this,
                        pif.getStringFormat(PlayListParseInterface.ID_FMT_HISTORY),
                        IDX_HISTORY + 100000L,
                        IDX_HISTORY + 1,
                        true
                );
                groups.add(grp);
                grp = new PlayListGroup(
                        this,
                        pif.getStringFormat(PlayListParseInterface.ID_FMT_FAVORITE),
                        IDX_ONLINE_FAV + 100000L,
                        IDX_ONLINE_FAV + 1,
                        true
                );
                groups.add(grp);
                grp = new PlayListGroup(
                        this,
                        pif.getStringFormat(PlayListParseInterface.ID_FMT_TV_ONLINE),
                        IDX_ONLINE_TV + 100000L,
                        IDX_ONLINE_TV + 1,
                        true
                );
                groups.add(grp);
                grp = new PlayListGroup(
                        this,
                        pif.getStringFormat(PlayListParseInterface.ID_FMT_RADIO_ONLINE),
                        IDX_ONLINE_RADIO + 100000L,
                        IDX_ONLINE_RADIO + 1,
                        true
                );
                groups.add(grp);
                grp = new PlayListGroup(
                        this,
                        pif.getStringFormat(PlayListParseInterface.ID_FMT_RADIO_ONLINE),
                        IDX_ONLINE_FILMS + 100000L,
                        IDX_ONLINE_FILMS + 1,
                        true
                );
                groups.add(grp);
        }

        ///

        private Runnable initPlayListRunnable(final JSONObject obj) {
                return  new Runnable() {
                        @Override
                        public void run() {
                                try {
                                        if (dbMgr != null) {

                                                initPlayList();
                                                createFromVlc_(obj);
                                                loadOnline_();
                                                loadFavorites_();
                                                pif.loadStage1();
                                                isCompleteDb.set(true);
                                                isUpdateDb.set(true);

                                                if (dbMgr.isEmpty()) {
                                                        pif.loadStageOnce();
                                                        updateFromNfo_();
                                                        pif.loadStageEnd();
                                                        saveToDb_();
                                                } else {
                                                        updateFromDb_();
                                                        loadHistory_();
                                                        pif.loadStageEnd();
                                                }
                                                isUpdateDb.set(false);
                                        } else {
                                                initPlayList();
                                                createFromVlc_(obj);
                                                loadOnline_();
                                                loadFavorites_();
                                                pif.loadStageEnd();
                                                isCompleteDb.set(true);
                                        }
                                        if (BuildConfig.DEBUG) Log.d("- LOAD PlayList", "- END = " + new Date().toString());

                                } catch (Exception e) {
                                        if (BuildConfig.DEBUG) Log.e(TAG, Text.requireString(e.getLocalizedMessage()), e);
                                }
                        }
                };
        }
}
