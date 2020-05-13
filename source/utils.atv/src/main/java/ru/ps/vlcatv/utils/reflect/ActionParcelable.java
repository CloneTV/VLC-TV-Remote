package ru.ps.vlcatv.utils.reflect;

import android.os.Parcel;
import android.util.Log;
import java.lang.reflect.Type;
import java.util.ArrayList;

import ru.ps.vlcatv.utils.BuildConfig;

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
    public void to(String name, Type type, Object val) throws Exception {
        if (type == String.class) {
            par.writeString((String) val);
        } else if ((type == Boolean.class) || (type == boolean.class)) {
            par.writeInt(boolToInt((boolean)val));
        } else if ((type == Integer.class) || (type == int.class)) {
            par.writeInt((Integer) val);
        } else if ((type == Long.class) || (type == long.class)) {
            par.writeLong((long) val);
        } else if ((type == Float.class) || (type == float.class)) {
            par.writeFloat((float) val);
        } else if ((type == Double.class) || (type == double.class)) {
            par.writeDouble((double) val);
        }
    }
    @Override
    public void to(String name, ArrayList<Object> array) throws Exception {
        try {
            ArrayList<String> set = new ArrayList<>();
            for (Object o : array) {
                if (o.getClass() == String.class)
                    set.add((String) o);
            }
            if (set.size() > 0)
                par.writeList(set);

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("to Parcelable", e.getLocalizedMessage(), e);
        }
    }
    @Override
    public Object to(ReflectAttribute fa) throws Exception {
        fa.toParcelable(par);
        return null;
    }

    /// From

    @Override
    public Object from(String name, Type type) throws Exception {
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
    public Object from(ReflectAttribute fa, String name) throws Exception {
        fa.fromParcelable(par);
        return (Object) fa;
    }

    @Override
    public Object from(ReflectAttribute fa, Object obj) throws Exception {
        fa.fromParcelable(par);
        return (Object) fa;
    }

    @Override
    public ArrayList<Object> from(String name) throws Exception {
        try {
            @SuppressWarnings("unchecked")
            ArrayList<Object> array = par.readArrayList(String.class.getClassLoader());
            return array;

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("from Preferences", e.getLocalizedMessage(), e);
        }
        return new ArrayList<Object>();
    }

    @Override
    public Object data() {
        return null;
    }

    @Override
    public void Save() {}
}
