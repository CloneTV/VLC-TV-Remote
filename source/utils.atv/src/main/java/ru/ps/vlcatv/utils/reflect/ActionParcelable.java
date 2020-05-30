package ru.ps.vlcatv.utils.reflect;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.os.Parcel;
import android.util.Log;

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

import ru.ps.vlcatv.utils.BuildConfig;
import ru.ps.vlcatv.utils.Text;
import ru.ps.vlcatv.utils.reflect.annotation.ActionInterface;
import ru.ps.vlcatv.utils.reflect.annotation.IBaseTableReflect;

public class ActionParcelable implements ActionInterface {

    private Parcel par = null;

    ActionParcelable(Parcel p) {
        par = p;
    }
    private int boolToInt(boolean b) {
        return Boolean.compare(b, false);
    }
    private boolean intToBoolean(int i) {
        return (i != 0);
    }

    /// To

    @Override
    public void to(Field field, Type type, Object val, String name) throws Exception {

        try {
            Class<?> clazz = ((Class<?>)type);
            if (clazz == null)
                return;
            if (clazz == ObservableInt.class)
                par.writeInt((int) ((ObservableInt) val).get());
            else if (clazz == ObservableLong.class)
                par.writeLong((long) ((ObservableLong) val).get());
            else if (clazz == ObservableBoolean.class)
                par.writeInt(((boolean) ((ObservableBoolean) val).get()) ? 1 : 0);
            else if (clazz == ObservableDouble.class)
                par.writeDouble((double) ((ObservableDouble) val).get());
            else if (clazz == ObservableFloat.class)
                par.writeFloat((float) ((ObservableFloat) val).get());
            else if (clazz == ObservableShort.class)
                par.writeInt((short) ((ObservableShort) val).get());
            else if (clazz == ObservableChar.class)
                par.writeInt((char) ((ObservableChar) val).get());
            else if (clazz == ObservableByte.class)
                par.writeByte((byte)((ObservableByte) val).get());
            else if (type == String.class)
                par.writeString((String) val);
            else if ((type == Boolean.class) || (type == boolean.class))
                par.writeInt(boolToInt((boolean) val));
            else if ((type == Integer.class) || (type == int.class))
                par.writeInt((int) val);
            else if ((type == Long.class) || (type == long.class))
                par.writeLong((long) val);
            else if ((type == Float.class) || (type == float.class))
                par.writeFloat((float) val);
            else if ((type == Double.class) || (type == double.class))
                par.writeDouble((double) val);

        } catch (Exception ignore) {

            try {
                Class<?> clazz = (Class<?>) ((ParameterizedType) field.getGenericType())
                        .getActualTypeArguments()[0];

                if (clazz == null)
                    return;
                if (clazz == String.class) {
                    par.writeString((String)((ObservableField<String>) val).get());
                } else if (clazz == Date.class) {
                    @SuppressLint("SimpleDateFormat")
                    SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
                    Date date = (Date) ((ObservableField<Date>) val).get();
                    if (date != null)
                        par.writeString(fmt.format(date));
                }
            } catch (Exception ignored) {}
        }
    }
    @Override
    public void to(Field field, ArrayList<Object> array, String name) throws Exception {
        try {
            ArrayList<String> set = new ArrayList<>();
            for (Object o : array) {
                if (o.getClass() == String.class)
                    set.add((String) o);
            }
            if (set.size() > 0)
                par.writeList(set);

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("to Parcelable", Text.requireString(e.getLocalizedMessage()), e);
        }
    }
    @Override
    public Object to(Field field, ReflectAttribute fa, boolean skipAttr) throws Exception {
        fa.toParcelable(par);
        return null;
    }

    /// From

    @Override
    public Object from(Field field, Type type, String name) throws Exception {
        if (type == String.class) {
            return par.readString();
        } else if ((type == Boolean.class) || (type == boolean.class)) {
            return intToBoolean(par.readInt());
        } else if ((type == Integer.class) || (type == int.class)) {
            return par.readInt();
        } else if ((type == Long.class) || (type == long.class)) {
            return par.readLong();
        } else if ((type == Float.class) || (type == float.class)) {
            return par.readFloat();
        } else if ((type == Double.class) || (type == double.class)) {
            return par.readDouble();
        }
        return null;
    }

    @Override
    public Object from(Field field, ReflectAttribute fa, String name) throws Exception {
        fa.fromParcelable(par);
        return (Object) fa;
    }

    @Override
    public Object from(Field field, ReflectAttribute fa, Object obj, String name, boolean skipAttr) throws Exception {
        fa.fromParcelable(par);
        return (Object) fa;
    }

    @Override
    public ArrayList<Object> from(Field field, String name) throws Exception {
        try {
            @SuppressWarnings("unchecked")
            ArrayList<Object> array = par.readArrayList(String.class.getClassLoader());
            return array;

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("from Preferences", Text.requireString(e.getLocalizedMessage()), e);
        }
        return new ArrayList<Object>();
    }

    @Override
    public void createField(Type type, String name) throws Exception {

    }

    @Override
    public void createFieldIndex(Type type, String name) throws Exception {

    }

    @Override
    public void createObject(ReflectAttribute fa, String name) throws Exception {

    }

    @Override
    public void createArray(Type type, String name) throws Exception {

    }

    @Override
    public void foreignKey(IBaseTableReflect fk) {

    }

    @Override
    public Object data() {
        return null;
    }

    @Override
    public void Save(ReflectAttribute ra, ContentValues cv) {}

    @Override
    public void Load(Object obj, ContentValues cv) {

    }

    @Override
    public List<ActionDb.RootTableHolder.RootTableCreator> Create(boolean ignore) {
        return null;
    }

    @Override
    public void Create(List<ActionDb.RootTableHolder.RootTableCreator> list) {
    }

    @Override
    public void Delete(ReflectAttribute ra, ContentValues cv) {
    }
}
