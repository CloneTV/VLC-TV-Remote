package ru.ps.vlcatv.utils.playlist;

import androidx.annotation.Keep;

import java.util.Calendar;
import java.util.Date;

import ru.ps.vlcatv.utils.reflect.ReflectAttribute;
import ru.ps.vlcatv.utils.reflect.annotation.IBaseTableReflect;
import ru.ps.vlcatv.utils.reflect.annotation.IFieldReflect;

@Keep
@IBaseTableReflect(
        IThisTableName = "TableHistoryIndex",
        IParentTable = "TablePlayList",
        IParentKey = "idx",
        IParentSyncDelete = IBaseTableReflect.CASCADE,
        IParentSyncUpdate = IBaseTableReflect.DEFAULT,
        IIndexOne = { "idx_id" }
)
public class PlayListHistoryIndex extends ReflectAttribute {
    public PlayListHistoryIndex() {}
    public PlayListHistoryIndex(long l, int pos) {
        historyVlcId = l;
        historyPosition = pos;
        historyDate = Calendar.getInstance().getTime();
    }
    public PlayListHistoryIndex(long l, int pos, Date date) {
        historyVlcId = l;
        historyPosition = pos;
        historyDate = date;
    }
    @IFieldReflect("idx_id")
    public long historyVlcId = -1;
    @IFieldReflect("date_view")
    public Date historyDate = null;
    @IFieldReflect("play_position")
    public int historyPosition = 0;
}
