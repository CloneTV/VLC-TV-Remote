package ru.ps.vlcatv.utils.playlist.parse;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.ps.vlcatv.constanttag.DataUriApi;
import ru.ps.vlcatv.utils.Text;
import ru.ps.vlcatv.utils.playlist.PlayListConstant;

public class ParseControllerKINOP {

    private final KINOParseObject kpo = new KINOParseObject();
    private int type;

    public ParseControllerKINOP(final String title, int t) {
        try {
            type = t;
            final String[] FIND_TYPE = new String[]{
                    "",                                         // MOVIE
                    //"&m_act%5Bcontent_find%5D=film",          // MOVIE
                    "&m_act%5Bcontent_find%5D=serial",          // SEASON
                    "&m_act%5Bcontent_find%5D=serial",          // EPISODE
                    "",
            };
            final Document doc = Jsoup.connect(
                    String.format(
                            Locale.getDefault(),
                            "https://www.kinopoisk.ru/index.php?kp_query=%s%s",
                            buildUrl(title),
                            FIND_TYPE[ParseUtils.getUriType(type)]
                    ))
                    .userAgent(PlayListConstant.UA)
                    .referrer(PlayListConstant.KINOP_REF)
                    .followRedirects(true)
                    .get();

            if (doc == null)
                throw new RuntimeException("Kinopoisk Document empty");
            kpo.parse(doc);

        } catch (final Exception e) {
            kpo.strError = Text.requireString(e.getLocalizedMessage() + " [" + title + "]");
            throw new RuntimeException(Text.requireString(e.getLocalizedMessage()));
        }
    }

    private String buildUrl(final String title) throws UnsupportedEncodingException {
        return URLEncoder
                .encode(title, StandardCharsets.UTF_8.displayName())
                .replace("+", "%20");
    }

    public String getId() {
        return kpo.strId;
    }
    public String getTitle() {
        return kpo.strTitle;
    }
    public String getError() {
        return kpo.strError;
    }
    public boolean isEmpty() {
        return (Text.isempty(kpo.strId));
    }

    @Override
    public String toString() {
        return toJson();
    }
    public String toJson() {
        return "{\"searchType\":\"" + ParseUtils.getType(type) + "\"" +
                ",\"type\":\"kpo\"" +
                ",\"results\":[{" +
                "\"id\":\"" + kpo.strId + "\"" +
                ",\"year\":" + kpo.strYear +
                ",\"title\":\"" + kpo.strTitle + "\"" +
                ",\"image\":\"" + kpo.strImage + "\"" +
                ",\"rating\":" + kpo.strRatingKpo +
                "}],\"errorMessage\":\"" + getError() + "\"}";
    }

    private static class KINOParseObject {

        final static String ELE_TITLE = "p.name";
        final static String ELE_POSTER = "p.pic";
        final static String ELE_RATING = "div.rating";
        final static String TAG_LINK = "a";
        final static String TAG_IMAGE = "img";
        final static String TAG_TITLE = "title";
        final static Pattern[] pattern = new Pattern[] {
                Pattern.compile("([\\w\\s\\S,:_-]+)\\s\\(.*\\)\\s([\\d]+)", Pattern.UNICODE_CASE),
                Pattern.compile(".*/([\\d]+)\\.", Pattern.UNICODE_CASE)
        };
        private static final String EXCEPT = "element not found, media search failed";

        private String strId = null;
        private String strYear = null;
        private String strTitle = null;
        private String strImage = null;
        private String strRatingKpo = null;
        public String strError = "";

        private void parse(final Document doc) {
            try {

                String str;
                Element ele;

                if ((ele = parseElement(doc, KINOParseObject.ELE_POSTER, KINOParseObject.TAG_LINK)) == null)
                    throw new RuntimeException(EXCEPT + " (1)");

                if ((ele = ele.select(KINOParseObject.TAG_IMAGE).first()) == null)
                    throw new RuntimeException(EXCEPT + " (2)");

                str = ele.attr(KINOParseObject.TAG_TITLE);
                if (Text.isempty(str))
                    throw new RuntimeException(EXCEPT + " (3)");

                Matcher m = KINOParseObject.pattern[1].matcher(str.trim());
                if (m.find()) {
                    if (m.groupCount() >= 1)
                        strId = m.group(1).trim();
                }

                str = String.format(
                        Locale.getDefault(),
                        PlayListConstant.KINOP_IMG, str
                );

                try {
                    URL url = new URL(str);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod(DataUriApi.HTTP_ATTR_GET);
                    con.addRequestProperty(DataUriApi.HTTP_ATTR_UA, PlayListConstant.UA);
                    con.addRequestProperty(DataUriApi.HTTP_ATTR_REF, PlayListConstant.KINOP_REF);
                    con.setInstanceFollowRedirects(false);
                    con.connect();
                    str = con.getHeaderField(DataUriApi.HTTP_ATTR_LOC);
                } catch (Exception e) {
                    throw new RuntimeException(Text.requireString(e.getLocalizedMessage()));
                }

                if (Text.isempty(str))
                    throw new RuntimeException(EXCEPT + " (4)");

                int pos = str.lastIndexOf('/');
                if (pos > 0)
                    str = str.substring(0, pos);

                strImage = String.format(
                        Locale.getDefault(),
                        "%s/300x450", str
                );
                strRatingKpo = parseElementText(doc, KINOParseObject.ELE_RATING);

                str = parseElementText(doc, KINOParseObject.ELE_TITLE);
                if (Text.isempty(str))
                    throw new RuntimeException(EXCEPT + " (5)");

                m = KINOParseObject.pattern[0].matcher(str.trim());
                if (m.find()) {
                    if (m.groupCount() >= 1)
                        strTitle = m.group(1).trim();
                    if (m.groupCount() >= 2)
                        strYear = m.group(2).trim();
                }

            } catch (Exception e) {
                strError = Text.requireString(e.getLocalizedMessage());
                throw new RuntimeException(strError);
            }
        }
        private String parseElementText(final Document doc, final String ele) {
            Element element = doc.select(ele).first();
            if (element == null)
                return null;
            return element.text().trim();
        }
        private Element parseElement(final Document doc, final String ele, final String tag) {
            Element element = doc.select(ele).first();
            if (element == null)
                return null;
            return element.select(tag).first();
        }
    }
}
