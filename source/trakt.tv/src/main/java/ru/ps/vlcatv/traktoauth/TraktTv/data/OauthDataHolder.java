package ru.ps.vlcatv.traktoauth.TraktTv.data;

import android.content.SharedPreferences;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;

import ru.ps.vlcatv.traktoauth.BuildConfig;
import ru.ps.vlcatv.traktoauth.TraktTv.data.type.DeviceCodeRequest;
import ru.ps.vlcatv.traktoauth.TraktTv.data.type.DeviceCodeResponse;
import ru.ps.vlcatv.traktoauth.TraktTv.data.type.DeviceTokenRequest;
import ru.ps.vlcatv.traktoauth.TraktTv.data.type.DeviceTokenResponse;
import ru.ps.vlcatv.traktoauth.TraktTv.net.OauthCallMessage;
import ru.ps.vlcatv.utils.Log;
import ru.ps.vlcatv.utils.Text;

public class OauthDataHolder {
    static final public String url = "https://api.trakt.tv/";
    static final public String MIME_JSON = "application/json";
    static final public String HEADER_AUTHORIZATION = "Authorization";
    static final public String HEADER_AUTHENTICATE = "WWW-Authenticate";
    static final public String HEADER_TRAKT_API_KEY = "trakt-api-key";
    static final public String HEADER_TRAKT_API_VER = "trakt-api-version: 2";
    static final public String HEADER_ACCEPT_JSON = "Accept: application/json";
    static final public String HEADER_CONTENT_JSON = "Content-Type: application/json";


    private SharedPreferences prefs = null;
    public DeviceCodeRequest codeReq = new DeviceCodeRequest();
    public DeviceTokenRequest tokenReq = new DeviceTokenRequest();
    public DeviceCodeResponse codeRes = new DeviceCodeResponse();
    public DeviceTokenResponse tokenRes = new DeviceTokenResponse();
    public AtomicBoolean tokenRefreshed = new AtomicBoolean(false);

    static public long getUnixTime() {
        return (System.currentTimeMillis() / 1000L);
    }
    static public String getUtc8601() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'.000Z'", Locale.ENGLISH);
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        return df.format(new Date());
    }

    public void setPreferences(SharedPreferences sp) {
        prefs = sp;
        loadPreference();
    }
    private void loadPreference() {
        codeRes.fromPreferences(prefs);
        tokenRes.fromPreferences(prefs);
        tokenReq.DeviceCodeUpdate(codeRes.getDeviceCode());
    }
    public boolean setDeviceCodeResponse(String s) {
        try {
            codeRes.fromJson(s);
            if (!codeRes.isempty()) {
                codeRes.setExpired();
                codeRes.toPreferences(prefs);
                tokenReq.DeviceCodeUpdate(codeRes.getDeviceCode());
                return true;
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("set Device Code Response", Text.requireString(e.getLocalizedMessage()), e);
        }
        return false;
    }
    public boolean setDeviceTokenResponse(String s) {
        try {
            tokenRes.fromJson(s);
            if (!tokenRes.isempty()) {
                tokenRes.setExpired();
                tokenRes.toPreferences(prefs);
                return true;
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("set Device Token Response", Text.requireString(e.getLocalizedMessage()), e);
        }
        return false;
    }
    public void clearDeviceResponse() {
        try {
            codeRes.clear();
            tokenReq.clear();
            tokenRes.clear();
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("clear Device Code Response", Text.requireString(e.getLocalizedMessage()), e);
        }
    }
    public String PendingTimeLeft(int count) {
        int tml = -1;
        if (codeRes.isempty())
            return OauthCallMessage.getPendingFormat(tml);
        if ((tml = codeRes.getExpired()) == 0)
            return OauthCallMessage.getPendingFormat(tml);
        return String.format(
                Locale.getDefault(),
                OauthCallMessage.getPendingFormat(tml),
                count, tml
        );
    }
    public boolean isActive() {
        if ((codeRes.isempty()) || (tokenRes.isempty()))
            return false;
        return ((tokenRes.getDataExpired() > 0) && (!tokenRes.isExpired()));
    }
}
