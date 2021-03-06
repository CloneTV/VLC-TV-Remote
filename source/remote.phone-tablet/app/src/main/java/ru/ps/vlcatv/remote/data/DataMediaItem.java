package ru.ps.vlcatv.remote.data;

import android.view.View;
import android.widget.ImageView;
import androidx.databinding.BaseObservable;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import com.squareup.picasso.Picasso;
import java.util.Locale;

import ru.ps.vlcatv.remote.R;
import ru.ps.vlcatv.constanttag.DataTagPlayItem;
import ru.ps.vlcatv.remote.AppMain;
import ru.ps.vlcatv.remote.BuildConfig;
import ru.ps.vlcatv.utils.Text;
import ru.ps.vlcatv.utils.json.JSONObject;

public class DataMediaItem extends BaseObservable {

    private static final String TAG = DataMediaItem.class.getSimpleName();

    public final ObservableField<String> title = new ObservableField<>("");
    public final ObservableField<String> description = new ObservableField<>("");
    public final ObservableField<String> category = new ObservableField<>("");
    public final ObservableField<String> date = new ObservableField<>("");
    public final ObservableField<String> rating = new ObservableField<>("");
    public final ObservableField<String> poster = new ObservableField<>("");
    public final ObservableField<String> filename = new ObservableField<>("");
    public final ObservableField<String> nfo = new ObservableField<>("");
    public final ObservableField<String> serial = new ObservableField<>("");
    public final ObservableField<String> time = new ObservableField<>("");

    public final ObservableInt season = new ObservableInt(-1);
    public final ObservableInt episode = new ObservableInt(-1);
    public final ObservableInt id = new ObservableInt(-1);
    public final ObservableInt grpid = new ObservableInt(-1);
    public final ObservableInt grpitem = new ObservableInt(-1);
    public final ObservableInt grptotal = new ObservableInt(-1);
    public final ObservableInt lastpos = new ObservableInt(0);
    public final ObservableInt type = new ObservableInt(-1);
    public final ObservableInt duration = new ObservableInt(0);

    public final ObservableInt descVisible = new ObservableInt(View.GONE);
    public final ObservableInt imgVisible = new ObservableInt(View.GONE);
    public final ObservableInt serialVisible = new ObservableInt(View.GONE);
    public final ObservableInt dateVisible = new ObservableInt(View.GONE);
    public final ObservableInt ratingVisible = new ObservableInt(View.GONE);
    public final ObservableInt timeVisible = new ObservableInt(View.GONE);

    public DataMediaItem() {}
    public DataMediaItem(JSONObject obj)
    {
        fromJson(obj);
    }

    public void copy(DataMediaItem d)
    {
        title.set(d.title.get());
        description.set(d.description.get());
        category.set(d.category.get());
        date.set(d.date.get());
        rating.set(d.rating.get());
        poster.set(d.poster.get());
        filename.set(d.filename.get());
        nfo.set(d.nfo.get());

        serial.set(d.serial.get());
        season.set(d.season.get());
        episode.set(d.episode.get());
        time.set(d.time.get());
        id.set(d.id.get());
        grpid.set(d.grpid.get());
        grpitem.set(d.grpitem.get());
        grptotal.set(d.grptotal.get());
        type.set(d.type.get());
        duration.set(d.duration.get());
        lastpos.set(d.lastpos.get());
        dateVisible.set(d.dateVisible.get());
        descVisible.set(d.descVisible.get());
        ratingVisible.set(d.ratingVisible.get());
        serialVisible.set(d.serialVisible.get());
        timeVisible.set(d.timeVisible.get());
    }

    public void fromJson(JSONObject obj)
    {
        try
        {
            title.set(obj.optString(DataTagPlayItem.TAG_TITLE, ""));
            description.set(obj.optString(DataTagPlayItem.TAG_DESC, ""));
            category.set(obj.optString(DataTagPlayItem.TAG_CAT, ""));
            date.set(obj.optString(DataTagPlayItem.TAG_DATE, ""));
            rating.set(obj.optString(DataTagPlayItem.TAG_RATING, ""));
            poster.set(obj.optString(DataTagPlayItem.TAG_POSTER, ""));
            filename.set(obj.optString(DataTagPlayItem.TAG_FILE, ""));
            nfo.set(obj.optString(DataTagPlayItem.TAG_NFO, ""));

            season.set(obj.optInt(DataTagPlayItem.TAG_SEASON, -1));
            episode.set(obj.optInt(DataTagPlayItem.TAG_EPISODE, -1));
            id.set(obj.optInt(DataTagPlayItem.TAG_ID, -1));
            grpid.set(obj.optInt(DataTagPlayItem.TAG_GRPID, -1));
            grpitem.set(obj.optInt(DataTagPlayItem.TAG_GRPITEMID, -1));
            grptotal.set(obj.optInt(DataTagPlayItem.TAG_CATTOTAL, -1));
            type.set(obj.optInt(DataTagPlayItem.TAG_TYPE, -1));
            duration.set(obj.optInt(DataTagPlayItem.TAG_DURATION, -1));
            lastpos.set(obj.optInt(DataTagPlayItem.TAG_LASTPOS, -1));
            dateVisible.set(
                    ((Text.isempty(date.get())) ? View.GONE : View.VISIBLE)
            );
            descVisible.set(
                    ((Text.isempty(description.get())) ? View.GONE : View.VISIBLE)
            );
            ratingVisible.set(
                    ((Text.isempty(rating.get())) ? View.GONE : View.VISIBLE)
            );
            if ((season.get() > -1) && (episode.get() > -1)) {
                serial.set(
                        String.format(Locale.getDefault(), "%d/%d-%d/%d",
                                season.get(),
                                episode.get(),
                                grptotal.get(),
                                id.get()
                        )
                );
                serialVisible.set(View.VISIBLE);
            } else {
                serialVisible.set(View.GONE);
            }
            if (duration.get() > 0) {
                setDurations();
                timeVisible.set(View.VISIBLE);
            } else if (id.get() > -1) {
                time.set(Integer.toString(id.get()));
                timeVisible.set(View.VISIBLE);
            } else {
                timeVisible.set(View.GONE);
            }

        } catch (Exception e) {
            if (BuildConfig.DEBUG) AppMain.printError(e.getLocalizedMessage());
        }
    }

    public void setDurations(int duration, int current, int remain, String s)
    {
        if (duration > 0)
            time.set(String.format(Locale.getDefault(), "%d/%d/%d %s",
                duration, current, remain,
                    ((Text.isempty(s)) ?
                            AppMain.getAppResources()
                                    .getQuantityString(R.plurals.plurals_minutes, remain) :
                            s)
            ));
        else
            time.set("");
    }

    private void setDurations()
    {
        try {
            if (duration.get() > 0) {
                boolean b = (duration.get() > 60);
                int d = (b ? (duration.get() / 60) : duration.get());
                int l = (b ? ((lastpos.get() >= 60) ? (lastpos.get() / 60) : 0) : lastpos.get());
                int r = (d - l);
                time.set(String.format(Locale.getDefault(), "%d/%d/%d %s",
                        d, l, r,
                        (b ?
                                AppMain.getAppResources().getQuantityString(R.plurals.plurals_minutes, r) :
                                AppMain.getAppResources().getQuantityString(R.plurals.plurals_second, r)
                        )
                ));
                return;
            }

        } catch (Exception e) {
            if (BuildConfig.DEBUG) AppMain.printError(e.getLocalizedMessage());
        }
        time.set("");
    }

    public void updatePoster(ImageView imgv)
    {
        if (Text.isempty(poster.get())) {
            imgVisible.set(View.GONE);
        } else {
            try {
                Picasso.get().load(poster.get()).into(imgv);
                imgVisible.set(View.VISIBLE);

            } catch (Exception e) {
                if (BuildConfig.DEBUG) AppMain.printError(e.getLocalizedMessage());
                imgVisible.set(View.GONE);
            }
        }
    }

    public boolean isempty()
    {
        return ((Text.isempty(title.get())) || (id.get() == -1));
    }
}
