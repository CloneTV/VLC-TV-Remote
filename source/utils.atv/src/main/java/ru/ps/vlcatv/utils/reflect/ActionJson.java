package ru.ps.vlcatv.utils.reflect;

import java.lang.reflect.Type;
import java.util.ArrayList;
import ru.ps.vlcatv.utils.json.JSONObject;
import ru.ps.vlcatv.utils.json.JSONArray;

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
    public void to(String name, Type type, Object val) throws Exception {
        if (obj != null)
            obj.put(name, val);
    }
    @Override
    public void to(String name, ArrayList<Object> array) throws Exception {
        if (obj != null) {
            JSONArray arr = new JSONArray(array);
            obj.put(name, arr);
        }
    }
    @Override
    public Object to(ReflectAttribute fa) throws Exception {
        JSONObject o = fa.toJson();
        if ((o == null) || (o.length() == 0))
            return null;
        return o;
    }

    /// From

    @Override
    public Object from(String name, Type type) throws Exception {
        if (obj != null)
            return obj.opt(name);
        return null;
    }
    @Override
    public Object from(ReflectAttribute fa, String name) throws Exception {
        JSONObject jo = obj.optJSONObject(name);
        if (jo != null) {
            fa.fromJson(jo);
            return (Object) fa;
        }
        return null;
    }
    @Override
    public Object from(ReflectAttribute fa, Object obj) throws Exception {
        fa.fromJson((JSONObject) obj);
        return (Object) fa;
    }
    @Override
    public ArrayList<Object> from(String name) throws Exception {
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
    public Object data() {
        return obj;
    }

    @Override
    public void Save() {}
}

