package ru.ps.vlcatv.utils.playlist;

import androidx.annotation.Keep;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import ru.ps.vlcatv.utils.reflect.annotation.IFieldReflect;

@Keep
public class PlayListEpgItem {
    @IFieldReflect("epg_title")
    public String epgTitle = null;
    @IFieldReflect("epg_date")
    public String epgDate = null;
    private Date date = null;

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
    public PlayListEpgItem get(final Date d1) {
        return get(d1.getTime());
    }
    public PlayListEpgItem get(final long l1) {
        if (date == null)
            return null;
        if (l1 > (date.getTime() + 3600))
            return null;
        return this;
    }
}
