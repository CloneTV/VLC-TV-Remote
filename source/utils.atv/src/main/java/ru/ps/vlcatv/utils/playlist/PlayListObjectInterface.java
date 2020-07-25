package ru.ps.vlcatv.utils.playlist;

import androidx.databinding.ObservableBoolean;

import ru.ps.vlcatv.utils.playlist.parse.ParseObject;

public interface PlayListObjectInterface {

    public void setTitle(String s);
    public void setImage(String s);
    public void setTrailer(String s);
    public void setDescription(String s);
    public default void setUri(String s) {}
    public default void setNfo(String s) {}
    public default void setNfoFake(String s) {}
    public default void setSeason(int s, int e) {}
    public default void setSeason(int s) {}
    public default void setDuration() {}
    public default void setDuration(int d) {}
    public default void setPosition(int d) {}

    public void reloadBindingData();
    public int visibleLastViewDate(ObservableBoolean b);
    public int visibleLastViewLayout(ObservableBoolean b);
    public int visibleProgress(ObservableBoolean b);
    public int visibleWatchedImage(ObservableBoolean b);
    public boolean visibleWatchedColor(ObservableBoolean b);
    public int visibleWatched(ObservableBoolean b);
    public int visibleDate(ObservableBoolean b);
    public int visibleDateField(ObservableBoolean b);
    public int visibleRatingField(ObservableBoolean b);
    public int visibleDurationField(ObservableBoolean b);
    public int visibleUriField(ObservableBoolean b);
    public int visibleMediaLayout(ObservableBoolean b);
    public int visibleMediaImage(ObservableBoolean b);
    public int visibleAwards(ObservableBoolean b);
    public int visibleIsEmpty(ObservableBoolean b);

    public long getVlcId();
    public long getDbIndex();
    public long getDbParent();
    public String getDescription(ObservableBoolean b);
    public default String getTotalSeasonViewTime(ObservableBoolean b) {
        return "";
    }
    public String getLastViewDate(ObservableBoolean b);
    public String getSeasonEpisode(ObservableBoolean b);
    public default void copy(PlayListGroup plg) {}
    public default void copy(PlayListItem pli) {}
    public default void copy(PlayListFavorite fav) {}
    public void copy(ParseObject pa);

    public boolean isDataEmpty();
    public boolean isEmpty();
    public default void updateFromDb(int type) {}
    public void updateFromDb();
    public void updateFromNfo();
    public void updateFromOmdb();
    public void updateTrailer();
}
