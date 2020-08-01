package ru.ps.vlcatv.utils.playlist;

import androidx.annotation.Keep;
import java.util.ArrayList;
import java.util.List;

import ru.ps.vlcatv.utils.Text;
import ru.ps.vlcatv.utils.db.DbManager;
import ru.ps.vlcatv.utils.reflect.ReflectAttribute;
import ru.ps.vlcatv.utils.reflect.annotation.IArrayReflect;
import ru.ps.vlcatv.utils.reflect.annotation.IBaseTableReflect;

@Keep
@IBaseTableReflect(
        IThisTableName = "TableOnlineRenameList"
)
public class PlayListOnlineRenameList extends ReflectAttribute {

    @IArrayReflect(value = "rename_list", SkipRecursion = false)
    private List<PlayListOnlineRenameItem> renameList = new ArrayList<>();

    PlayListOnlineRenameList() {}
    public PlayListOnlineRenameList(DbManager dbm) {
        dbParent = 0;
        if (dbm != null)
            fromDb(dbm, 0);
    }
    public boolean isEmpty() {
        return (renameList.size() == 0);
    }
    public PlayListOnlineRenameItem find(String name) {
        if (Text.isempty(name))
            return null;
        for (PlayListOnlineRenameItem epg : renameList)
            if (epg.titleId.equals(name))
                return epg;
        return null;
    }
    public boolean findEpgId(String name) {
        if (Text.isempty(name))
            return false;
        for (PlayListOnlineRenameItem epg : renameList)
            if (name.startsWith(epg.epgId))
                return true;
        return false;
    }
}
