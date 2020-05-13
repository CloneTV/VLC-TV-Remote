package ru.ps.vlcatv.utils.reflect;

import java.lang.reflect.Type;
import java.util.ArrayList;

public interface ActionInterface {
    public void to(String name, Type type, Object val) throws Exception;
    public void to(String name, ArrayList<Object> array) throws Exception;
    public Object to(ReflectAttribute fa) throws Exception;

    public Object from(String name, Type type) throws Exception;
    public Object from(ReflectAttribute fa, String name) throws Exception;
    public Object from(ReflectAttribute fa, Object obj) throws Exception;
    public ArrayList<Object> from(String name) throws Exception;

    public Object data();
    public void Save();
}

