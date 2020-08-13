package ru.ps.vlcatv.utils.reflect;

import android.content.ContentValues;
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

import ru.ps.vlcatv.utils.Text;
import ru.ps.vlcatv.utils.json.JSONObject;
import ru.ps.vlcatv.utils.json.JSONArray;
import ru.ps.vlcatv.utils.reflect.annotation.ActionInterface;
import ru.ps.vlcatv.utils.reflect.annotation.IBaseTableReflect;

@SuppressWarnings("ConstantConditions")
public class ActionJson implements ActionInterface {
    private final JSONObject obj;
    private ReflectAttribute rfa;
    ActionJson() {
        obj = new JSONObject();
        rfa = null;
    }
    ActionJson(ReflectAttribute ra, JSONObject o) {
        rfa = ra;
        obj = o;
    }

    private boolean setFieldObservable_(Field field, Type type, Object val) {
        if (rfa == null)
            return false;
        try {
            Class<?> clazz = ((Class<?>) type);
            if (clazz != null) {

                if (clazz == ObservableInt.class) {
                    final ObservableInt oe;
                    if ((oe = (ObservableInt) field.get(rfa)) != null)
                        oe.set((Integer) val);

                } else if (clazz == ObservableLong.class) {
                    final ObservableLong oe;
                    if ((oe = (ObservableLong) field.get(rfa)) != null)
                        oe.set((Long) val);

                } else if (clazz == ObservableBoolean.class) {
                    final ObservableBoolean oe;
                    if ((oe = (ObservableBoolean) field.get(rfa)) != null)
                        oe.set((Boolean) val);

                } else if (clazz == ObservableDouble.class) {
                    final ObservableDouble oe;
                    if ((oe = (ObservableDouble) field.get(rfa)) != null)
                        oe.set((Double) val);

                } else if (clazz == ObservableFloat.class) {
                    final ObservableFloat oe;
                    if ((oe = (ObservableFloat) field.get(rfa)) != null)
                        oe.set((Float) val);

                } else if (clazz == ObservableShort.class) {
                    final ObservableShort oe;
                    if ((oe = (ObservableShort) field.get(rfa)) != null)
                        oe.set((Short) val);

                } else if (clazz == ObservableChar.class) {
                    final ObservableChar oe;
                    if ((oe = (ObservableChar) field.get(rfa)) != null)
                        oe.set((char) val);

                } else if (clazz == ObservableByte.class) {
                    final ObservableByte oe;
                    if ((oe = (ObservableByte) field.get(rfa)) != null)
                        oe.set((Byte) val);

                } else if (clazz == ObservableField.class) {

                    try {
                        Class<?> clz = (Class<?>) ((ParameterizedType) field.getGenericType())
                                .getActualTypeArguments()[0];

                        if (clz == String.class) {
                            final ObservableField<String> oe;
                            if ((oe = (ObservableField<String>) field.get(rfa)) != null)
                                oe.set((String) val);

                        } else if (clz == Date.class) {
                            final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            final String s = (String) val;
                            if (Text.isempty(s))
                                return true;
                            final ObservableField<Date> oe;
                            if ((oe = (ObservableField<Date>) field.get(rfa)) != null)
                                oe.set(fmt.parse(s));
                        }
                    } catch (Exception ignore) {}
                } else {
                    return false;
                }
            }
        } catch (Exception ignore) { return false; }
        return true;
    }
    private void setFieldGeneric_(Field field, Type type, Object val) {
        if (rfa == null)
            return;
        try {
            if (type == String.class)
                field.set(rfa, (String)val);
            else if ((type == Integer.class) || (type == int.class))
                field.set(rfa, (Integer)val);
            else if ((type == Long.class) || (type == long.class))
                field.set(rfa, (Long)val);
            else if ((type == Float.class) || (type == float.class))
                field.set(rfa, (Float)val);
            else if ((type == Double.class) || (type == double.class))
                field.set(rfa, (Double)val);
            else if ((type == Boolean.class) || (type == boolean.class))
                field.set(rfa, (boolean)val);
            else if (type == Date.class) {
                try {
                    final String s = (String) val;
                    if (!Text.isempty(s)) {
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        field.set(rfa, format.parse(s));
                    } else {
                        field.set(rfa, null);
                    }
                } catch (Exception ignore) {}
            }
        } catch (Exception ignore) {}
    }

    /// To

    @Override
    public void to(Field field, Type type, Object val, String name) throws Exception {
        if (obj == null)
            return;

        try {
            Class<?> clazz = ((Class<?>)type);
            if (clazz == null)
                return;
            if (clazz == ObservableInt.class)
                obj.put(name, (int) ((ObservableInt) val).get());
            else if (clazz == ObservableLong.class)
                obj.put(name, (long) ((ObservableLong) val).get());
            else if (clazz == ObservableBoolean.class)
                obj.put(name, (boolean) ((ObservableBoolean) val).get());
            else if (clazz == ObservableDouble.class)
                obj.put(name, (double) ((ObservableDouble) val).get());
            else if (clazz == ObservableFloat.class)
                obj.put(name, (float) ((ObservableFloat) val).get());
            else if (clazz == ObservableShort.class)
                obj.put(name, (short) ((ObservableShort) val).get());
            else if (clazz == ObservableChar.class)
                obj.put(name, (char) ((ObservableChar) val).get());
            else if (clazz == ObservableByte.class)
                obj.put(name, (byte) ((ObservableByte) val).get());
            else
                obj.put(name, val);

        } catch (Exception ignore) {
            try {
                Class<?> clazz = (Class<?>) ((ParameterizedType) field.getGenericType())
                        .getActualTypeArguments()[0];

                if (clazz == null)
                    return;
                if (clazz == String.class)
                    obj.put(name, (String) ((ObservableField<String>) val).get());
                else if (clazz == Date.class)
                    obj.put(name, (Date) ((ObservableField<Date>) val).get());
            } catch (Exception ignored) {}
        }
    }
    @Override
    public void to(Field field, ArrayList<Object> array, String name) throws Exception {
        if (obj != null) {
            JSONArray arr = new JSONArray(array);
            obj.put(name, arr);
        }
    }
    @Override
    public Object to(final Field field, ReflectAttribute fa, final String name, boolean skipAttr) throws Exception {
        JSONObject o = fa.toJson();
        if ((o == null) || (o.length() == 0))
            return null;
        return o;
    }

    /// From

    @Override  // Field create
    public Object from(Field field, Type type, String name) throws Exception {
        try {
            if (obj != null) {
                final Object val = obj.opt(name);
                if (!setFieldObservable_(field, type, val))
                    setFieldGeneric_(field, type, val);
            }
        } catch (Exception ignore) {}
        return null;
    }
    @Override   // Object create
    public Object from(Field field, ReflectAttribute fa, String name) throws Exception {
        try {
            JSONObject jo = obj.optJSONObject(name);
            if (jo != null) {
                fa.fromJson(jo);
                return (Object) fa;
            }
        } catch (Exception ignore) {}
        return null;
    }
    @Override   // Array create
    public Object from(Field field, ReflectAttribute fa, Object obj, String name, boolean skipAttr) throws Exception {
        try {
            if (obj == null)
                return (Object) fa;

            fa.fromJson((JSONObject) obj);
        } catch (Exception ignore) {}
        return (Object) fa;
    }
    @Override  // Array list create
    public ArrayList<Object> from(Field field, String name) throws Exception {
        try {
            if (obj == null)
                return null;

            JSONArray arr = obj.optJSONArray(name);
            if (arr == null)
                return null;

            ArrayList<Object> array = new ArrayList<>();
            for (int i = 0; i < arr.length(); i++)
                array.add(arr.get(i));
            return array;
        } catch (Exception ignore) {}
        return null;
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
        return obj;
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

