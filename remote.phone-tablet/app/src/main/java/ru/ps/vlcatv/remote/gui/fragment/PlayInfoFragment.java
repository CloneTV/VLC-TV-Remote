package ru.ps.vlcatv.remote.gui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import ru.ps.vlcatv.remote.AppMain;
import ru.ps.vlcatv.remote.R;
import ru.ps.vlcatv.remote.data.DataMediaItem;
import ru.ps.vlcatv.remote.data.DataSharedControl;
import ru.ps.vlcatv.remote.databinding.FragmentPlayInfoBinding;

public class PlayInfoFragment extends Fragment implements FragmentInterface {

    public DataMediaItem item = new DataMediaItem();
    public DataSharedControl status;
    private ImageView imageView;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

        status = AppMain.getStatus();
        item.copy(status.MmItem);
        FragmentPlayInfoBinding binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_play_info,
                container,
                false);
        binding.setFrag(this);
        View v = binding.getRoot();
        imageView = v.findViewById(R.id.img_logo);
        item.updatePoster(imageView);
        return v;
    }
    @Override
    public void onResume() {
        super.onResume();
        AppMain.getStatus().AppInfo.set(true);
        item.copy(status.MmItem);
        item.updatePoster(imageView);
    }
    @Override
    public void onPause() {
        AppMain.getStatus().AppInfo.set(false);
        super.onPause();
    }
    @Override
    public void setTitle(String s) {}
}