package ru.ps.vlcatv.remote.gui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableField;
import androidx.fragment.app.Fragment;
import ru.ps.vlcatv.remote.R;
import ru.ps.vlcatv.remote.databinding.FragmentErrorBinding;

public class ErrorFragment extends Fragment implements FragmentInterface {

    public final ObservableField<String> tvError = new ObservableField<>("");

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

        FragmentErrorBinding binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_error,
                container,
                false);
        binding.setFrag(this);
        return binding.getRoot();
    }
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void setTitle(String s) {
        tvError.set(s);
    }
}
