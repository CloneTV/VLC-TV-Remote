package ru.ps.vlcatv.utils.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Locale;
import java.util.Objects;

import ru.ps.vlcatv.utils.BuildConfig;
import ru.ps.vlcatv.utils.Log;
import ru.ps.vlcatv.utils.Text;

public class DbHelper extends SQLiteOpenHelper {

    public static String USING_NAME_PKG;
    public static String USING_NAME_DB;
    private static final String DEFAULT_DB_NAME = "DEFAULT_BASE_SQLITE";
    public static int dbVersion;
    private final WeakReference<Context> wContext;

    public static DbHelper createDbHelper(Context context, final int dbv, Class<?> clz) {
        try {
            String s = Objects.requireNonNull(clz.getPackage()).getName();
            if (!Text.isempty(s)) {
                DbHelper.USING_NAME_PKG = s;
                s = s.replace(".", "_");
            } else {
                s = DbHelper.DEFAULT_DB_NAME;
            }
            DbHelper.USING_NAME_DB = String.format(
                    Locale.getDefault(),
                    "%s.db", s
            );
            return new DbHelper(
                    context,
                    dbv,
                    DbHelper.USING_NAME_DB
            );
        } catch (Exception e) {
            if (BuildConfig.DEBUG_DB) Log.e("- create Db Helper", Text.requireString(e.getLocalizedMessage()), e);
            return null;
        }
    }

    private DbHelper(Context context, final int version, String dbName) {
        super(context, dbName, null, version);
        dbVersion = version;
        wContext = new WeakReference<>(context);
    }
    public DbHelper(
            @Nullable Context context,
            @Nullable String name,
            @Nullable SQLiteDatabase.CursorFactory factory,
            int version) {
        super(context, name, factory, version);
        dbVersion = version;
        wContext = new WeakReference<>(context);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        if (BuildConfig.DEBUG_DB)
            db.disableWriteAheadLogging();
        super.onOpen(db);
    }
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        for (String s : ConstantDataDb.BaseCreate) {
            try {
                db.execSQL(s);
            } catch (Exception e) {
                if (BuildConfig.DEBUG_DB) Log.e("- Db onCreate (BaseCreate)", Text.requireString(e.getLocalizedMessage()), e);
            }
        }
        for (String s : ConstantDataDb.BaseUpdateOnCreate) {
            try {
                db.execSQL(s);
            } catch (Exception e) {
                if (BuildConfig.DEBUG_DB) Log.e("- Db onCreate (BaseUpdateOnCreate)", Text.requireString(e.getLocalizedMessage()), e);
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
        if (oldVer == newVer)
            return;

        if (oldVer > newVer) {
            try {
                File path = wContext.get().getDatabasePath(DbHelper.USING_NAME_DB);
                if (!path.isDirectory())
                    path.deleteOnExit();

                path = wContext.get().getDatabasePath(DbHelper.USING_NAME_DB + "-journal");
                if (!path.isDirectory())
                    path.deleteOnExit();

                if (BuildConfig.DEBUG_DB) Log.e("- Db onCreate (Base file Delete)", path.getAbsolutePath());
            } catch (Exception e) {
                if (BuildConfig.DEBUG_DB) Log.e("- Db onCreate (Base file Delete)", Text.requireString(e.getLocalizedMessage()), e);
            }
        } else {
            for (String s : ConstantDataDb.BaseDeleteTable) {
                try {
                    db.execSQL(s);
                } catch (Exception e) {
                    if (BuildConfig.DEBUG_DB) Log.e("- Db onCreate (Base table Delete)", Text.requireString(e.getLocalizedMessage()), e);
                }
            }
        }
        onCreate(db);
    }
}
