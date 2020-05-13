package ru.ps.vlcatv.remote.gui.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

import ru.ps.vlcatv.remote.R;
import ru.ps.vlcatv.remote.AppMain;
import ru.ps.vlcatv.utils.Text;

public class SettingsFragment extends Fragment implements FragmentInterface {

    private TextView tvIp;
    private TextView tvPort;
    private String sIp = null;
    private String sPort = null;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        sIp = AppMain.getSettings().Address.get();
        tvIp = v.findViewById(R.id.tv_ip);
        if (!Text.isempty(sIp))
            tvIp.setText(sIp);
        tvIp.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sIp = tvIp.getText().toString();
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });
        sPort = AppMain.getSettings().Port.get();
        tvPort = v.findViewById(R.id.tv_port);
        if (!Text.isempty(sPort))
            tvPort.setText(sPort);
        tvPort.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sPort = tvPort.getText().toString();
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });
        return v;
    }
    @Override
    public void onResume() {
        super.onResume();
        AppMain.getStatus().AppSetup.set(true);
    }
    @Override
    public void onPause() {
        AppMain.getStatus().AppSetup.set(false);
        super.onPause();
    }
    @Override
    public void onStop() {
        try {
            if (!Text.isempty(sIp)) {
                if (sIp.length() < 7)
                    throw new IllegalArgumentException(getString(R.string.settings_except_1));

                String[] split = sIp.split("\\.");
                if (split.length != 4)
                    throw new IllegalArgumentException(getString(R.string.settings_except_2));

                for (String value : split) {
                    if (Integer.parseInt(value) > 255)
                        throw new IllegalArgumentException(getString(R.string.settings_except_3));
                }
                AppMain.getSettings().Address.set(sIp);

            } else {
                throw new IllegalArgumentException(getString(R.string.settings_except_1));
            }
            if (!Text.isempty(sPort)) {
                if (sPort.length() < 4)
                    throw new IllegalArgumentException(getString(R.string.settings_except_4));
                if (Integer.parseInt(sPort) >= 65536)
                    throw new IllegalArgumentException(getString(R.string.settings_except_4));
                AppMain.getSettings().Port.set(sPort);
            } else {
                throw new IllegalArgumentException(getString(R.string.settings_except_5));
            }

        } catch (Exception e) {
            AppMain.printError(e.getLocalizedMessage());
        }
        super.onStop();
    }

    @Override
    public void setTitle(String s) {}
}
