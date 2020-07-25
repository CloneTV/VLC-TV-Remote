package ru.ps.vlcatv.utils.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import ru.ps.vlcatv.utils.BuildConfig;
import ru.ps.vlcatv.utils.Log;
import ru.ps.vlcatv.utils.Text;
import ru.ps.vlcatv.utils.reflect.ActionDb;

public class DbUtils {

    private final WeakReference<DbManager> wdbm;
    private static final String TXT_TBL = "\n(Table)=";
    private static final String TXT_FC = ",\n(Fetch columns)=";
    private static final String TXT_IC = ",\n(Insert columns)=";
    private static final String TXT_FE = ",\n(is fetch empty)=";
    private static final String TXT_S = ",\n(Selection)=";
    private static final String TXT_SA = ",\n(Selection args)=";
    private static final String CONST_FILE = "ConstantDataDb.java";
    private static final String TBL_TABLE = "sqlite_master";
    private static final String TBL_WHERE1 = "type = ? AND name NOT LIKE ? AND name NOT LIKE ?";
    private static final String TBL_WHERE2 = "name LIKE ?";
    private static final String TBL_QUERY = "SELECT name FROM sqlite_master WHERE type = `table`";
    private static final String TBL_LPREFIX = "sqlite_";
    private static final String[] TBL_IGNORE = { "table", "sqlite_%", "android_%" };
    private static final String[] TBL_FIELD1 = { "name" };
    private static final String[] TBL_FIELD2 = { "sql" };

    DbUtils(DbManager dbm) {
        wdbm = new WeakReference<>(dbm);
    }

    public String dumpAllTables() {
        SQLiteDatabase db = wdbm.get().getDb();
        if (db == null)
            return "";

        StringBuilder sb = new StringBuilder();
        Cursor cursor = null;
        try {
            cursor = db.query(
                    TBL_TABLE,
                    TBL_FIELD1,
                    TBL_WHERE1,
                    TBL_IGNORE,
                    null,
                    null,
                    null
            );
            if (cursor != null) {
                if (cursor.moveToFirst()) {

                    do {
                        int idf = cursor.getColumnIndex(TBL_FIELD1[0]);
                        if (idf != -1) {
                            String name = cursor.getString(idf);
                            Cursor curTbl = null;
                            try {
                                curTbl = db.query(
                                        TBL_TABLE,
                                        TBL_FIELD2,
                                        TBL_WHERE2,
                                        new String[] { name },
                                        null,
                                        null,
                                        null
                                );
                                if (curTbl != null) {
                                    if (curTbl.moveToFirst()) {
                                        idf = curTbl.getColumnIndex(TBL_FIELD2[0]);
                                        if (idf != -1) {
                                            String sql = curTbl.getString(idf);
                                            sb.append(sql);
                                            sb.append("\n");
                                        }
                                    }
                                }
                            } finally {
                                if (curTbl != null)
                                    curTbl.close();
                            }
                        }
                    } while (cursor.moveToNext());
                }
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return sb.toString();
    }

    public void deleteAllTables() {
        SQLiteDatabase db = wdbm.get().getDb();
        if (db == null)
            return;

        Cursor cursor = null;
        try {
            try {
                cursor = db.rawQuery(TBL_QUERY, null);
                if (cursor != null) {
                    List<String> tables = new ArrayList<>(cursor.getCount());
                    while (cursor.moveToNext()) {
                        tables.add(cursor.getString(0));
                    }
                    for (String table : tables) {
                        if (table.startsWith(TBL_LPREFIX))
                            continue;

                        db.execSQL(
                                String.format(
                                        Locale.getDefault(),
                                        "DROP TABLE IF EXISTS `%s`", table
                                )
                        );
                        if (BuildConfig.DEBUG) Log.v("- Dropped table ", table);
                    }
                }

            } catch (Exception e) {
                if (BuildConfig.DEBUG) Log.e("- Db delete All Tables", Text.requireString(e.getLocalizedMessage()), e);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    public String dumpFetchQuery(DbManager.DbArguments args) {
        StringBuilder sb = new StringBuilder();
        sb.append(TXT_TBL);
        sb.append(args.getTable());
        sb.append(TXT_FC);
        sb.append(Arrays.toString(args.getFetchColumns()));
        if (args.getSelection() != null) {
            sb.append(TXT_S);
            sb.append(args.getSelection());
        }
        if (args.getSelectionArgs() != null) {
            sb.append(TXT_SA);
            sb.append(Arrays.toString(args.getSelectionArgs()));
        }
        sb.append(TXT_FE);
        sb.append(args.isFetchEmpty());
        return sb.toString();
    }

    public String dumpInsertOrUpdateQuery(DbManager.DbArguments args) {
        StringBuilder sb = new StringBuilder();
        sb.append(TXT_TBL);
        sb.append(args.getTable());
        sb.append(TXT_IC);
        sb.append(args.getInsertColumns().toString());
        if (args.getSelection() != null) {
            sb.append(TXT_S);
            sb.append(args.getSelection());
        }
        if (args.getSelectionArgs() != null) {
            sb.append(TXT_SA);
            sb.append(Arrays.toString(args.getSelectionArgs()));
        }
        return sb.toString();
    }

    public void sqlSchemeWrite(Context context, String pkg, int dbv, List<ActionDb.RootTableHolder.RootTableCreator> list) {
        try {
            File path = context.getFilesDir();
            File file = new File(path, CONST_FILE);

            if (BuildConfig.DEBUG) Log.e("- Db sql scheme save in path", file.getAbsolutePath());
            try (FileOutputStream stream = new FileOutputStream(file)) {
                String header = String.format(
                        Locale.getDefault(),
                        "package %s;\n\npublic class ConstantDataDb {" +
                                "\n\tpublic static final int BaseVersion = %d;\n\n",
                        pkg, dbv
                );
                StringBuilder sbCreate = new StringBuilder(
                        "\tpublic static final String[] BaseCreate = new String[] {\n\t\t"
                );
                StringBuilder sbDelete = new StringBuilder(
                        "\tpublic static final String[] BaseDelete = new String[] {\n\t\t"
                );
                stream.write(header.getBytes());

                for (ActionDb.RootTableHolder.RootTableCreator rtc : list) {
                    sbCreate.append(
                            String.format(
                                    Locale.getDefault(),
                                    "\"%s\",\n\t\t",
                                    rtc.sql
                            )
                    );

                    String types = ((rtc.table.startsWith("Index_")) ? "INDEX" : "TABLE");
                    sbDelete.append(
                            String.format(
                                    Locale.getDefault(),
                                    "\"DROP %s IF EXISTS `%s`\",\n\t\t",
                                    types, rtc.table
                            )
                    );
                }
                sbCreate.append("};\n\n");
                sbDelete.append("};\n\n");
                stream.write(sbCreate.toString().getBytes());
                stream.write(sbDelete.toString().getBytes());
                stream.write("}\n".getBytes());

            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("- Db sql scheme write", Text.requireString(e.getLocalizedMessage()), e);
        }
    }
}
