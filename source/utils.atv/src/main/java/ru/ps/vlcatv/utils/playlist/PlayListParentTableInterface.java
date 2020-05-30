package ru.ps.vlcatv.utils.playlist;

public interface PlayListParentTableInterface {
    public default String getName() {
        return "";
    }
    public default void setName(String s) {
    }
    public default long getId() {
        return -1L;
    }
    public default void setId(long l) {}

    public default boolean equalsName(PlayListParentTableInterface pti) {
        return false;
    }
    public default boolean equalsName(String s) {
        return false;
    }
    public default <T> T getNewClassIndex() {
        return null;
    }
    public default <T> T getNewClassIndex(long d) {
        return null;
    }
    public default <T> T getNewClassInstance(String s, long d) {
        return null;
    }

}
