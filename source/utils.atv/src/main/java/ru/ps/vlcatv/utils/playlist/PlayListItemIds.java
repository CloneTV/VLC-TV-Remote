package ru.ps.vlcatv.utils.playlist;

import androidx.annotation.Keep;
import ru.ps.vlcatv.utils.reflect.ReflectAttribute;
import ru.ps.vlcatv.utils.reflect.annotation.IBaseTableReflect;
import ru.ps.vlcatv.utils.reflect.annotation.IFieldReflect;
import ru.ps.vlcatv.utils.reflect.annotation.IUniqueReflect;

@Keep
@IBaseTableReflect(
        IThisTableName = "TableItemIds",
        IParentTable = "TableItems",
        IParentKey = "idx",
        IParentSyncDelete = IBaseTableReflect.CASCADE,
        IParentSyncUpdate = IBaseTableReflect.DEFAULT,
        IIndexUnique = {
                @IUniqueReflect(IUnique = {"id_parent"})
        },
        IIndexOne = { "type" }
)
public class PlayListItemIds extends ReflectAttribute implements PlayListIdsInterface {

    public PlayListItemIds() {}
    public PlayListItemIds(String t, String v) {
        typeId = t;
        valId = v;
    }
    public PlayListItemIds(String t, int v) {
        typeId = t;
        valId = Integer.toString(v);
    }
    public PlayListItemIds(String t, long v) {
        typeId = t;
        valId = Long.toString(v);
    }

    @IFieldReflect("type")
    public String typeId = null;
    @IFieldReflect("val")
    public String valId = null;

    @Override
    public String getTypeId() {
        return typeId;
    }
    @Override
    public String getValId() {
        return valId;
    }
    @Override
    public void setTypeId(String s) {
        typeId = s;
    }
    @Override
    public void setValId(String s) {
        valId = s;
    }
}
