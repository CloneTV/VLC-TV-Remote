package ru.ps.vlcatv.utils.reflect;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ru.ps.vlcatv.utils.BuildConfig;
import ru.ps.vlcatv.utils.Text;
import ru.ps.vlcatv.utils.db.DbManager;
import ru.ps.vlcatv.utils.json.JSONObject;
import ru.ps.vlcatv.utils.json.JSONException;
import ru.ps.vlcatv.utils.reflect.annotation.ActionInterface;
import ru.ps.vlcatv.utils.reflect.annotation.IArrayReflect;
import ru.ps.vlcatv.utils.reflect.annotation.IBaseTableReflect;
import ru.ps.vlcatv.utils.reflect.annotation.IDbIndexReflect;
import ru.ps.vlcatv.utils.reflect.annotation.IFieldReflect;
import ru.ps.vlcatv.utils.reflect.annotation.IObjectReflect;

public class ReflectAttribute implements Parcelable {

    public static final String EXCEPT_RUN = "don't use this class for save Db!";
    public static final String ID_INDEX = "idx";
    public static final String ID_PARENT = "id_parent";

    private String prefixName = "";

    protected ReflectAttribute() {}
    protected ReflectAttribute(Parcel in) {
        fromParcelable(in);
    }
    protected void setPrefixName(String s) {
        prefixName = s;
    }
    /* required for DB* method, this table name */

    /// for DB

    @IDbIndexReflect(ReflectAttribute.ID_INDEX)
    public long dbIndex = -1;
    @IDbIndexReflect(ReflectAttribute.ID_PARENT)
    public long dbParent = -1;

    ///

    public String toJsonString() throws NullPointerException {
        JSONObject obj = toJson();
        if (obj == null)
            throw new NullPointerException("JSONObject is null");
        return obj.toString();
    }
    public JSONObject toJson() {
        try {

            ActionJson aj = new ActionJson();
            iteratorTo(aj);
            return (JSONObject)aj.data();

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("toJson", Text.requireString(e.getLocalizedMessage()), e);
        }
        return null;
    }
    public void fromJson(String s) throws NullPointerException, JSONException {
        fromJson(new JSONObject(s));
    }
    public void fromJson(JSONObject obj) throws NullPointerException {
        try {

            ActionJson aj = new ActionJson(obj);
            iteratorFrom(aj);
            aj.Save(this, null);

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("fromJson", Text.requireString(e.getLocalizedMessage()), e);
            throw new NullPointerException(e.getLocalizedMessage());
        }
    }
    public void toPreferences(SharedPreferences pref) {
        try {
            ActionPreferences ap = new ActionPreferences(pref, prefixName, this);
            iteratorTo(ap);
            ap.Save(this, null);

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("toPreferences", Text.requireString(e.getLocalizedMessage()), e);
            throw new NullPointerException(e.getLocalizedMessage());
        }
    }
    public void fromPreferences(SharedPreferences pref) {
        try {
            ActionPreferences ap = new ActionPreferences(pref, prefixName, this);
            iteratorFrom(ap);

        } catch(Exception e) {
            if (BuildConfig.DEBUG) Log.e("fromPreferences", Text.requireString(e.getLocalizedMessage()), e);
            throw new NullPointerException(e.getLocalizedMessage());
        }
    }
    public void toParcelable(Parcel dest) {
        try {
            ActionParcelable ap = new ActionParcelable(dest);
            iteratorTo(ap);

        } catch(Exception e) {
            if (BuildConfig.DEBUG) Log.e("toParcelable", Text.requireString(e.getLocalizedMessage()), e);
            throw new NullPointerException(e.getLocalizedMessage());
        }
    }
    public void fromParcelable(Parcel in) {
        try {
            ActionParcelable ap = new ActionParcelable(in);
            iteratorFrom(ap);

        } catch(Exception e) {
            if (BuildConfig.DEBUG) Log.e("fromParcelable", Text.requireString(e.getLocalizedMessage()), e);
            throw new NullPointerException(e.getLocalizedMessage());
        }
    }
    //
    public void DbCreateDump(DbManager dbm) {
        try {
            List<ActionDb.RootTableHolder.RootTableCreator> list = DbCreate(dbm, true);
            ActionDb ap = new ActionDb(dbm, prefixName, dbIndex, dbParent, false);
            ap.Create(list);

        } catch(Exception e) {
            if (BuildConfig.DEBUG) Log.e("DbCreate", Text.requireString(e.getLocalizedMessage()), e);
            throw new NullPointerException(e.getLocalizedMessage());
        }

    }
    //
    public void DbCreate(DbManager dbm) {
        DbCreate(dbm, false);
    }
    public List<ActionDb.RootTableHolder.RootTableCreator> DbCreate(DbManager dbm, boolean isWriteScheme) {
        try {
            ActionDb ap = new ActionDb(dbm, prefixName, dbIndex, dbParent, false);
            iteratorCreate(ap);
            return ap.Create(isWriteScheme);

            /*
            if (isWriteScheme) {
                List<ActionDb.RootTableHolder.RootTableCreator> list = ap.Create(true);
                ap.Create(list);
                return list;
            } else {
                return ap.Create(false);
            }
            */

        } catch(Exception e) {
            if (BuildConfig.DEBUG) Log.e("DbCreate", Text.requireString(e.getLocalizedMessage()), e);
            throw new NullPointerException(Text.requireString(e.getLocalizedMessage()));
        }
    }
    //
    public void DbDelete(DbManager dbm) { DbDelete(dbm, null); }
    public void DbDelete(DbManager dbm, ContentValues cv) {
        try {
            ActionDb ap = new ActionDb(dbm, prefixName, dbIndex, dbParent, false);
            iteratorDelete(ap);
            ap.Delete(this, cv);

        } catch(Exception e) {
            if (BuildConfig.DEBUG) Log.e("DbDelete", Text.requireString(e.getLocalizedMessage()), e);
            throw new NullPointerException(Text.requireString(e.getLocalizedMessage()));
        }
    }
    //
    public void toDb(DbManager dbm) {
        toDb(dbm, dbParent, null, false);
    }
    public void toDb(DbManager dbm, boolean skipRecursion) {
        toDb(dbm, dbParent, null, skipRecursion);
    }
    public void toDb(DbManager dbm, long parent) {
        toDb(dbm, parent, null, false);
    }
    public void toDb(DbManager dbm, long parent, boolean skipRecursion) {
        toDb(dbm, parent, null, skipRecursion);
    }
    public void toDb(DbManager dbm, long parent, ContentValues cv) {
        toDb(dbm, parent, cv, false);
    }
    public void toDb(DbManager dbm, long parent, ContentValues cv, boolean skipRecursion) {
        try {
            ActionDb ap = new ActionDb(dbm, prefixName, dbIndex, parent, skipRecursion);
            iteratorTo(ap);
            ap.Save(this, cv);

        } catch(Exception e) {
            if (BuildConfig.DEBUG) Log.e("toDb", Text.requireString(e.getLocalizedMessage()), e);
            throw new NullPointerException(e.getLocalizedMessage());
        }
    }
    //
    public void fromDb(DbManager dbm) {
        fromDb(dbm, dbParent, dbIndex, null, false);
    }
    public void fromDb(DbManager dbm, ContentValues cv) {
        fromDb(dbm, dbParent, dbIndex, cv, false);
    }
    public void fromDb(DbManager dbm, ContentValues cv, boolean skipRecursion) {
        fromDb(dbm, dbParent, dbIndex, cv, skipRecursion);
    }
    public void fromDb(DbManager dbm, long parent) {
        fromDb(dbm, parent, dbIndex, null, false);
    }
    public void fromDb(DbManager dbm, long parent, long id) {
        fromDb(dbm, parent, id, null, false);
    }
    public void fromDb(DbManager dbm, long parent, long id, boolean skipRecursion) {
        fromDb(dbm, parent, id, null, skipRecursion);
    }
    public void fromDb(DbManager dbm, long parent, long id, ContentValues cv, boolean skipRecursion) {
        try {
            dbIndex = id;
            ActionDb ap = new ActionDb(dbm, prefixName, id, parent, skipRecursion);
            iteratorFrom(ap);
            ap.Load(this, cv);

        } catch(Exception e) {
            if (BuildConfig.DEBUG) Log.e("fromDb", Text.requireString(e.getLocalizedMessage()), e);
            throw new NullPointerException(e.getLocalizedMessage());
        }
    }

    ///
    /// To/From iterators
    ///

    private void iteratorTo(ActionInterface action) throws NullPointerException {
        try {
            final IBaseTableReflect fk = getClass().getAnnotation(IBaseTableReflect.class);
            if (fk != null)
                action.foreignKey(fk);

            for (Field field : getClass().getDeclaredFields()) {
                if (field.isSynthetic())
                    continue;

                field.setAccessible(true);
                Object obj = field.get(this);
                if (obj == null)
                    continue;

                final IFieldReflect jf = field.getAnnotation(IFieldReflect.class);
                if (jf == null) {
                    final IObjectReflect jo = field.getAnnotation(IObjectReflect.class);
                    if (jo == null) {
                        final IArrayReflect ja = field.getAnnotation(IArrayReflect.class);
                        if (ja == null) {
                            final IDbIndexReflect ji = field.getAnnotation(IDbIndexReflect.class);
                            if (ji == null)
                                continue;

                            actionFieldInterfaceTo(action, field, obj, field.getGenericType(), ji.value());
                            continue;
                        }
                        actionArrayInterfaceTo(action, field, obj, ja.value(), ja.SkipRecursion());
                        continue;
                    }
                    actionObjectInterfaceTo(action, field, obj, field.getGenericType(), jo.value());
                    continue;
                }
                actionFieldInterfaceTo(action, field, obj, field.getGenericType(), jf.value());
            }

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("iteratorTo Exception", Text.requireString(e.getLocalizedMessage()), e);
            throw new NullPointerException(e.getLocalizedMessage());
        }
    }
    private void iteratorFrom(ActionInterface action) throws NullPointerException {
        try {
            final IBaseTableReflect fk = getClass().getAnnotation(IBaseTableReflect.class);
            if (fk != null)
                action.foreignKey(fk);

            for (Field field : getClass().getDeclaredFields()) {
                if (field.isSynthetic())
                    continue;

                field.setAccessible(true);
                final IFieldReflect jf = field.getAnnotation(IFieldReflect.class);
                if (jf == null) {
                    final IObjectReflect jo = field.getAnnotation(IObjectReflect.class);
                    if (jo == null) {
                        final IArrayReflect ja = field.getAnnotation(IArrayReflect.class);
                        if (ja == null) {
                            final IDbIndexReflect ji = field.getAnnotation(IDbIndexReflect.class);
                            if (ji == null)
                                continue;

                            actionFieldInterfaceFrom(action, field, ji.value());
                            continue;
                        }
                        actionArrayInterfaceFrom(action, field, ja.value(), ja.SkipRecursion());
                        continue;
                    }
                    actionObjectInterfaceFrom(action, field, jo.value());
                    continue;
                }
                actionFieldInterfaceFrom(action, field, jf.value());
            }

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("iteratorFrom Exception", Text.requireString(e.getLocalizedMessage()), e);
            throw new NullPointerException(e.getLocalizedMessage());
        }
    }
    private void iteratorCreate(ActionInterface action) throws NullPointerException {
        try {
            final IBaseTableReflect fk = getClass().getAnnotation(IBaseTableReflect.class);
            if (fk != null)
                action.foreignKey(fk);

            for (Field field : getClass().getDeclaredFields()) {
                if (field.isSynthetic())
                    continue;

                field.setAccessible(true);
                final IFieldReflect jf = field.getAnnotation(IFieldReflect.class);
                if (jf == null) {
                    final IObjectReflect jo = field.getAnnotation(IObjectReflect.class);
                    if (jo == null) {
                        final IArrayReflect ja = field.getAnnotation(IArrayReflect.class);
                        if (ja == null) {
                            final IDbIndexReflect ji = field.getAnnotation(IDbIndexReflect.class);
                            if (ji == null)
                                continue;

                            actionFieldInterfaceCreate(action, field, ji.value(), true);
                            continue;
                        }
                        actionArrayInterfaceCreate(action, field, ja.value());
                        continue;
                    }
                    actionObjectInterfaceCreate(action, field, jo.value());
                    continue;
                }
                actionFieldInterfaceCreate(action, field, jf.value(), false);
            }

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("iteratorCreate Exception", Text.requireString(e.getLocalizedMessage()), e);
            throw new NullPointerException(e.getLocalizedMessage());
        }
    }

    private void iteratorDelete(ActionInterface action) throws NullPointerException {
        try {
            final IBaseTableReflect fk = getClass().getAnnotation(IBaseTableReflect.class);
            if (fk != null)
                action.foreignKey(fk);

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("iteratorDelete Exception", Text.requireString(e.getLocalizedMessage()), e);
            throw new NullPointerException(e.getLocalizedMessage());
        }
    }
    ///
    /// To interface action
    ///

    private void actionFieldInterfaceTo(ActionInterface action, Field field, Object obj, Type type, String name) {
        try {
            if (!actionIsEmptyTo(obj, type))
                action.to(field, type, obj, name);

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("actionFieldInterfaceTo", Text.requireString(e.getLocalizedMessage()), e);
        }
    }
    private void actionObjectInterfaceTo(ActionInterface action, Field field, Object obj, Type type, String name) {
        try {
            if (obj instanceof ReflectAttribute) {
                Object data = action.to(field, (ReflectAttribute) obj, false);
                if (data != null)
                    action.to(field, type, data, name);
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("actionObjectInterfaceTo", Text.requireString(e.getLocalizedMessage()), e);
        }
    }
    @SuppressWarnings("rawtypes")
    private void actionArrayInterfaceTo(ActionInterface action, Field field, Object obj, String name, boolean skipRecursive) {
        try {
            Iterable iterable;
            try {
                iterable = ((Iterable) obj);
                if (iterable == null)
                    return;
            } catch (Exception ignore) {
                return;
            }
            ArrayList<Object> abj = new ArrayList<>();
            for (Object o : iterable) {
                if (o instanceof ReflectAttribute) {
                    Object ao = action.to(field, (ReflectAttribute) o, skipRecursive);
                    if (ao != null)
                        abj.add(ao);
                } else {
                    if ((!skipRecursive) && (!actionIsEmptyTo(o, o.getClass())))
                        abj.add(o);
                }
            }
            if (abj.size() > 0)
                action.to(field, abj, name);

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("actionArrayInterfaceTo", Text.requireString(e.getLocalizedMessage()), e);
        }
    }
    private boolean actionIsEmptyTo(Object obj, Type type) {
        return  ((obj == null) ||
                 ((type == String.class) && (Text.isempty((String) obj))) ||
                  (((type == Integer.class) || (type == int.class)) && ((Integer) obj == -1)) ||
                   ((type == Long.class) && ((long) obj == -1)));
    }

    ///
    /// From interface action
    ///

    private void actionFieldInterfaceFrom(ActionInterface action, Field field, String name) {
        try {
            Object data = action.from(field, field.getType(), name);
            if (data != null)
                field.set(this, data);

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("actionFieldInterfaceFrom", Text.requireString(e.getLocalizedMessage()), e);
        }
    }
    private void actionObjectInterfaceFrom(ActionInterface action, Field field, String name) {
        try {

            Object obj = field.get(this);
            if (obj == null)
                return;

            Class<?> clazz = obj.getClass();
            Constructor<?> constructor;
            try {
                constructor = clazz.getDeclaredConstructor();
            } catch (NoSuchMethodException e) {
                return;
            }
            if (Modifier.isPrivate(constructor.getModifiers()))
                return;

            constructor.setAccessible(true);
            Object instance = constructor.newInstance();
            action.from(field, (ReflectAttribute) instance, name);
            field.set(this, instance);

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("actionObjectInterfaceFrom", Text.requireString(e.getLocalizedMessage()), e);
        }
    }
    private void actionArrayInterfaceFrom(ActionInterface action, Field field, String name, boolean skipRecursive) {
        try {
            Object obj = field.get(this);
            if (obj == null)
                return;

            Class<?> clazz = obj.getClass();
            if (!Iterable.class.isAssignableFrom(clazz))
                return;

            ArrayList<Object> aout = new ArrayList<>();
            ArrayList<Object> arr = action.from(field, name);
            if (arr != null) {
                for (Object a : arr) {
                    if (a != null) {
                        Object o = null;
                        try {
                            ReflectAttribute fa = actionGetReflectByType(field.getGenericType());
                            if (fa != null)
                                o = action.from(field, fa, a, name, skipRecursive);
                        } catch (Exception ignore) {}

                        if (o != null)
                            aout.add(o);
                        else
                            aout.add(a);
                    }
                }
            } else {
                ReflectAttribute fa = actionGetReflectByType(field.getGenericType());
                if (fa != null)
                    action.from(field, fa, null, name, skipRecursive);
                else {
                    Class<?> type = (Class<?>) ((ParameterizedType) field.getGenericType())
                            .getActualTypeArguments()[0];
                    if (type != null)
                        action.from(field, null, type, name, skipRecursive);
                }

            }
            field.set(this, aout);

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("actionArrayInterfaceFrom", Text.requireString(e.getLocalizedMessage()), e);
        }
    }

    ///
    /// Create interface action
    ///
    private void actionFieldInterfaceCreate(ActionInterface action, Field field, String name, boolean isIndex) {
        try {
            if (isIndex)
                action.createFieldIndex(field.getType(), name);
            else
                action.createField(field.getType(), name);

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("actionFieldInterfaceCreate", Text.requireString(e.getLocalizedMessage()), e);
        }
    }
    private void actionObjectInterfaceCreate(ActionInterface action, Field field, String name) {
        try {

            Object obj = field.get(this);
            if (obj == null)
                return;

            Class<?> clazz = obj.getClass();
            Constructor<?> constructor;
            try {
                constructor = clazz.getDeclaredConstructor();
            } catch (NoSuchMethodException e) {
                return;
            }
            if (Modifier.isPrivate(constructor.getModifiers()))
                return;

            constructor.setAccessible(true);
            Object instance = constructor.newInstance();
            action.createObject((ReflectAttribute) instance, name);

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("actionObjectInterfaceCreate", Text.requireString(e.getLocalizedMessage()), e);
        }
    }
    private void actionArrayInterfaceCreate(ActionInterface action, Field field, String name) {
        try {
            Object obj = field.get(this);
            if (obj == null)
                return;

            Class<?> clazz = obj.getClass();
            if (!Iterable.class.isAssignableFrom(clazz))
                return;

            ReflectAttribute fa = actionGetReflectByType(field.getGenericType());
            if (fa != null) {
                action.createObject(fa, null);
            } else {
                Class<?> type = (Class<?>) ((ParameterizedType) field.getGenericType())
                        .getActualTypeArguments()[0];
                action.createArray(type, name);
            }

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("actionArrayInterfaceCreate", Text.requireString(e.getLocalizedMessage()), e);
        }
    }

    /// Get Reflect by type

    public ReflectAttribute getReflectByType(Type type) {
        try {
            return actionGetReflectByType(type);
        } catch (Exception ignore) {
            return null;
        }
    }

    private ReflectAttribute actionGetReflectByType(Type type) throws Exception {

        Class<?> inner = (Class<?>) ((ParameterizedType) type)
                .getActualTypeArguments()[0];

        try {
            if (inner.getSuperclass() != ReflectAttribute.class)
                return null;
        } catch (Exception ignore) { return null; }

        Constructor<?> constructor;
        try {
            constructor = inner.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            return null;
        }
        if (Modifier.isPrivate(constructor.getModifiers()))
            return null;

        constructor.setAccessible(true);
        return (ReflectAttribute) constructor.newInstance();
    }

    /// Parcelable

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        toParcelable(dest);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ReflectAttribute> CREATOR = new Creator<ReflectAttribute>() {
        @Override
        public ReflectAttribute createFromParcel(Parcel in) {
            return new ReflectAttribute(in);
        }

        @Override
        public ReflectAttribute[] newArray(int size) {
            return new ReflectAttribute[size];
        }
    };

}
