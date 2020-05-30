package ru.ps.vlcatv.traktoauth.TraktTv.data.type;

import androidx.annotation.Keep;
import ru.ps.vlcatv.utils.reflect.ReflectAttribute;
import ru.ps.vlcatv.utils.reflect.annotation.IFieldReflect;
import ru.ps.vlcatv.traktoauth.TraktTv.data.TraktConstant;

@Keep
public class DeviceTokenRefresh extends ReflectAttribute {

    static final public String url = "oauth/token";

    @IFieldReflect("refresh_token")
    private String RefreshToken = null;
    @IFieldReflect("client_id")
    private final String ClientId = TraktConstant.APP_ID;
    @IFieldReflect("client_secret")
    private final String ClientSecret = TraktConstant.APP_SECRET;
    @IFieldReflect("redirect_url")
    private final String RedirectUri = TraktConstant.APP_REDIRECT;
    @IFieldReflect("grant_type")
    private final String GrantType = "refresh_token";

    public DeviceTokenRefresh(String refToken) {
        RefreshToken = refToken;
    }
}
