package ru.ps.vlcatv.traktoauth.TraktTv.data.type;

import androidx.annotation.Keep;

import ru.ps.vlcatv.utils.reflect.ReflectAttribute;
import ru.ps.vlcatv.utils.reflect.annotation.IFieldReflect;
import ru.ps.vlcatv.traktoauth.TraktTv.data.OauthDataHolder;

@Keep
public class DeviceTokenResponse extends ReflectAttribute implements ResponseInterface {

    @IFieldReflect("access_token")
    private String AccessToken = null;
    @IFieldReflect("token_type")
    private String TokenType = null;
    @IFieldReflect("refresh_token")
    private String RefreshToken = null;
    @IFieldReflect("scope")
    private String Scope = null;

    @IFieldReflect("expires_in")
    private long ExpiresToken = 0L;
    @IFieldReflect("created_at")
    private long CreatedToken = 0L;

    @IFieldReflect("app_data_expired")
    private long DataExpired = 0L;

    public DeviceTokenResponse() {
        super.setPrefixName(DeviceCodeResponse.class.getSimpleName());
    }

    @Override
    public int getExpired() {
        if (DataExpired <= 0L)
            return 0;
        long tml = OauthDataHolder.getUnixTime();
        return (int)((DataExpired > tml) ? ((DataExpired - tml) / 60) : 0);
    }
    @Override
    public void setExpired() {
        DataExpired = (OauthDataHolder.getUnixTime() + ExpiresToken);
    }
    @Override
    public boolean isExpired() {
        return ((DataExpired > 0L) && (OauthDataHolder.getUnixTime() >= DataExpired));
    }
    @Override
    public void clear() {
        AccessToken = null;
        TokenType = null;
        RefreshToken = null;
        Scope = null;
        ExpiresToken = 0L;
        CreatedToken = 0L;
        DataExpired = 0L;
    }
    @Override
    public boolean isempty() {
        return ((AccessToken == null) ||
                (RefreshToken == null) ||
                (TokenType == null));
    }

    public long getDataExpired() {
        return DataExpired;
    }
    public String getTokenType() {
        return TokenType;
    }
    public String getAccessToken() {
        return AccessToken;
    }
    public String getRefreshToken() {
        return RefreshToken;
    }

    public String statusCode(int code) {
        switch (code)
        {
            case 200: return ":200: Success - save the access token";
            case 400: return ":400: Pending - waiting for the user to authorize your";
            case 404: return ":404: Not Found - invalid device code";
            case 409: return ":409: Already Used - user already approved this code";
            case 410: return ":410: Expired - the tokens have expired, restart the process";
            case 418: return ":418: Denied - user explicitly denied this code";
            case 429: return ":429: Slow Down - your app is polling too quickly";
            default: return Integer.toString(code);
        }
    }
    public boolean isFatalCode(int code) {
        switch (code)
        {
            case 200:
            case 400:
            case 429: return false;
            default: return true;
        }
    }
}
