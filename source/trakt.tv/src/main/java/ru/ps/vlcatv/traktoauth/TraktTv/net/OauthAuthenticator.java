package ru.ps.vlcatv.traktoauth.TraktTv.net;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.Locale;
import okhttp3.Authenticator;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;
import ru.ps.vlcatv.traktoauth.TraktTv.data.OauthDataHolder;
import ru.ps.vlcatv.traktoauth.TraktTv.data.type.DeviceTokenRefresh;
import ru.ps.vlcatv.utils.Text;

public class OauthAuthenticator implements Authenticator {

    private OauthDataHolder holder = null;

    OauthAuthenticator(OauthDataHolder d) {
        holder = d;
    }

    @Nullable
    @Override
    public Request authenticate(@Nullable Route route, @NonNull Response res) throws IOException {
        if ((!isAccessToken(res)) || (Text.isempty(holder.tokenRes.getRefreshToken()))) {
            return null;
        }
        synchronized (this) {
            return requestAccessToken(res.request(), holder.tokenRes.getRefreshToken());
        }
    }
    private boolean isAccessToken(@NonNull Response res) {
        String hd = res.request().header(OauthDataHolder.HEADER_AUTHENTICATE);
        return ((hd != null) && (res.code() == 401));
    }
    @NonNull
    private Request requestAccessToken(@NonNull Request req, @NonNull String accessToken) {
        holder.tokenRefreshed.set(true);
        return req.newBuilder()
                .url(
                        String.format(
                                Locale.getDefault(),
                                "%s%s",
                                OauthDataHolder.url,
                                DeviceTokenRefresh.url
                        )
                )
                .post(RequestBody.create(
                        MediaType.parse(OauthDataHolder.MIME_JSON),
                        new DeviceTokenRefresh(accessToken).toJsonString()
                        )
                )
                .build();
    }
}
