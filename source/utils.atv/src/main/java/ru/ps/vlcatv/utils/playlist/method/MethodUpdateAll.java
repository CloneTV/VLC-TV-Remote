package ru.ps.vlcatv.utils.playlist.method;

import java.util.Date;
import java.util.List;

import ru.ps.vlcatv.utils.BuildConfig;
import ru.ps.vlcatv.utils.Log;
import ru.ps.vlcatv.utils.playlist.PlayList;
import ru.ps.vlcatv.utils.playlist.PlayListGroup;
import ru.ps.vlcatv.utils.playlist.PlayListItem;

public class MethodUpdateAll {
    private static final String TAG = " - All UPDATE PlayList from external API";

    public static void go(List<PlayListGroup> groups) {
        if (BuildConfig.DEBUG) Log.d(TAG, PlayList.PL_STAGE_BEGIN + new Date().toString());

        final PlayListGroup grp = groups.get(PlayList.IDX_ONLINE_FILMS);
        if ((grp != null) && (grp.items.size() > 0))
            updateAll_(grp);

        if (groups.size() <= PlayList.IDX_GROUP_LAST + 1) {
            if (BuildConfig.DEBUG) Log.d(TAG, PlayList.PL_STAGE_BREAK + new Date().toString());
            return;
        }

        for (int i = PlayList.IDX_GROUP_LAST + 1; i < groups.size(); i++)
            updateAll_(groups.get(i));

        if (BuildConfig.DEBUG) Log.d(TAG, PlayList.PL_STAGE_END + new Date().toString());
    }
    private static void updateAll_(PlayListGroup grp) {
        if (grp == null)
            return;

        try {
            grp.updateFromNFO();
        } catch (Exception ignore) {}
        try {
            grp.updateFromIDB();
        } catch (Exception ignore) {}
        try {
            grp.updateTRAILER();
        } catch (Exception ignore) {}

        if (grp.items.size() > 0)
            for (PlayListItem item : grp.items) {
                try {
                    if (item.isDataEmpty())
                        item.updateFromNFO();
                } catch (Exception ignore) {}
                try {
                    item.updateFromIDB();
                } catch (Exception ignore) {}
                try {
                    item.updateTRAILER();
                } catch (Exception ignore) {}
            }

        if (grp.groups.size() > 0)
            for (PlayListGroup gp : grp.groups)
                updateAll_(gp);
    }
}
