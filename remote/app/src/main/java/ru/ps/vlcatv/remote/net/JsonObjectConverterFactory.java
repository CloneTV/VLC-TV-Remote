package ru.ps.vlcatv.remote;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

public class JsonObjectConverterFactory extends Converter.Factory {
    public static JsonObjectConverterFactory create() {
        return new JsonObjectConverterFactory();
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
                return new JSONObject(responseBody.string());
            } catch (JSONException e) {
                throw new IOException("Failed to parse JSON", e);
            }
        }
    }
}
