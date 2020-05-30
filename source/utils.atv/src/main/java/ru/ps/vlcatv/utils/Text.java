package ru.ps.vlcatv.utils;

public class Text {

    public static boolean isempty(final String s) {
        return ((s == null) || (s.length() == 0));
    }
    public static String requireString(final String s) {
        if (isempty(s))
            return "";
        return s;
    }
    public static String requireString(final String s, final String d) {
        if (isempty(s))
            return d;
        return s;
    }
}
