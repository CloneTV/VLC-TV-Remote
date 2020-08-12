package ru.ps.vlcatv.utils.playlist.method;

import java.util.Date;
import java.util.List;

import ru.ps.vlcatv.utils.BuildConfig;
import ru.ps.vlcatv.utils.Log;
import ru.ps.vlcatv.utils.Text;
import ru.ps.vlcatv.utils.playlist.PlayList;
import ru.ps.vlcatv.utils.playlist.PlayListGroup;
import ru.ps.vlcatv.utils.playlist.PlayListItem;

public class MethodCheckAll {
    private static final String TAG = " - All CHECK IMDB ID and UPDATE";

    public static void go(List<PlayListGroup> groups) {

        if (BuildConfig.DEBUG) Log.d(TAG, PlayList.PL_STAGE_BEGIN + new Date().toString());

        final PlayListGroup grp = groups.get(PlayList.IDX_ONLINE_FILMS);
        if ((grp != null) && (grp.items.size() > 0))
            checkAll_(grp);

        if (groups.size() <= PlayList.IDX_GROUP_LAST + 1) {
            if (BuildConfig.DEBUG) Log.e(TAG, PlayList.PL_STAGE_BREAK + new Date().toString());
            return;
        }

        for (int i = PlayList.IDX_GROUP_LAST + 1; i < groups.size(); i++)
            checkAll_(groups.get(i));

        if (BuildConfig.DEBUG) Log.d(TAG, PlayList.PL_STAGE_END + new Date().toString());
    }
    private static void checkAll_(PlayListGroup grp) {
        if (grp == null)
            return;

        try {
            if (grp.getImdbId() == null) {
                grp.updateFromIDB();
                grp.updateTRAILER();
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e(TAG, Text.requireString(e.getLocalizedMessage()), e);
        }

        if (grp.items.size() > 0)
            for (PlayListItem item : grp.items) {
                try {
                    if (item.getImdbId() == null) {
                        item.updateFromIDB();
                        item.updateTRAILER();
                    }
                } catch (Exception e) {
                    if (BuildConfig.DEBUG) Log.e(TAG, Text.requireString(e.getLocalizedMessage()), e);
                }
            }

        if (grp.groups.size() > 0)
            for (PlayListGroup gp : grp.groups)
                checkAll_(gp);
    }
}
