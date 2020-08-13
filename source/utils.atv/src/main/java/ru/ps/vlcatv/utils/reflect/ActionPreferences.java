package ru.ps.vlcatv.utils.reflect;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.SharedPreferences;
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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import ru.ps.vlcatv.utils.Text;
import ru.ps.vlcatv.utils.reflect.annotation.ActionInterface;
import ru.ps.vlcatv.utils.reflect.annotation.IBaseTableReflect;

public class ActionPreferences implements ActionInterface {

    private final SharedPreferences pref;
    private final SharedPreferences.Editor prefed;
    private final String prefixPreference;
    private final ReflectAttribute root;

    @SuppressLint("CommitPrefEdits")
    ActionPreferences(SharedPreferences sp, String s, ReflectAttribute ra) {
        root = ra;
        pref = sp;
        prefed = sp.edit();
        prefixPreference = s;
    }

    /// To

    @Override
    public void to(Field field, Type type, Object val, String name) throws Exception {
        try {
            String prefixName = prefixPreference + name;
            if (type == String.class) {
                prefed.putString(prefixName, (String) val);
                return;
            } else if ((type == Boolean.class) || (type == boolean.class)) {
                prefed.putBoolean(prefixName, (boolean) val);
                return;
            } else if ((type == Integer.class) || (type == int.class)) {
                prefed.putInt(prefixName, (int) val);
                return;
            } else if ((type == Long.class) || (type == long.class)) {
                prefed.putLong(prefixName, (long) val);
                return;
            } else if ((type == Float.class) || (type == float.class)) {
                prefed.putFloat(prefixName, (float) val);
                return;
            } else if ((type == Double.class) || (type == double.class)) {
                prefed.putFloat(prefixName, (float) val);
                return;
            } else if (type == Date.class) {
                try {
                    String s = pref.getString(prefixName, "");
                    if (!Text.isempty(s)) {
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        prefed.putString(prefixName, (String) format.format((Date) val));
                    }
                } catch (Exception ignore) {}
                return;
            }
            try {
                Class<?> clazz = ((Class<?>)type);
                if (clazz != null) {
                    if (clazz == ObservableInt.class) {
                        prefed.putInt(prefixName, (int) ((ObservableInt) val).get());
                        return;
                    } else if (clazz == ObservableLong.class) {
                        prefed.putLong(prefixName, (long) ((ObservableLong) val).get());
                        return;
                    } else if (clazz == ObservableBoolean.class) {
                        prefed.putBoolean(prefixName, (boolean) ((ObservableBoolean) val).get());
                        return;
                    } else if (clazz == ObservableDouble.class) {
                        prefed.putFloat(prefixName, (float) ((ObservableDouble) val).get());
                        return;
                    } else if (clazz == ObservableFloat.class) {
                        prefed.putFloat(prefixName, (float) ((ObservableFloat) val).get());
                        return;
                    } else if (clazz == ObservableShort.class) {
                        prefed.putInt(prefixName, (short) ((ObservableShort) val).get());
                        return;
                    } else if (clazz == ObservableChar.class) {
                        prefed.putInt(prefixName, (char) ((ObservableChar) val).get());
                        return;
                    } else if (clazz == ObservableByte.class) {
                        prefed.putInt(prefixName, (byte) ((ObservableByte) val).get());
                        return;
                    }
                }
            } catch (Exception ignore) {}

            Class<?> clazz = (Class<?>) ((ParameterizedType) field.getGenericType())
                    .getActualTypeArguments()[0];

            if (clazz == null)
                return;
            if (clazz == String.class) {
                prefed.putString(prefixName, (String) ((ObservableField<String>) val).get());
            } else if (clazz == Date.class) {
                SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date date = (Date) ((ObservableField<Date>) val).get();
                if (date != null)
                    prefed.putString(prefixName, fmt.format(date));
            }

        } catch (Exception ignore) {}
    }
    @Override
    public void to(Field field, ArrayList<Object> array, String name) throws Exception {
        try {

            String prefixName = prefixPreference + name;
            Set<String> set = new HashSet<>();
            for (Object o : array) {
                if (o.getClass() == String.class)
                    set.add((String) o);
            }
            prefed.putStringSet(prefixName, set);
        } catch (Exception ignore) {}
    }
    @Override
    public Object to(final Field field, ReflectAttribute fa, final String name, boolean skipAttr) throws Exception {
        try {
            fa.toPreferences(pref, name + "_");
        } catch (Exception ignore) {}
        return null;
    }

    /// From

    @Override
    public Object from(Field field, Type type, String name) throws Exception {
        try {
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
            } else if (type == Date.class) {
                try {
                    String s = pref.getString(prefixName, "");
                    if (!Text.isempty(s)) {
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        return format.parse(s);
                    }
                    return null;
                } catch (Exception ignore) {}
            }

            Class<?> clazz = ((Class<?>)type);
            if (clazz != null) {
                if (clazz == ObservableInt.class) {
                    ObservableInt oi = (ObservableInt) field.get(root);
                    if (oi != null)
                        oi.set(pref.getInt(prefixName, -1));
                } else if (clazz == ObservableLong.class) {
                    ObservableLong ol = (ObservableLong) field.get(root);
                    if (ol != null)
                        ol.set(pref.getLong(prefixName, -1));
                } else if (clazz == ObservableBoolean.class) {
                    ObservableBoolean ob = (ObservableBoolean) field.get(root);
                    if (ob != null)
                        ob.set(pref.getBoolean(prefixName, false));
                } else if (clazz == ObservableDouble.class) {
                    ObservableDouble od = (ObservableDouble) field.get(root);
                    if (od != null)
                        od.set(pref.getFloat(prefixName, 0));
                } else if (clazz == ObservableFloat.class) {
                    ObservableFloat of = (ObservableFloat) field.get(root);
                    if (of != null)
                        of.set(pref.getFloat(prefixName, 0));
                } else if (clazz == ObservableShort.class) {
                    ObservableShort os = (ObservableShort) field.get(root);
                    if (os != null)
                        os.set((short) pref.getInt(prefixName, 0));
                } else if (clazz == ObservableChar.class) {
                    ObservableChar oc = (ObservableChar) field.get(root);
                    if (oc != null)
                        oc.set((char) pref.getInt(prefixName, '\0'));
                } else if (clazz == ObservableByte.class) {
                    ObservableByte ob = (ObservableByte) field.get(root);
                    if (ob != null)
                        ob.set((byte) pref.getInt(prefixName, '\0'));
                } else if (clazz == ObservableField.class) {

                    try {
                        Class<?> clz = (Class<?>) ((ParameterizedType) field.getGenericType())
                                .getActualTypeArguments()[0];

                        if (clz == String.class) {
                            //noinspection ConstantConditions
                            do {
                                ObservableField<String> os = (ObservableField<String>) field.get(root);
                                if (os == null)
                                    break;
                                os.set(pref.getString(prefixName, ""));
                            } while (false);

                        } else if (clz == Date.class) {
                            //noinspection ConstantConditions
                            do {
                                SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                String s = pref.getString(prefixName, "");
                                if (Text.isempty(s))
                                    break;
                                ObservableField<Date> od = (ObservableField<Date>) field.get(root);
                                if (od == null)
                                    break;
                                od.set(fmt.parse(s));
                            } while (false);

                        }
                    } catch (Exception ignore) {}
                }
            }
        } catch (Exception ignore) {}
        return null;
    }
    @Override
    public Object from(Field field, ReflectAttribute fa, String name) throws Exception {
        try {
            fa.fromPreferences(pref, name + "_");
        } catch (Exception ignore) {}
        return (Object) fa;
    }
    @Override
    public Object from(Field field, ReflectAttribute fa, Object obj, String name, boolean skipAttr) throws Exception {
        try {
            fa.fromPreferences(pref, name + "_");
        } catch (Exception ignore) {}
        return (Object) fa;
    }
    @Override
    public ArrayList<Object> from(Field field, String name) throws Exception {
        String prefixName = prefixPreference + name;
        ArrayList<Object> array = new ArrayList<>();
        try {
            Set<String> set = pref.getStringSet(prefixName, null);
            if (set != null) {
                for (String s : set)
                    array.add((Object) s);
            }
        } catch (Exception ignore) {}
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
        return null;
    }

    @Override
    public void Save(ReflectAttribute ra, ContentValues cv) {
        prefed.apply();
    }

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
