package ru.ps.vlcatv.utils.playlist;

import androidx.annotation.Keep;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ru.ps.vlcatv.utils.Text;
import ru.ps.vlcatv.utils.reflect.ReflectAttribute;
import ru.ps.vlcatv.utils.reflect.annotation.IBaseTableReflect;
import ru.ps.vlcatv.utils.reflect.annotation.IFieldReflect;
import ru.ps.vlcatv.utils.reflect.annotation.IUniqueReflect;

@Keep
@IBaseTableReflect(
        IThisTableName = "TableSchedule",
        IParentTable = "TablePlayList",
        IParentKey = "idx",
        IParentSyncDelete = IBaseTableReflect.CASCADE,
        IParentSyncUpdate = IBaseTableReflect.DEFAULT,
        IIndexUnique = {
                @IUniqueReflect(IUnique = {"date_start"})
        }
)
public class PlayListSchedule extends ReflectAttribute {
    public static final int GET_DATE = 1001;
    public static final int GET_FAVORITE = 1002;

    PlayListSchedule() {
        itemType = PlayListConstant.TYPE_ONLINE;
    }
    public PlayListSchedule(final Date d, final PlayListFavorite fav) {
        itemType = fav.itemType;
        itemDateStart = d;
        itemEpgNotify = fav.itemEpgNotify;
        itemTitle = fav.itemTitle;
        itemUrl = fav.itemUrl;
        itemDesc = fav.itemDesc;
        itemImage = fav.itemImage;
        itemEpg = fav.itemEpg;
    }
    public Object get(int idx) {
        switch (idx) {
            case GET_DATE:
                return itemDateStart;
            case GET_FAVORITE:
                return new PlayListFavorite(
                        itemTitle,
                        itemUrl,
                        itemDesc,
                        itemImage,
                        itemEpg,
                        ((Text.isempty(itemEpgNotify)) ? getEpgNotify() : itemEpgNotify)
                );
            default:
                return null;
        }
    }
    private String getEpgNotify() {
        try {
            final SimpleDateFormat sf = new SimpleDateFormat("MMMM dd HH:mm", Locale.getDefault());
            return String.format(
                    Locale.getDefault(),
                    "%s - turn On this channel",
                    sf.format(itemDateStart)
            );
        } catch (Exception ignore) {}
        return "";
    }

    public final int itemType;

    @IFieldReflect("vlc_id")
    public long itemVlcId = -1;
    @IFieldReflect("date_start")
    public Date itemDateStart = null;
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
    @IFieldReflect("notify")
    public String itemEpgNotify = null;
}
