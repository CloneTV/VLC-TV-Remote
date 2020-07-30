package ru.ps.vlcatv.utils.playlist;

import android.content.ContentValues;
import android.content.Context;

import androidx.annotation.Keep;
import androidx.databinding.Observable;
import androidx.databinding.ObservableField;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import ru.ps.vlcatv.utils.BuildConfig;
import ru.ps.vlcatv.utils.Log;
import ru.ps.vlcatv.utils.Text;
import ru.ps.vlcatv.utils.db.ConstantDataDb;
import ru.ps.vlcatv.utils.db.DbManager;
import ru.ps.vlcatv.utils.json.JSONArray;
import ru.ps.vlcatv.utils.json.JSONObject;
import ru.ps.vlcatv.utils.playlist.method.MethodCheckAll;
import ru.ps.vlcatv.utils.playlist.method.MethodCreateFromVlc;
import ru.ps.vlcatv.utils.playlist.method.MethodUpdateAll;
import ru.ps.vlcatv.utils.playlist.method.MethodUpdateFromDB;
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
        private static final String TAG1 = " - LOAD PlayList";
        public static final String PL_EXCEPT1 = "not property init before run";
        public static final String PL_STAGE_BEGIN = "- BEGIN = ";
        public static final String PL_STAGE_BREAK = "- BREAK = ";
        public static final String PL_STAGE_END = "- END = ";

        public static final int DB_ACTION_EMPTY = -1;
        public static final int DB_ACTION_USING = 1;
        public static final int DB_ACTION_CLOSE = 2;
        public static final int DB_ACTION_SAVE_PART = 3;
        public static final int DB_ACTION_SAVE_ALL = 4;
        public static final int DB_ACTION_EMPTY_GET_VLC = 5;

        public static final int IDX_EMPTY = -1;
        public static final int IDX_ROOT_EMPTY = 0;
        public static final int IDX_HISTORY = 1;                // not saved to db!
        public static final int IDX_ONLINE_FAV = 2;             // not saved to db!
        public static final int IDX_ONLINE_TV = 3;
        public static final int IDX_ONLINE_RADIO = 4;
        public static final int IDX_ONLINE_FILMS = 5;
        public static final int IDX_ONLINE_USER_DEFINE = 6;
        public static final int IDX_GROUP_LAST = IDX_ONLINE_USER_DEFINE;

        private Executor mExecutor = null;
        private PlayListParseInterface pif = null;
        private DbManager dbMgr = null;
        private PlayStatusInterface playStatus = null;
        private AtomicInteger actionStateDb = new AtomicInteger(DB_ACTION_EMPTY);
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
        @IArrayReflect(value = "schedule", SkipRecursion = false)
        public List<PlayListSchedule> schedule = new ArrayList<>();

        @IArrayReflect(value = "groups", SkipRecursion = true)
        public List<PlayListGroup> groups = new ArrayList<>();

        @IArrayReflect(value = "items_edit", SkipRecursion = true)
        public List<PlayListItemEdit> itemsEdit = new ArrayList<>();
        public final ObservableField<PlayListItem> currentPlay = new ObservableField<>();
        private PlayListItem currentPlayOld = null;

        /// public base method

        PlayList() {
                dbIndex = 1;
        }
        public PlayList(Context context, PlayListParseInterface ifc, PlayStatusInterface psi) {
                setInstance(new DbManager(context), ifc, psi);
                dbMgr.open(ConstantDataDb.BaseVersion, this.getClass());
                dbIndex = 1;
                currentPlay.addOnPropertyChangedCallback(changePlayItemCb);
        }
        public boolean isBusy() {
                return ((actionStateDb.get() != DB_ACTION_EMPTY) || !isCompleteDb.get());
        }
        public boolean isEmpty() {
                return (groups.size() == 0);
        }
        public void setInstance(DbManager dbm, PlayListParseInterface ifc, PlayStatusInterface psi) {
                dbMgr =  dbm;
                pif = ifc;
                if (psi != null)
                        psi.setPlayList(this);
                playStatus = psi;
        }
        public int getOffset() {
                return IDX_GROUP_LAST + 1;
        }
        public PlayListGroup getGroup() {
                return (groups.size() > getOffset()) ? groups.get(getOffset()) : null;
        }
        public int getDbState() {
                return actionStateDb.get();
        }
        private void setDbState_(int i) {
                actionStateDb.set(i);
                if (pif != null)
                        pif.saveStage(i);
                if (BuildConfig.DEBUG) Log.w(
                        TAG + " Db State",
                        " change=" + i + ", time=" + new Date(System.currentTimeMillis())
                );
        }
        private Executor getExecutor() {
                if (mExecutor == null)
                        mExecutor = Executors.newSingleThreadExecutor();
                return mExecutor;
        }

        /// schedule

        public PlayListFavorite getScheduleEx() {
                try {
                        long now = Calendar.getInstance().getTime().getTime();
                        for (PlayListSchedule p : schedule) {
                                long offset = (now - ((Date) p.get(PlayListSchedule.GET_DATE)).getTime());
                                if (offset > -30000) {   // 30 seconds
                                        schedule.remove(p);
                                        if (offset > 600000)  // 10 minutes
                                                return null;
                                        PlayListFavorite fav = (PlayListFavorite) p.get(PlayListSchedule.GET_FAVORITE);
                                        if (BuildConfig.DEBUG) Log.d(TAG + " EPG schedule found=" + offset, " title=" + fav.itemTitle);
                                        getExecutor().execute(new Runnable() {
                                                final PlayListSchedule item = p;
                                                @Override
                                                public void run() {
                                                        try {
                                                                if (waitDb_(DB_ACTION_USING)) {
                                                                        try {
                                                                                item.DbDelete(dbMgr);
                                                                        } finally {
                                                                                waitDbEnd_();
                                                                        }
                                                                }
                                                        } catch (Exception ignore) {}
                                                }
                                        });
                                        return fav;
                                }
                        }
                } catch (Exception ignore) {}
                return null;
        }
        public void addSchedule(final Date d, final PlayListFavorite fav) {
                if (d == null)
                        return;
                long now = Calendar.getInstance().getTime().getTime();
                if (now >= d.getTime())
                        return;
                addScheduleEx_(fav, d);
        }
        private void addScheduleEx_(final PlayListFavorite fav, final Date d) {
                if ((Text.isempty(fav.itemTitle)) || (d == null))
                        return;
                getExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                                try {
                                        for (PlayListSchedule p : schedule)
                                                if ((Date) p.get(PlayListSchedule.GET_DATE) == d)
                                                        return;

                                        schedule.add(new PlayListSchedule(d, fav));
                                        PlayListItem item;
                                        if ((item = PlayListUtils.findItemByTitle(groups.get(IDX_ONLINE_FAV), fav.itemTitle)) != null)
                                                item.trailerInfo.set(fav.itemEpgNotify);

                                        if ((item = PlayListUtils.findItemByTitle(groups.get(IDX_ONLINE_TV), fav.itemTitle)) == null)
                                                if ((item = PlayListUtils.findItemByTitle(groups.get(IDX_ONLINE_RADIO), fav.itemTitle)) == null)
                                                        return;
                                        item.trailerInfo.set(fav.itemEpgNotify);

                                } catch (Exception ignore) {}
                        }
                });
        }
        private void saveSchedule_() {
                try {
                        long now = Calendar.getInstance().getTime().getTime();
                        for (PlayListSchedule p : schedule) {
                                long play = ((Date) p.get(PlayListSchedule.GET_DATE)).getTime();
                                if (now > play) {
                                        schedule.remove(p);
                                        if (waitDb_(DB_ACTION_USING)) {
                                                try {
                                                        p.DbDelete(dbMgr);
                                                } finally {
                                                        waitDbEnd_();
                                                }
                                        }
                                }
                        }
                } catch (Exception ignore) {}
        }

        /// favorites

        public void AddFavoritesEx(PlayListFavorite fav) { // remote 184 Green button
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
                                        if (waitDb_(DB_ACTION_SAVE_PART)) {
                                                try {
                                                        toDb(dbMgr, -1, true);
                                                } finally {
                                                        waitDbEnd_();
                                                }
                                        }
                                }
                        });
                }
        }
        public void RemoveFavoritesEx(String url) { // remote 183 RED button
                boolean b = false;
                for (PlayListFavorite f : favorites)
                        if (f.itemUrl.equals(url)) {
                                favorites.remove(f);
                                loadFavorites_();
                                getExecutor().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                                if (waitDb_(DB_ACTION_SAVE_PART)) {
                                                        try {
                                                                toDb(dbMgr, -1, true);
                                                        } finally {
                                                                waitDbEnd_();
                                                        }
                                                }
                                        }
                                });
                                break;
                        }
        }

        ///

        public void load(final JSONObject obj) {
                if (obj == null)
                        return;
                if ((groups.size() > IDX_GROUP_LAST) && (isCompleteDb.get())) {
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
        public void setNewStatusEx(final JSONObject obj) {
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

        public void ClearDbEx() {
                getExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                                Clear_();
                                isCompleteDb.set(false);
                        }
                });
        }
        public void saveToDb() {
                saveToDb_();
        }
        public void closeDbEx() {
                getExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                                closeDb_();
                        }
                });
        }
        public void checkAll() {
                if ((dbMgr == null) || (pif == null))
                        throw new RuntimeException(PlayList.PL_EXCEPT1);
                MethodCheckAll.go(groups);
                saveToDb_();
        }
        public void updateAll() {
                if ((dbMgr == null) || (pif == null))
                        throw new RuntimeException(PlayList.PL_EXCEPT1);
                MethodUpdateAll.go(groups);
                saveToDb_();
        }

        ///
        /// play list private method

        private void Clear_() {
                try {
                        if (currentPlay.get() != null)
                                synchronized (currentPlay) {
                                        currentPlay.set(null);
                                }
                        if (waitDb_(DB_ACTION_SAVE_PART)) {
                                try {
                                        toDb(dbMgr, -1, true);
                                } finally {
                                        waitDbEnd_();
                                }
                        }
                        groups.clear();
                } catch (Exception ignore) {}
        }
        private void saveToDb_() {
                saveSchedule_();
                if (waitDb_(DB_ACTION_SAVE_PART)) {
                        try {
                                toDb(dbMgr, -1, true);
                        } finally {
                                waitDbEnd_();
                        }
                }
                if (groups.size() <= (IDX_GROUP_LAST + 1))
                        return;

                if (BuildConfig.DEBUG) Log.d("- SAVE PlayList to DB", "- BEGIN = " + new Date().toString());

                if (waitDb_(DB_ACTION_SAVE_ALL)) {
                        try {
                                for (int i = IDX_ONLINE_FAV + 1; i < groups.size(); i++)
                                        groups.get(i).toDb(dbMgr);
                        } finally {
                                waitDbEnd_();
                        }
                }
                if (BuildConfig.DEBUG) Log.d("- SAVE PlayList to DB", "- END = " + new Date().toString());
        }
        private void closeDb_() {
                if ((dbMgr != null) && (waitDb_(DB_ACTION_CLOSE))) {
                                dbMgr.close();
                                dbMgr = null;
                        }
                if (pif != null)
                        pif.close();
        }

        /// private Load

        private void loadOnline_() {
                if (pif == null)
                        return;

                if (BuildConfig.DEBUG) Log.d("- UPDATE PlayList from Online", "- BEGIN");
                {
                        final String s1 = pif.downloadM3U8(null, IDX_ONLINE_TV);
                        if (!Text.isempty(s1)) {
                                ParseM3UList.parseM3u8(this, s1, IDX_ONLINE_TV);
                        } else {
                                for (String uri : PlayListConstant.TV_ONLINE_ARRAY) {
                                        final String s2 = pif.downloadM3U8(uri, IDX_ONLINE_TV);
                                        if (!Text.isempty(s2))
                                                ParseM3UList.parseM3u8(this, s2, IDX_ONLINE_TV);
                                }
                        }
                }
                //
                {
                        final String s1 = pif.downloadM3U8(null, IDX_ONLINE_RADIO);
                        if (!Text.isempty(s1)) {
                                ParseM3UList.parseM3u8(this, s1, IDX_ONLINE_RADIO);
                        } else {
                                final String s2 = pif.downloadM3U8(PlayListConstant.RADIO_ONLINE, IDX_ONLINE_RADIO);
                                if (!Text.isempty(s2))
                                        ParseM3UList.parseM3u8(this, s2, IDX_ONLINE_RADIO);
                        }
                }
                //
                {
                        final String s1 = pif.downloadM3U8(null, IDX_ONLINE_FILMS);
                        if (!Text.isempty(s1)) {
                                ParseM3UList.parseM3u8(this, s1, IDX_ONLINE_FILMS);
                        } else {
                                final String s2 = pif.downloadM3U8(PlayListConstant.FILMS_ONLINE, IDX_ONLINE_FILMS);
                                if (!Text.isempty(s2))
                                        ParseM3UList.parseM3u8(this, s2, IDX_ONLINE_FILMS);
                        }
                }
                //
                {
                        final String s1 = pif.downloadM3U8(null, IDX_ONLINE_USER_DEFINE);
                        if (!Text.isempty(s1)) {
                                ParseM3UList.parseM3u8(this, s1, IDX_ONLINE_USER_DEFINE);
                        }
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
                                if ((item = PlayListUtils.findItemByVlcId(getGroup(), hi.historyVlcId)) != null)
                                        grp.items.add(item);
                        }
                        if (BuildConfig.DEBUG) Log.d("- UPDATE PlayList from History", "- END");
                } catch (Exception e) {
                        if (BuildConfig.DEBUG) Log.e(TAG, Text.requireString(e.getLocalizedMessage()), e);
                }
        }

        /// DB Lock and Wait

        private boolean waitDb_(int type) {
                try {
                        if ((actionStateDb.get() == DB_ACTION_CLOSE) ||
                            ((type == DB_ACTION_SAVE_PART) && (actionStateDb.get() == DB_ACTION_SAVE_PART)))
                                return false;
                        while (actionStateDb.get() != DB_ACTION_EMPTY) {
                                Thread.yield();
                                Thread.sleep(1000);
                        }
                        setDbState_(type);
                        return true;
                } catch (Exception ignore) {}
                return false;
        }
        private void waitDbEnd_() {
                setDbState_(DB_ACTION_EMPTY);
        }

        /// INIT play list data

        private void initPlayList() {

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
                        pif.getStringFormat(PlayListParseInterface.ID_FMT_IPTV_ONLINE),
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
                        pif.getStringFormat(PlayListParseInterface.ID_FMT_FILMS_ONLINE),
                        IDX_ONLINE_FILMS + 100000L,
                        IDX_ONLINE_FILMS + 1,
                        true
                );
                groups.add(grp);
                grp = new PlayListGroup(
                        this,
                        pif.getStringFormat(PlayListParseInterface.ID_FMT_USER_ONLINE),
                        IDX_ONLINE_USER_DEFINE + 100000L,
                        IDX_ONLINE_USER_DEFINE + 1,
                        true
                );
                groups.add(grp);
        }

        ///

        private Observable.OnPropertyChangedCallback changePlayItemCb =
                new Observable.OnPropertyChangedCallback() {
                        @Override
                        public void onPropertyChanged(Observable sender, int propertyId) {
                                try {
                                        final PlayListItem item = currentPlayOld;
                                        if ((item != null) && (history.size() > 0)) {
                                                long vid = item.getVlcId();
                                                if (vid > 0L)
                                                        for (PlayListHistoryIndex p : history)
                                                                if (p.historyVlcId == vid) {
                                                                        p.historyPosition = item.stat.lastProgress.get();
                                                                        p.historyDate = new Date(System.currentTimeMillis());
                                                                        getExecutor().execute(
                                                                                new Runnable() {
                                                                                        @Override
                                                                                        public void run() {
                                                                                                if (waitDb_(DB_ACTION_USING)) {
                                                                                                        try {
                                                                                                                if ((item.dbIndex <= 0) || (item.dbParent <= 0)) {
                                                                                                                        ContentValues cv = new ContentValues();
                                                                                                                        cv.put("uri", item.uri);
                                                                                                                        item.toDb(dbMgr, item.dbParent, cv);
                                                                                                                } else {
                                                                                                                        item.toDb(dbMgr);
                                                                                                                }
                                                                                                        } catch (Exception ignore) {
                                                                                                        } finally {
                                                                                                                waitDbEnd_();
                                                                                                        }
                                                                                                }
                                                                                        }
                                                                                }
                                                                        );
                                                                        break;
                                                                }
                                        }
                                } catch (Exception e) { if (BuildConfig.DEBUG) Log.e(TAG, Text.requireString(e.getLocalizedMessage()), e); }
                                try {
                                        final PlayListItem item;
                                        currentPlayOld = item = currentPlay.get();
                                        if (item == null)
                                                return;

                                        switch (item.type) {
                                                case PlayListConstant.TYPE_MOVIE:
                                                case PlayListConstant.TYPE_SERIES:
                                                case PlayListConstant.TYPE_VIDEO: {
                                                        long hid = 0,
                                                             vid = item.getVlcId();

                                                        if (history.size() > 0)
                                                                hid = history.get(history.size() - 1).historyVlcId;
                                                        if (vid != hid) {
                                                                history.add(
                                                                        new PlayListHistoryIndex(
                                                                                vid,
                                                                                item.stat.lastProgress.get()
                                                                        )
                                                                );
                                                                if (groups.size() > 0) {
                                                                        final PlayListGroup grp = groups.get(IDX_HISTORY);
                                                                        item.reloadBindingData();
                                                                        grp.items.add(0, item);
                                                                        synchronized (grp) {
                                                                                grp.notify();
                                                                        }
                                                                }
                                                                if (pif != null)
                                                                    pif.loadStage1();

                                                                getExecutor().execute(
                                                                        new Runnable() {
                                                                                @Override
                                                                                public void run() {
                                                                                        if (waitDb_(DB_ACTION_USING)) {
                                                                                                try {
                                                                                                        if ((item.dbIndex <= 0) || (item.dbParent <= 0)) {
                                                                                                                ContentValues cv = new ContentValues();
                                                                                                                cv.put("uri", item.uri);
                                                                                                                item.toDb(dbMgr, item.dbParent, cv);
                                                                                                        } else {
                                                                                                                item.toDb(dbMgr);
                                                                                                        }
                                                                                                } catch (Exception ignore) {
                                                                                                } finally {
                                                                                                        waitDbEnd_();
                                                                                                }

                                                                                        }
                                                                                }
                                                                        }
                                                                );
                                                        }
                                                        break;
                                                }
                                                case PlayListConstant.TYPE_AUDIO:
                                                case PlayListConstant.TYPE_ONLINE: {
                                                        // item.visibleWatched() = ...
                                                        break;
                                                }
                                        }
                                } catch (Exception e) { if (BuildConfig.DEBUG) Log.e(TAG, Text.requireString(e.getLocalizedMessage()), e); }

                                getExecutor().execute(
                                        new Runnable() {
                                                @Override
                                                public void run() {
                                                        if (waitDb_(DB_ACTION_SAVE_PART)) {
                                                                try {
                                                                        toDb(dbMgr, -1, true);
                                                                        if (BuildConfig.DEBUG) Log.d("- SAVE ITEM/HISTORY :: SAVE MAIN PLAYLIST TABLE TO DB", " OK");
                                                                } catch (Exception ignore) {

                                                                } finally {
                                                                        waitDbEnd_();
                                                                }
                                                        }
                                                }
                                        }
                                );
                        }
                };

        private Runnable initPlayListRunnable(final JSONObject obj) {
                return  new Runnable() {
                        @Override
                        public void run() {
                                try {
                                        if ((dbMgr == null) || (pif == null))
                                                throw new RuntimeException(PL_EXCEPT1);

                                        boolean b = !dbMgr.isEmpty();
                                        if (b) {
                                                if (waitDb_(DB_ACTION_USING)) {
                                                        try {
                                                                fromDb(dbMgr, -1, 1, true);
                                                        } finally {
                                                                waitDbEnd_();
                                                        }
                                                }
                                        }

                                        ///

                                        initPlayList();
                                        try {
                                                if (obj != null) {
                                                        setDbState_(DB_ACTION_EMPTY_GET_VLC);
                                                        MethodCreateFromVlc.go(PlayList.this, obj);
                                                }
                                        } finally {
                                                if (obj != null)
                                                        setDbState_(DB_ACTION_EMPTY);
                                        }
                                        loadOnline_();
                                        if (b) {
                                                loadFavorites_();
                                                loadHistory_();
                                        }
                                        pif.loadStage1();
                                        isCompleteDb.set(true);

                                        if (b) {
                                                if (waitDb_(DB_ACTION_USING)) {
                                                        try {
                                                                MethodUpdateFromDB.go(groups);
                                                        } finally {
                                                                waitDbEnd_();
                                                        }
                                                }
                                                pif.loadStageEnd();
                                        } else {
                                                pif.loadStageOnce();
                                                MethodUpdateAll.go(groups);
                                                pif.loadStageEnd();
                                                saveToDb_();
                                                pif.updateStageOnce();
                                        }
                                        if (BuildConfig.DEBUG) Log.d(TAG1, PL_STAGE_END + new Date().toString());

                                } catch (Exception e) { if (BuildConfig.DEBUG) Log.e(TAG, Text.requireString(e.getLocalizedMessage()), e); }
                        }
                };
        }
}
