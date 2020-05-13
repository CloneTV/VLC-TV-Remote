package ru.ps.vlcatv.traktoauth.TraktTv.net;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import ru.ps.vlcatv.traktoauth.BuildConfig;
import ru.ps.vlcatv.traktoauth.HttpDebugLogging;
import ru.ps.vlcatv.traktoauth.TraktTv.data.MediaObject;
import ru.ps.vlcatv.traktoauth.TraktTv.data.OauthDataHolder;
import ru.ps.vlcatv.traktoauth.TraktTv.data.TraktConstant;
import ru.ps.vlcatv.traktoauth.TraktTv.data.type.ScrobbleRequest;
import ru.ps.vlcatv.traktoauth.TraktTv.net.OAuthTraktGetInterface.RequestId;
import ru.ps.vlcatv.utils.Text;
import ru.ps.vlcatv.utils.Log;

public class OauthCallTraktAPI {

    private final OauthDataHolder holder;
    private OAuthTraktGetInterface traktInterface = null;
    private ArrayList<OAuthTraktOutInterface> outInterface = new ArrayList<>();
    private MediaObject currentMediaObject = null;

    OauthCallTraktAPI(OauthDataHolder h) {
        holder = h;
    }
    void clear() {
        traktInterface = null;
    }

    public boolean isActive() {
        return (traktInterface != null);
    }

    public void addOutInterface(OAuthTraktOutInterface i) {
        if (!outInterface.contains(i))
            outInterface.add(i);
    }
    public void removeOutInterface(OAuthTraktOutInterface i) {
        outInterface.remove(i);
    }

    ///

    private int getTraktId() {
        if (currentMediaObject == null)
            return -1;
        return currentMediaObject.getTraktId();
    }
    private void foreachOutInterfaceResponse(RequestId id, int trakt, JSONObject obj) {
        for (OAuthTraktOutInterface i : outInterface)
            i.outResponse(id, obj, trakt);
    }
    private void foreachOutInterfaceError(RequestId id, String msg, Exception e) {
        for (OAuthTraktOutInterface i : outInterface)
            i.outError(id, msg, e, getTraktId());
    }

    boolean Init()
    {
        clear();

        try {
            OkHttpClient okclient = HttpDebugLogging
                    .httpClientInit(new OauthAuthenticator(holder));

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(OauthDataHolder.url)
                    .client(okclient)
                    .addConverterFactory(OauthConverterFactory.create())
                    .build();

            traktInterface = retrofit.create(OAuthTraktGetInterface.class);
            return true;

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("Trakt Init", e.getLocalizedMessage(), e);
            foreachOutInterfaceError(RequestId.REQ_INIT, e.getLocalizedMessage(), e);
        }
        clear();
        return false;
    }

    public void scrobbleStart(MediaObject.MediaObjectType mediaType, String query, String filename) {
        search(mediaType, query, filename);
    }

    public void scrobblePlay(int pos) {
        if (isActive()) {
            try {
                if (currentMediaObject == null)
                    return;

                ScrobbleRequest sbr = new ScrobbleRequest(pos);
                JSONObject obj = currentMediaObject.toJsonScrobble(sbr.toJson());
                if (obj == null)
                    return;
                traktInterface
                        .scrobblePlay(
                                TraktConstant.APP_ID,
                                String.format(Locale.getDefault(),
                                        "%s %s",
                                        holder.tokenRes.getTokenType(),
                                        holder.tokenRes.getAccessToken()
                                ),
                                RequestBody.create(
                                        MediaType.parse(OauthDataHolder.MIME_JSON),
                                        obj.toString()
                                )
                        )
                        .enqueue(new CallbackDefault(RequestId.REQ_SCROB_PLAY));

            } catch (Exception e) {
                if (BuildConfig.DEBUG) Log.e("scrobble Stop", e.getLocalizedMessage(), e);
            }
        }
    }

    public void scrobbleStop(int pos) {
        if (isActive()) {
            try {
                if (currentMediaObject == null)
                    return;

                ScrobbleRequest sbr = new ScrobbleRequest(pos);
                JSONObject obj = currentMediaObject.toJsonScrobble(sbr.toJson());
                if (obj == null)
                    return;
                traktInterface
                        .scrobbleStop(
                                TraktConstant.APP_ID,
                                String.format(Locale.getDefault(),
                                        "%s %s",
                                        holder.tokenRes.getTokenType(),
                                        holder.tokenRes.getAccessToken()
                                ),
                                RequestBody.create(
                                        MediaType.parse(OauthDataHolder.MIME_JSON),
                                        obj.toString()
                                )
                        )
                        .enqueue(new CallbackDefault(RequestId.REQ_SCROB_STOP));

            } catch (Exception e) {
                if (BuildConfig.DEBUG) Log.e("scrobble Stop", e.getLocalizedMessage(), e);
            }
        }
    }

    public void scrobblePause(int pos) {
        if (isActive()) {
            try {
                if (currentMediaObject == null)
                    return;

                ScrobbleRequest sbr = new ScrobbleRequest(pos);
                JSONObject obj = currentMediaObject.toJsonScrobble(sbr.toJson());
                if (obj == null)
                    return;
                traktInterface
                        .scrobblePause(
                                TraktConstant.APP_ID,
                                String.format(Locale.getDefault(),
                                        "%s %s",
                                        holder.tokenRes.getTokenType(),
                                        holder.tokenRes.getAccessToken()
                                ),
                                RequestBody.create(
                                        MediaType.parse(OauthDataHolder.MIME_JSON),
                                        obj.toString()
                                )
                        )
                        .enqueue(new CallbackDefault(RequestId.REQ_SCROB_PAUSE));

            } catch (Exception e) {
                if (BuildConfig.DEBUG) Log.e("scrobble Pause", e.getLocalizedMessage(), e);
            }
        }
    }

    /// Don't using directly!!!
    private void search(MediaObject.MediaObjectType mediaType, String query, String filename) {
        if ((isActive()) || (!Text.isempty(query))) {
            try {
                String type;
                switch (mediaType) {
                    case MEDIA_TYPE_MOVIE:
                        type = "movie";
                        break;
                    case MEDIA_TYPE_EPISODE:
                        type = "show,episode";
                        break;
                    default:
                        return;
                }
                traktInterface
                        .search(
                                TraktConstant.APP_ID,
                                type,
                                query,
                                3
                        )
                        .enqueue(new CallbackDefault(RequestId.REQ_SEARCH, mediaType, filename));

            } catch (Exception e) {
                if (BuildConfig.DEBUG) Log.e("search", e.getLocalizedMessage(), e);
            }
        }
    }

    private void scrobbleNew() {
        if (isActive()) {
            try {
                if (currentMediaObject == null)
                    return;

                ScrobbleRequest sbr = new ScrobbleRequest();
                JSONObject obj = currentMediaObject.toJsonScrobble(sbr.toJson());
                if (obj == null)
                    return;
                traktInterface
                        .scrobblePlay(
                                TraktConstant.APP_ID,
                                String.format(Locale.getDefault(),
                                        "%s %s",
                                        holder.tokenRes.getTokenType(),
                                        holder.tokenRes.getAccessToken()
                                ),
                                RequestBody.create(
                                        MediaType.parse(OauthDataHolder.MIME_JSON),
                                        obj.toString()
                                )
                        )
                        .enqueue(new CallbackDefault(RequestId.REQ_SCROB_NEW));

            } catch (Exception e) {
                if (BuildConfig.DEBUG) Log.e("scrobble Start", e.getLocalizedMessage(), e);
            }
        }
    }

    private void addHistory() {
        if (isActive()) {
            try {
                if (currentMediaObject == null)
                    return;

                JSONObject obj = currentMediaObject.toJsonHistory();
                if (obj == null)
                    return;
                traktInterface
                        .addHistory(
                                TraktConstant.APP_ID,
                                String.format(Locale.getDefault(),
                                        "%s %s",
                                        holder.tokenRes.getTokenType(),
                                        holder.tokenRes.getAccessToken()
                                ),
                                RequestBody.create(
                                        MediaType.parse(OauthDataHolder.MIME_JSON),
                                        obj.toString()
                                )
                        )
                        .enqueue(new CallbackDefault(RequestId.REQ_HISTORY_ADD));

            } catch (Exception e) {
                if (BuildConfig.DEBUG) Log.e("history add", e.getLocalizedMessage(), e);
            }
        }
    }

    private class CallbackDefault implements Callback<JSONObject> {
        RequestId mId;
        String fileName = null;
        MediaObject.MediaObjectType mediaObj = MediaObject.MediaObjectType.MEDIA_TYPE_NONE;

        CallbackDefault(RequestId id) {
            mId = id;
        }
        CallbackDefault(RequestId id, MediaObject.MediaObjectType mobj, String name) {
            mId = id;
            mediaObj = mobj;
            fileName = name;
        }
        @Override
        public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
            if (response.isSuccessful()) {
                eventResponse(mId, mediaObj, fileName, response.body());
            } else {
                foreachOutInterfaceError(mId, statusCode(response.code()), null);
            }
        }
        @Override
        public void onFailure(Call<JSONObject> call, Throwable t) {
            foreachOutInterfaceError(mId, t.getLocalizedMessage(), new Exception(t));
        }
    }

    private void eventResponse(RequestId id, MediaObject.MediaObjectType mtype, String name, JSONObject obj) {
        if (obj == null) {
            return;
        }
        try {

            int trakt = getTraktId();

            if (id == RequestId.REQ_SEARCH) {
                currentMediaObject = null;
                if (mtype == MediaObject.MediaObjectType.MEDIA_TYPE_NONE)
                    return;

                JSONArray root = obj.optJSONArray("array");
                if (root == null)
                    return;

                for (int i = 0; i < root.length(); i++) {
                    JSONObject o = root.optJSONObject(i);
                    if (o == null)
                        continue;

                    String stmp = o.optString("type", "");
                    if (Text.isempty(stmp))
                        continue;

                    if (mtype == MediaObject.MediaObjectType.MEDIA_TYPE_EPISODE) {
                        if (!stmp.equals("episode"))
                            continue;
                        o = o.optJSONObject("episode");
                    } else if (mtype == MediaObject.MediaObjectType.MEDIA_TYPE_MOVIE) {
                        if (!stmp.equals("movie"))
                            continue;
                        o = o.optJSONObject("movie");
                    }
                    if (o == null)
                        continue;
                    o = o.optJSONObject("ids");
                    if (o == null)
                        continue;
                    trakt = o.optInt("trakt", -1);
                    if (trakt == -1)
                        continue;
                    currentMediaObject = new MediaObject(mtype, trakt);
                    break;
                }

                if (currentMediaObject != null) {
                    addHistory();
                } else if (!Text.isempty(name)) {
                    String slug = name.replace('.','-');
                    scrobbleStart(mtype, slug, null);
                }

            } else if (id == RequestId.REQ_HISTORY_ADD) {
                if (currentMediaObject != null)
                    scrobbleNew();

            } else if (id == RequestId.REQ_SCROB_STOP) {
                currentMediaObject = null;
            }
            foreachOutInterfaceResponse(id, trakt, obj);

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("pre Response out: " + id.toString(), e.getLocalizedMessage(), e);
        }
    }

    private String statusCode(int code) {
        switch (code)
        {
            case 200: return ":200: Success";
            case 201: return ":201: Success - new resource created";
            case 204: return ":204: Success - no content to return";
            case 400: return ":400: Bad Request - request couldn't be parsed";
            case 401: return ":401: Unauthorized - OAuth must be provided";
            case 403: return ":403: Forbidden - invalid API key or unapproved application";
            case 404: return ":404: Not Found - method exists, but no record found";
            case 405: return ":405: Method Not Found - method doesn't exist";
            case 409: return ":409: Conflict - resource already created";
            case 412: return ":412: Precondition Failed - use application/json content type";
            case 418: return ":422: Unprocessed Entity - validation errors";
            case 429: return ":429: Rate Limit Exceeded";
            case 500: return ":500: Trakt.tv server Error - please open a support issue";
            case 503:
            case 504: return ":503-504: Service Unavailable - server overloaded, try again in 30s";
            case 520:
            case 521:
            case 522: return ":520-522: Service Unavailable - CloudFlare error";
            default: return Integer.toString(code);
        }
    }
}
