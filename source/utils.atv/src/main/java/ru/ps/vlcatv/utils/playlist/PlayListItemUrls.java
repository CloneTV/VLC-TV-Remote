package ru.ps.vlcatv.utils.playlist;


import androidx.annotation.Keep;

import ru.ps.vlcatv.utils.playlist.PlayListParentTableInterface;
import ru.ps.vlcatv.utils.reflect.ReflectAttribute;
import ru.ps.vlcatv.utils.reflect.annotation.IBaseTableReflect;
import ru.ps.vlcatv.utils.reflect.annotation.IFieldReflect;
import ru.ps.vlcatv.utils.reflect.annotation.IUniqueReflect;

@Keep
@IBaseTableReflect(
        IThisTableName = "TableItemUrls",
        IParentTable = "TableItems",
        IParentKey = "idx",
        IParentSyncDelete = IBaseTableReflect.CASCADE,
        IParentSyncUpdate = IBaseTableReflect.DEFAULT,
        IIndexUnique = {
                @IUniqueReflect(IUnique = {"url"})
        },
        IIndexOne = { "url" }
)
public class PlayListItemUrls extends ReflectAttribute implements PlayListParentTableInterface {

    public PlayListItemUrls() {}
    public PlayListItemUrls(String n, String u, String e) {
        name = n;
        url = u;
        epg = e;
    }

    @IFieldReflect("name")
    public String name = null;
    @IFieldReflect("url")
    public String url = null;
    @IFieldReflect("epg")
    public String epg = null;

    @IFieldReflect("active")
    public boolean active = true;

    @Override
    public String getName() {
        return name;
    }
    @Override
    public String getUrl() {
        return url;
    }
    @Override
    public String getEpg() {
        return epg;
    }

}
