package ru.ps.vlcatv.utils.reflect;

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
import ru.ps.vlcatv.utils.BuildConfig;
import ru.ps.vlcatv.utils.Text;
import ru.ps.vlcatv.utils.json.JSONObject;
import ru.ps.vlcatv.utils.json.JSONException;

public class ReflectAttribute implements Parcelable {

    private String prefixPreference = "";

    protected ReflectAttribute() {}
    protected ReflectAttribute(Parcel in) {
        fromParcelable(in);
    }
    protected void setPrefixPreference(String s) {
        prefixPreference = s;
    }

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
            if (BuildConfig.DEBUG) Log.e("toJson", e.getLocalizedMessage(), e);
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
            aj.Save();

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("fromJson", e.getLocalizedMessage(), e);
            throw new NullPointerException(e.getLocalizedMessage());
        }
    }
    public void toPreferences(SharedPreferences pref) {
        try {
            ActionPreferences ap = new ActionPreferences(pref, prefixPreference);
            iteratorTo(ap);
            ap.Save();

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("toPreferences", e.getLocalizedMessage(), e);
            throw new NullPointerException(e.getLocalizedMessage());
        }
    }
    public void fromPreferences(SharedPreferences pref) {
        try {
            ActionPreferences ap = new ActionPreferences(pref, prefixPreference);
            iteratorFrom(ap);

        } catch(Exception e){
            if (BuildConfig.DEBUG) Log.e("fromPreferences", e.getLocalizedMessage(), e);
            throw new NullPointerException(e.getLocalizedMessage());
        }
    }
    public void toParcelable(Parcel dest) {
        try {
            ActionParcelable ap = new ActionParcelable(dest);
            iteratorTo(ap);

        } catch(Exception e){
            if (BuildConfig.DEBUG) Log.e("toParcelable", e.getLocalizedMessage(), e);
            throw new NullPointerException(e.getLocalizedMessage());
        }
    }
    public void fromParcelable(Parcel in) {
        try {
            ActionParcelable ap = new ActionParcelable(in);
            iteratorFrom(ap);

        } catch(Exception e){
            if (BuildConfig.DEBUG) Log.e("fromParcelable", e.getLocalizedMessage(), e);
            throw new NullPointerException(e.getLocalizedMessage());
        }
    }

    ///
    /// To/From iterators
    ///

    private void iteratorTo(ActionInterface action) throws NullPointerException {
        try {

            for (Field field : getClass().getDeclaredFields()) {
                if (field.isSynthetic())
                    continue;

                field.setAccessible(true);
                Object obj = field.get(this);
                if (obj == null)
                    continue;

                final FieldReflect jf = field.getAnnotation(FieldReflect.class);
                if (jf == null) {
                    final ObjectReflect jo = field.getAnnotation(ObjectReflect.class);
                    if (jo == null) {
                        final ArrayReflect ja = field.getAnnotation(ArrayReflect.class);
                        if (ja == null)
                            continue;

                        actionArrayInterfaceTo(action, obj, ja.value());
                        continue;
                    }
                    actionObjectInterfaceTo(action, obj, field.getGenericType(), jo.value());
                    continue;
                }
                actionFieldInterfaceTo(action, obj, field.getGenericType(), jf.value());
            }

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("iteratorTo Exception", e.getLocalizedMessage(), e);
            throw new NullPointerException(e.getLocalizedMessage());
        }
    }
    private void iteratorFrom(ActionInterface action) throws NullPointerException {
        try {

            for (Field field : getClass().getDeclaredFields()) {
                if (field.isSynthetic())
                    continue;

                field.setAccessible(true);
                final FieldReflect jf = field.getAnnotation(FieldReflect.class);
                if (jf == null) {
                    final ObjectReflect jo = field.getAnnotation(ObjectReflect.class);
                    if (jo == null) {
                        final ArrayReflect ja = field.getAnnotation(ArrayReflect.class);
                        if (ja == null)
                            continue;

                        actionArrayInterfaceFrom(action, field, ja.value());
                        continue;
                    }
                    actionObjectInterfaceFrom(action, field, jo.value());
                    continue;
                }
                actionFieldInterfaceFrom(action, field, jf.value());
            }

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("iteratorFrom Exception", e.getLocalizedMessage(), e);
            throw new NullPointerException(e.getLocalizedMessage());
        }
    }

    ///
    /// To interface action
    ///

    private void actionFieldInterfaceTo(ActionInterface action, Object obj, Type type, String name) {
        try {
            if (!actionIsEmptyTo(obj, type))
                action.to(name, type, obj);

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("actionFieldInterfaceTo", e.getLocalizedMessage(), e);
        }
    }
    private void actionObjectInterfaceTo(ActionInterface action, Object obj, Type type, String name) {
        try {
            if (obj instanceof ReflectAttribute) {
                Object data = action.to((ReflectAttribute) obj);
                if (data != null)
                    action.to(name, type, data);
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("actionObjectInterfaceTo", e.getLocalizedMessage(), e);
        }
    }
    private void actionArrayInterfaceTo(ActionInterface action, Object obj, String name) {
        try {
            Iterable iterable = ((Iterable) obj);
            if (iterable == null)
                return;

            ArrayList<Object> abj = new ArrayList<>();
            for (Object o : iterable) {
                if (o instanceof ReflectAttribute) {
                    Object ao = action.to((ReflectAttribute) o);
                    if (ao != null)
                        abj.add(ao);
                } else {
                    if (!actionIsEmptyTo(o, o.getClass()))
                        abj.add(o);
                }
            }
            if (abj.size() > 0)
                action.to(name, abj);

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("actionArrayInterfaceTo", e.getLocalizedMessage(), e);
        }
    }
    private boolean actionIsEmptyTo(Object obj, Type type) {
        return  ((obj == null) ||
                  ((type == String.class) && (Text.isempty((String) obj))) ||
                    (((type == Integer.class) || (type == int.class)) && ((Integer) obj == -1)) ||
                      ((type == long.class) && ((long) obj == -1)));
    }

    ///
    /// From interface action
    ///

    private void actionFieldInterfaceFrom(ActionInterface action, Field field, String name) {
        try {
            Object data = action.from(name, field.getType());
            if (data != null)
                field.set(this, data);

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("actionFieldInterfaceFrom", e.getLocalizedMessage(), e);
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
            action.from((ReflectAttribute) instance, name);
            field.set(this, instance);

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("actionObjectInterfaceFrom", e.getLocalizedMessage(), e);
        }
    }
    private void actionArrayInterfaceFrom(ActionInterface action, Field field, String name) {
        try {
            Object obj = field.get(this);
            if (obj == null)
                return;

            Class<?> clazz = obj.getClass();
            if (!Iterable.class.isAssignableFrom(clazz))
                return;

            ArrayList<Object> aout = new ArrayList<>();
            ArrayList<Object> arr = action.from(name);
            for (Object a : arr) {
                Object o = actionGetArrayInstanceFrom(action, field.getGenericType(), a);
                if (o == null)
                    aout.add(a);
                else
                    aout.add(o);
            }
            field.set(this, aout);

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("actionArrayInterfaceFrom", e.getLocalizedMessage(), e);
        }
    }
    private Object actionGetArrayInstanceFrom(ActionInterface action, Type type, Object obj) throws Exception {

        Class<?> inner = (Class<?>) ((ParameterizedType) type)
                .getActualTypeArguments()[0];

        final ObjectReflect jo = inner.getAnnotation(ObjectReflect.class);
        if (jo == null)
            return null;

        Constructor<?> constructor;
        try {
            constructor = inner.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            return null;
        }
        if (Modifier.isPrivate(constructor.getModifiers()))
            return null;

        constructor.setAccessible(true);
        Object instance = constructor.newInstance();
        return action.from((ReflectAttribute) instance, obj);
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
