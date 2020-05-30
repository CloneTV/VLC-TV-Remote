package ru.ps.vlcatv.utils.playlist;

import android.view.View;

import androidx.annotation.Keep;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.databinding.ObservableLong;

import java.util.Date;
import java.util.Locale;

import ru.ps.vlcatv.utils.playlist.parse.PlayListParseInterface;
import ru.ps.vlcatv.utils.reflect.ReflectAttribute;
import ru.ps.vlcatv.utils.reflect.annotation.IBaseTableReflect;
import ru.ps.vlcatv.utils.reflect.annotation.IFieldReflect;
import ru.ps.vlcatv.utils.reflect.annotation.IUniqueReflect;

@Keep
@IBaseTableReflect(
        IThisTableName = "TableItemStatistic",
        IParentTable = "TableItems",
        IParentKey = "idx",
        IParentSyncDelete = IBaseTableReflect.CASCADE,
        IParentSyncUpdate = IBaseTableReflect.DEFAULT,
        IIndexUnique = {
                @IUniqueReflect(IUnique = {"id_parent"})
        },
        IIndexOne = { "last_view" }
)
public final class PlayListItemStatistic extends ReflectAttribute {

    @IFieldReflect("time_total")
    public ObservableInt totalProgress = new ObservableInt(0);
    @IFieldReflect("time_last")
    public ObservableInt lastProgress = new ObservableInt(0);
    @IFieldReflect("watched")
    public ObservableBoolean watched = new ObservableBoolean(false);
    @IFieldReflect("view_count")
    public ObservableLong playCount = new ObservableLong(0);
    @IFieldReflect("time_bool")
    public boolean isMinutes = false;
    @IFieldReflect("last_view")
    public Date lastView = null;
}
