package ru.ps.vlcatv.utils.playlist;

import androidx.annotation.Keep;
import ru.ps.vlcatv.utils.reflect.ReflectAttribute;
import ru.ps.vlcatv.utils.reflect.annotation.IBaseTableReflect;
import ru.ps.vlcatv.utils.reflect.annotation.IFieldReflect;
import ru.ps.vlcatv.utils.reflect.annotation.IUniqueReflect;

@Keep
@IBaseTableReflect(
        IThisTableName = "TableItemTitles",
        IParentTable = "TableItems",
        IParentKey = "idx",
        IParentSyncDelete = IBaseTableReflect.CASCADE,
        IParentSyncUpdate = IBaseTableReflect.DEFAULT,
        IIndexUnique = {
                @IUniqueReflect(IUnique = {"name"})
        },
        IIndexOne = { "name" }
)
public class PlayListItemTitles extends ReflectAttribute implements PlayListParentTableInterface {

    public PlayListItemTitles() {}
    public PlayListItemTitles(String s) {
        Name = s;
    }

    @IFieldReflect("name")
    public String Name = null;

    @Override
    public String getName() {
        return Name;
    }
}
