package ru.ps.vlcatv.remote.gui.fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableField;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ru.ps.vlcatv.constanttag.DataUriApi;
import ru.ps.vlcatv.remote.AppMain;
import ru.ps.vlcatv.remote.BuildConfig;
import ru.ps.vlcatv.remote.R;
import ru.ps.vlcatv.remote.databinding.FragmentSearchBinding;
import ru.ps.vlcatv.remote.databinding.ListviewSearchItemBinding;
import ru.ps.vlcatv.utils.Log;
import ru.ps.vlcatv.utils.Text;

interface CallFromHolderInterface {
    public void setQuery();
    public void setQuery(String s);
}

public class SearchFragment extends Fragment
        implements FragmentInterface, TextToSpeech.OnInitListener, CallFromHolderInterface {

    private static final String TAG = SearchFragment.class.getSimpleName();
    private static final int REQUEST_SPEECH = 0x00000010;
    private SearchItemAdapter adapter = null;
    private TextToSpeech textToSpeech = null;
    private boolean isVoiceSearch = false;

    public final ObservableField<String> txtSearch = new ObservableField<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            PackageManager pm = requireContext().getPackageManager();
            assert pm != null;
            List activities = pm.queryIntentActivities(
                    new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0
            );
            adapter = new SearchItemAdapter(this);
            isVoiceSearch = (activities.size() != 0);
            if (BuildConfig.DEBUG) Log.d(TAG, "isVoiceSearch=" + isVoiceSearch);

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e(TAG, e.getLocalizedMessage(), e);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

        FragmentSearchBinding binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_search,
                container,
                false);
        binding.setFrag(this);
        View v = binding.getRoot();

        v.findViewById(R.id.tv_search).requestFocus();
        ImageButton btn = v.findViewById(R.id.imgbtn_micro);

        if (isVoiceSearch) {

            btn.setEnabled(true);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                @SuppressWarnings("deprecation")
                public void onClick(View v) {
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE,true);
                    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.voice_search));
                    startActivityForResult(intent, REQUEST_SPEECH);
                }
            });
        } else {
            btn.setEnabled(false);
        }
        btn = v.findViewById(R.id.imgbtn_go_search);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setQuery();
            }
        });

        RecyclerView recyclerView = v.findViewById(R.id.list_search_tag);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false)
        );
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        return v;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode != REQUEST_SPEECH)
                return;
            if ((resultCode != android.app.Activity.RESULT_OK) || (data == null))
                throw new RuntimeException(getString(R.string.exception_search_1));

            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if ((results == null) || (results.size() == 0))
                throw new RuntimeException(getString(R.string.exception_search_2));

            setQuery(TextUtils.join(" ", results));

        } catch (Exception e) {
            AppMain.printError(e.getLocalizedMessage());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        AppMain.getStatus().AppSearch.set(true);
    }
    @Override
    public void onPause() {
        AppMain.getStatus().AppSearch.set(false);
        if (textToSpeech != null)
            textToSpeech.stop();
        super.onPause();
    }
    @Override
    public void onStart() {
        textToSpeech = new TextToSpeech(getContext(), this);
        super.onStart();
    }
    @Override
    public void onStop() {
        try {
            if (textToSpeech != null) {
                textToSpeech.stop();
                textToSpeech.shutdown();
                textToSpeech = null;
            }
        } catch (Exception ignoring) {}
        super.onStop();
    }

    @Override
    public void setTitle(String s) {}

    @Override
    public void onInit(int status) {}

    @Override
    public void setQuery() {
        try {
            if (Text.isempty(txtSearch.get()))
                return;

            String b64 = new String(
                    Base64
                      .encodeToString(Objects.requireNonNull(txtSearch.get()).getBytes(),
                    Base64.DEFAULT)
            );
            if (Text.isempty(b64))
                return;

            /*
            if (b64.charAt(b64.length() - 2) == '=')
                b64 = b64.replaceAll("[=]", "");
            */

            AppMain.getRequest(DataUriApi.GET_SEARCH_ACTIVITY, b64.trim());
            if (adapter != null)
                adapter.add(txtSearch.get());
            if (BuildConfig.DEBUG) Log.e(TAG, "search=[" + txtSearch.get() + "], Base64=[" + b64 + "]");

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e(TAG, e.getLocalizedMessage(), e);
        }
    }
    @Override
    public void setQuery(String s) {
        if (Text.isempty(s))
            return;
        txtSearch.set(s);
        setQuery();
    }

    static protected class SearchItemAdapter extends RecyclerView.Adapter<SearchItemAdapter.SearchItemViewHolder> {

        private CallFromHolderInterface rootClass;

        SearchItemAdapter(CallFromHolderInterface iface) {
            rootClass = iface;
        }

        @NonNull
        @Override
        public SearchItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ListviewSearchItemBinding bind =
                    DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                            R.layout.listview_search_item, parent, false);
            return new SearchItemViewHolder(bind, rootClass);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchItemViewHolder holder, final int position) {
            String s = AppMain.getStatus().SearchTag.get(position);
            holder.bind.setTag(s);
        }

        @Override
        public int getItemCount() {
            return AppMain.getStatus().SearchTag.size();
        }

        void add(String s)
        {
            if (!AppMain.getStatus().SearchTag.contains(s)) {
                AppMain.getStatus().SearchTag.add(s);
                notifyDataSetChanged();
                if (BuildConfig.DEBUG) Log.e(TAG, "items.add=" + s);
            }
        }

        static class SearchItemViewHolder extends RecyclerView.ViewHolder {
            final ListviewSearchItemBinding bind;
            final CallFromHolderInterface rootClass;

            SearchItemViewHolder(@NonNull ListviewSearchItemBinding b, CallFromHolderInterface iface) {
                super(b.getRoot());
                rootClass = iface;
                bind = b;
                bind.getRoot()
                        .findViewById(R.id.lv_tag)
                        .setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    rootClass.setQuery(
                                            ((TextView) v).getText().toString()
                                    );
                                } catch (Exception ignoring) {}
                            }
                        });
            }
        }
    }
}
