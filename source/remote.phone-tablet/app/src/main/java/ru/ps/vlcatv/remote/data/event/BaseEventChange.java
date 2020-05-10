package ru.ps.vlcatv.remote.data.event;

import androidx.databinding.BaseObservable;
import java.util.ArrayList;

import ru.ps.vlcatv.remote.data.SettingsInterface;

public class BaseEventChange extends BaseObservable {
    private ArrayList<SettingsInterface> cb_EventChanged = new ArrayList<>();

    /// to Override

    protected void onChangeProperty() {
        bindPropertyChanged();
    }
    protected void onChangeProperty(int id) {
        bindPropertyChanged();
    }
    protected void onChangeProperty(String s) {
        bindPropertyChanged();
    }
    protected void callPropertyChanged(SettingsInterface si) {}

    ///

    private void bindPropertyChanged() {
        try {
            for (SettingsInterface si : cb_EventChanged)
                callPropertyChanged(si);
        } catch (Exception ignored) {}
    }
    public void setCallbackChanged(SettingsInterface cb) {
        try {
            if (!cb_EventChanged.contains(cb))
                cb_EventChanged.add(cb);
        } catch (Exception ignored) {}
    }
    public void removeCallbackChanged(SettingsInterface cb) {
        try {
            cb_EventChanged.remove(cb);
        } catch (Exception ignored) {}
    }

}
