package ru.ps.vlcatv.remote;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.Log;

import androidx.annotation.StringRes;

import org.jetbrains.annotations.NotNull;
import java.util.Objects;

import ru.ps.vlcatv.remote.BuildConfig;
import ru.ps.vlcatv.remote.data.DataSettings;
import ru.ps.vlcatv.remote.data.DataSharedControl;
import ru.ps.vlcatv.remote.gui.activity.AppMainActivity;
import ru.ps.vlcatv.remote.net.RemoteEngine;

public class AppMain extends Application {

    private static final String TAG = AppMain.class.getSimpleName();
    protected static AppMain instance = null;
    DataSettings settings = null;
    DataSharedControl data = null;
    RemoteEngine req = null;
    AppMainActivity activity = null;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        try
        {
            settings = new DataSettings();
            data = new DataSharedControl();
            req = new RemoteEngine();

            try {
                settings.getPreferences();
                req.status();
            } catch (Exception ignored) {}


        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e(TAG, Objects.requireNonNull(e.getMessage()));
        }
    }
    @Override
    public void onTerminate() {
        settings.setPreferences();
        super.onTerminate();
        instance = null;
    }
    @Override
    public void onConfigurationChanged(@NotNull Configuration newConfig) {
        settings.setPreferences();
        super.onConfigurationChanged(newConfig);
    }
    @Override
    public void onTrimMemory(int level) {
        settings.setPreferences();
        super.onTrimMemory(level);
    }

    public static Context getAppContext() {
        return instance.getBaseContext();
    }
    public static Resources getAppResources() {
        return instance.getResources();
    }
    public static DataSharedControl getStatus() {
        return instance.data;
    }
    public static DataSettings getSettings() {
        return instance.settings;
    }
    public static void getRequestStatus() {
        if (instance.req != null)
            instance.req.status();
    }
    public static void getMediaItem(int id) {
        if (instance.req != null)
            instance.req.mediaItem(id);
    }
    public static void getMediaItems() {
        if (instance.req != null)
            instance.req.mediaItems();
    }
    public static void getRequest(String s) {
        if (instance.req != null)
            instance.req.cmd(s);
    }
    public static void getRequest(String s, String opt) {
        if (instance.req != null)
            instance.req.cmd(s, opt);
    }
    public static AppMainActivity getActivity() {
        return instance.activity;
    }
    public static void setActivity(AppMainActivity act) {
        instance.activity = act;
    }
    public static void printError(String s) {
        if (instance.activity != null)
            instance.activity.OnClickBtn(DataSharedControl.BTN_ERRORE, s);
        else if (BuildConfig.DEBUG) Log.e(TAG, s);
    }
    public static void printError(@StringRes int id) {
        try {
            String s = getAppResources().getString(id);
            if (instance.activity != null)
                instance.activity.OnClickBtn(DataSharedControl.BTN_ERRORE, s);
            else if (BuildConfig.DEBUG) Log.e(TAG, s);
        } catch (Exception ignored) {}
    }
}
