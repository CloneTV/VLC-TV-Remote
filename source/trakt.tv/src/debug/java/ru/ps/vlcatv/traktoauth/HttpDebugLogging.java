package ru.ps.vlcatv.traktoauth;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import ru.ps.vlcatv.traktoauth.TraktTv.net.OauthAuthenticator;

public class HttpDebugLogging {

    public static OkHttpClient httpClientInit() {
        HttpLoggingInterceptor toHttpLog = new HttpLoggingInterceptor();
        toHttpLog.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient.Builder()
                .addInterceptor(toHttpLog)
                .build();
    }
    public static OkHttpClient httpClientInit(OauthAuthenticator oauth) {
        HttpLoggingInterceptor toHttpLog = new HttpLoggingInterceptor();
        toHttpLog.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient.Builder()
                .addInterceptor(toHttpLog)
                .authenticator(oauth)
                .build();
    }
}
