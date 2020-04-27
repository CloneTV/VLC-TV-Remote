package ru.ps.vlcatv.remote.data;

import android.util.Log;

import androidx.databinding.BaseObservable;
import androidx.databinding.Observable;
import androidx.databinding.ObservableField;
import androidx.preference.PreferenceManager;
import android.content.SharedPreferences;

import java.util.ArrayList;

import ru.ps.vlcatv.remote.AppMain;
import ru.ps.vlcatv.remote.R;
import ru.ps.vlcatv.remote.SettingsInterface;
import ru.ps.vlcatv.remote.Utils;

import static androidx.preference.PreferenceManager.getDefaultSharedPreferences;

public class DataSettings extends BaseObservable {

    private static final String PREF_IP = "netaddr";
    private static final String PREF_PORT = "netport";

    public final ObservableField<String> Address = new ObservableField<>();
    public final ObservableField<String> Port = new ObservableField<>();
    private ArrayList<SettingsInterface> cb_interface = new ArrayList<>();

    public DataSettings() {
        Address.set("");
        Port.set("");
        OnPropertyChangedCallback cb = new OnPropertyChangedCallback() {
                    @Override
                    public void onPropertyChanged(Observable sender, int propertyId) {
                        bindPropertyChanged();
                    }
                };
        Address.addOnPropertyChangedCallback(cb);
        Port.addOnPropertyChangedCallback(cb);
    }
    private void bindPropertyChanged() {
        for (SettingsInterface si : cb_interface)
            si.onSettingsChange();
    }
    public void setCallbackChanged(SettingsInterface cb) {
        if (!cb_interface.contains(cb))
            cb_interface.add(cb);
    }
    public boolean isempty() {
        return (Utils.isempty(Address.get()) || Utils.isempty(Port.get()));
    }
    public void getPreferences() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(AppMain.getAppContext());
        Address.set(pref.getString(PREF_IP, ""));
        Port.set(pref.getString(PREF_PORT, ""));
        bindPropertyChanged();
    }
    public void setPreferences() {
        SharedPreferences pref = getDefaultSharedPreferences(AppMain.getAppContext());
        SharedPreferences.Editor prefer = pref.edit();
        prefer.putString(PREF_IP, Address.get());
        prefer.putString(PREF_PORT, Port.get());
        prefer.apply();
    }
}
