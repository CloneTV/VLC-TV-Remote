package ru.ps.vlcatv.utils.playlist;

import androidx.annotation.Keep;
import ru.ps.vlcatv.utils.Text;
import ru.ps.vlcatv.utils.reflect.ReflectAttribute;
import ru.ps.vlcatv.utils.reflect.annotation.IBaseTableReflect;
import ru.ps.vlcatv.utils.reflect.annotation.IFieldReflect;
import ru.ps.vlcatv.utils.reflect.annotation.IUniqueReflect;

@Keep
@IBaseTableReflect(
        IThisTableName = "TableOnlineRenameItems",
        IParentTable = "TableOnlineRenameList",
        IParentKey = "idx",
        IParentSyncDelete = IBaseTableReflect.CASCADE,
        IParentSyncUpdate = IBaseTableReflect.DEFAULT,
        IIndexUnique = {
                @IUniqueReflect(IUnique = {"title_id"})
        }
)
public class PlayListOnlineRenameItem extends ReflectAttribute {

    @IFieldReflect("title_id")
    public String titleId = null;
    @IFieldReflect("epg_id")
    public String epgId = null;
    @IFieldReflect("title_default")
    public String titleDefault = null;
    @IFieldReflect("poster_default")
    public String posterDefault = null;
    @IFieldReflect("change_id")
    public boolean isNameChange = true;

    PlayListOnlineRenameItem() {
        dbParent = 1;
    }

    public String getName(String name) {
        if (!Text.isempty(titleDefault))
            return titleDefault;
        if ((isNameChange) && (!Text.isempty(epgId)))
            return epgId.replace('_', ' ').trim();
        return name;
    }
}
