package ru.ps.vlcatv.traktoauth.TraktTv.data.type;

import androidx.annotation.Keep;

@Keep
public interface ResponseInterface {
    public void setExpired();
    public int getExpired();
    public boolean isExpired();
    public boolean isempty();
    public void clear();
}
