package ru.ps.vlcatv.utils.playlist;

import androidx.annotation.Keep;
import ru.ps.vlcatv.utils.reflect.ReflectAttribute;
import ru.ps.vlcatv.utils.reflect.annotation.IBaseTableReflect;
import ru.ps.vlcatv.utils.reflect.annotation.IFieldReflect;

@Keep
@IBaseTableReflect(
        IThisTableName = "TableFavorites",
        IParentTable = "TablePlayList",
        IParentKey = "idx",
        IParentSyncDelete = IBaseTableReflect.CASCADE,
        IParentSyncUpdate = IBaseTableReflect.DEFAULT,
        IIndexOne = { "title" }
)
public class PlayListFavorite extends ReflectAttribute {

    PlayListFavorite() {}
    public PlayListFavorite(String title, String uri, String desc, String img, String epg) {
        itemTitle = title;
        itemDesc = desc;
        itemUrl = uri;
        itemImage = img;
        itemEpg = epg;
    }

    public final int itemType = PlayListConstant.TYPE_ONLINE;

    @IFieldReflect("vlc_id")
    public long itemVlcId = -1;
    @IFieldReflect("title")
    public String itemTitle = null;
    @IFieldReflect("desc")
    public String itemDesc = null;
    @IFieldReflect("img")
    public String itemImage = null;
    @IFieldReflect("url")
    public String itemUrl = null;
    @IFieldReflect("epg")
    public String itemEpg = null;
}
