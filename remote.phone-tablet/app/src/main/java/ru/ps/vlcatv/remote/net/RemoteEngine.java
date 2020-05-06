package ru.ps.vlcatv.remote.net;

import android.util.Log;
import android.widget.Toast;
import java.util.Locale;
import java.util.Objects;

import org.json.JSONArray;
import org.json.JSONObject;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import ru.ps.vlcatv.remote.AppMain;
import ru.ps.vlcatv.remote.BuildConfig;
import ru.ps.vlcatv.remote.JsonObjectConverterFactory;
import ru.ps.vlcatv.remote.R;
import ru.ps.vlcatv.remote.data.SettingsInterface;
import ru.ps.vlcatv.remote.Utils;
import ru.ps.vlcatv.remote.data.DataMediaItem;

public class RemoteEngine implements SettingsInterface {

    private static final String TAG = RemoteEngine.class.getSimpleName();
    private RemoteInterface m_remoteInterface = null;
    private CallbackCmdDefault cb_default = new CallbackCmdDefault();
    private CallbackMediaItem cb_mediaItem = new CallbackMediaItem();
    private CallbackMediaItems cb_mediaItems = new CallbackMediaItems();

    public RemoteEngine() {
        init();
        try {
            AppMain.getSettings().setCallbackChanged(this);
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e(TAG, e.getLocalizedMessage(), e);
        }
    }

    @Override
    public void onSettingsChange() {
        init();
    }

    @Override
    public void onPlayChange() {}

    @Override
    public void onHistoryChange() {}

    private void init() {

        if (AppMain.getSettings().isempty())
            return;

        m_remoteInterface = null;

        try {
            Retrofit retrofit;
            OkHttpClient okHttpClient;

            okHttpClient = new OkHttpClient.Builder().build();
            Retrofit.Builder builder = new Retrofit.Builder()
                    .baseUrl(String.format(
                            Locale.getDefault(),
                            "http://%s:%s/",
                            AppMain.getSettings().Address.get(),
                            AppMain.getSettings().Port.get()
                            )
                    )
                    .addConverterFactory(JsonObjectConverterFactory.create())
                    .client(okHttpClient);
            retrofit = builder.build();
            m_remoteInterface = retrofit.create(RemoteInterface.class);

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e(TAG, e.getLocalizedMessage(), e);
            else Toast.makeText(AppMain.getAppContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            m_remoteInterface = null;
        }
    }

    private boolean checkInstance() {
        if (m_remoteInterface == null) {
            if ((Utils.isempty(AppMain.getSettings().Address.get())) ||
                    (Utils.isempty(AppMain.getSettings().Port.get())))
                Toast.makeText(AppMain.getAppContext(), R.string.warning_connect1, Toast.LENGTH_LONG).show();
            else
                Toast.makeText(AppMain.getAppContext(), R.string.warning_connect2, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public void status()
    {
        if (checkInstance()) {
            m_remoteInterface.status().enqueue(cb_default);
        }
    }
    public void cmd(String uri)
    {
        if (checkInstance()) {
            m_remoteInterface.cmd(uri).enqueue(cb_default);
        }
    }
    public void cmd(String uri, String opt)
    {
        if (checkInstance()) {
            m_remoteInterface.cmd(uri, opt).enqueue(cb_default);
        }
    }
    public void mediaItem(int id)
    {
        if (checkInstance()) {
            m_remoteInterface.mediaItem(id).enqueue(cb_mediaItem);
        }
    }
    public void mediaItems()
    {
        if (checkInstance()) {
            m_remoteInterface.mediaItems().enqueue(cb_mediaItems);
        }
    }

    // Default command, return status (Json response)
    private static class CallbackCmdDefault implements Callback<JSONObject> {
        @Override
        public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
            try {
                if (!response.isSuccessful())
                    return;

                JSONObject obj = response.body();
                if (obj == null)
                    return;
                int error = obj.optInt("error", -1);
                if (error != 200)
                    return;
                if (obj.optString("type", "").equalsIgnoreCase("STATUS"))
                    AppMain.getStatus().fromJson(obj);

            } catch (Exception e) {
                if (BuildConfig.DEBUG) Log.e(TAG, Objects.requireNonNull(e.getMessage()));
            }
        }
        @Override
        public void onFailure(Call<JSONObject> call, Throwable t) {
            if (BuildConfig.DEBUG) Log.e(TAG, Objects.requireNonNull(t.getMessage()));
        }
    }

    // MediaItem command, return MediaItem (Json response)
    private static class CallbackMediaItem implements Callback<JSONObject> {
        @Override
        public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
            try {
                if (!response.isSuccessful())
                    return;

                JSONObject obj = response.body();
                if (obj == null)
                    return;
                int error = obj.optInt("error", -1);
                if (error != 200)
                    return;
                if (!obj.optString("type", "").equalsIgnoreCase("MEDIAITEM"))
                    return;
                AppMain.getStatus().MmItem = new DataMediaItem(obj);

            } catch (Exception e) {
                if (BuildConfig.DEBUG) Log.e(TAG, Objects.requireNonNull(e.getMessage()));
            }
        }
        @Override
        public void onFailure(Call<JSONObject> call, Throwable t) {
            if (BuildConfig.DEBUG) Log.e(TAG, Objects.requireNonNull(t.getMessage()));
        }
    }

    // MediaItems command, return MediaItem array (Json response)
    private static class CallbackMediaItems implements Callback<JSONObject> {
        @Override
        public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
            try {
                if (!response.isSuccessful())
                    return;

                JSONObject obj = response.body();
                if (obj == null)
                    return;
                int error = obj.optInt("error", -1);
                if (error != 200)
                    return;
                if (!obj.optString("type", "").equalsIgnoreCase("MEDIAITEMS"))
                    return;
                JSONArray array = obj.optJSONArray("data");
                if (array == null)
                    return;
                if (array.length() == 0)
                    return;

                DataMediaItem[] items = new DataMediaItem[array.length()];
                for (int i = 0; i < array.length(); i++) {
                    JSONObject o = array.optJSONObject(i);
                    if (o != null)
                        items[i] = new DataMediaItem(o);
                }
                AppMain.getStatus().setItemsList(items);

            } catch (Exception e) {
                if (BuildConfig.DEBUG) Log.e(TAG, Objects.requireNonNull(e.getMessage()));
            }
        }
        @Override
        public void onFailure(Call<JSONObject> call, Throwable t) {
            if (BuildConfig.DEBUG) Log.e(TAG, Objects.requireNonNull(t.getMessage()));
        }
    }
}
