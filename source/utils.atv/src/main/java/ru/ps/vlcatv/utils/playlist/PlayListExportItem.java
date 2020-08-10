package ru.ps.vlcatv.utils.playlist;

import androidx.annotation.Keep;

import java.util.Date;

import ru.ps.vlcatv.constanttag.DataTagParse;
import ru.ps.vlcatv.utils.reflect.ReflectAttribute;
import ru.ps.vlcatv.utils.reflect.annotation.IFieldReflect;

@Keep
public class PlayListExportItem extends ReflectAttribute {
    PlayListExportItem() {}
    public PlayListExportItem(final PlayListItem item, Date d) {
        itemTitle = item.title.get();
        itemDesc = item.description.get();
        itemUrl = item.uri;
        itemImage = item.poster.get();
        itemTrailer = item.trailer.get();
        itemVlcId = item.getVlcId();
        itemDbId = item.dbIndex;
        itemType = item.type;
        itemDuration = item.stat.totalProgress.get();
        itemLastPos = item.stat.lastProgress.get();
        itemDate = item.date.get();
        itemSeason = item.season.get();
        itemEpisode = item.episode.get();
        itemLastView = d;
    }
    public PlayListExportItem(final PlayListFavorite fav) {
        itemTitle = fav.itemTitle;
        itemDesc = fav.itemDesc;
        itemUrl = fav.itemUrl;
        itemImage = fav.itemImage;
        itemTrailer = fav.itemTrailer;
        itemEpg = fav.itemEpg;
        itemEpgNotify = fav.itemEpgNotify;
        itemVlcId = fav.itemVlcId;
        itemDbId = fav.dbIndex;
        itemType = fav.itemType;
    }

    @IFieldReflect(DataTagParse.TAG_TYPE)
    public int itemType;
    @IFieldReflect(DataTagParse.TAG_VLCID)
    public long itemVlcId = -1;
    @IFieldReflect(DataTagParse.TAG_DBID)
    public long itemDbId = -1;

    @IFieldReflect(DataTagParse.TAG_DURATION)
    public long itemDuration = -1;
    @IFieldReflect(DataTagParse.TAG_POSITION)
    public long itemLastPos = -1;

    @IFieldReflect(DataTagParse.TAG_SEASON)
    public int itemSeason = -1;
    @IFieldReflect(DataTagParse.TAG_EPISODE)
    public int itemEpisode = -1;

    @IFieldReflect(DataTagParse.TAG_LASTVIEW)
    public Date itemLastView = null;

    @IFieldReflect(DataTagParse.TAG_DATE)
    public String itemDate = null;
    @IFieldReflect(DataTagParse.TAG_TITLE)
    public String itemTitle = null;
    @IFieldReflect(DataTagParse.TAG_DESC)
    public String itemDesc = null;
    @IFieldReflect(DataTagParse.TAG_URI)
    public String itemUrl = null;
    @IFieldReflect(DataTagParse.TAG_POSTERU)
    public String itemImage = null;
    @IFieldReflect(DataTagParse.TAG_TRAILER)
    public String itemTrailer = null;
    @IFieldReflect(DataTagParse.TAG_EPG)
    public String itemEpg = null;
    @IFieldReflect(DataTagParse.TAG_EPGNOTIFY)
    public String itemEpgNotify = null;

}
