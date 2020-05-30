package ru.ps.vlcatv.utils.playlist;


import androidx.annotation.Keep;

import ru.ps.vlcatv.utils.reflect.ReflectAttribute;
import ru.ps.vlcatv.utils.reflect.annotation.IBaseTableReflect;
import ru.ps.vlcatv.utils.reflect.annotation.IFieldReflect;
import ru.ps.vlcatv.utils.reflect.annotation.IUniqueReflect;

@Keep
@IBaseTableReflect(
        IThisTableName = "TableActorsIndex",
        IParentTable = "TableItems",
        IParentKey = "idx",
        IParentSyncDelete = IBaseTableReflect.CASCADE,
        IParentSyncUpdate = IBaseTableReflect.DEFAULT,
        IIndexUnique = {
                @IUniqueReflect(IUnique = {"id_parent", "idx_id"})
        },
        IIndexOne = { "idx_id", "id_parent" }
)
public class PlayListActorsIndex extends ReflectAttribute {

    public PlayListActorsIndex() {}
    public PlayListActorsIndex(long l) {
        indexActor = l;
    }
    @IFieldReflect("idx_id")
    public long indexActor = -1;

}
