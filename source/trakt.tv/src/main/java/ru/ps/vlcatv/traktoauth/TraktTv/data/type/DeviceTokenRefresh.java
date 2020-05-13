package ru.ps.vlcatv.traktoauth.TraktTv.data.type;

import androidx.annotation.Keep;
import ru.ps.vlcatv.utils.reflect.ReflectAttribute;
import ru.ps.vlcatv.utils.reflect.FieldReflect;
import ru.ps.vlcatv.traktoauth.TraktTv.data.TraktConstant;

@Keep
public class DeviceTokenRefresh extends ReflectAttribute {

    static final public String url = "oauth/token";

    @FieldReflect("refresh_token")
    private String RefreshToken = null;
    @FieldReflect("client_id")
    private final String ClientId = TraktConstant.APP_ID;
    @FieldReflect("client_secret")
    private final String ClientSecret = TraktConstant.APP_SECRET;
    @FieldReflect("redirect_url")
    private final String RedirectUri = TraktConstant.APP_REDIRECT;
    @FieldReflect("grant_type")
    private final String GrantType = "refresh_token";

    public DeviceTokenRefresh(String refToken) {
        RefreshToken = refToken;
    }
}
