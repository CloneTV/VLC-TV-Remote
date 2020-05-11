package ru.ps.vlcatv.remote.data;

import android.view.KeyEvent;
import androidx.annotation.ColorRes;
import androidx.databinding.BaseObservable;
import androidx.databinding.Observable;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import org.json.JSONObject;

import ru.ps.vlcatv.constanttag.DataTagVlcStatus;
import ru.ps.vlcatv.constanttag.DataUriApi;
import ru.ps.vlcatv.remote.AppMain;
import ru.ps.vlcatv.remote.BuildConfig;
import ru.ps.vlcatv.remote.R;
import ru.ps.vlcatv.remote.data.event.EventPlayHistoryChange;
import ru.ps.vlcatv.remote.data.event.EventPlayItemChange;
import ru.ps.vlcatv.remote.data.event.EventPlayStateChange;

public class DataSharedControl extends BaseObservable {
    private static final String TAG = DataSharedControl.class.getSimpleName();

    //

    public static final int BTN_LOOP = R.id.imgbtn_loop;
    public static final int BTN_RANDOM = R.id.imgbtn_random;
    public static final int BTN_REPEAT = R.id.imgbtn_repeat;
    public static final int BTN_FULLSCREEN = R.id.imgbtn_fullscreen;
    public static final int BTN_MUTE = R.id.imgbtn_vol_mute;
    public static final int BTN_VOLUP = R.id.imgbtn_vol_up;
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
    public static final int BTN_SEARCH = R.id.imgbtn_search;
    public static final int BTN_MLIST = R.id.imgbtn_mlist;
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
    public final ObservableInt TimeTypeId = new ObservableInt(0);
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
    public final ObservableBoolean AppSearch = new ObservableBoolean(false);
    public final ObservableBoolean AppError = new ObservableBoolean(false);

    public DataMediaItem MmItem  = new DataMediaItem();
    public EventPlayHistoryChange eventHistory = new EventPlayHistoryChange();
    public EventPlayStateChange eventState = new EventPlayStateChange();
    public EventPlayItemChange eventItem = new EventPlayItemChange();

    // virtual changer
    public final ObservableBoolean StateChange = new ObservableBoolean();

    private int colorTransparent;
    private int colorActivate;
    private int oldPlayId = -1;

    public DataSharedControl() {
        colorTransparent = AppMain.getAppResources().getColor(R.color.colorTransparent, null);
        colorActivate = AppMain.getAppResources().getColor(R.color.colorAccent, null);

        PlayId.addOnPropertyChangedCallback(
                new OnPropertyChangedCallback() {
                    @Override
                    public void onPropertyChanged(Observable sender, int propertyId) {
                        eventItem.onChangeProperty(PlayId.get());
                    }
                });
        PlayState.addOnPropertyChangedCallback(
                new OnPropertyChangedCallback() {
                    @Override
                    public void onPropertyChanged(Observable sender, int propertyId) {
                        eventState.onChangeProperty(PlayState.get());
                    }
                });
        Title.addOnPropertyChangedCallback(
                new OnPropertyChangedCallback() {
                    @Override
                    public void onPropertyChanged(Observable sender, int propertyId) {
                        eventItem.onChangeProperty();
                    }
                });
    }

    public void fromJson(JSONObject obj) {
        if (obj == null)
            return;
        try {
            Title.set(obj.optString(DataTagVlcStatus.TAG_TITLE, ""));
            FileName.set(obj.optString(DataTagVlcStatus.TAG_FILE, ""));

            PlayId.set(obj.optInt(DataTagVlcStatus.TAG_PLAYID, -1));
            AudioVolume.set(obj.optInt(DataTagVlcStatus.TAG_VOLUME, 0));

            TimeTypeId.set(obj.optInt(DataTagVlcStatus.TAG_TIME_FMT, 1));
            TimeTotal.set(obj.optInt(DataTagVlcStatus.TAG_LENGTH, 0));
            TimeCurrent.set(obj.optInt(DataTagVlcStatus.TAG_TIME, 0));
            TimeRemain.set(TimeTotal.get() - TimeCurrent.get());

            PlayState.set(obj.optInt(DataTagVlcStatus.TAG_STATE, -1));
            VlcApiVersion.set(obj.optInt(DataTagVlcStatus.TAG_API, -1));

            PlayIsRepeat.set(obj.optBoolean(DataTagVlcStatus.TAG_REPEAT, false));
            PlayIsLoop.set(obj.optBoolean(DataTagVlcStatus.TAG_LOOP, false));
            PlayIsRandom.set(obj.optBoolean(DataTagVlcStatus.TAG_RANDOM, false));
            PlayIsFullscreen.set(obj.optBoolean(DataTagVlcStatus.TAG_FULLSCREEN, false));

            if (TimeTypeId.get() > 0)
                TimeType.set(
                        AppMain.getAppResources().getQuantityString(R.plurals.plurals_minutes, TimeRemain.get())
                );
            else
                TimeType.set(
                        AppMain.getAppResources().getQuantityString(R.plurals.plurals_second, TimeRemain.get())
                );

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
                return DataUriApi.PLAY_TOGGLE;
            case BTN_PLAY_BY_ID:
                return DataUriApi.PLAY_ID;
            case BTN_PLAY:
                return DataUriApi.PLAY_PLAY;
            case BTN_PAUSE:
                return DataUriApi.PLAY_PAUSE;
            case BTN_STOP:
                return DataUriApi.PLAY_STOP;
            case BTN_FWD:
                return DataUriApi.PLAY_FORWARD;
            case BTN_REV:
                return DataUriApi.PLAY_REWIND;
            case BTN_NEXT:
                return DataUriApi.PLAY_NEXT_TRACK;
            case BTN_PREV:
                return DataUriApi.PLAY_PREVIOUS_TRACK;
            case GET_MEDIA_ITEM:
                return DataUriApi.GET_MEDIA_ITEM;
            case GET_MEDIA_ITEMS:
                return DataUriApi.GET_MEDIA_ITEMS;
            case GET_STAT:
                return DataUriApi.GET_STAT;
            case BTN_VOLUP:
                return DataUriApi.AUDIO_VOLUME_UP;
            case BTN_VOLDOWN:
                return DataUriApi.AUDIO_VOLUME_DOWN;
            case BTN_MUTE:
                return DataUriApi.AUDIO_VOLUME_MUTE;
            case BTN_UP:
                return DataUriApi.PAD_UP;
            case BTN_DOWN:
                return DataUriApi.PAD_DOWN;
            case BTN_LEFT:
                return DataUriApi.PAD_LEFT;
            case BTN_RIGHT:
                return DataUriApi.PAD_RIGHT;
            case BTN_CENTER:
                return DataUriApi.PAD_CENTER;
            case BTN_BACK:
                return DataUriApi.KEY_BACK;
            case BTN_SEARCH:
                return DataUriApi.GET_SEARCH_ACTIVITY;
            case BTN_HOME: {
                if (AppRun.get()) {
                    return DataUriApi.GET_MAIN_ACTIVITY;
                } else {
                    return DataUriApi.KEY_BACK;
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
            case BTN_SEARCH: {
                setStateChange();
                return AppSearch.get() ? colorActivate : colorTransparent;
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

    private void setStateChange() {
        StateChange.set(!StateChange.get());
    }
    public void clickStateChange() {
        StateChange.set(!StateChange.get());
        eventState.onChangeProperty(PlayState.get());
    }
    public boolean checkPlayState(int id) {
        return (PlayState.get() == id);
    }
}
