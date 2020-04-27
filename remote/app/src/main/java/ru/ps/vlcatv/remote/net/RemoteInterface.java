package ru.ps.vlcatv.remote.net;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RemoteInterface {

    @GET("cmd/GET_STATUS/")
    Call<JSONObject> status();

    @GET("cmd/GET_MEDIA_ITEMS/")
    Call<JSONObject> mediaItems();

    @GET("cmd/GET_MEDIA_ITEM/{idx}/")
    Call<JSONObject> mediaItem(@Path("idx") int idx);

    @GET("cmd/{target}/")
    Call<JSONObject> cmd(@Path("target") String s);

    @GET("cmd/{target}/{opt}/")
    Call<JSONObject> cmd(@Path("target") String s, @Path("opt") String opt);
}
