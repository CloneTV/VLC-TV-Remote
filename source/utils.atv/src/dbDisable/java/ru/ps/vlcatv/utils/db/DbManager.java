package ru.ps.vlcatv.utils.db;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import ru.ps.vlcatv.utils.BuildConfig;
import ru.ps.vlcatv.utils.Log;
import ru.ps.vlcatv.utils.reflect.ActionDb;

public class DbManager {

    public DbManager(Context c) {
    }

    public DbManager open(final int v, Class<?> clz) throws SQLException {
        return null;
    }
    public void close() {}
}
