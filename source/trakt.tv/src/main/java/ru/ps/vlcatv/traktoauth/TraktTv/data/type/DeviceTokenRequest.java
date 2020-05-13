package ru.ps.vlcatv.traktoauth.TraktTv.data.type;

import androidx.annotation.Keep;
import ru.ps.vlcatv.utils.reflect.ReflectAttribute;
import ru.ps.vlcatv.utils.reflect.FieldReflect;
import ru.ps.vlcatv.traktoauth.TraktTv.data.TraktConstant;

@Keep
public class DeviceTokenRequest extends ReflectAttribute {

    public static final String url = "oauth/device/token";
    @FieldReflect("code")
    private String Code = null;
    @FieldReflect("client_id")
    private final String ClientId = TraktConstant.APP_ID;
    @FieldReflect("client_secret")
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
