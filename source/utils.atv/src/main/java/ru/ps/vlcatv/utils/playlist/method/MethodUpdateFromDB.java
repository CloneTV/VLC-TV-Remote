package ru.ps.vlcatv.utils.playlist.method;

import java.util.Date;
import java.util.List;

import ru.ps.vlcatv.utils.BuildConfig;
import ru.ps.vlcatv.utils.Log;
import ru.ps.vlcatv.utils.Text;
import ru.ps.vlcatv.utils.playlist.PlayList;
import ru.ps.vlcatv.utils.playlist.PlayListGroup;
import ru.ps.vlcatv.utils.playlist.PlayListItem;

public class MethodUpdateFromDB {
    private static final String TAG = " - UPDATE PlayList from DB";

    public static void go(List<PlayListGroup> groups) {

        if (BuildConfig.DEBUG) Log.d(TAG, PlayList.PL_STAGE_BEGIN + new Date().toString());

        for (int i = PlayList.IDX_ONLINE_FAV + 1; i < groups.size(); i++)
            updateFromDb_(groups.get(i));

        if (BuildConfig.DEBUG) Log.d(TAG, PlayList.PL_STAGE_END + new Date().toString());
    }

    private static void updateFromDb_(PlayListGroup grp) {
        if (grp == null)
            return;

        try {
            grp.updateFromDB();
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
                            grp.parent.updateFromDB(item.type);
                        }
                        if (Text.isempty(grp.nfo)) {
                            grp.setNfoFake(item.uri);
                            grp.updateFromDB();
                        }
                    } // END isStart (update group)
                    item.updateFromDB();
                } catch (Exception e) {
                    if (BuildConfig.DEBUG) Log.e(TAG, Text.requireString(e.getLocalizedMessage()), e);
                }
            }
        }
        if (grp.groups.size() > 0)
            for (PlayListGroup gp : grp.groups)
                updateFromDb_(gp);
    }
}
