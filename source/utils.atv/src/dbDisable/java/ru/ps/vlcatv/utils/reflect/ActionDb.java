package ru.ps.vlcatv.utils.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import ru.ps.vlcatv.utils.db.DbManager;

public class ActionDb implements ActionInterface {

    static private final String NOT_SUPPORT = "This compiled flavor not support DB Sqlite";
    ActionDb(String s, DbManager dbm, long index, long parent) {
        super();
        throw new NullPointerException(NOT_SUPPORT);
    }

    @Override
    public void to(Type type, Object val, String name) throws Exception {

    }

    @Override
    public void to(ArrayList<Object> array, String name) throws Exception {

    }

    @Override
    public Object to(ReflectAttribute ra) throws Exception {
        return null;
    }

    @Override
    public Object from(Field field, Type type, String name) throws Exception {
        return null;
    }

    @Override
    public Object from(Field field, ReflectAttribute ra, String name) throws Exception {
        return null;
    }

    @Override
    public Object from(Field field, ReflectAttribute ra, Object obj, String name) throws Exception {
        return null;
    }

    @Override
    public ArrayList<Object> from(Field field, String name) throws Exception {
        return null;
    }

    @Override
    public void createArray(Type type, String name) throws Exception {

    }

    @Override
    public void createObject(ReflectAttribute ra, String name) throws Exception {

    }

    @Override
    public void createField(Type type, String name) throws Exception {

    }

    @Override
    public void createFieldIndex(Type type, String name) throws Exception {

    }

    @Override
    public void foreignKey(ForeignKeyReflect fk) {

    }

    @Override
    public Object data() {
        return null;
    }

    @Override
    public void Save() {
    }

    @Override
    public void Load(Object obj) {
    }

    @Override
    public List<ActionDb.RootTableHolder.RootTableCreator> Create(boolean isWriteScheme) {
        return null;
    }

    @Override
    public void Create(List<ActionDb.RootTableHolder.RootTableCreator> list) {
    }

    @Override
    public void Delete() {
    }

    ///

    public static final class RootTableHolder {
        public static final class RootTableCreator {
            RootTableCreator(String tbl, String s) {}
        }
    }

}
