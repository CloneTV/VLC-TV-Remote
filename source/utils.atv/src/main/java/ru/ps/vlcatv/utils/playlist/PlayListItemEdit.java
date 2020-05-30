package ru.ps.vlcatv.utils.playlist;

import androidx.annotation.Keep;

import java.util.Date;

import ru.ps.vlcatv.utils.reflect.ReflectAttribute;
import ru.ps.vlcatv.utils.reflect.annotation.IBaseTableReflect;
import ru.ps.vlcatv.utils.reflect.annotation.IFieldReflect;

@Keep
@IBaseTableReflect(
        IThisTableName = "TableItemEdit",
        IParentTable = "TablePlayList",
        IParentKey = "idx",
        IParentSyncDelete = IBaseTableReflect.CASCADE,
        IParentSyncUpdate = IBaseTableReflect.DEFAULT,
        IIndexOne = { "db_id", "vlc_id" }
)
public class PlayListItemEdit extends ReflectAttribute implements PlayListParentTableInterface {

    public PlayListItemEdit() {}
    public PlayListItemEdit(long db, long vlc, String s, Date d) {
        dbId = db;
        vlcId = vlc;
        Title = s;
        date = d;
    }

    @IFieldReflect("db_id")
    public long dbId = -1;

    @IFieldReflect("vlc_id")
    public long vlcId = -1;

    @IFieldReflect("title")
    public String Title = null;

    @IFieldReflect("date_add")
    public Date date = null;

    @Override
    public String getName() {
        return Title;
    }
    @Override
    public long getId() {
        return dbId;
    }
}
