package ru.ps.vlcatv.traktoauth;

import okhttp3.OkHttpClient;
import ru.ps.vlcatv.traktoauth.TraktTv.net.OauthAuthenticator;

public class HttpDebugLogging {

    public static OkHttpClient httpClientInit() {
        return new OkHttpClient.Builder()
                .build();
    }
    public static OkHttpClient httpClientInit(OauthAuthenticator oauth) {
        return new OkHttpClient.Builder()
                .authenticator(oauth)
                .build();
    }
}
