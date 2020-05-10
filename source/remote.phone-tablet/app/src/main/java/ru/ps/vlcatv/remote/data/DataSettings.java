package ru.ps.vlcatv.remote.data;

import androidx.databinding.Observable;
import androidx.databinding.ObservableField;
import androidx.preference.PreferenceManager;
import android.content.SharedPreferences;

import ru.ps.vlcatv.remote.AppMain;
import ru.ps.vlcatv.remote.Utils;
import ru.ps.vlcatv.remote.data.event.BaseEventChange;

import static androidx.preference.PreferenceManager.getDefaultSharedPreferences;

public class DataSettings extends BaseEventChange {

    private static final String PREF_IP = "netaddr";
    private static final String PREF_PORT = "netport";

    public final ObservableField<String> Address = new ObservableField<>();
    public final ObservableField<String> Port = new ObservableField<>();

    public DataSettings() {
        Address.set("");
        Port.set("");
        OnPropertyChangedCallback cb = new OnPropertyChangedCallback() {
                    @Override
                    public void onPropertyChanged(Observable sender, int propertyId) {
                        onChangeProperty();
                    }
                };
        Address.addOnPropertyChangedCallback(cb);
        Port.addOnPropertyChangedCallback(cb);
    }
    @Override
    protected void callPropertyChanged(SettingsInterface si) {
        si.onSettingsChange();
    }
    public boolean isempty() {
        return (Utils.isempty(Address.get()) || Utils.isempty(Port.get()));
    }
    public void getPreferences() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(AppMain.getAppContext());
        Address.set(pref.getString(PREF_IP, ""));
        Port.set(pref.getString(PREF_PORT, ""));
        onChangeProperty();
    }
    public void setPreferences() {
        SharedPreferences pref = getDefaultSharedPreferences(AppMain.getAppContext());
        SharedPreferences.Editor prefer = pref.edit();
        prefer.putString(PREF_IP, Address.get());
        prefer.putString(PREF_PORT, Port.get());
        prefer.apply();
    }
}
