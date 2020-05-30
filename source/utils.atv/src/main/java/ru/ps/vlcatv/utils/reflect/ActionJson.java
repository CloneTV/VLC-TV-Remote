package ru.ps.vlcatv.utils.reflect;

import android.content.ContentValues;

import androidx.databinding.Observable;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.ps.vlcatv.utils.BuildConfig;
import ru.ps.vlcatv.utils.Log;
import ru.ps.vlcatv.utils.json.JSONObject;
import ru.ps.vlcatv.utils.json.JSONArray;
import ru.ps.vlcatv.utils.reflect.annotation.ActionInterface;
import ru.ps.vlcatv.utils.reflect.annotation.IBaseTableReflect;

@SuppressWarnings("ConstantConditions")
public class ActionJson implements ActionInterface {
    private JSONObject obj = null;
    ActionJson() {
        obj = new JSONObject();
    }
    ActionJson(JSONObject o) {
        obj = o;
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
    public Object to(Field field, ReflectAttribute fa, boolean skipAttr) throws Exception {
        JSONObject o = fa.toJson();
        if ((o == null) || (o.length() == 0))
            return null;
        return o;
    }

    /// From

    @Override
    public Object from(Field field, Type type, String name) throws Exception {
        if (obj != null)
            return obj.opt(name);
        return null;
    }
    @Override
    public Object from(Field field, ReflectAttribute fa, String name) throws Exception {
        JSONObject jo = obj.optJSONObject(name);
        if (jo != null) {
            fa.fromJson(jo);
            return (Object) fa;
        }
        return null;
    }
    @Override
    public Object from(Field field, ReflectAttribute fa, Object obj, String name, boolean skipAttr) throws Exception {
        if (obj == null)
            return (Object) fa;
        fa.fromJson((JSONObject) obj);
        return (Object) fa;
    }
    @Override
    public ArrayList<Object> from(Field field, String name) throws Exception {
        if (obj == null)
            return null;

        JSONArray arr = obj.optJSONArray(name);
        if (arr == null)
            return null;

        ArrayList<Object> array = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++)
            array.add(arr.get(i));
        return array;
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

