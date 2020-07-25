package ru.ps.vlcatv.utils.playlist.parse;

import androidx.databinding.ObservableLong;

import java.util.List;

import ru.ps.vlcatv.utils.playlist.PlayListItemTitles;

public interface PlayListParseInterface {

    public static final int ID_FMT_GROUP_TOTAL1 = 1001;
    public static final int ID_FMT_GROUP_TOTAL2 = 1002;
    public static final int ID_FMT_GROUP_TOTAL3 = 1003;
    public static final int ID_FMT_TIME_HOUR = 1004;
    public static final int ID_FMT_TIME_MIN = 1005;
    public static final int ID_FMT_TIME_SEC = 1006;
    public static final int ID_FMT_SEASON = 1007;
    public static final int ID_FMT_EPISODE = 1008;
    public static final int ID_FMT_HISTORY = 1009;
    public static final int ID_FMT_FAVORITE = 1010;
    public static final int ID_FMT_IPTV_ONLINE = 1011;
    public static final int ID_FMT_RADIO_ONLINE = 1012;
    public static final int ID_FMT_FILMS_ONLINE = 1013;
    public static final int ID_FMT_USER_ONLINE = 1014;

    public void downloadFile(String uri, String file, ObservableLong event);
    public Object downloadNfo(String uri);
    public String downloadM3u8(String uri, int idx);
    public Object downloadOmdb(String ids);
    public Object searchOmdb(List<PlayListItemTitles> titles);
    public String checkTrailer(String uri);
    public boolean isEmpty();
    public void close();
    public void loadStageOnce();
    public void loadStage1();
    public void loadStage2();
    public void loadStageEnd();
    public void saveStage(int idx);
    public void updateStageOnce();
    public void newMedia();
    public String getStringFormat(int idx);
    public String getStringFormat(int idx, int val);
    public int getHistoryMax();

    public Object getPicasso();
    public Object getHttpClient();
    public Object getVlcCmd();

    public void writeFile(String f, String s);
}
