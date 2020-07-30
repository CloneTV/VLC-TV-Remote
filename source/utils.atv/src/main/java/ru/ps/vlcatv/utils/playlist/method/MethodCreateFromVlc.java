package ru.ps.vlcatv.utils.playlist.method;

import java.util.Date;

import ru.ps.vlcatv.utils.BuildConfig;
import ru.ps.vlcatv.utils.Log;
import ru.ps.vlcatv.utils.Text;
import ru.ps.vlcatv.utils.json.JSONArray;
import ru.ps.vlcatv.utils.json.JSONObject;
import ru.ps.vlcatv.utils.playlist.PlayList;
import ru.ps.vlcatv.utils.playlist.PlayListConstant;
import ru.ps.vlcatv.utils.playlist.PlayListGroup;
import ru.ps.vlcatv.utils.playlist.PlayListItem;
import ru.ps.vlcatv.utils.playlist.PlayListUtils;

public class MethodCreateFromVlc {
    private static final String TAG = " - CREATE PlayList from VLC";

    public static void go(PlayList playList, JSONObject obj) {
        JSONArray root = jArray_(obj);
        if (root == null)
            return;

        try {
            if (BuildConfig.DEBUG) Log.d(MethodCreateFromVlc.TAG, PlayList.PL_STAGE_BEGIN + new Date().toString());

            for (int i = 0; i < root.length(); i++) {

                JSONObject ele = root.optJSONObject(i);
                if (ele == null)
                    continue;

                String strName = jName_(ele);
                if (Text.isempty(strName))
                    continue;
                PlayListGroup grp = new PlayListGroup(
                        playList, strName, jId_(ele), (playList.groups.size() + 1), true
                );
                playList.groups.add(grp);
                createFromVlc_(playList, grp, jArray_(ele));
            }
            for (PlayListGroup gr : playList.groups)
                updateFromVlc_(gr);

            playList.createDate.set(new Date(System.currentTimeMillis()));

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e(MethodCreateFromVlc.TAG, Text.requireString(e.getLocalizedMessage()), e);
        } finally {
            if (BuildConfig.DEBUG) Log.d(MethodCreateFromVlc.TAG, PlayList.PL_STAGE_END + new Date().toString());
        }
    }
    private static void createFromVlc_(PlayList playList, PlayListGroup grp, JSONArray array) {
        if ((grp == null) || (array == null))
            return;

        int grpIdx = 0,
                grpId = PlayListUtils.getIdsInt(PlayListConstant.IDS_GRP_ID, grp.ids);

        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject ele = array.optJSONObject(i);
                String strName = jName_(ele);
                switch (jType(ele)) {
                    case PlayListConstant.TYPE_NODE: {
                        PlayListGroup gr = new PlayListGroup(
                                playList, strName, jId_(ele), grpId, false
                        );
                        gr.parent = grp;
                        grp.groups.add(gr);
                        createFromVlc_(playList, gr, jArray_(ele));
                        break;
                    }
                    case PlayListConstant.TYPE_LEAF: {
                        PlayListItem item = new PlayListItem(
                                playList, strName,
                                jUri_(ele), jId_(ele), jDuration_(ele),
                                grpId, grpIdx++
                        );
                        if (i == 0) {
                            grp.setSeason(item.season.get());
                            grp.setNfoFake(item.uri);
                            if (grp.parent != null)
                                grp.parent.setNfo(item.uri);
                        }
                        grp.items.add(item);
                        break;
                    }
                }
            } catch (Exception ignore) {}
        }
    }
    private static void updateFromVlc_(PlayListGroup grp) {
        if (grp == null)
            return;

        for (PlayListGroup gr : grp.groups)
            updateFromVlc_(gr);

        try {
            if ((grp.items.size() > 0) && (grp.season.get() > 0)) {
                grp.episodes.set(grp.items.size());

                if (grp.parent != null) {
                    long timeTotal = 0;
                    int episodesTotal = 0;
                    PlayListGroup parent = grp.parent;

                    for (PlayListGroup gr : parent.groups) {
                        long timeSeason = 0;
                        for (PlayListItem item : gr.items)
                            timeSeason += item.stat.totalProgress.get();

                        if (timeSeason > 0)
                            gr.totalTime.set(timeSeason);
                        gr.episodes.set(gr.items.size());
                        episodesTotal += gr.items.size();
                        timeTotal += timeSeason;
                    }
                    parent.totalTime.set(timeTotal);
                    parent.episodes.set(episodesTotal);
                    parent.season.set(parent.groups.size());
                }
            }
        } catch (Exception ignore) {}
    }
    private static int jType(JSONObject obj) {
        String s = obj.optString("type", null);
        switch (s) {
            case "node": return PlayListConstant.TYPE_NODE;
            case "leaf": return PlayListConstant.TYPE_LEAF;
            default: return PlayListConstant.TYPE_NONE;
        }

    }
    private static String jName_(JSONObject obj) {
        return obj.optString("name", null);
    }
    private static String jUri_(JSONObject obj) {
        return obj.optString("uri", null);
    }
    private static long jId_(JSONObject obj) {
        return obj.optLong("id", -1);
    }
    private static int jDuration_(JSONObject obj) {
        return obj.optInt("duration", -1);
    }
    private static JSONArray jArray_(JSONObject obj) {
        return obj.optJSONArray("children");
    }
}
