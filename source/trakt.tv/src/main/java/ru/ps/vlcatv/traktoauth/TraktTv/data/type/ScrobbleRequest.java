package ru.ps.vlcatv.traktoauth.TraktTv.data.type;

import androidx.annotation.Keep;
import ru.ps.vlcatv.traktoauth.BuildConfig;
import ru.ps.vlcatv.utils.reflect.ReflectAttribute;
import ru.ps.vlcatv.utils.reflect.annotation.IFieldReflect;

@Keep
public class ScrobbleRequest extends ReflectAttribute {

    public static final String url_start = "scrobble/start";
    public static final String url_pause = "scrobble/pause";
    public static final String url_stop = "scrobble/stop";

    public ScrobbleRequest() {}
    public ScrobbleRequest(int pos) { MediaProgress = (float)pos; }

    @IFieldReflect("progress")
    private float MediaProgress = 0;
    @IFieldReflect("app_version")
    private final String AppVersion = BuildConfig.VERSION_NAME;
    @IFieldReflect("app_date")
    private final String AppBuildDate = BuildConfig.TIMEBUILD;

}
