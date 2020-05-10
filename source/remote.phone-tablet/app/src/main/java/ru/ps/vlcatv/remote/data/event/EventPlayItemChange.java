package ru.ps.vlcatv.remote.data.event;

import ru.ps.vlcatv.remote.Utils;
import ru.ps.vlcatv.remote.data.SettingsInterface;

public class EventPlayItemChange extends BaseEventChange {
    private int playId = -1;

    @Override
    public void onChangeProperty()
    {
        super.onChangeProperty();
    }
    @Override
    public void onChangeProperty(String s)
    {
        if (!Utils.isempty(s))
            super.onChangeProperty();
    }
    @Override
    public void onChangeProperty(int id)
    {
        if (id == playId)
            return;
        playId = id;
        if (playId > -1)
            super.onChangeProperty();
    }
    @Override
    protected void callPropertyChanged(SettingsInterface si) {
        si.onPlayItemChange();
    }
}
