package ru.ps.vlcatv.traktoauth.TraktTv.net;

public interface InfoOauthInterface {
    public void setUserCode(String s);
    public void setConnectInfo(OauthCall.State t, String s);
    public void setConnectError(OauthCall.State t, String s);
}
