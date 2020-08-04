package ru.ps.vlcatv.utils.playlist.parse;

import ru.ps.vlcatv.utils.BuildConfig;
import ru.ps.vlcatv.utils.Log;
import ru.ps.vlcatv.utils.Text;
import ru.ps.vlcatv.utils.json.JSONObject;

public class ParseControllerIMDBAPI {

    private static final String TAG = ParseControllerIMDBAPI.class.getSimpleName();
    private static final String TAG_IF_ERROR = "errorMessage";
    private static final String TAG_MSG_ERROR1 = "Maximum usage";
    private static final String TAG_MSG_ERROR2 = "Your account has been suspended";
    private static final String TAG_TYPE = "searchType";
    public final static String TAG_MOVIE = "SearchMovie";
    public final static String TAG_SEASON = "SearchSeries";
    public final static String TAG_EPISODE = "SearchEpisode";
    private boolean netError = false;

    public void setNetError() {
        netError = true;
    }
    public boolean verify() {
        return !netError;
    }
    public void emptyCount() {
        netError = false;
    }
    public Object parse(String s, int code) {
        try {
            if (Text.isempty(s))
                return null;

            final JSONObject obj = new JSONObject(s.trim());
            String str = obj.optString(TAG_IF_ERROR, null);
            if (!Text.isempty(str)) {
                if ((str.startsWith(TAG_MSG_ERROR1)) || (str.startsWith(TAG_MSG_ERROR2)))
                    netError = true;
                if (BuildConfig.DEBUG) Log.e(TAG, str);
                return null;
            }
            str = obj.optString(TAG_TYPE, null);
            if (Text.isempty(str)) {
                return null;
            }
            if (code >= 400) {
                return null;
            }
            return obj;

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e(
                    TAG + " - IMDB exception: ",
                    Text.requireString(e.getLocalizedMessage()), e
            );
        }
        return null;
    }
}
