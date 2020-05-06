package ru.ps.vlcatv.remote.data;

import android.view.View;
import android.widget.ImageView;
import androidx.databinding.BaseObservable;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import com.squareup.picasso.Picasso;
import org.json.JSONObject;
import java.util.Locale;

import ru.ps.vlcatv.remote.AppMain;
import ru.ps.vlcatv.remote.BuildConfig;
import ru.ps.vlcatv.remote.R;
import ru.ps.vlcatv.remote.Utils;

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
            title.set(obj.optString("titleTxt", ""));
            description.set(obj.optString("descriptionTxt", ""));
            category.set(obj.optString("CategoryTxt", ""));
            date.set(obj.optString("dateTxt", ""));
            rating.set(obj.optString("ratingTxt", ""));
            poster.set(obj.optString("arturlTxt", ""));
            filename.set(obj.optString("filenameTxt", ""));
            nfo.set(obj.optString("nfoTxt", ""));

            season.set(obj.optInt("seasonInt", -1));
            episode.set(obj.optInt("episodeInt", -1));
            id.set(obj.optInt("IdInt", -1));
            grpid.set(obj.optInt("grpIdInt", -1));
            grpitem.set(obj.optInt("grpItemInt", -1));
            grptotal.set(obj.optInt("CategoryTotalInt", -1));
            type.set(obj.optInt("typeIdInt", -1));
            duration.set(obj.optInt("DurationInt", -1));
            lastpos.set(obj.optInt("LastposInt", -1));
            dateVisible.set(
                    ((Utils.isempty(date.get())) ? View.GONE : View.VISIBLE)
            );
            descVisible.set(
                    ((Utils.isempty(description.get())) ? View.GONE : View.VISIBLE)
            );
            ratingVisible.set(
                    ((Utils.isempty(rating.get())) ? View.GONE : View.VISIBLE)
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
                boolean b = (duration.get() > 60);
                int d = (b ? (duration.get() / 60) : duration.get());
                int l = (b ? ((lastpos.get() >= 60) ? (lastpos.get() / 60) : 0) : lastpos.get());
                int r = (d - l);
                time.set(
                        String.format(Locale.getDefault(), "%d/%d/%d %s",
                                d, l, r,
                                (b ?
                                   AppMain.getAppResources().getQuantityString(R.plurals.plurals_minutes, r) :
                                   AppMain.getAppResources().getQuantityString(R.plurals.plurals_second, r)
                                )
                        )
                );
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

    public void updatePoster(ImageView imgv)
    {
        if (Utils.isempty(poster.get())) {
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
        return ((Utils.isempty(title.get())) || (id.get() == -1));
    }
}
