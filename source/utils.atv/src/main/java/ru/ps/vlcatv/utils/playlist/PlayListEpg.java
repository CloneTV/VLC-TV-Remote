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
        IThisTableName = "TableEpgList"
)
public class PlayListEpg extends ReflectAttribute {

    @IArrayReflect(value = "epg_list", SkipRecursion = false)
    private List<PlayListEpgItem> epgList = new ArrayList<>();

    PlayListEpg() {}
    public PlayListEpg(DbManager dbm) {
        dbParent = 0;
        if (dbm != null)
            fromDb(dbm, 0);
    }
    public boolean isEmpty() {
        return (epgList.size() == 0);
    }
    public PlayListEpgItem find(String name) {
        if (Text.isempty(name))
            return null;
        for (PlayListEpgItem epg : epgList)
            if (epg.titleId.equals(name))
                return epg;
        return null;
    }
    public boolean findEpgId(String name) {
        if (Text.isempty(name))
            return false;
        for (PlayListEpgItem epg : epgList)
            if (name.startsWith(epg.epgId))
                return true;
        return false;
    }
}
