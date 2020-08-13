package ru.ps.vlcatv.utils.reflect;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableByte;
import androidx.databinding.ObservableChar;
import androidx.databinding.ObservableDouble;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableFloat;
import androidx.databinding.ObservableInt;
import androidx.databinding.ObservableLong;
import androidx.databinding.ObservableShort;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ru.ps.vlcatv.utils.BuildConfig;
import ru.ps.vlcatv.utils.Log;
import ru.ps.vlcatv.utils.Text;
import ru.ps.vlcatv.utils.db.ConstantDataDb;
import ru.ps.vlcatv.utils.db.DbManager;
import ru.ps.vlcatv.utils.reflect.annotation.ActionInterface;
import ru.ps.vlcatv.utils.reflect.annotation.IBaseTableReflect;
import ru.ps.vlcatv.utils.reflect.annotation.IUniqueReflect;

public class ActionDb implements ActionInterface {

    private final StringBuilder sb = new StringBuilder();
    private final RootTableHolder sqlRoot;
    private final DbManager dbMgr;
    private IBaseTableReflect fKey = null;
    private boolean skipRecursion;

    ActionDb(DbManager dbm, long index, long parent, boolean skip) {
        super();
        skipRecursion = skip;
        sqlRoot = new RootTableHolder(index, parent);
        if (dbm == null)
            throw new RuntimeException("Data Base handle is NULL!");
        dbMgr = dbm;
    }

    /// string utils

    private String getIndexName(String table, String field) {
        boolean b = (Text.isempty(field));
        return String.format(
                Locale.getDefault(),
                "Index_%s%s%s",
                table,
                ((b) ? "" : "_"),
                ((b) ? "" : field)
        );
    }
    private String getTableArrayName(final String table, final String field) {
        String sTable;
        int pos = table.lastIndexOf('.');
        if (pos > 0)
            sTable = table.substring(pos + 1);
        else
            sTable = table;
        return String.format(
                Locale.getDefault(),
                "Table_%s_%s",
                sTable, field
        );
    }
    private void addParentIndex(String tableName) {
        String tbl = getIndexName(tableName, ReflectAttribute.ID_PARENT);
        sqlRoot.addTableCreator(
                tbl,
                String.format(
                        Locale.getDefault(),
                        "CREATE INDEX IF NOT EXISTS `%s` ON `%s` (`%s`)",
                        tbl,
                        tableName,
                        ReflectAttribute.ID_PARENT
                )
        );
    }

    ///

    private void initCreateBegin(StringBuilder sBuilder, String name) {
        sBuilder.append(
                String.format(
                        Locale.getDefault(),
                        "CREATE TABLE IF NOT EXISTS `%s` (`%s` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL ON CONFLICT IGNORE, `%s` INTEGER",
                        name,
                        ReflectAttribute.ID_INDEX,
                        ReflectAttribute.ID_PARENT
                )
        );
    }
    private void initCreateFinalize() {

        if (fKey == null) {
            sb.append(" )");
            sqlRoot.addTableCreator(
                    sqlRoot.table,
                    sb.toString()
            );
            sb.delete(0, sb.length());
            return;
        }

        if ((!Text.isempty(fKey.IParentTable())) && (!Text.isempty(fKey.IParentKey()))) {
            sb.append(
                    String.format(
                            Locale.getDefault(),
                            ", FOREIGN KEY(`%s`) REFERENCES `%s`(`%s`)",
                            ReflectAttribute.ID_PARENT,
                            fKey.IParentTable(),
                            fKey.IParentKey()
                    )
            );
            switch (fKey.IParentSyncUpdate())
            {
                case IBaseTableReflect.CASCADE: {
                    sb.append(" ON UPDATE CASCADE");
                    break;
                }
                case IBaseTableReflect.DEFAULT: {
                    sb.append(" ON UPDATE NO ACTION");
                    break;
                }
                default: {
                    break;
                }
            }
            switch (fKey.IParentSyncDelete())
            {
                case IBaseTableReflect.CASCADE: {
                    sb.append(" ON DELETE CASCADE");
                    break;
                }
                case IBaseTableReflect.DEFAULT: {
                    sb.append(" ON DELETE NO ACTION");
                    break;
                }
                default: {
                    break;
                }
            }
        }

        if (sb.length() > 0) {
            sb.append(" )");
            sqlRoot.addTableCreator(
                    sqlRoot.table,
                    sb.toString()
            );
            sb.delete(0, sb.length());
        }

        if ((fKey.IIndexUnique().length > 0) && (fKey.IIndexUnique()[0].IUnique().length > 0)) {
            sb.append("CREATE UNIQUE INDEX IF NOT EXISTS `");
            int cnt = 0;
            StringBuilder sbName = new StringBuilder();
            StringBuilder sbValue = new StringBuilder();
            sbName.append(getIndexName(sqlRoot.table, null));
            for (IUniqueReflect unique : fKey.IIndexUnique())
                for (String s : unique.IUnique()) {
                    sbName.append("_");
                    sbName.append(s);
                    sbValue.append(
                            String.format(
                                    Locale.getDefault(),
                                    "%s`%s`", ((cnt++ == 0) ? "" : ", "), s
                            )
                    );
                }
            sb.append(
                    String.format(
                            Locale.getDefault(),
                            "%s` ON `%s` (%s)",
                            sbName.toString(),
                            sqlRoot.table,
                            sbValue.toString()
                    )
            );
            sqlRoot.addTableCreator(
                    sbName.toString(),
                    sb.toString()
            );
            sb.delete(0, sb.length());
        }
        if (fKey.IIndexOne().length > 0) {
            for (String s : fKey.IIndexOne()) {
                String tbl = getIndexName(sqlRoot.table, s);
                sqlRoot.addTableCreator(
                        tbl,
                        String.format(
                                Locale.getDefault(),
                                "CREATE INDEX IF NOT EXISTS `%s` ON `%s` (`%s`)",
                                tbl,
                                sqlRoot.table, s
                        )
                );
            }
        }
        addParentIndex(sqlRoot.table);
    }
    private void getDbType(StringBuilder sBuilder, String name, Type type) {

        if ((type == String.class) || (type == Date.class)) {
            sBuilder.append(
                    String.format(
                            Locale.getDefault(),
                            ", `%s` TEXT",
                            name
                    )
            );
        } else if ((type == Boolean.class) || (type == boolean.class) ||
                (type == Integer.class) || (type == int.class) ||
                (type == Long.class) || (type == long.class)) {
            sBuilder.append(
                    String.format(
                            Locale.getDefault(),
                            ", `%s` INTEGER",
                            name
                    )
            );
        } else if ((type == Float.class) || (type == float.class) ||
                (type == Double.class) || (type == double.class)) {
            sBuilder.append(
                    String.format(
                            Locale.getDefault(),
                            ", `%s` REAL",
                            name
                    )
            );
        }
    }
    private void addByType(Field field, ContentValues cVal, Type type, Object val, String name) {
        try {
            Class<?> clazz = ((Class<?>)type);
            if (clazz != null) {
                if (clazz == ObservableInt.class) {
                    cVal.put(name, (int) ((ObservableInt) val).get());
                    return;
                } else if (clazz == ObservableLong.class) {
                    cVal.put(name, (long) ((ObservableLong) val).get());
                    return;
                } else if (clazz == ObservableBoolean.class) {
                    cVal.put(name, (boolean) ((ObservableBoolean) val).get());
                    return;
                } else if (clazz == ObservableDouble.class) {
                    cVal.put(name, (double) ((ObservableDouble) val).get());
                    return;
                } else if (clazz == ObservableFloat.class) {
                    cVal.put(name, (float) ((ObservableFloat) val).get());
                    return;
                } else if (clazz == ObservableShort.class) {
                    cVal.put(name, (short) ((ObservableShort) val).get());
                    return;
                } else if (clazz == ObservableChar.class) {
                    cVal.put(name, "" + (char) ((ObservableChar) val).get());
                    return;
                } else if (clazz == ObservableByte.class) {
                    cVal.put(name, (byte) ((ObservableByte) val).get());
                    return;
                }
            }
            if (type == String.class)
                cVal.put(name, (String) val);
            else if ((type == Boolean.class) || (type == boolean.class))
                cVal.put(name, (boolean) val);
            else if ((type == Integer.class) || (type == int.class))
                cVal.put(name, (int) val);
            else if ((type == Long.class) || (type == long.class))
                cVal.put(name, (long) val);
            else if ((type == Float.class) || (type == float.class))
                cVal.put(name, (float) val);
            else if ((type == Double.class) || (type == double.class))
                cVal.put(name, (double) val);
            else if (type == Date.class) {
                try {
                    if (val != null) {
                        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        cVal.put(name, (String) fmt.format(val));
                    }
                } catch (Exception ignore) {}
            }

        } catch (Exception ignore) {
            try {
                Class<?> clz = (Class<?>) ((ParameterizedType) field.getGenericType())
                        .getActualTypeArguments()[0];

                if (clz == null)
                    return;
                if (clz == String.class)
                    cVal.put(name, (String) ((ObservableField<String>) val).get());
                else if (clz == Date.class) {
                    SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    Date date = (Date) ((ObservableField<Date>) val).get();
                    if (date != null)
                        cVal.put(name, fmt.format(date));
                }
            } catch (Exception e) {
                if (BuildConfig.DEBUG_DB) Log.e("- addByType except", Text.requireString(e.getLocalizedMessage()), e);
            }
        }
    }
    private boolean setFieldGeneric(DbManager.DbArguments.TableField tf, Cursor cursor, Object obj) {
        try {
            if (tf.field == null) {
                return false;
            }
            int idf = cursor.getColumnIndex(tf.name);
            if (idf == -1) {
                return false;
            }
            try {
                Class<?> clazz = ((Class<?>) tf.type);
                if (clazz != null) {
                    if (clazz == ObservableInt.class) {
                        ObservableInt oi = (ObservableInt) tf.field.get(obj);
                        if (oi == null)
                            return false;
                        oi.set(cursor.getInt(idf));
                        return true;
                    } else if (clazz == ObservableLong.class) {
                        ObservableLong ol = (ObservableLong) tf.field.get(obj);
                        if (ol == null)
                            return false;
                        ol.set(cursor.getLong(idf));
                        return true;
                    } else if (clazz == ObservableBoolean.class) {
                        ObservableBoolean ob = (ObservableBoolean) tf.field.get(obj);
                        if (ob == null)
                            return false;
                        ob.set((boolean)(cursor.getInt(idf) > 0));
                        return true;
                    } else if (clazz == ObservableDouble.class) {
                        ObservableDouble od = (ObservableDouble) tf.field.get(obj);
                        if (od == null)
                            return false;
                        od.set(cursor.getDouble(idf));
                        return true;
                    } else if (clazz == ObservableFloat.class) {
                        ObservableFloat of = (ObservableFloat) tf.field.get(obj);
                        if (of == null)
                            return false;
                        of.set(cursor.getFloat(idf));
                        return true;
                    } else if (clazz == ObservableShort.class) {
                        ObservableShort of = (ObservableShort) tf.field.get(obj);
                        if (of == null)
                            return false;
                        of.set(cursor.getShort(idf));
                        return true;
                    } else if (clazz == ObservableChar.class) {
                        ObservableChar of = (ObservableChar) tf.field.get(obj);
                        if (of == null)
                            return false;
                        of.set((char) cursor.getInt(idf));
                        return true;
                    } else if (clazz == ObservableByte.class) {
                        ObservableByte of = (ObservableByte) tf.field.get(obj);
                        if (of == null)
                            return false;
                        of.set((byte) cursor.getInt(idf));
                        return true;
                    } else if (clazz == ObservableField.class) {

                        try {
                            Class<?> clz = (Class<?>) ((ParameterizedType) tf.field.getGenericType())
                                    .getActualTypeArguments()[0];

                            if (clz == String.class) {
                                do {
                                    ObservableField<String> os = (ObservableField<String>) tf.field.get(obj);
                                    if (os == null)
                                        break;
                                    os.set(cursor.getString(idf));
                                    return true;
                                } while (false);
                                return false;

                            } else if (clz == Date.class) {
                                do {
                                    SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                    String s = cursor.getString(idf);
                                    if (Text.isempty(s))
                                        break;
                                    ObservableField<Date> od = (ObservableField<Date>) tf.field.get(obj);
                                    if (od == null)
                                        break;
                                    od.set(fmt.parse(s));
                                    return true;
                                } while (false);
                                return false;
                            }
                        } catch (Exception ignore) {}
                    }
                }
            } catch (Exception ignore) {}

            if (tf.type == String.class)
                tf.field.set(obj, cursor.getString(idf));
            else if ((tf.type == Integer.class) || (tf.type == int.class))
                tf.field.set(obj, cursor.getInt(idf));
            else if ((tf.type == Long.class) || (tf.type == long.class))
                tf.field.set(obj, cursor.getLong(idf));
            else if ((tf.type == Float.class) || (tf.type == float.class))
                tf.field.set(obj, cursor.getFloat(idf));
            else if ((tf.type == Double.class) || (tf.type == double.class))
                tf.field.set(obj, cursor.getDouble(idf));
            else if ((tf.type == Boolean.class) || (tf.type == boolean.class))
                tf.field.set(obj, (boolean)(cursor.getInt(idf) > 0));
            else if (tf.type == Date.class) {
                try {
                    String s = cursor.getString(idf);
                    if (!Text.isempty(s)) {
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        tf.field.set(obj, format.parse(s));
                    } else {
                        tf.field.set(obj, null);
                    }
                    return true;
                } catch (Exception ignore) {
                    return false;
                }
            }
            else
                return false;
            return true;

        } catch (Exception e) {
            if (BuildConfig.DEBUG_DB) Log.e("- PRE setFieldGeneric", Text.requireString(e.getLocalizedMessage()), e);
            return false;
        }
    }
    private void setArrayGeneric(List<Object> list, Cursor cursor, RootTableHolder.RootTableObject rto) {
        try {
            int idf = cursor.getColumnIndex(rto.name);
            if (idf == -1) {
                return;
            }
            if (rto.type == String.class) {
                list.add(cursor.getString(idf));
            } else if ((rto.type == Integer.class) || (rto.type == int.class)) {
                list.add(cursor.getInt(idf));
            } else if ((rto.type == Long.class) || (rto.type == long.class)) {
                list.add(cursor.getLong(idf));
            } else if ((rto.type == Float.class) || (rto.type == float.class)) {
                list.add(cursor.getFloat(idf));
            } else if ((rto.type == Double.class) || (rto.type == double.class)) {
                list.add(cursor.getDouble(idf));
            } else if ((rto.type == Boolean.class) || (rto.type == boolean.class)) {
                list.add((boolean)(cursor.getInt(idf) > 0));
            } else if (rto.type == Date.class) {
                try {
                    String s = cursor.getString(idf);
                    if (!Text.isempty(s)) {
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        list.add(format.parse(s));
                    }
                } catch (Exception ignore) {}
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG_DB) Log.e("setArray", Text.requireString(e.getLocalizedMessage()), e);
        }
    }

    /// CREATE

    @Override
    public void createField(Type type, String name) throws Exception {
        if (sb.length() == 0)
            initCreateBegin(sb, sqlRoot.table);

        getDbType(sb, name, type);
    }
    @Override
    public void createObject(ReflectAttribute fa, String name) throws Exception {
        List<ActionDb.RootTableHolder.RootTableCreator> list = fa.DbCreate(dbMgr, true);
        if (list != null)
            sqlRoot.createTables.addAll(list);
    }
    @Override
    public void createArray(Type type, String name) throws Exception {
        StringBuilder sbTable = new StringBuilder();
        String sTable = getTableArrayName(type.toString(), name);
        initCreateBegin(sbTable, sTable);
        getDbType(sbTable, name, type);
        sbTable.append(
                String.format(
                        Locale.getDefault(),
                        ", FOREIGN KEY(`%s`) REFERENCES `%s`(`%s`) ON UPDATE NO ACTION ON DELETE CASCADE )",
                        ReflectAttribute.ID_PARENT,
                        sqlRoot.table,
                        ReflectAttribute.ID_INDEX
                )
        );
        sqlRoot.addTableCreator(sTable, sbTable.toString());
        addParentIndex(sTable);
        /* addParentUniqueIndex(sTable, name); */
    }
    @Override
    public void createFieldIndex(Type type, String name) throws Exception {

    }
    @Override
    public void foreignKey(IBaseTableReflect fk) {
        fKey = fk;
        if ((fk != null) && (!Text.isempty(fk.IThisTableName())))
            sqlRoot.table = fk.IThisTableName();
    }

    /// INSERT/UPDATE

    @Override
    public void to(Field field, Type type, Object val, String name) throws Exception {
        addByType(field, sqlRoot.contentSql, type, val, name);
    }

    @Override
    public void to(Field field, ArrayList<Object> array, String name) throws Exception {
        if ((array == null) || (array.size() == 0))
            return;

        try {
            Class<?> clazz = array.get(0).getClass();
            String table = getTableArrayName(clazz.getSimpleName(), name);
            for (Object val : array) {
                final ContentValues sqlVal = new ContentValues();
                addByType(field, sqlVal, ((Type) clazz), val, name);
                sqlRoot.addParentArray(table, sqlVal);
            }

        } catch (Exception ignore) {}
    }

    @Override
    public Object to(final Field field, ReflectAttribute fa, final String name, boolean skipAttr) throws Exception {
        if (skipRecursion && skipAttr)
            return null;
        sqlRoot.objectTables.add(fa);
        return null;
    }

    /// SELECT/LOAD

    @Override // Field create
    public Object from(Field field, Type type, String name) throws Exception {
        sqlRoot.addField(field, type, name);
        return null;
    }

    @Override // Object create
    public Object from(Field field, ReflectAttribute fa, String name) throws Exception {
        sqlRoot.addObject(field, fa, name);
        return null;
    }

    @Override // Array create
    public Object from(Field field, ReflectAttribute fa, Object obj, String name, boolean skipAttr) throws Exception {
        if (skipRecursion && skipAttr)
            return null;
        if (fa == null)
            sqlRoot.addArray(field, null, (Type) obj, name);
        else
            sqlRoot.addArray(field, fa, null, name);
        return null;
    }

    @Override // Array list create (this empty)
    public ArrayList<Object> from(Field field, String name) throws Exception {
        return null;
    }

    ///

    @Override
    public void Save(ReflectAttribute ra, ContentValues cv) {
        try {
            if (sqlRoot.isInsertEmpty()) {
                if (BuildConfig.DEBUG_DB) Log.e("- Db Save ", sqlRoot.table + " empty!");
                return;
            }
            DbManager.DbArguments dataArgs = new DbManager.DbArguments(
                    new DbManager.DbArguments.TableBase(sqlRoot.index, sqlRoot.parent, sqlRoot.table),
                    sqlRoot.fields,
                    sqlRoot.contentSql
            );
            dataArgs.setWhere(cv);
            long idx = dbMgr.insertOrUpdate(dataArgs);
            if (BuildConfig.DEBUG_DB) Log.e("--- IDX return: " + idx, "table=" + sqlRoot.table);
            if (idx <= 0)
                return;

            sqlRoot.index = idx;
            ra.dbIndex = sqlRoot.index;
            ra.dbParent = sqlRoot.parent;

            for (ReflectAttribute fa : sqlRoot.objectTables) {
                fa.toDb(dbMgr, idx);
            }
            for (RootTableHolder.ParentTableHolder p : sqlRoot.arrayTables) {
                DbManager.DbArguments tblArgs = new DbManager.DbArguments(
                        new DbManager.DbArguments.TableBase(-1L, idx, p.table),
                        sqlRoot.fields,
                        p.sqlVal
                );
                dbMgr.insertOrUpdate(tblArgs);
            }

        } catch (Exception e) {
            if (BuildConfig.DEBUG_DB) Log.e("Save", Text.requireString(e.getLocalizedMessage()), e);
        }
        sqlRoot.clear();
    }

    @Override
    public void Load(Object obj, ContentValues cv) {
        DbManager.DbArguments dataArgs = new DbManager.DbArguments(
                new DbManager.DbArguments.TableBase(sqlRoot.index, sqlRoot.parent, sqlRoot.table),
                sqlRoot.fields
        );
        dataArgs.setWhere(cv);
        Cursor cursor = dbMgr.fetch(dataArgs);
        if (cursor == null)
            return;

        try {
            /// Fields
            for (DbManager.DbArguments.TableField tf : sqlRoot.fields) {
                if (!setFieldGeneric(tf, cursor, obj)) {
                    int idf = cursor.getColumnIndex(tf.name);
                    if (idf == -1)
                        continue;

                    if (tf.name.equals(ReflectAttribute.ID_INDEX)) {
                        sqlRoot.index = cursor.getLong(idf);
                        ((ReflectAttribute) obj).dbIndex = sqlRoot.index;
                    } else if (tf.name.equals(ReflectAttribute.ID_PARENT)) {
                        sqlRoot.parent = cursor.getLong(idf);
                        ((ReflectAttribute) obj).dbParent = sqlRoot.parent;
                    }
                }
            }
        } finally {
            cursor.close();
            cursor = null;
        }

        /// Object
        for (RootTableHolder.RootTableObject o : sqlRoot.objects) {
            try {
                if ((o.ra != null) && (o.field != null)) {
                    o.ra.fromDb(dbMgr, sqlRoot.index);
                    o.field.set(obj, (Object) o.ra);
                }
            } catch (Exception e) {
                if (BuildConfig.DEBUG) Log.e("RootTableObject", Text.requireString(e.getLocalizedMessage()), e);
            }
        }

        /// Debug object list dump
        if (BuildConfig.DEBUG_DB) {
            Log.e("!!!", "-----------------------------------------");
            for (RootTableHolder.RootTableObject o : sqlRoot.arrays) {
                if (o.ra != null)
                    Log.e(" + CLASS " + o.ra.getClass().getSimpleName(), o.name);
                else if (o.type != null)
                    Log.e(" + TYPE  " + o.type.toString(), o.name);
            }
            Log.e("!!!", "-----------------------------------------");
        }

        /// Array object/generic
        for (RootTableHolder.RootTableObject o : sqlRoot.arrays) {
            try {
                sqlRoot.fields.clear();
                sqlRoot.table = null;

                if (o.ra != null) {
                    final IBaseTableReflect fk = o.ra.getClass().getAnnotation(IBaseTableReflect.class);
                    if (fk != null)
                        sqlRoot.table = fk.IThisTableName();
                } else if (o.type != null) {
                    final IBaseTableReflect fk = ((Class<?>) o.type).getAnnotation(IBaseTableReflect.class);
                    if (fk != null)
                        sqlRoot.table = fk.IThisTableName();
                    else
                        sqlRoot.table = getTableArrayName(o.type.toString(), o.name);
                } else {
                    continue;
                }

                if (Text.isempty(sqlRoot.table))
                    continue;

                sqlRoot.addField(null, null, ReflectAttribute.ID_INDEX);
                DbManager.DbArguments objArgs = new DbManager.DbArguments(
                        new DbManager.DbArguments.TableBase(-1, sqlRoot.index, sqlRoot.table),
                        sqlRoot.fields
                );

                if (BuildConfig.DEBUG_DB) Log.e("Load -> DbArguments", objArgs.toString());
                if ((cursor = dbMgr.fetch(objArgs)) == null)
                    continue;

                try {
                    List<Object> list = new ArrayList<>();
                    int idf = cursor.getColumnIndex(ReflectAttribute.ID_INDEX);
                    if (idf == -1)
                        continue;

                    int cnt = 1;
                    do {
                        try {

                            long id = cursor.getLong(idf);
                            if (id <= 0)
                                continue;

                            /// Array object
                            if (o.ra != null) {
                                ReflectAttribute ra = o.ra.getReflectByType(o.field.getGenericType());
                                if (ra != null) {
                                    ra.fromDb(dbMgr, -1, id);
                                    list.add(ra);
                                }

                            /// Array generic
                            } else if (o.type != null) {
                                setArrayGeneric(list, cursor, o);
                            }
                            if (BuildConfig.DEBUG_DB) Log.e("+ add to array items count", "" + cnt++);

                        } catch (Exception e) {
                            if (BuildConfig.DEBUG) Log.e("RootTableObject cursor", Text.requireString(e.getLocalizedMessage()), e);
                        }
                    } while (cursor.moveToNext());
                    o.field.set(obj, list);

                } finally {
                    cursor.close();
                    cursor = null;
                }

            } catch (Exception e) {
                if (BuildConfig.DEBUG) Log.e("RootTableObject", Text.requireString(e.getLocalizedMessage()), e);
            }
        }
        sqlRoot.clear();
    }

    @Override
    public List<RootTableHolder.RootTableCreator> Create(boolean isWriteScheme) {
        initCreateFinalize();
        if (sqlRoot.createTables.size() > 0)
            if (isWriteScheme) {
                return sqlRoot.createTables;
            } else {
                for (RootTableHolder.RootTableCreator rtc : sqlRoot.createTables) {
                    dbMgr.rawSql(rtc.sql);
                }
            }
        sqlRoot.clear();
        return null;
    }

    @Override
    public void Create(List<RootTableHolder.RootTableCreator> list) {
        if ((list == null) || (list.size() == 0))
            return;
        dbMgr.sqlSchemeWrite(list);
        if (BuildConfig.DEBUG) Log.d("- Dump all available Tables", dbMgr.utils.dumpAllTables());
    }

    @Override
    public void Delete(ReflectAttribute ra, ContentValues cv) {
        if (sqlRoot.index <= 0)
            return;
        if (Text.isempty(sqlRoot.table))
            throw new RuntimeException("Db delete, table name empty!");

        dbMgr.rawSql(
                String.format(
                        Locale.getDefault(),
                        ConstantDataDb.BaseDeleteRow,
                        sqlRoot.table,
                        ReflectAttribute.ID_INDEX,
                        sqlRoot.index
                )
        );
        sqlRoot.clear();
    }

    @Override
    public Object data() {
        return null;
    }

    ///

    public static final class RootTableHolder {
        String table;                                       // table name
        ContentValues contentSql = new ContentValues();
        final List<DbManager.DbArguments.TableField> fields = new ArrayList<>();
        final List<RootTableObject> objects = new ArrayList<>();
        final List<RootTableObject> arrays = new ArrayList<>();
        final List<ParentTableHolder> arrayTables = new ArrayList<>();
        final List<ReflectAttribute> objectTables = new ArrayList<>();
        final List<RootTableCreator> createTables = new ArrayList<>();
        protected long index;                               // Id
        protected long parent;                              // parent Id

        RootTableHolder(long idx, long p) {
            index = idx;
            parent = p;
            table = null;
            fields.add(new DbManager.DbArguments.TableField(null, long.class, ReflectAttribute.ID_INDEX));
            fields.add(new DbManager.DbArguments.TableField(null, long.class, ReflectAttribute.ID_PARENT));
        }
        void addField(Field field, Type type, String name) {
            fields.add(new DbManager.DbArguments.TableField(field, type, name));
        }
        void addObject(Field field, ReflectAttribute ra, String s) {
            objects.add(new RootTableObject(field, ra, null, s));
        }
        void addArray(Field field, ReflectAttribute ra, Type t, String s) {
            arrays.add(new RootTableObject(field, ra, t, s));
        }
        void addParentArray(String table, ContentValues cv) {
            arrayTables.add(new ParentTableHolder(table, cv));
        }
        void addTableCreator(String table, String sql) {
            createTables.add(new RootTableCreator(table, sql));
        }
        public boolean isInsertEmpty() {
            return ((contentSql == null) || (contentSql.size() == 0));
        }
        void clear() {
            contentSql.clear();
            fields.clear();
            objects.clear();
            arrays.clear();
            arrayTables.clear();
            objectTables.clear();
            createTables.clear();
        }

        public static final class RootTableCreator {
            public final String table;
            public final String sql;

            RootTableCreator(String tbl, String s) {
                table = tbl;
                sql = s;
            }
        }
        public static final class RootTableObject {
            public final String name;
            public final Field field;
            public final Type type;
            public final ReflectAttribute ra;

            RootTableObject(Field f, ReflectAttribute r, Type t, String s) {
                field = f;
                ra = r;
                type = t;
                name = s;
            }
        }
        public static final class ParentTableHolder {
            final String table;
            final ContentValues sqlVal;

            ParentTableHolder(String tbl, ContentValues cv) {
                table = tbl;
                sqlVal = cv;
            }
        }
    }
}
