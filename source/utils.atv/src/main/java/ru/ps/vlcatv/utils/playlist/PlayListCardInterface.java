package ru.ps.vlcatv.utils.playlist;

import android.graphics.Bitmap;
import androidx.annotation.Nullable;

public interface PlayListCardInterface {
    public Object getItem();
    public int getId();
    public int getType();
    public String getTitle();
    public boolean isEmpty();
    public default @Nullable Bitmap getCardBitmapDrawable() {
        return null;
    }
}
