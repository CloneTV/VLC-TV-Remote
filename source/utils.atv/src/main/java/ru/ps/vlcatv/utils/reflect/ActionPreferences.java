package ru.ps.vlcatv.utils.reflect;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.util.Log;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import ru.ps.vlcatv.utils.BuildConfig;

public class ActionPreferences implements ActionInterface {

    private SharedPreferences pref = null;
    private SharedPreferences.Editor prefed = null;
    private final String prefixPreference;

    @SuppressLint("CommitPrefEdits")
    ActionPreferences(SharedPreferences sp, String s) {
        pref = sp;
        prefed = sp.edit();
        prefixPreference = s;
    }

    /// To

    @Override
    public void to(String name, Type type, Object val) throws Exception {

        String prefixName = prefixPreference + name;
        if (type == String.class) {
            prefed.putString(prefixName, (String) val);
        } else if ((type == Boolean.class) || (type == boolean.class)) {
            prefed.putBoolean(prefixName, (boolean) val);
        } else if ((type == Integer.class) || (type == int.class)) {
            prefed.putInt(prefixName, (Integer) val);
        } else if ((type == Long.class) || (type == long.class)) {
            prefed.putLong(prefixName, (long) val);
        } else if ((type == Float.class) || (type == float.class)) {
            prefed.putFloat(prefixName, (float) val);
        } else if ((type == Double.class) || (type == double.class)) {
            prefed.putFloat(prefixName, (float) val);
        }
    }
    @Override
    public void to(String name, ArrayList<Object> array) throws Exception {
        try {

            String prefixName = prefixPreference + name;
            Set<String> set = new HashSet<>();
            for (Object o : array) {
                if (o.getClass() == String.class)
                    set.add((String) o);
            }
            prefed.putStringSet(prefixName, set);

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("to Preferences", e.getLocalizedMessage(), e);
        }
    }
    @Override
    public Object to(ReflectAttribute fa) throws Exception {
        fa.toPreferences(pref);
        return null;
    }

    /// From

    @Override
    public Object from(String name, Type type) throws Exception {
        String prefixName = prefixPreference + name;
        if (type == String.class) {
            return pref.getString(prefixName, "");
        } else if ((type == Boolean.class) || (type == boolean.class)) {
            return pref.getBoolean(prefixName, false);
        } else if ((type == Integer.class) || (type == int.class)) {
            return pref.getInt(prefixName, -1);
        } else if ((type == Long.class) || (type == long.class)) {
            return pref.getLong(prefixName, 0);
        } else if ((type == Float.class) || (type == float.class)) {
            return pref.getFloat(prefixName, 0);
        } else if ((type == Double.class) || (type == double.class)) {
            return (double)pref.getFloat(prefixName, 0);
        }
        return null;
    }
    @Override
    public Object from(ReflectAttribute fa, String name) throws Exception {
        fa.fromPreferences(pref);
        return (Object) fa;
    }
    @Override
    public Object from(ReflectAttribute fa, Object obj) throws Exception {
        fa.fromPreferences(pref);
        return (Object) fa;
    }
    @Override
    public ArrayList<Object> from(String name) throws Exception {
        String prefixName = prefixPreference + name;
        ArrayList<Object> array = new ArrayList<>();
        try {
            Set<String> set = pref.getStringSet(prefixName, null);
            if (set != null) {
                for (String s : set)
                    array.add((Object) s);
            }
        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("from Preferences", e.getLocalizedMessage(), e);
        }
        return array;
    }
    @Override
    public Object data() {
        return null;
    }

    @Override
    public void Save() {
        prefed.apply();
    }
}

