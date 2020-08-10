package ru.ps.vlcatv.utils.playlist;

import androidx.annotation.Keep;
import androidx.databinding.ObservableInt;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ru.ps.vlcatv.utils.reflect.ReflectAttribute;
import ru.ps.vlcatv.utils.reflect.annotation.IFieldReflect;

@Keep
public class PlayListEpgItem extends ReflectAttribute {
    @IFieldReflect("epg_title")
    public String epgTitle = null;
    @IFieldReflect("epg_date")
    public String epgDate = null;
    private Date date = null;

    public ObservableInt pbMax = new ObservableInt(0);
    public ObservableInt pbCur = new ObservableInt(0);

    PlayListEpgItem() {}
    public PlayListEpgItem(String t, Date d) {
        epgTitle = t;
        date = d;
        if (d == null) {
            epgDate = "";
        } else {
            final SimpleDateFormat fmt = new SimpleDateFormat(
                    "dd - HH:mm", Locale.getDefault()
            );
            epgDate = fmt.format(d);
        }
    }
    public Date getDate() {
        return date;
    }
    public PlayListEpgItem get(final long d1, final long d2) {
        if (date == null)
            return null;
        final long tm = date.getTime();
        if ((d1 > tm) && (d2 > d1))
            return this;
        else if (d1 > tm)
            return null;
        return this;
    }
}
