package ru.ps.vlcatv.traktoauth.TraktTv.net;

import android.content.res.Resources;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.lang.ref.WeakReference;

import ru.ps.vlcatv.traktoauth.R;

public class OauthCallMessage {

    static private WeakReference<Resources> mRes = null;
    public static void setResources(Resources res) {
        mRes = new WeakReference<>(res);
    }

    @Nullable
    static String getMessage(OauthCall.State st) {
        if (mRes == null)
            throw new RuntimeException("bad Resources handle");

        switch (st) {
            case STAGE1_ERR:
            case STAGE2_ERR:
                return mRes.get().getString(R.string.trakt_oauth_message_ERR);
            case STAGE1_EXCEPT:
                return mRes.get().getString(R.string.trakt_oauth_message_EXCEPT1);
            case STAGE2_EXCEPT:
                return mRes.get().getString(R.string.trakt_oauth_message_EXCEPT2);
            case STAGE1_BAD_CODE:
            case STAGE2_BAD_CODE:
                return mRes.get().getString(R.string.trakt_oauth_message_BAD_CODE);
            case STAGE1_BAD_DATA:
            case STAGE2_BAD_DATA:
                return mRes.get().getString(R.string.trakt_oauth_message_BAD_DATA);
            case STAGE3_BAD_INIT:
                return mRes.get().getString(R.string.trakt_oauth_message_BAD_INIT);
            case STAGE1_OK:
                return mRes.get().getString(R.string.trakt_oauth_message_OK);
            case STAGE_RUN:
                return mRes.get().getString(R.string.trakt_oauth_message_RUN);
            case STAGE_BAD_ID:
                return mRes.get().getString(R.string.trakt_oauth_message_BAD_ID);
            case STAGE_EXCEPT:
                return mRes.get().getString(R.string.trakt_oauth_message_EXCEPT);
            case STAGE_EXPIRE:
                return mRes.get().getString(R.string.trakt_oauth_message_EXPIRE);
            case STAGE_END:
            case STAGE_CANCELED:
                return mRes.get().getString(R.string.trakt_oauth_message_CANCELED);
            case STAGE_SUCCESSFUL:
                return mRes.get().getString(R.string.trakt_oauth_message_SUCCESS);
            case STAGE_NONE:
                return mRes.get().getString(R.string.trakt_oauth_message_NONE);
            default:
                return null;
        }
    }

    @NonNull
    static String getFormat(OauthCall.State st) {
        if (mRes == null)
            throw new RuntimeException("bad Resources handle");

        switch (st) {
            case STAGE1_ERR:
            case STAGE2_ERR:
            case STAGE1_EXCEPT:
            case STAGE2_EXCEPT:
            case STAGE1_BAD_CODE:
            case STAGE1_BAD_DATA:
            case STAGE2_BAD_DATA:
            case STAGE3_BAD_INIT:
                return mRes.get().getString(R.string.trakt_oauth_format_1);
            case STAGE2_BAD_CODE:
                return mRes.get().getString(R.string.trakt_oauth_format_2);
            case STAGE_EXCEPT:
                return mRes.get().getString(R.string.trakt_oauth_format_3);
            case STAGE1_OK:
                return "%s %s";
            default:
                return mRes.get().getString(R.string.trakt_oauth_format_4);
        }
    }

    @NonNull
    static public String getPendingFormat(int status) {
        if (mRes == null)
            throw new RuntimeException("bad Resources handle");

        if (status == -1)
            return "-";
        else if (status == 0)
            return mRes.get().getString(R.string.trakt_oauth_pending1);
        else
            return mRes.get().getString(R.string.trakt_oauth_pending2);
    }
}
