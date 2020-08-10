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

    /*
            String title,
            String desc,
            String uri,
            String img,
            String trailer,
            String epg,
            String ent,
            int type
     */

    PlayListFavorite() {
        itemType = PlayListConstant.TYPE_ONLINE;
    }
    public PlayListFavorite(
            final String title, final String uri, final String img,
            final String trailer) {
        itemTitle = title;
        itemUrl = uri;
        itemImage = img;
        itemTrailer = trailer;
        itemType = PlayListConstant.TYPE_IPCAM;
    }
    public PlayListFavorite(
            final String title, final String desc, final String uri,
            final String img, final String trailer, final String epg, int type) {
        itemTitle = title;
        itemDesc = desc;
        itemUrl = uri;
        itemImage = img;
        itemTrailer = trailer;
        itemEpg = epg;
        itemType = (type <= 0) ? PlayListConstant.TYPE_ONLINE : type;
    }
    public PlayListFavorite(
            final String title, final String desc, final String uri,
            final String img, final String trailer, final String epg,
            final String ent, int type) {
        itemTitle = title;
        itemDesc = desc;
        itemUrl = uri;
        itemImage = img;
        itemTrailer = trailer;
        itemEpg = epg;
        itemEpgNotify = ent;
        itemType = (type <= 0) ? PlayListConstant.TYPE_ONLINE : type;
    }

    public final int itemType;

    @IFieldReflect("vlc_id")
    public long itemVlcId = -1;
    @IFieldReflect("title")
    public String itemTitle = null;
    @IFieldReflect("trailer")
    public String itemTrailer = null;
    @IFieldReflect("desc")
    public String itemDesc = null;
    @IFieldReflect("img")
    public String itemImage = null;
    @IFieldReflect("url")
    public String itemUrl = null;
    @IFieldReflect("epg")
    public String itemEpg = null;
    public String itemEpgNotify = null;
}
