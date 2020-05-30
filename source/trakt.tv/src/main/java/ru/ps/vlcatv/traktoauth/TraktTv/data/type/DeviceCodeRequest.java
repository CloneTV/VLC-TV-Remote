package ru.ps.vlcatv.traktoauth.TraktTv.data.type;

import android.os.Parcel;
import androidx.annotation.Keep;

import ru.ps.vlcatv.utils.reflect.ReflectAttribute;
import ru.ps.vlcatv.utils.reflect.annotation.IFieldReflect;
import ru.ps.vlcatv.traktoauth.TraktTv.data.TraktConstant;

@Keep
public class DeviceCodeRequest extends ReflectAttribute {

    public static final String url = "oauth/device/code";
    @IFieldReflect("client_id")
    private final String ClientId = TraktConstant.APP_ID;

    public DeviceCodeRequest() {
        super();
    }
    protected DeviceCodeRequest(Parcel in) {
        super(in);
    }

}
