package ru.ps.vlcatv.utils.playlist;

import android.graphics.drawable.Drawable;
import androidx.databinding.ObservableBoolean;
import ru.ps.vlcatv.utils.json.JSONObject;

public interface PlayStatusInterface {
    public static final int BTN_REPEAT = 11001;
    public static final int BTN_RANDOM = 11002;
    public static final int BTN_LOOP = 11003;
    public static final int BTN_FULLSCREEN = 11004;
    public static final int BTN_MUTE = 11005;
    public static final int BTN_UNMUTE = 11006;
    public static final int BTN_PLAY = 11007;
    public static final int BTN_PAUSE = 11008;
    public static final int BTN_STOP = 11009;
    public static final int BTN_PREV = 11010;
    public static final int BTN_NEXT = 11011;
    public static final int BTN_FWD = 11012;
    public static final int BTN_BACK = 11013;
    public static final int BTN_MENU = 11014;
    public static final int BTN_EXIT = 11015;

    public ObservableBoolean eventNewPlay();
    public void Clear();
    public void callCmd(int idx);
    public void setNewStatus(JSONObject obj);
    public void setPlayList(PlayList p);
    public int getControlBackground(int id, ObservableBoolean __not_use_virtual_changer);
    public int getControlForeground(int id, ObservableBoolean __not_use_virtual_changer);
    public Drawable getControlDrawable(int id, ObservableBoolean __not_use_virtual_changer);
    public Drawable getControlDrawableEmpty(int id, ObservableBoolean __not_use_virtual_changer);
    public String getTitle(ObservableBoolean __not_use_virtual_changer);
    public String getDuration(ObservableBoolean __not_use_virtual_changer);
    public String getPlayId(ObservableBoolean __not_use_virtual_changer);
    public int getPlayTotal(ObservableBoolean __not_use_virtual_changer);
    public int getPlayPosition(ObservableBoolean __not_use_virtual_changer);
    public int getPlayTotal();
    public int getPlayPosition();
    public int getPlayId();
    public int getAudioVolume();
    public int getVlcApiVersion();
    public int getPlayState();
    public boolean getPlayIsRepeat();
    public boolean getPlayIsLoop();
    public boolean getPlayIsRandom();
    public boolean getPlayIsFullscreen();
    public boolean isPlay();
    public int getLastCommand();
    public void setLastCommand(int event);
}
