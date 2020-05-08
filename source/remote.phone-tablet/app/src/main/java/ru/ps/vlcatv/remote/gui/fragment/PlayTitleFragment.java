package ru.ps.vlcatv.remote.gui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import ru.ps.vlcatv.remote.AppMain;
import ru.ps.vlcatv.remote.R;
import ru.ps.vlcatv.remote.data.DataSharedControl;
import ru.ps.vlcatv.remote.databinding.FragmentPlayTitleBinding;

public class PlayTitleFragment extends Fragment implements FragmentInterface {

    public DataSharedControl status;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

        status = AppMain.getStatus();
        FragmentPlayTitleBinding binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_play_title,
                container,
                false);
        binding.setFrag(this);
        return binding.getRoot();
    }
    @Override
    public void onResume() {
        super.onResume();
        AppMain.getStatus().AppTitle.set(true);
    }
    @Override
    public void onPause() {
        AppMain.getStatus().AppTitle.set(false);
        super.onPause();
    }

    @Override
    public void setTitle(String s) {}
}