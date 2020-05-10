package ru.ps.vlcatv.remote.data.event;

import ru.ps.vlcatv.remote.data.SettingsInterface;

public class EventPlayStateChange extends BaseEventChange {
    private int stateId = -1;

    @Override
    protected void callPropertyChanged(SettingsInterface si) {
        si.onPlayStateChange();
    }
    @Override
    public void onChangeProperty(int id)
    {
        if (id == stateId)
            return;
        stateId = id;
        super.onChangeProperty();
    }
}
