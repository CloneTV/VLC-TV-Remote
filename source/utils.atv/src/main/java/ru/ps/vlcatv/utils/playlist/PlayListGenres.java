package ru.ps.vlcatv.utils.playlist;

import androidx.annotation.Keep;

import ru.ps.vlcatv.utils.Text;
import ru.ps.vlcatv.utils.reflect.ReflectAttribute;
import ru.ps.vlcatv.utils.reflect.annotation.IBaseTableReflect;
import ru.ps.vlcatv.utils.reflect.annotation.IFieldReflect;
import ru.ps.vlcatv.utils.reflect.annotation.IUniqueReflect;

@Keep
@IBaseTableReflect(
        IThisTableName = "TableGenres",
        IParentTable = "TablePlayList",
        IParentKey = "idx",
        IParentSyncDelete = IBaseTableReflect.CASCADE,
        IParentSyncUpdate = IBaseTableReflect.DEFAULT,
        IIndexUnique = {
                @IUniqueReflect(IUnique = {"name"})
        },
        IIndexOne = { "name" }
)
public class PlayListGenres extends ReflectAttribute implements PlayListParentTableInterface {

    public PlayListGenres() {}
    public PlayListGenres(String s, long d) {
        id = d;
        Name = s;
    }

    @IFieldReflect("ele_id")
    public long id = -1;

    @IFieldReflect("name")
    public String Name = null;

    public String getName() {
        return Name;
    }
    public void setName(String s) {
        Name = s;
    }
    public long getId() {
        return id;
    }
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
        return (T) new PlayListGenresIndex(id);
    }
    @Override
    public <T> T getNewClassIndex(long id) {
        //noinspection unchecked
        return (T) new PlayListGenresIndex(id);
    }
    @Override
    public <T> T getNewClassInstance(String s, long d) {
        //noinspection unchecked
        return (T) new PlayListGenres(s, d);
    }
}
