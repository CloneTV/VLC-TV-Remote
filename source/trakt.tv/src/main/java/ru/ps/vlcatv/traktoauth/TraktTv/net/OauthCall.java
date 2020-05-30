package ru.ps.vlcatv.traktoauth.TraktTv.net;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Handler;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import ru.ps.vlcatv.traktoauth.BuildConfig;
import ru.ps.vlcatv.traktoauth.HttpDebugLogging;
import ru.ps.vlcatv.traktoauth.TraktTv.data.OauthDataHolder;
import ru.ps.vlcatv.traktoauth.TraktTv.data.type.DeviceCodeRequest;
import ru.ps.vlcatv.traktoauth.TraktTv.data.type.DeviceTokenRequest;
import ru.ps.vlcatv.utils.Text;
import ru.ps.vlcatv.utils.Log;

public class OauthCall {

    public enum State {
        STAGE1_OK,
        STAGE2_OK,
        STAGE1_ERR,
        STAGE2_ERR,
        STAGE1_BAD_CODE,
        STAGE2_BAD_CODE,
        STAGE1_BAD_DATA,
        STAGE2_BAD_DATA,
        STAGE3_BAD_INIT,
        STAGE1_EXCEPT,
        STAGE2_EXCEPT,
        STAGE_RUN,
        STAGE_BAD_ID,
        STAGE_EXCEPT,
        STAGE_EXPIRE,
        STAGE_CANCELED,
        STAGE_SUCCESSFUL,
        STAGE_COUNT,
        STAGE_END,
        STAGE_NONE
    }

    private static final String TAG = OauthCall.class.getSimpleName();
    private final OauthDataHolder holder;
    private final OkHttpClient client;
    private ArrayList<InfoOauthInterface> infoInterface;
    private Handler handler = null;
    private Runnable runnable = null;
    private State state;
    private int mCount;
    public OauthCallTraktAPI api;

    public OauthCall() {
        state = State.STAGE_NONE;
        infoInterface = new ArrayList<>();
        holder = new OauthDataHolder();
        api = new OauthCallTraktAPI(holder);
        client = HttpDebugLogging.httpClientInit();
    }
    public OauthCall setResources(Resources res) {
        OauthCallMessage.setResources(res);
        return this;
    }
    public OauthCall setPreferences(SharedPreferences sp) {
        holder.setPreferences(sp);
        if ((holder.isActive()) && (api.Init())) {
            state = State.STAGE_SUCCESSFUL;
        } else {
            state = State.STAGE_NONE;
        }
        return this;
    }

    public void StartRegisterDevice() {
        if (handler == null)
            handler = new Handler();

        state = State.STAGE_NONE;
        api.clear();
        holder.codeRes.clear();
        holder.tokenRes.clear();
        holder.tokenReq.DeviceCodeUpdate(null);
        state = State.STAGE_RUN;
        mCount = 1;
        Stage1();
    }
    public void StopRegisterDevice() {
        if (state == State.STAGE_SUCCESSFUL)
            return;
        state = State.STAGE_CANCELED;
        holder.clearDeviceResponse();
    }
    public State getState() {
        return state;
    }
    public boolean isComplete() {
        return (state == State.STAGE_SUCCESSFUL);
    }
    private void setState(State st) {
        setState(st, null);
    }
    private void setState(State st, String str) {

        int stageId = 0;
        switch (st) {
            case STAGE_COUNT: {
                break;
            }
            case STAGE_END:
            case STAGE_CANCELED: {
                state = State.STAGE_NONE;
                break;
            }
            case STAGE3_BAD_INIT: {
                stageId = 3;
                state = State.STAGE_NONE;
                break;
            }
            case STAGE1_EXCEPT: {
                stageId = 1;
                state = State.STAGE_NONE;
                break;
            }
            case STAGE1_ERR:
            case STAGE1_BAD_CODE:
            case STAGE1_BAD_DATA: {
                stageId = 1;
                state = st;
                break;
            }
            case STAGE2_ERR:
            case STAGE2_EXCEPT:
            case STAGE2_BAD_CODE:
            case STAGE2_BAD_DATA: {
                stageId = 2;
                state = st;
                break;
            }
            default: {
                state = st;
                break;
            }
        }
        switch (st) {
            case STAGE1_ERR:
            case STAGE2_ERR:
            case STAGE1_EXCEPT:
            case STAGE2_EXCEPT:
            case STAGE1_BAD_CODE:
            case STAGE1_BAD_DATA:
            case STAGE2_BAD_DATA:
            case STAGE3_BAD_INIT: {
                String msg = String.format(
                        Locale.getDefault(),
                        OauthCallMessage.getFormat(st),
                        stageId,
                        OauthCallMessage.getMessage(st),
                        ((Text.isempty(str)) ? "" : str)
                );
                if ((st == State.STAGE1_EXCEPT) || (st == State.STAGE2_EXCEPT))
                    cbInfo(st, msg);
                else
                    cbError(st, msg);
                break;
            }
            case STAGE2_BAD_CODE: {
                cbInfo(st, String.format(
                        Locale.getDefault(),
                        OauthCallMessage.getFormat(st),
                        ((Text.isempty(str)) ? "" : str)
                        )
                );
                break;
            }
            case STAGE_EXCEPT: {
                cbError(st, String.format(
                        Locale.getDefault(),
                        OauthCallMessage.getFormat(st),
                        OauthCallMessage.getMessage(st),
                        ((Text.isempty(str)) ? "" : str)
                        )
                );
                break;
            }
            case STAGE1_OK: {
                cbInfo(st, String.format(
                        Locale.getDefault(),
                        OauthCallMessage.getFormat(st),
                        OauthCallMessage.getMessage(st),
                        ((Text.isempty(str)) ? "" : str)
                        )
                );
                break;
            }
            case STAGE_COUNT: {
                cbInfo(st, (Text.isempty(str)) ? "?" : str);
                break;
            }
            default: {
                String s = OauthCallMessage.getMessage(st);
                if (!Text.isempty(s)) {
                    String msg = String.format(
                            Locale.getDefault(),
                            OauthCallMessage.getFormat(st),
                            s,
                            ((Text.isempty(str)) ? "" : str)
                    );
                    if ((st == State.STAGE_END) || (st == State.STAGE_CANCELED))
                        cbError(st, msg);
                    else
                        cbInfo(st, msg);
                }
                break;
            }
        }
    }

    private void stageException(State st, Exception e) {
        setState(st);
        String msg = e.getLocalizedMessage();
        if (!Text.isempty(msg)) {
            cbError(State.STAGE_EXCEPT, msg);
            if (BuildConfig.DEBUG) Log.e(TAG, msg, e);
        } else if (BuildConfig.DEBUG) Log.e(TAG, st.toString(), e);
    }

    private void Stage1() {

        Request request = new Request.Builder()
                .url(
                        String.format(
                                Locale.getDefault(),
                                "%s%s",
                                OauthDataHolder.url,
                                DeviceCodeRequest.url
                                )
                )
                .post(RequestBody.create(
                        MediaType.parse(OauthDataHolder.MIME_JSON),
                        holder.codeReq.toJsonString()
                        )
                )
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(@NotNull Call call, @NotNull IOException e) {
                stageException(State.STAGE1_EXCEPT, e);
            }
            @Override public void onResponse(@NotNull Call call, @NotNull Response response) {
                try {
                    if (response.isSuccessful()) {
                        int code = response.code();
                        ResponseBody body = response.body();
                        if (code != 200) {
                            setState(State.STAGE1_BAD_CODE, Integer.toString(code));
                        }
                        else if (body != null) {
                            if (holder.setDeviceCodeResponse(body.string())) {
                                setState(State.STAGE1_OK, holder.codeRes.getVerificationUrl());
                                cbUserCode(holder.codeRes.getUserCode());
                                if (BuildConfig.DEBUG) Log.e("CallbackDeviceCode", "---onResponse go oauthStage2: " + holder.codeRes.toJsonString());
                                runStage2();
                                return;
                            } else {
                                setState(State.STAGE1_BAD_DATA, Integer.toString(code));
                            }
                        } else {
                            setState(State.STAGE1_ERR, Integer.toString(code));
                        }
                    }
                } catch (Exception e) {
                    stageException(State.STAGE1_EXCEPT, e);
                    return;
                }
                holder.codeRes.clear();
                setState(State.STAGE_CANCELED);
            }
        });
    }

    private void Stage2() {

        Request request = new Request.Builder()
                .url(
                        String.format(
                                Locale.getDefault(),
                                "%s%s",
                                OauthDataHolder.url,
                                DeviceTokenRequest.url
                        )
                )
                .post(RequestBody.create(
                        MediaType.parse(OauthDataHolder.MIME_JSON),
                        holder.tokenReq.toJsonString()
                        )
                )
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override public void onFailure(@NotNull Call call, @NotNull IOException e) {
                stageException(State.STAGE2_EXCEPT, e);
                runStage2();
            }
            @Override public void onResponse(@NotNull Call call, @NotNull Response response) {
                try {
                    int code = response.code();
                    if (response.isSuccessful()) {
                        ResponseBody body = response.body();
                        if (code != 200) {
                            setState(State.STAGE2_BAD_CODE,
                                    holder.tokenRes.statusCode(code)
                            );
                            if (holder.tokenRes.isFatalCode(code))
                                setState(State.STAGE_END);
                        }
                        else if (body != null) {
                            if (holder.setDeviceTokenResponse(body.string()))
                                setState(State.STAGE2_OK);
                            else
                                setState(State.STAGE2_BAD_DATA,
                                        holder.tokenRes.statusCode(code)
                                );
                        } else {
                            setState(State.STAGE2_ERR,
                                    holder.tokenRes.statusCode(code)
                            );
                        }
                    } else {
                        setState(State.STAGE2_BAD_CODE,
                                holder.tokenRes.statusCode(code)
                        );
                        if (holder.tokenRes.isFatalCode(code))
                            setState(State.STAGE_END);
                    }
                } catch (Exception e) {
                    stageException(State.STAGE2_EXCEPT, e);
                }
                runStage2();
            }
        });
    }

    ///

    private void runStage2()
    {
        try {
            if (runnable != null)
                handler.removeCallbacksAndMessages(runnable);

            runnable = null;

            switch (state)
            {
                case STAGE_END:
                case STAGE1_ERR:
                case STAGE1_EXCEPT:
                case STAGE1_BAD_DATA: {
                    setState(State.STAGE_NONE);
                    break;
                }
                case STAGE1_OK:
                case STAGE2_ERR:
                case STAGE2_EXCEPT:
                case STAGE2_BAD_CODE:
                case STAGE2_BAD_DATA: {
                    if (holder.codeRes.isExpired()) {
                        holder.clearDeviceResponse();
                        setState(State.STAGE_EXPIRE, Integer.toString(holder.codeRes.getExpired()));
                        return;
                    }
                    holder.tokenRes.clear();
                    runnable = new Runnable() {
                        @Override
                        public void run() {
                            if (state == State.STAGE_CANCELED)
                                return;
                            Stage2();
                        }
                    };
                    handler.postDelayed(runnable, (holder.codeRes.getInterval() * 1000));
                    setState(State.STAGE_COUNT, holder.PendingTimeLeft(mCount++));
                    break;
                }
                case STAGE_CANCELED: {
                    setState(State.STAGE_CANCELED);
                    break;
                }
                case STAGE2_OK: {
                    setState(State.STAGE_SUCCESSFUL);
                    if (!api.Init())
                        setState(State.STAGE3_BAD_INIT);
                    break;
                }
                default: {
                    setState(State.STAGE_BAD_ID, state.toString());
                    break;
                }
            }

        } catch (Exception e) {
            stageException(State.STAGE_EXCEPT, e);
            if (BuildConfig.DEBUG) Log.e("", Text.requireString(e.getLocalizedMessage()), e);
        }
    }

    ///

    public void addInfoInterface(InfoOauthInterface i) {
        if (!infoInterface.contains(i))
            infoInterface.add(i);
    }
    public void removeInfoInterface(InfoOauthInterface i) {
        infoInterface.remove(i);
    }

    ///

    private void cbInfo(State t, String s) {
        try {
            for (InfoOauthInterface i : infoInterface)
                i.setConnectInfo(t, s);

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e(TAG + " cbInfo", Text.requireString(e.getLocalizedMessage()), e);
        }
    }
    private void cbError(State t, String s) {
        try {
            for (InfoOauthInterface i : infoInterface)
                i.setConnectError(t, s);

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e(TAG + " cbError", Text.requireString(e.getLocalizedMessage()), e);
        }
    }
    private void cbUserCode(String s) {
        try {
            for (InfoOauthInterface i : infoInterface)
                i.setUserCode(s);

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e(TAG + " cbError", Text.requireString(e.getLocalizedMessage()), e);
        }
    }

}
