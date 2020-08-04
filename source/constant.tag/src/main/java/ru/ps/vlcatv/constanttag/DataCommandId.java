package ru.ps.vlcatv.constanttag;

import android.view.KeyEvent;

//@Keep
public class DataCommandId {

    public static final int PLAY_TOGGLE = KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE;
    public static final int PLAY_PLAY = KeyEvent.KEYCODE_MEDIA_PLAY;
    public static final int PLAY_PAUSE = KeyEvent.KEYCODE_MEDIA_PAUSE;
    public static final int PLAY_STOP = KeyEvent.KEYCODE_MEDIA_STOP;
    public static final int PLAY_FORWARD = KeyEvent.KEYCODE_MEDIA_FAST_FORWARD;
    public static final int PLAY_REWIND = KeyEvent.KEYCODE_MEDIA_REWIND;
    public static final int PLAY_NEXT_TRACK = KeyEvent.KEYCODE_MEDIA_NEXT;
    public static final int PLAY_PREVIOUS_TRACK = KeyEvent.KEYCODE_MEDIA_PREVIOUS;
    public static final int PLAY_RANDOM = 12002;
    public static final int PLAY_REPEAT = 12003;
    public static final int PLAY_LOOP = 12004;
    public static final int PLAY_SEEK = 12005;
    public static final int PLAY_URL = 12006;
    public static final int PLAY_ID = 12007;
    public static final int PLAY_FAVORITE = 12008;
    public static final int FULLSCREEN = 12009;
    public static final int GET_TEST_CONNECT = 12010;
    public static final int GET_TEST_MEDIA_LIST =  12011;
    public static final int GET_STAT = 12012;
    public static final int GET_MEDIA_LIST = 12013;
    public static final int GET_ACTIVITY = KeyEvent.KEYCODE_MEDIA_RECORD; // 130
    public static final int GET_CONTROL_PANEL = 12014;
    public static final int GET_INFO_PANEL = 12015;
    public static final int UNDEFINED = 12016;
    public static final int SERVICE_START = 12017;
    public static final int SERVICE_STOP = 12018;
    public static final int SERVICE_BEGIN = 12019;
    public static final int SERVICE_END = 12020;
    public static final int MEDIA_LIST_OK = 12021;
    public static final int MEDIA_LIST_EMPTY = 12022;
    public static final int ACTIVITY_EXIT = 12023;
    public static final int ACTIVITY_RELOAD = 12024;
    public static final int ACTIVITY_RELOAD_TIMEOUT = 12025;
    public static final int ACTIVITY_SPIN_BEGIN = 12026;
    public static final int ACTIVITY_SPIN_END = 12027;
    public static final int ACTIVITY_SET_TITLE = 12028;
    public static final int GET_PLAYBACK_ACTIVITY = 12029;
    public static final int ACTIVITY_PLAYBACK_EXIT = 12030;
    public static final int AUDIO_VOLUME = 12031;
    public static final int AUDIO_VOLUME_UP = KeyEvent.KEYCODE_VOLUME_UP;
    public static final int AUDIO_VOLUME_DOWN = KeyEvent.KEYCODE_VOLUME_DOWN;
    public static final int AUDIO_VOLUME_MUTE = KeyEvent.KEYCODE_VOLUME_MUTE;
    public static final int PAD_UP = KeyEvent.KEYCODE_DPAD_UP;
    public static final int PAD_DOWN = KeyEvent.KEYCODE_DPAD_DOWN;
    public static final int PAD_LEFT = KeyEvent.KEYCODE_DPAD_LEFT;
    public static final int PAD_RIGHT = KeyEvent.KEYCODE_DPAD_RIGHT;
    public static final int PAD_CENTER = KeyEvent.KEYCODE_DPAD_CENTER;
    public static final int KEY_ENTER = KeyEvent.KEYCODE_ENTER;
    public static final int KEY_MENU = KeyEvent.KEYCODE_MENU;
    public static final int KEY_BACK = KeyEvent.KEYCODE_BACK;
    public static final int KEY_HOME = KeyEvent.KEYCODE_HOME;
    public static final int GET_MEDIA_ITEM = 12032;
    public static final int GET_MEDIA_HISTORY = 12033;
    public static final int GET_MEDIA_SEARCH = 12034;
    public static final int GET_MEDIA_FAVORITES = 12035;
    public static final int GET_MEDIA_POSTER = 12036;
    public static final int GET_MEDIA_EPG = 12037;
    public static final int HIDE_ALL_PANEL = 12038;
    public static final int NEW_STAT = 12039;
    public static final int NEW_MEDIA = 12040;
    public static final int NOTIFY = 12041;
    public static final int REMOTE_SERVER = 12042;
    public static final int UPDATE_DB_NOTIFY = 12043;
    public static final int UPDATE_NO_VLC = 12044;
    public static final int ONLINE_PLAY = 12045;
    public static final int SYSTEM_KEY = 12046;
    public static final int EXCEPTION = 12047;

        /*
        public static int fromString(String s, int  id) {
            switch (s) {
                case DataTagVlcStatus.TAG_PLAYING: {
                    return PLAY_PLAY;
                }
                case DataTagVlcStatus.TAG_STOPPED: {
                    if (id == -1)
                        return GET_CONTROL_PANEL;
                    else
                        return PLAY_PAUSE;
                }
                case DataTagVlcStatus.TAG_PAUSED: {
                    return PLAY_PAUSE;
                }
                default: {
                    return UNDEFINED;
                }
            }
        }
        */

    public static int fromKey(int status, int playid, int key) {
        if (key == PLAY_TOGGLE) {
            if (status == PLAY_PLAY) {
                return PLAY_PAUSE;
            } else if (status == PLAY_PAUSE) {
                return PLAY_PLAY;
            } else if (status == GET_ACTIVITY) {
                return GET_ACTIVITY;
            } else if (status == GET_CONTROL_PANEL) {
                return GET_CONTROL_PANEL;
            } else if ((status == PLAY_STOP) && (playid == -1)) {
                return GET_CONTROL_PANEL;
            } else if ((status == PLAY_STOP) && (playid > 0)) {
                return PLAY_PLAY;
            }
            return UNDEFINED;
        }
        return key;
    }
}
