package ru.ps.vlcatv.utils;

public class Log {
    static final boolean ISDEBUG = BuildConfig.DEBUG;

    public static void i(String tag, String string) {
        if (ISDEBUG) android.util.Log.i(tag, string);
    }

    public static void e(String tag, String string) {
        if (ISDEBUG) android.util.Log.e(tag, string);
    }

    public static void e(String tag, String string, Exception e) {
        if (ISDEBUG) android.util.Log.e(tag, string, e);
    }

    public static void d(String tag, String string) {
        if (ISDEBUG) android.util.Log.d(tag, string);
    }

    public static void v(String tag, String string) {
        if (ISDEBUG) android.util.Log.v(tag, string);
    }

    public static void w(String tag, String string) {
        if (ISDEBUG) android.util.Log.w(tag, string);
    }

    public static void wtf(String tag, String string) {
        if (ISDEBUG) android.util.Log.wtf(tag, string);
    }
    public static void getStackTraceString(Throwable e) {
        if (ISDEBUG) android.util.Log.e(
                "StackTrace",
                android.util.Log.getStackTraceString(e));
    }
}
