package ru.ps.vlcatv.remote.data.event;

import ru.ps.vlcatv.remote.data.DataMediaItem;
import ru.ps.vlcatv.remote.data.SettingsInterface;

public class EventPlayHistoryChange extends BaseEventChange {

    private DataMediaItem[] MmItems = null;

    @Override
    public void onChangeProperty()
    {
        super.onChangeProperty();
    }
    @Override
    protected void callPropertyChanged(SettingsInterface si) {
        si.onHistoryChange();
    }

    ///

    public void setItemsList(DataMediaItem[] items)
    {
        MmItems = items;
        super.onChangeProperty();
    }
    public DataMediaItem[] getItemsList()
    {
        return MmItems;
    }
    public boolean isempty()
    {
        if (MmItems == null)
            return true;
        for (DataMediaItem itm : MmItems)
            if (itm != null)
                return false;
        return true;
    }
}
