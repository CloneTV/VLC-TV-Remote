package ru.ps.vlcatv.utils.reflect.annotation;

import android.content.ContentValues;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import ru.ps.vlcatv.utils.reflect.ActionDb;
import ru.ps.vlcatv.utils.reflect.ReflectAttribute;
import ru.ps.vlcatv.utils.reflect.annotation.IBaseTableReflect;

public interface ActionInterface {
    public void to(final Field field, Type type, Object val, final String name) throws Exception;               // Field create
    public void to(final Field field, ArrayList<Object> array, final String name) throws Exception;             // Array create
    public Object to(final Field field, ReflectAttribute ra, final String name, boolean skipAttr) throws Exception; // Object create

    public Object from(final Field field, Type type, final String name) throws Exception;                       // Field create
    public Object from(final Field field, ReflectAttribute ra, final String name) throws Exception;             // Object create
    public Object from(final Field field, ReflectAttribute ra, Object obj, final String name, boolean skipAttr) throws Exception; // Array create
    public ArrayList<Object> from(final Field field, final String name) throws Exception;                       // Array list Object create

    public void createArray(Type type, final String name) throws Exception;
    public void createObject(ReflectAttribute ra, final String name) throws Exception;
    public void createField(Type type, final String name) throws Exception;
    public void createFieldIndex(Type type, final String name) throws Exception;
    public void foreignKey(IBaseTableReflect fk);

    public Object data();
    public void Save(ReflectAttribute ra, ContentValues cv);
    public void Load(Object obj, ContentValues cv);
    public List<ActionDb.RootTableHolder.RootTableCreator> Create(boolean isWriteScheme);
    public void Create(List<ActionDb.RootTableHolder.RootTableCreator> list);
    public void Delete(ReflectAttribute ra, ContentValues cv);
}

