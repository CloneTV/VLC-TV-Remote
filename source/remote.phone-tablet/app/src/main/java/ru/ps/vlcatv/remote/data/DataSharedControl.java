package ru.ps.vlcatv.remote.data;

import android.view.KeyEvent;
import androidx.annotation.ColorRes;
import androidx.databinding.BaseObservable;
import androidx.databinding.Observable;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import org.json.JSONObject;
import java.util.ArrayList;

import ru.ps.vlcatv.remote.AppMain;
import ru.ps.vlcatv.remote.BuildConfig;
import ru.ps.vlcatv.remote.R;

public class DataSharedControl extends BaseObservable {
    private static final String TAG = DataSharedControl.class.getSimpleName();

    private static final String FIELD_LOOP = "loop";
    private static final String FIELD_REPEAT = "repeat";
    private static final String FIELD_RANDOM = "random";
    private static final String FIELD_FULLSCREEN = "fullscreen";
    private static final String FIELD_VOLUME = "volume";
    private static final String FIELD_API = "apiversion";
    private static final String FIELD_TITLE = "title";
    private static final String FIELD_FILE = "filename";
    private static final String FIELD_PLAYID = "currentplid";
    private static final String FIELD_TIME = "time";
    private static final String FIELD_LENGTH = "length";
    private static final String FIELD_STATE = "state";

    //

    public static final int BTN_LOOP = R.id.imgbtn_loop;
    public static final int BTN_RANDOM = R.id.imgbtn_random;
    public static final int BTN_REPEAT = R.id.imgbtn_repeat;
    public static final int BTN_FULLSCREEN = R.id.imgbtn_fullscreen;
    public static final int BTN_MUTE = R.id.imgbtn_vol_up;
    public static final int BTN_VOLUP = R.id.imgbtn_vol_mute;
    public static final int BTN_VOLDOWN = R.id.imgbtn_vol_down;
    public static final int BTN_PLAY_BY_ID = R.id.imgebtn_history_play;
    public static final int BTN_PLAY = R.id.imgbtn_play;
    public static final int BTN_PAUSE = R.id.imgbtn_pause;
    public static final int BTN_STOP = R.id.imgbtn_stop;
    public static final int BTN_PREV = R.id.imgbtn_rev;
    public static final int BTN_NEXT = R.id.imgbtn_next;
    public static final int BTN_FWD = R.id.imgbtn_fwd;
    public static final int BTN_REV = R.id.imgbtn_prev;
    //
    public static final int BTN_BACK = R.id.imgbtn_back;
    public static final int BTN_HISTORY = R.id.imgbtn_history;
    public static final int BTN_HISTORY_BACK = R.id.imgbtn_return;
    public static final int BTN_UP = R.id.imgbtn_dpad_up;
    public static final int BTN_DOWN = R.id.imgbtn_dpad_down;
    public static final int BTN_LEFT = R.id.imgbtn_dpad_left;
    public static final int BTN_RIGHT = R.id.imgbtn_dpad_right;
    public static final int BTN_CENTER = R.id.imgbtn_dpad_center;
    public static final int BTN_HOME = R.id.imgbtn_home;
    public static final int BTN_SETUP = R.id.imgbtn_setup;
    public static final int BTN_SETUPD = R.id.imgbtn_net_end;
    public static final int BTN_TITLE = R.id.tv_title_close;
    public static final int BTN_ERROR = R.id.imgbtn_error;
    public static final int BTN_ERRORD = R.id.tv_error;
    public static final int BTN_ERRORE = 10001;

    public static final int BTN_TOGGLE = 10002;
    public static final int GET_STAT = 10003;
    public static final int GET_MEDIA_ITEM = 10004;
    public static final int GET_MEDIA_ITEMS = 10005;

    ///

    public final ObservableField<String> Title = new ObservableField<>("");
    public final ObservableField<String> FileName = new ObservableField<>("");
    public final ObservableField<String> TimeType = new ObservableField<>("");
    public final ObservableInt PlayId = new ObservableInt(-1);
    public final ObservableInt AudioVolume = new ObservableInt(0);
    public final ObservableInt TimeTotal = new ObservableInt(0);
    public final ObservableInt TimeCurrent = new ObservableInt(0);
    public final ObservableInt TimeRemain = new ObservableInt(0);
    public final ObservableInt PlayState = new ObservableInt(-1);
    public final ObservableInt VlcApiVersion = new ObservableInt(-1);
    public final ObservableBoolean PlayIsRepeat = new ObservableBoolean(false);
    public final ObservableBoolean PlayIsLoop = new ObservableBoolean(false);
    public final ObservableBoolean PlayIsRandom = new ObservableBoolean(false);
    public final ObservableBoolean PlayIsFullscreen = new ObservableBoolean(false);
    public final ObservableBoolean AppRun = new ObservableBoolean(false);
    public final ObservableBoolean AppSetup = new ObservableBoolean(false);
    public final ObservableBoolean AppTitle = new ObservableBoolean(false);
    public final ObservableBoolean AppInfo = new ObservableBoolean(false);
    public final ObservableBoolean AppHistory = new ObservableBoolean(false);
    public final ObservableBoolean AppError = new ObservableBoolean(false);

    public final ObservableInt CalcTimeTotal = new ObservableInt(0);
    public final ObservableInt CalcTimeCurrent = new ObservableInt(0);
    public final ObservableInt CalcTimeRemain = new ObservableInt(0);
    public final ObservableField<String> CalcTimeType = new ObservableField<>("");

    public DataMediaItem MmItem;

    // virtual changer
    public final ObservableBoolean StateChange = new ObservableBoolean();

    private ArrayList<SettingsInterface> cb_PlayStateChanged = new ArrayList<>();
    private ArrayList<SettingsInterface> cb_PlayHistoryChanged = new ArrayList<>();
    private DataMediaItem[] MmItems = null;
    private int colorTransparent;
    private int colorActivate;
    private int oldPlayId = -1;

    public DataSharedControl() {
        colorTransparent = AppMain.getAppResources().getColor(R.color.colorTransparent, null);
        colorActivate = AppMain.getAppResources().getColor(R.color.colorAccent, null);
        MmItem = new DataMediaItem();

        OnPropertyChangedCallback cb = new OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                bindPlayStatePropertyChanged();
            }
        };
        PlayId.addOnPropertyChangedCallback(cb);
        PlayState.addOnPropertyChangedCallback(cb);
    }

    ///

    public void setItemsList(DataMediaItem[] items)
    {
        MmItems = items;
        bindPlayHistoryPropertyChanged();
    }
    public DataMediaItem[] getItemsList()
    {
        return MmItems;
    }

    ///

    private void bindPlayStatePropertyChanged() {
        try {
            for (SettingsInterface si : cb_PlayStateChanged)
                si.onPlayChange();
        } catch (Exception ignored) {}
    }
    public void setCallbackPlayStateChanged(SettingsInterface cb) {
        try {
            if (!cb_PlayStateChanged.contains(cb))
                cb_PlayStateChanged.add(cb);
        } catch (Exception ignored) {}
    }

    ///

    private void bindPlayHistoryPropertyChanged() {
        try {
            for (SettingsInterface si : cb_PlayHistoryChanged)
                si.onHistoryChange();
        } catch (Exception ignored) {}
    }
    public void setCallbackPlayHistoryChanged(SettingsInterface cb) {
        try {
            if (!cb_PlayHistoryChanged.contains(cb))
                cb_PlayHistoryChanged.add(cb);
        } catch (Exception ignored) {}
    }
    public void removeCallbackPlayHistoryChanged(SettingsInterface cb) {
        try {
            cb_PlayHistoryChanged.remove(cb);
        } catch (Exception ignored) {}
    }

    ///

    public void fromJson(JSONObject obj) {
        if (obj == null)
            return;
        try {
            Title.set(obj.optString(FIELD_TITLE, ""));
            FileName.set(obj.optString(FIELD_FILE, ""));

            PlayId.set(obj.optInt(FIELD_PLAYID, -1));
            AudioVolume.set(obj.optInt(FIELD_VOLUME, 0));

            // TimeType ??
            TimeTotal.set(obj.optInt(FIELD_LENGTH, 0));
            TimeCurrent.set(obj.optInt(FIELD_TIME, 0));
            TimeRemain.set(TimeTotal.get() - TimeCurrent.get());

            if (TimeTotal.get() > 60) {
                CalcTimeTotal.set(TimeTotal.get() / 60);
                CalcTimeCurrent.set(
                        ((TimeCurrent.get() >= 60) ? (TimeCurrent.get() / 60) : 0)
                );
                CalcTimeRemain.set(
                        ((TimeRemain.get() >= 60) ? (TimeRemain.get() / 60) : 0)
                );
                CalcTimeType.set("min");
            } else {
                CalcTimeTotal.set(TimeTotal.get());
                CalcTimeCurrent.set(TimeCurrent.get());
                CalcTimeRemain.set(TimeRemain.get());
                CalcTimeType.set("sec");
            }

            PlayState.set(obj.optInt(FIELD_STATE, -1));
            VlcApiVersion.set(obj.optInt(FIELD_API, -1));

            PlayIsRepeat.set(obj.optBoolean(FIELD_REPEAT, false));
            PlayIsLoop.set(obj.optBoolean(FIELD_LOOP, false));
            PlayIsRandom.set(obj.optBoolean(FIELD_RANDOM, false));
            PlayIsFullscreen.set(obj.optBoolean(FIELD_FULLSCREEN, false));

            switch (PlayState.get())
            {
                case KeyEvent.KEYCODE_MEDIA_PLAY:
                case KeyEvent.KEYCODE_MEDIA_PAUSE: {
                    if ((PlayId.get() > -1) && (oldPlayId != PlayId.get()))
                        AppMain.getMediaItem(PlayId.get());
                }
            }

        } catch (Exception e) {
            if (BuildConfig.DEBUG) AppMain.printError(e.getLocalizedMessage());
        }
        setStateChange();
        oldPlayId = PlayId.get();
    }

    public String getCtrlCmd(int id)
    {
        switch (id)
        {
            case BTN_TOGGLE:
                return "PLAY_TOGGLE";
            case BTN_PLAY_BY_ID:
                return "PLAY_BY_ID";
            case BTN_PLAY:
                return "PLAY_PLAY";
            case BTN_PAUSE:
                return "PLAY_PAUSE";
            case BTN_STOP:
                return "PLAY_STOP";
            case BTN_FWD:
                return "PLAY_FORWARD";
            case BTN_REV:
                return "PLAY_REWIND";
            case BTN_NEXT:
                return "PLAY_NEXT_TRACK";
            case BTN_PREV:
                return "PLAY_PREVIOUS_TRACK";
            case GET_MEDIA_ITEM:
                return "GET_MEDIA_ITEM";
            case GET_MEDIA_ITEMS:
                return "GET_MEDIA_ITEMS";
            case GET_STAT:
                return "GET_STATUS";
            case BTN_VOLUP:
                return "AUDIO_VOLUME_UP";
            case BTN_VOLDOWN:
                return "AUDIO_VOLUME_DOWN";
            case BTN_MUTE:
                return "AUDIO_VOLUME_MUTE";
            case BTN_UP:
                return "PAD_UP";
            case BTN_DOWN:
                return "PAD_DOWN";
            case BTN_LEFT:
                return "PAD_LEFT";
            case BTN_RIGHT:
                return "PAD_RIGHT";
            case BTN_CENTER:
                return "PAD_CENTER";
            case BTN_BACK:
                return "KEY_BACK";
            case BTN_HOME: {
                if (AppRun.get()) {
                    return "GET_ACTIVITY";
                } else {
                    return "KEY_BACK";
                }
            }
            default:
                return "";
        }
    }
    public @ColorRes int getCtrlButtonBg(int id, ObservableBoolean ignoring)
    {
        switch (id) {
            case BTN_REPEAT: {
                return PlayIsRepeat.get() ? colorActivate : colorTransparent;
            }
            case BTN_RANDOM: {
                return PlayIsRandom.get() ? colorActivate : colorTransparent;
            }
            case BTN_LOOP: {
                return PlayIsLoop.get() ? colorActivate : colorTransparent;
            }
            case BTN_FULLSCREEN: {
                return PlayIsFullscreen.get() ? colorActivate : colorTransparent;
            }
            case BTN_MUTE: {
                return (AudioVolume.get() == 0) ? colorActivate : colorTransparent;
            }
            case BTN_VOLUP: {
                return (AudioVolume.get() >= 90) ? colorActivate : colorTransparent;
            }
            case BTN_PLAY: {
                return (PlayState.get() == KeyEvent.KEYCODE_MEDIA_PLAY) ? colorActivate : colorTransparent;
            }
            case BTN_PAUSE: {
                return (PlayState.get() == KeyEvent.KEYCODE_MEDIA_PAUSE) ? colorActivate : colorTransparent;
            }
            case BTN_STOP: {
                return ((PlayState.get() == KeyEvent.KEYCODE_MEDIA_STOP) || (PlayId.get() == -1)) ? colorActivate : colorTransparent;
            }
            case BTN_HOME: {
                setStateChange();
                return AppRun.get() ? colorActivate : colorTransparent;
            }
            case BTN_SETUP: {
                setStateChange();
                return AppSetup.get() ? colorActivate : colorTransparent;
            }
            case BTN_HISTORY: {
                setStateChange();
                return AppHistory.get() ? colorActivate : colorTransparent;
            }
            default:
                return colorTransparent;
        }
    }

    void setStateChange() {
        StateChange.set(!StateChange.get());
    }
    public void clickStateChange() {
        StateChange.set(!StateChange.get());
        bindPlayStatePropertyChanged();
    }
    public boolean checkPlayState(int id) {
        return (PlayState.get() == id);
    }
    public boolean isMediaItemsEmpty()
    {
        for (DataMediaItem itm : MmItems)
            if (itm != null)
                return false;
        return true;
    }
}
