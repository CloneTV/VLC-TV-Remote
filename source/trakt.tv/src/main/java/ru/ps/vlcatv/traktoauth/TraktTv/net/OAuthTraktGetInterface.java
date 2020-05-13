package ru.ps.vlcatv.traktoauth.TraktTv.net;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import okhttp3.RequestBody;
import retrofit2.http.Path;
import retrofit2.http.Query;
import ru.ps.vlcatv.traktoauth.TraktTv.data.OauthDataHolder;
import ru.ps.vlcatv.traktoauth.TraktTv.data.type.ScrobbleRequest;

public interface OAuthTraktGetInterface {

    public enum RequestId {
        REQ_SCROB_NEW,
        REQ_SCROB_PLAY,
        REQ_SCROB_STOP,
        REQ_SCROB_PAUSE,
        REQ_HISTORY_ADD,
        REQ_SEARCH,
        REQ_INIT
    }

    @POST(ScrobbleRequest.url_start)
    @Headers({
            OauthDataHolder.HEADER_ACCEPT_JSON,
            OauthDataHolder.HEADER_CONTENT_JSON,
            OauthDataHolder.HEADER_TRAKT_API_VER
    })
    Call<JSONObject> scrobblePlay(@Header(OauthDataHolder.HEADER_TRAKT_API_KEY) String appid,
                                   @Header(OauthDataHolder.HEADER_AUTHORIZATION) String token,
                                   @Body RequestBody jsonText);

    @POST(ScrobbleRequest.url_stop)
    @Headers({
            OauthDataHolder.HEADER_ACCEPT_JSON,
            OauthDataHolder.HEADER_CONTENT_JSON,
            OauthDataHolder.HEADER_TRAKT_API_VER
    })
    Call<JSONObject> scrobbleStop(@Header(OauthDataHolder.HEADER_TRAKT_API_KEY) String appid,
                                  @Header(OauthDataHolder.HEADER_AUTHORIZATION) String token,
                                  @Body RequestBody jsonText);

    @POST(ScrobbleRequest.url_pause)
    @Headers({
            OauthDataHolder.HEADER_ACCEPT_JSON,
            OauthDataHolder.HEADER_CONTENT_JSON,
            OauthDataHolder.HEADER_TRAKT_API_VER
    })
    Call<JSONObject> scrobblePause(@Header(OauthDataHolder.HEADER_TRAKT_API_KEY) String appid,
                                   @Header(OauthDataHolder.HEADER_AUTHORIZATION) String token,
                                   @Body RequestBody jsonText);

    @POST("sync/history")
    @Headers({
            OauthDataHolder.HEADER_ACCEPT_JSON,
            OauthDataHolder.HEADER_CONTENT_JSON,
            OauthDataHolder.HEADER_TRAKT_API_VER
    })
    Call<JSONObject> addHistory(@Header(OauthDataHolder.HEADER_TRAKT_API_KEY) String appid,
                                @Header(OauthDataHolder.HEADER_AUTHORIZATION) String token,
                                @Body RequestBody jsonText);

    @GET("search/{type}")
    @Headers({
            OauthDataHolder.HEADER_ACCEPT_JSON,
            OauthDataHolder.HEADER_TRAKT_API_VER
    })
    Call<JSONObject> search(@Header(OauthDataHolder.HEADER_TRAKT_API_KEY) String appid,
                            @Path("type") String type,
                            @Query("query") String query,
                            @Query("limit") int limit);

}
