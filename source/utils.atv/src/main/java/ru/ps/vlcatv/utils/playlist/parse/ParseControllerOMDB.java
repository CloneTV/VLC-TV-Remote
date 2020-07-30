package ru.ps.vlcatv.utils.playlist.parse;

import ru.ps.vlcatv.utils.BuildConfig;
import ru.ps.vlcatv.utils.Log;
import ru.ps.vlcatv.utils.Text;
import ru.ps.vlcatv.utils.json.JSONArray;
import ru.ps.vlcatv.utils.json.JSONObject;

public class ParseControllerOMDB {

    private static final String TAG = ParseControllerOMDB.class.getSimpleName();
    private final static String TAG_RESPONSE = "Response";
    private final static String TAG_ERROR = "Error";
    private final static String TAG_FALSE = "False";
    private final static String TAG_LIMIT = "Request limit reached!";
    ///
    public String ids = null;
    public int count;
    public boolean netError;

    public ParseControllerOMDB() {
        count = 0;
        netError = false;
    }

    public Object parse(String s, String id, int code) {
        try {
            if (Text.isempty(s))
                return null;

            if (s.charAt(0) == '{') {

                final JSONObject obj = new JSONObject(s.trim());
                String str = obj.optString(TAG_RESPONSE, null);
                if (Text.isempty(str))
                    return null;
                if (!str.equals(TAG_FALSE))
                    return obj;

                if ((str = obj.optString(TAG_ERROR, null)) != null)
                    if ((!Text.isempty(str)) && (str.equals(TAG_LIMIT))) {
                        count = 1000;
                        ids = id;
                    }

                if (BuildConfig.DEBUG) Log.e(
                        TAG + " OMDB base error: " + ((ids == null) ? ".." : ids),
                        "[" + ((str == null) ? ".." : str) + "]"
                );
                return null;

            } else if (s.charAt(0) == '[') {
                return new JSONArray(s);

            } else if (code == 401) {
                count = 1000;
                ids = id;
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e(
                    TAG + " OMDB exception: ",
                    Text.requireString(e.getLocalizedMessage()), e
            );
        }
        return null;
    }
    public boolean check(String id) {
        if ((netError) || (count >= 1000))
            return false;
        else if (!Text.isempty(ids)) {
            if (id.equals(ids))
                ids = null;
            else
                return false;
        }
        return true;
    }
    public boolean verify(String id) {
        count += 1;
        if (count >= 1000) {
            ids = id;
            return false;
        }
        return true;
    }
    public void emptyCount() {
        count = 0;
        netError = false;
    }
    public void emptyIds() {
        ids = null;
        netError = false;
    }
    public void setNetError() {
        netError = true;
    }
}
