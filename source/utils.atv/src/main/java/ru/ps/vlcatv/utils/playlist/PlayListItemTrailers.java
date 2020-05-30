package ru.ps.vlcatv.utils.playlist;

import androidx.annotation.Keep;

import ru.ps.vlcatv.utils.reflect.ReflectAttribute;
import ru.ps.vlcatv.utils.reflect.annotation.IBaseTableReflect;
import ru.ps.vlcatv.utils.reflect.annotation.IFieldReflect;
import ru.ps.vlcatv.utils.reflect.annotation.IUniqueReflect;

@Keep
@IBaseTableReflect(
        IThisTableName = "TableItemTrailers",
        IParentTable = "TableItems",
        IParentKey = "idx",
        IParentSyncDelete = IBaseTableReflect.CASCADE,
        IParentSyncUpdate = IBaseTableReflect.DEFAULT,
        IIndexUnique = {
                @IUniqueReflect(IUnique = {"name"})
        },
        IIndexOne = { "name" }
)
public class PlayListItemTrailers extends ReflectAttribute implements PlayListParentTableInterface {

    public PlayListItemTrailers() {}
    public PlayListItemTrailers(String s) {
        Name = s;
    }

    @IFieldReflect("name")
    public String Name = null;

    @Override
    public String getName() {
        return Name;
    }
}
