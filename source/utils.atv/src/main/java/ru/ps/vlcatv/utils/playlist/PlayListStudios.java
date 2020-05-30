package ru.ps.vlcatv.utils.playlist;

import androidx.annotation.Keep;

import ru.ps.vlcatv.utils.Text;
import ru.ps.vlcatv.utils.reflect.ReflectAttribute;
import ru.ps.vlcatv.utils.reflect.annotation.IBaseTableReflect;
import ru.ps.vlcatv.utils.reflect.annotation.IFieldReflect;
import ru.ps.vlcatv.utils.reflect.annotation.IUniqueReflect;

@Keep
@IBaseTableReflect(
        IThisTableName = "TableStudios",
        IParentTable = "TablePlayList",
        IParentKey = "idx",
        IParentSyncDelete = IBaseTableReflect.CASCADE,
        IParentSyncUpdate = IBaseTableReflect.DEFAULT,
        IIndexUnique = {
                @IUniqueReflect(IUnique = {"name"})
        },
        IIndexOne = { "name" }
)
public class PlayListStudios extends ReflectAttribute implements PlayListParentTableInterface {

    public PlayListStudios() {}
    public PlayListStudios(String s, long d) {
        id = d;
        Name = s;
    }

    @IFieldReflect("ele_id")
    public long id = -1;

    @IFieldReflect("name")
    public String Name = null;

    @Override
    public String getName() {
        return Name;
    }
    @Override
    public void setName(String s) {
        Name = s;
    }
    @Override
    public long getId() {
        return id;
    }
    @Override
    public void setId(long l) {
        id = l;
    }
    @Override
    public boolean equalsName(String s) {
        if ((Name == null) || (Text.isempty(s)))
            return false;
        return Name.equals(s);
    }
    @Override
    public <T> T getNewClassIndex() {
        //noinspection unchecked
        return (T) new PlayListStudiosIndex(id);
    }
    @Override
    public <T> T getNewClassIndex(long id) {
        //noinspection unchecked
        return (T) new PlayListStudiosIndex(id);
    }
    @Override
    public <T> T getNewClassInstance(String s, long d) {
        //noinspection unchecked
        return (T) new PlayListStudios(s, d);
    }
}

