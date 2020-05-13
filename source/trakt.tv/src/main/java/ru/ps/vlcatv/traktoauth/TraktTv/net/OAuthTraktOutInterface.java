package ru.ps.vlcatv.traktoauth.TraktTv.net;

import org.json.JSONObject;

public interface OAuthTraktOutInterface {
    public void outResponse(OAuthTraktGetInterface.RequestId id, JSONObject obj, int traktid);
    public void outError(OAuthTraktGetInterface.RequestId id, String msg, Exception e, int traktid);
}
