package ru.ps.vlcatv.traktoauth.TraktTv.data.type;

import androidx.annotation.Keep;
import ru.ps.vlcatv.utils.reflect.ReflectAttribute;
import ru.ps.vlcatv.utils.reflect.annotation.IFieldReflect;
import ru.ps.vlcatv.traktoauth.TraktTv.data.TraktConstant;

@Keep
public class DeviceTokenRequest extends ReflectAttribute {

    public static final String url = "oauth/device/token";
    @IFieldReflect("code")
    private String Code = null;
    @IFieldReflect("client_id")
    private final String ClientId = TraktConstant.APP_ID;
    @IFieldReflect("client_secret")
    private final String ClientSecret = TraktConstant.APP_SECRET;

    public boolean isempty() {
        return (Code == null);
    }
    public void clear() {
        Code = null;
    }
    public void DeviceCodeUpdate(String s) {
        Code = s;
    }
}
