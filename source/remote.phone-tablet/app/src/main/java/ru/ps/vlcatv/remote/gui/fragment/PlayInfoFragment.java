package ru.ps.vlcatv.remote.gui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;

import ru.ps.vlcatv.remote.AppMain;
import ru.ps.vlcatv.remote.R;
import ru.ps.vlcatv.remote.data.DataMediaItem;
import ru.ps.vlcatv.remote.data.DataSharedControl;
import ru.ps.vlcatv.remote.data.SettingsInterface;
import ru.ps.vlcatv.remote.databinding.FragmentPlayInfoBinding;

public class PlayInfoFragment extends Fragment implements FragmentInterface, SettingsInterface {

    public DataSharedControl status;
    public DataMediaItem item = new DataMediaItem();
    private ImageView imageView;

    @Override
    public View onCreateView(@NotNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

        status = AppMain.getStatus();
        FragmentPlayInfoBinding binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_play_info,
                container,
                false);
        binding.setFrag(this);
        View v = binding.getRoot();
        imageView = v.findViewById(R.id.img_logo);
        item.copy(status.MmItem);
        item.updatePoster(imageView);
        AppMain.getStatus().eventItem.setCallbackChanged(this);
        AppMain.getStatus().eventState.setCallbackChanged(this);
        return v;
    }
    @Override
    public void onResume() {
        super.onResume();
        onPlayStateChange();
        AppMain.getStatus().AppInfo.set(true);
        AppMain.getStatus().eventItem.setCallbackChanged(this);
        AppMain.getStatus().eventState.setCallbackChanged(this);
    }
    @Override
    public void onPause() {
        AppMain.getStatus().AppInfo.set(false);
        AppMain.getStatus().eventItem.removeCallbackChanged(this);
        AppMain.getStatus().eventState.removeCallbackChanged(this);
        super.onPause();
    }
    @Override
    public void setTitle(String s) {}

    @Override
    public void onSettingsChange() {
    }

    @Override
    public void onPlayStateChange() {
        try {
            item.copy(status.MmItem);
            item.updatePoster(imageView);
        } catch (Exception ignored) {}
    }

    @Override
    public void onPlayItemChange() {
        try {
            item.setDurations(
                    status.TimeTotal.get(),
                    status.TimeCurrent.get(),
                    status.TimeRemain.get(),
                    status.TimeType.get()
            );
        } catch (Exception ignored) {}
    }

    @Override
    public void onHistoryChange() {
    }
}
