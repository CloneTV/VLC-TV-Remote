package ru.ps.vlcatv.utils.playlist;

import androidx.annotation.Keep;

import ru.ps.vlcatv.utils.reflect.ReflectAttribute;
import ru.ps.vlcatv.utils.reflect.annotation.IBaseTableReflect;
import ru.ps.vlcatv.utils.reflect.annotation.IFieldReflect;
import ru.ps.vlcatv.utils.reflect.annotation.IUniqueReflect;

@Keep
@IBaseTableReflect(
        IThisTableName = "TableCountryIndex",
        IParentTable = "TableItems",
        IParentKey = "idx",
        IParentSyncDelete = IBaseTableReflect.CASCADE,
        IParentSyncUpdate = IBaseTableReflect.DEFAULT,
        IIndexUnique = {
                @IUniqueReflect(IUnique = {"id_parent", "idx_id"})
        },
        IIndexOne = { "idx_id", "id_parent" }
)
public class PlayListCountryIndex extends ReflectAttribute {

    public PlayListCountryIndex() {}
    public PlayListCountryIndex(long l) {
        indexGenre = l;
    }
    @IFieldReflect("idx_id")
    public long indexGenre = -1;

}
