package ru.ps.vlcatv.utils.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import ru.ps.vlcatv.utils.BuildConfig;
import ru.ps.vlcatv.utils.Log;
import ru.ps.vlcatv.utils.Text;
import ru.ps.vlcatv.utils.reflect.ActionDb;
import ru.ps.vlcatv.utils.reflect.ReflectAttribute;

public class DbManager {

    private DbHelper dbh;
    private final Timer mTimer = new Timer();
    private final WeakReference<Context> wContext;
    private SQLiteDatabase db = null;
    private Long mTimeMark = 0L;
    public DbUtils utils = null;

    public DbManager(Context c) {
        wContext = new WeakReference<>(c);
        mTimer.scheduleAtFixedRate(new dbConnectTask(), 0, 120000);
    }

    public DbManager open(final int v, Class<?> clz) throws SQLException {
        dbh = DbHelper.createDbHelper(wContext.get(), v, clz);
        if (dbh == null)
            throw new RuntimeException("db init: fatal error");

        db = dbh.getWritableDatabase();
        utils = new DbUtils(this);
        mTimeMark = new Date().getTime();
        return this;
    }

    public void close() {
        try {
            if (db != null)
                synchronized (db) {
                    db.close();
                    db = null;
                }
        } catch (Exception ignore) {}
        try {
            if (dbh != null)
                synchronized (dbh) {
                    dbh.close();
                    dbh = null;
                }
        } catch (Exception ignore) {}
        try {
            mTimer.cancel();
        } catch (Exception ignore) {}
    }
    public void sqlSchemeWrite(List<ActionDb.RootTableHolder.RootTableCreator> list) {
        utils.sqlSchemeWrite(wContext.get(), DbHelper.USING_NAME_PKG, DbHelper.dbVersion, list);
    }

    private long updateIGNORE(DbArguments args) {
        if (db.updateWithOnConflict(
                args.getTable(),
                args.getInsertColumns(),
                args.getSelection(),
                args.getSelectionArgs(),
                SQLiteDatabase.CONFLICT_IGNORE) > 0) {
            if (args.getSelectionArgs() != null) {
                try {
                    return Long.parseLong(args.getSelectionArgs()[0]);
                } catch (Exception ignore) {
                    try {
                        Cursor cursor = null;
                        try {
                            cursor = fetch(args);
                            if (cursor != null) {
                                do {
                                    int idf = cursor.getColumnIndex(ReflectAttribute.ID_INDEX);
                                    if (idf <= 0)
                                        continue;
                                    long id = cursor.getLong(idf);
                                    if (id > 0)
                                        return id;
                                } while (cursor.moveToNext());
                            }
                        } finally {
                            if (cursor != null)
                                cursor.close();
                            cursor = null;
                        }
                    } catch (Exception ignored) {}
                }
            }
        }
        return -1L;
    }
    public long insertOrUpdate(DbArguments args) {
        try {
            if (!setLastTimeAccess())
                return -1L;

            if (BuildConfig.DEBUG_DB) Log.d("- Dump insertOrUpdate", utils.dumpInsertOrUpdateQuery(args));
            long id = -1L;
            if (args.isUpdateAction())
                id = updateIGNORE(args);
            if (BuildConfig.DEBUG_DB) Log.d("- OUT insertOrUpdate: id=" + id + ", " + args.getTable(), " " + args.getInsertColumns().toString());
            if (id <= 0L) {
                id = db.insertWithOnConflict(
                        args.getTable(),
                        null,
                        args.getInsertColumns(),
                        SQLiteDatabase.CONFLICT_IGNORE
                );
            }
            if (BuildConfig.DEBUG_DB) Log.d("- ??? insertOrUpdate: " + id, " End=" + id);
            return ((id <= 0) ? -1L : id);

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("- Db insert/update", Text.requireString(e.getLocalizedMessage()), e);
            return -1L;
        }
    }
    public Cursor fetch(DbArguments args) {
        try {
            if (args.isFetchEmpty())
                return null;
            if (!setLastTimeAccess())
                return null;

            if (BuildConfig.DEBUG_DB) Log.d("- Dump fetch", utils.dumpFetchQuery(args));
            Cursor cursor = db.query(
                    args.getTable(),
                    args.getFetchColumns(),
                    args.getSelection(),
                    args.getSelectionArgs(),
                    null,
                    null,
                    null
            );
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    return cursor;
                }
                cursor.close();
            }

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("- Db fetch", Text.requireString(e.getLocalizedMessage()), e);
        }
        return null;
    }
    public void delete(String name) {
        if (setLastTimeAccess())
            db.execSQL(
                String.format(
                        Locale.getDefault(),
                        "DROP %s IF EXISTS `%s`",
                        ((name.startsWith("Index_")) ? "INDEX" : "TABLE"),
                        name
                )
            );
        if (BuildConfig.DEBUG_DB) Log.d("- Dump delete", utils.dumpAllTables());
    }
    public Cursor rawSql(String query, String[] fields) {
        if (setLastTimeAccess())
            return db.rawQuery(query, fields);
        return null;
    }
    public void rawSql(String query) {
        if (setLastTimeAccess())
            db.execSQL(query);
    }
    public void rawSql(String query, Object[] args) {
        if (setLastTimeAccess())
            db.execSQL(query, args);
    }
    public SQLiteDatabase getDb() {
        if (setLastTimeAccess())
            return db;
        return null;
    }

    ///

    public boolean isEmpty() {
        if (!setLastTimeAccess())
            return true;
        try {
            Cursor cursor = null;
            try {
                cursor = rawSql(ConstantDataDb.BaseCheckEmpty, null);
                if (cursor != null) {
                    int idx = 0;
                    do {
                        int idf = cursor.getColumnIndex(ConstantDataDb.FieldCount[idx++]);
                        if (idf <= 0)
                            continue;
                        if (cursor.getLong(idf) > 0) {
                            return false;
                        }
                        if (idx >= ConstantDataDb.FieldCount.length)
                            return true;
                    } while (cursor.moveToNext());
                }
            } finally {
                if (cursor != null)
                    cursor.close();
                cursor = null;
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("- Db isEmpty", Text.requireString(e.getLocalizedMessage()), e);
        }
        return true;
    }


    ///

    private boolean setLastTimeAccess() {
        mTimeMark = new Date().getTime();
        if (db == null) {
            db = dbh.getWritableDatabase();
            if (BuildConfig.DEBUG_DB) Log.e("Connect check", " DB base open = " + (db != null));
        }
        return (db != null);
    }

    ///

    private final class dbConnectTask extends TimerTask {
        @Override
        public void run()
        {
            try {
                if (mTimeMark <= ((new Date().getTime()) - 120)) {
                    if (db != null) {
                        synchronized (db) {
                            db.close();
                            db = null;
                        }
                    }
                }
                if (BuildConfig.DEBUG_DB) Log.e(
                        "Connect task status",
                        String.format(
                                Locale.getDefault(),
                                " DB base %s",
                                ((db == null) ? "closed" : "alive")
                        )
                );
            } catch (Exception e) {
                if (BuildConfig.DEBUG) Log.e("dbConnectTask", Text.requireString(e.getLocalizedMessage()), e);
            }
        }
    }

    ///

    public static final class DbArguments {

        private ContentValues contentWhere = null;
        private ContentValues contentSql = null;
        private List<TableField> fieldsList;
        private TableBase base;

        public static final class TableField {
            public final String name;
            public final Field field;
            public final Type type;

            public TableField(Field f, Type t, String n) {
                field = f;
                type = t;
                name = n;
            }
            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder();
                Formatter fmt = new Formatter(sb);
                sb.append("\tTableField = { ");
                if (!Text.isempty(name))
                    fmt.format("name=\"%s\" ", name);
                else
                    sb.append("name=NULL ");
                if (field != null)
                    fmt.format("field=%s ", field.toString());
                else
                    sb.append("field=NULL ");
                if (type != null)
                    fmt.format("type=%s ", type.toString());
                else
                    sb.append("type=NULL ");
                sb.append("}\n");
                return sb.toString();
            }
        }
        public static final class TableBase {
            public final long index;                         // Id
            public final long parent;                        // parent Id
            public final String table;                       // table name

            public TableBase(long idx, long p, String t) {
                index = idx;
                parent = p;
                table = t;
            }
            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder();
                Formatter fmt = new Formatter(sb);
                fmt.format("\tTableBase = { index=%d parent=%d ", index, parent);
                if (!Text.isempty(table))
                    fmt.format("table=\"%s\" ", table);
                else
                    sb.append("table=NULL ");
                sb.append("}\n");
                return sb.toString();
            }
        }

        public DbArguments(TableBase tb, List<TableField> f) {
            base = tb;
            fieldsList = f;
        }
        public DbArguments(TableBase tb, List<TableField> f, ContentValues cv) {
            base = tb;
            fieldsList = f;
            contentSql = cv;
        }
        public void setWhere(ContentValues cw) {
            contentWhere = cw;
        }
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("DbArguments = {\n\t");
            sb.append(base.toString());
            if ((fieldsList != null) && (fieldsList.size() > 0)) {
                for (TableField f : fieldsList)
                    sb.append(f.toString());
            }
            if (contentSql != null) {
                sb.append("\tContentValues = { ");
                sb.append(contentSql.toString());
                sb.append(" }\n");
            }
            if (contentWhere != null) {
                sb.append("\tContentWhere = { ");
                sb.append(contentWhere.toString());
                sb.append(" }\n");
            }
            if (base != null) {
                sb.append("\tBase Indexes = { ");
                sb.append(base.toString());
                sb.append(" }\n");
            }
            sb.append("\tSQL INSERT = { ");
            sb.append(!isUpdateAction());
            sb.append(" }\n");

            sb.append("\tSQL UPDATE = { ");
            sb.append(isUpdateAction());
            sb.append(" }\n");

            sb.append("\tSQL SELECT = { ");
            sb.append(isFetchEmpty());
            sb.append(" }\n");

            sb.append("\tSQL WHERE = { ");
            if (getSelection() != null) {
                sb.append(getSelection());
                String[] selectionArgs = getSelectionArgs();
                if ((selectionArgs != null) && (selectionArgs.length > 0))
                    sb.append(selectionArgs[0]);
                else
                    sb.append("NULL");
            } else {
                sb.append("NULL");
            }
            sb.append(" }\n}\n");
            return sb.toString();
        }

        private int check_() {
            return ((base.index > 0) ? 1 : 0) +
                    ((base.parent > 0) ? 2 : 0);
        }

        public boolean isUpdateAction() {
            if (contentSql == null)
                contentSql = new ContentValues();

            int c = check_();
            if ((c == 2) || (c == 3))
                contentSql.put(ReflectAttribute.ID_PARENT, base.parent);
            if ((c == 1) || (c == 3)) {
                contentSql.put(ReflectAttribute.ID_INDEX, base.index);
                return true;
            }
            return false;
        }
        public boolean isFetchEmpty() {
            return ((fieldsList == null) || (fieldsList.size() == 0) ||
                    (Text.isempty(base.table)));
        }
        public String getTable() {
            return base.table;
        }
        public ContentValues getInsertColumns() {
            if (contentSql == null)
                contentSql = new ContentValues();
            return contentSql;
        }
        public String[] getFetchColumns() {
            if ((fieldsList == null) || (fieldsList.size() == 0))
                return new String[]{};
            try {
                int i = 0;
                String[] fields = new String[fieldsList.size()];
                for (TableField f : fieldsList)
                    fields[i++] = f.name;
                return fields;
            } catch (Exception ignore) { return new String[]{}; }
        }
        public String getSelection() {
            if ((contentWhere != null) && (contentWhere.size() > 0)) {
                int cnt = 0;
                StringBuilder sb = new StringBuilder();
                for (String s : contentWhere.keySet()) {
                    if (cnt++ > 0)
                        sb.append(" AND `");
                    else
                        sb.append("`");
                    sb.append(s);
                    sb.append("` = ?");
                }
                return sb.toString();
            }
            switch (check_()) {
                case 1:
                case 3: return ReflectAttribute.ID_INDEX + " = ?";
                case 2: return ReflectAttribute.ID_PARENT + " = ?";
                default: return null;
            }
        }
        public String[] getSelectionArgs() {
            if ((contentWhere != null) && (contentWhere.size() > 0)) {
                int cnt = 0;
                String[] ss = new String[contentWhere.size()];
                for (Map.Entry<String, Object> en : contentWhere.valueSet())
                    ss[cnt++] = en.getValue().toString();
                return ss;
            }
            switch (check_()) {
                case 1:
                case 3: return new String[] { Long.toString(base.index) };
                case 2: return new String[] { Long.toString(base.parent) };
                default: return null;
            }
        }
    }
}
