package ru.ps.vlcatv.traktoauth.TraktTv.data.type;

import androidx.annotation.Keep;

import ru.ps.vlcatv.utils.reflect.ReflectAttribute;
import ru.ps.vlcatv.utils.reflect.annotation.IFieldReflect;
import ru.ps.vlcatv.traktoauth.TraktTv.data.OauthDataHolder;

@Keep
public class DeviceCodeResponse extends ReflectAttribute implements ResponseInterface {

    @IFieldReflect("device_code")
    private String DevCode = null;
    @IFieldReflect("user_code")
    private String UserCode = null;
    @IFieldReflect("verification_url")
    private String VerificationUrl = null;

    @IFieldReflect("expires_in")
    private long ExpiresIn = 0L;
    @IFieldReflect("interval")
    private long Interval = 0L;

    // Valid only pending User code request + <ExpiresIn> seconds
    private long DataCreated = 0L;

    @Override
    public void clear() {
        DevCode = null;
        UserCode = null;
        VerificationUrl = null;
        ExpiresIn = 0L;
        Interval = 0L;
        DataCreated = 0L;
    }
    @Override
    public void setExpired() {
        DataCreated = (OauthDataHolder.getUnixTime() + ExpiresIn);
    }
    @Override
    public int getExpired() {
        if (DataCreated <= 0L)
            return 0;
        long tml = OauthDataHolder.getUnixTime();
        return (int)((DataCreated > tml) ? ((DataCreated - tml) / 60) : 0);
    }
    @Override
    public boolean isExpired() {
        return (OauthDataHolder.getUnixTime() >= DataCreated);
    }
    @Override
    public boolean isempty() {
        return ((DevCode == null) ||
                (UserCode == null) ||
                (VerificationUrl == null) ||
                ((DataCreated > 0L) && isExpired()));
    }
    public String getDeviceCode() {
        return DevCode;
    }
    public String getUserCode() {
        return UserCode;
    }
    public String getVerificationUrl() {
        return VerificationUrl;
    }
    public long getInterval() {
        return Interval;
    }
}
