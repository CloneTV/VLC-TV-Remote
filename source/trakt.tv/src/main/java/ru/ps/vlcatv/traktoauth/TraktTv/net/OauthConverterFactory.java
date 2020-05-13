package ru.ps.vlcatv.traktoauth.TraktTv.net;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

public class OauthConverterFactory extends Converter.Factory {

    public static OauthConverterFactory create() {
        return new OauthConverterFactory();
    }
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return JsonConverter.INSTANCE;
    }
    final static class JsonConverter implements Converter<ResponseBody, JSONObject> {
        static final JsonConverter INSTANCE = new JsonConverter();

        @Override
        public JSONObject convert(ResponseBody responseBody) throws IOException {
            try {
                String text;
                try {
                    text = responseBody.string();
                } catch (Exception e) { return new JSONObject(); }

                if (text.charAt(0) == '[') {
                    JSONArray a = new JSONArray(text);
                    JSONObject obj = new JSONObject();
                    obj.put("array", a);
                    return obj;
                }
                return new JSONObject(text);
            } catch (JSONException e) {
                throw new IOException("Failed to parse JSON", e);
            }
        }
    }
}
