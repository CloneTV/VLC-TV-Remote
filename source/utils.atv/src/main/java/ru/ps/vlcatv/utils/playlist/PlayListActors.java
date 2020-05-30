package ru.ps.vlcatv.utils.playlist;

import androidx.annotation.Keep;

import ru.ps.vlcatv.utils.reflect.ReflectAttribute;
import ru.ps.vlcatv.utils.reflect.annotation.IBaseTableReflect;
import ru.ps.vlcatv.utils.reflect.annotation.IFieldReflect;
import ru.ps.vlcatv.utils.reflect.annotation.IUniqueReflect;

@Keep
@IBaseTableReflect(
        IThisTableName = "TableActors",
        IParentTable = "TablePlayList",
        IParentKey = "idx",
        IParentSyncDelete = IBaseTableReflect.CASCADE,
        IParentSyncUpdate = IBaseTableReflect.DEFAULT,
        IIndexUnique = {
                @IUniqueReflect(IUnique = {"name"})
        },
        IIndexOne = { "name" }
)
public class PlayListActors extends ReflectAttribute {

    @IFieldReflect("ele_id")
    public long id = -1;

    @IFieldReflect("name")
    public String Name = null;

    @IFieldReflect("role")
    public String Role = null;

    @IFieldReflect("thumb")
    public String thumbUri = null;

    @IFieldReflect("profile")
    public String profileUri = null;
}
