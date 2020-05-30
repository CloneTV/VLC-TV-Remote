package ru.ps.vlcatv.utils.playlist;

public interface PlayListIdsInterface {
    public default String getTypeId() {
        return null;
    }
    public default String getValId() {
        return null;
    }
    public default void   setTypeId(String s) {}
    public default void   setValId(String s) {}
}
