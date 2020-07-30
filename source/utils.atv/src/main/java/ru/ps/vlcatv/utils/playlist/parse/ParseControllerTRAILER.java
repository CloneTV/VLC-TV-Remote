package ru.ps.vlcatv.utils.playlist.parse;

import java.util.Locale;
import ru.ps.vlcatv.utils.Text;
import ru.ps.vlcatv.utils.json.JSONObject;

public class ParseControllerTRAILER {
    private final static String TAG_FOUND = "found";
    private final static String TAG_URI = "uri";

    public String buildRequest(final String uri, final String baseHttp, final String pathLocal) {
        try {
            int pos = uri.lastIndexOf('/');
            if (pos <= 0)
                return null;

            String name = uri.substring((pos + 1), uri.length());
            pos = name.lastIndexOf('.');
            if (pos <= 0)
                return null;
            name = name.substring(0, pos);
            return String.format(
                    Locale.getDefault(),
                    "%scheck?uri=file:///%s%s",
                    baseHttp,
                    pathLocal,
                    name
            );
        } catch (Exception ignore) {}
        return null;
    }
    public String buildResponse(final String txt, final String baseHttp) {
        try {
                if (Text.isempty(txt))
                    return null;
                JSONObject obj = new JSONObject(txt.trim());
                if (!obj.optBoolean(TAG_FOUND, false))
                    return null;
                String s = obj.optString(TAG_URI, null);
                if (Text.isempty(s))
                    return null;
                return String.format(
                        Locale.getDefault(),
                        "%sget?uri=%s",
                        baseHttp, s
                );
        } catch (Exception ignore) {}
        return null;
    }
}
