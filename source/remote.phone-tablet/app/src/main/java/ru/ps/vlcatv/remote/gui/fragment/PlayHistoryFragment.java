package ru.ps.vlcatv.remote.gui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import ru.ps.vlcatv.remote.AppMain;
import ru.ps.vlcatv.remote.BuildConfig;
import ru.ps.vlcatv.remote.R;
import ru.ps.vlcatv.remote.Utils;
import ru.ps.vlcatv.remote.data.DataMediaItem;
import ru.ps.vlcatv.remote.data.SettingsInterface;
import ru.ps.vlcatv.remote.databinding.FragmentPlayHistoryBinding;

public class PlayHistoryFragment extends Fragment implements FragmentInterface, SettingsInterface {
    public DataMediaItem item = new DataMediaItem();
    private ImageView ivPoster;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(@NotNull final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

        item.copy(AppMain.getStatus().MmItem);
        FragmentPlayHistoryBinding binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_play_history,
                container,
                false);
        binding.setFrag(this);
        View v = binding.getRoot();
        ImageButton iButton = v.findViewById(R.id.imgebtn_history_play);
        ivPoster = v.findViewById(R.id.img_logo);
        recyclerView = v.findViewById(R.id.list_view);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false)
        );
        iButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.id.get() > -1)
                    AppMain.getRequest(
                            AppMain.getStatus().getCtrlCmd(v.getId()),
                            Integer.toString(item.id.get())
                    );
            }
        });
        AppMain.getStatus().eventHistory.setCallbackChanged(this);
        AppMain.getMediaItems();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        AppMain.getStatus().AppHistory.set(true);
        AppMain.getStatus().eventHistory.setCallbackChanged(this);
        AppMain.getMediaItems();
    }

    @Override
    public void onPause() {
        AppMain.getStatus().AppHistory.set(false);
        AppMain.getStatus().eventHistory.removeCallbackChanged(this);
        super.onPause();
    }

    @Override
    public void onHistoryChange() {
        addHistoryList();
    }

    @Override
    public void setTitle(String s) {
    }

    @Override
    public void onSettingsChange() {
    }

    @Override
    public void onPlayStateChange() {
        try {
            item.setDurations(
                    AppMain.getStatus().TimeTotal.get(),
                    AppMain.getStatus().TimeCurrent.get(),
                    AppMain.getStatus().TimeRemain.get(),
                    AppMain.getStatus().TimeType.get()
            );
        } catch (Exception ignored) {}
    }

    @Override
    public void onPlayItemChange() {
        addHistoryList();
    }

    private void addHistoryList() {

        try {
            DataMediaItem[] items = AppMain.getStatus().eventHistory.getItemsList();
            if (items == null)
                return;

            recyclerView.setAdapter(new MediaItemImageAdapter(this, items));
            item.copy(items[(items.length - 1)]);
            item.updatePoster(ivPoster);

        } catch (Exception e) {
            if (BuildConfig.DEBUG) Log.e("parseHistoryList", Objects.requireNonNull(e.getMessage()));
        }
    }

    static protected class MediaItemImageAdapter extends RecyclerView.Adapter<MediaItemImageAdapter.MediaItemViewHolder> {

        private PlayHistoryFragment phf = null;
        private DataMediaItem[] items = null;

        MediaItemImageAdapter(PlayHistoryFragment instance, DataMediaItem[] array) {
            phf = instance;
            items = array;
        }

        @NonNull
        @Override
        public MediaItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View v = inflater.inflate(R.layout.listview_history_item, parent, false);
            return new MediaItemViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull MediaItemViewHolder holder, final int position) {
            holder.pos = position;
            holder.id = items[position].id.get();
            holder.pb.setMax(items[position].duration.get());
            holder.pb.setProgress(items[position].lastpos.get());
            holder.title.setText(items[position].title.get());
            holder.category.setText(items[position].category.get());
            if (!Utils.isempty(items[position].poster.get())) {
                holder.img.setOnClickListener(new View.OnClickListener() {
                    private MediaItemViewHolder hld = holder;
                    @Override
                    public void onClick(View v) {
                        phf.item.copy(items[holder.pos]);
                        items[holder.pos].updatePoster(phf.ivPoster);
                    }
                });
                items[position].updatePoster(holder.img);
            }
        }

        @Override
        public int getItemCount() {
            return ((items == null) ? 0 : items.length);
        }

        static class MediaItemViewHolder extends RecyclerView.ViewHolder {
            ImageView img;
            TextView title;
            TextView category;
            ProgressBar pb;
            int pos = 0;
            int id = -1;

            MediaItemViewHolder(@NonNull View itemView) {
                super(itemView);
                pb = itemView.findViewById(R.id.lv_pb);
                img = itemView.findViewById(R.id.lv_image);
                title = itemView.findViewById(R.id.lv_title);
                category = itemView.findViewById(R.id.lv_category);
            }
        }
    }
}
