package ru.ps.vlcatv.utils.playlist.parse;

import androidx.annotation.NonNull;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import ru.ps.vlcatv.utils.Text;
import ru.ps.vlcatv.utils.playlist.PlayListConstant;

public class ParseControllerIMDB {

    private final IMDBParseObject imdb  = new IMDBParseObject();
    private int type;

    public ParseControllerIMDB(final String title, int t) {
        try {
            type = t;
            final String[] FIND_TYPE = new String[]{
                    "&ref_=nv_sr_sm",                           // MOVIE
                    "&s=tt&ttype=tv&ref_=fn_tv",                // SEASON
                    "&s=tt&ttype=ep&exact=true&ref_=fn_tt_ex",  // EPISODE
                    "",
            };
            final Document doc = Jsoup.connect(
                    String.format(
                            Locale.getDefault(),
                            "https://www.imdb.com/find?q=%s&languages=%s%s",
                            titleEncode(title),
                            Locale.getDefault().getLanguage(),
                            FIND_TYPE[ParseUtils.getUriType(type)]
                    ))
                    .userAgent(PlayListConstant.UA)
                    .referrer(PlayListConstant.IMDB_REF)
                    .followRedirects(true)
                    .get();

            if (doc == null)
                throw new RuntimeException("IMDB Document empty");
            imdb.parse(doc);

        } catch (final Exception e) {
            imdb.strError = Text.requireString(e.getLocalizedMessage() + " [" + title + "]");
            throw new RuntimeException(imdb.strError);
        }
    }

    private String titleEncode(final String title) throws UnsupportedEncodingException {
        return URLEncoder
                .encode(title, StandardCharsets.UTF_8.displayName())
                .replace("+", "%20");
    }
    public String getId() {
        return imdb.strId;
    }
    public String getTitle() {
        return imdb.strTitle;
    }
    public String getError() {
        return imdb.strError;
    }
    public boolean isEmpty() {
        return (Text.isempty(imdb.strId));
    }

    @Override
    public @NonNull String toString() {
        return toJson();
    }
    public @NonNull String toJson() {
        return "{\"searchType\":\"" + ParseUtils.getType(type) + "\"" +
                ",\"type\":\"imdb\"" +
                ",\"results\":[{" +
                "\"id\":\"" + imdb.strId + "\"" +
                ",\"year\":" + imdb.strYear +
                ",\"title\":\"" + imdb.strTitle + "\"" +
                ",\"image\":\"" + imdb.strImage + "\"" +
                "}],\"errorMessage\":\"" + getError() + "\"}";
    }

    private static class IMDBParseObject {
        private final static int PAT_ID = 0;
        private final static int PAT_TITLE_YEAR = 1;
        private final static int PAT_IMAGE = 2;
        private final static int REGEX_FLAG =
                Pattern.CASE_INSENSITIVE |
                        Pattern.UNICODE_CASE |
                        Pattern.UNIX_LINES;
        private static final String EXCEPT = "element not found, media search failed";
        // private static final String IMAGE_REPLACE_V1 = "._V1_SX300";
        private static final String IMAGE_REPLACE_V2 = "_V2_UX300_CR0,0,300,0_AL_";
        private static final String ELE_TEXT = "td.result_text";
        private static final String ELE_PHOTO = "td.primary_photo";
        private static final String TAG_IMAGE = "img";
        private static final String TAG_LINK = "a";
        private static final String ATTR_HREF = "href";
        private static final String ATTR_SRC = "src";
        private static final Pattern[] pattern = new Pattern[] {
                Pattern.compile("^/title/([t0-9]+)/", REGEX_FLAG),
                Pattern.compile("^([\\W\\s\\S]+)\\s\\((\\d+)\\)", REGEX_FLAG),
                Pattern.compile("@\\.([A-Z0-9_,]+)\\..*$", REGEX_FLAG)
        };
        public String strId = null;
        public String strYear = null;
        public String strTitle = null;
        public String strImage = null;
        public String strError = "";

        public void parse(final Document doc) {
            try {

                Element ele = parseElement(doc, IMDBParseObject.ELE_TEXT, IMDBParseObject.TAG_LINK);
                if (ele == null)
                    throw new RuntimeException(EXCEPT + " (1)");

                String str = ele.attr(IMDBParseObject.ATTR_HREF);
                if (Text.isempty(str))
                    throw new RuntimeException(EXCEPT + " (2)");

                ///

                Matcher m = IMDBParseObject.pattern[PAT_ID].matcher(str);
                if ((m.find()) && (m.groupCount() >= 1)) {
                    strId = Objects.requireNonNull(m.group(1)).trim();
                }
                str = parseElementText(doc, IMDBParseObject.ELE_TEXT);
                if (!Text.isempty(str)) {
                    m = IMDBParseObject.pattern[PAT_TITLE_YEAR].matcher(str);
                    if (m.find()) {
                        if (m.groupCount() >= 1)
                            strTitle = Objects.requireNonNull(m.group(1)).trim();
                        if (m.groupCount() >= 2)
                            strYear = Objects.requireNonNull(m.group(2)).trim();
                    } else {
                        strTitle = str;
                    }
                }

                ///

                ele = parseElement(doc, IMDBParseObject.ELE_PHOTO, IMDBParseObject.TAG_IMAGE);
                if (ele == null)
                    throw new RuntimeException(EXCEPT + " (3)");

                str = ele.attr(IMDBParseObject.ATTR_SRC);
                if (Text.isempty(str))
                    return;

                ///

                m = IMDBParseObject.pattern[PAT_IMAGE].matcher(str);
                if (m.find()) {
                    if (m.groupCount() >= 1) {
                        String part = Objects.requireNonNull(m.group(1)).trim();
                        if (!Text.isempty(part))
                            strImage = str.replace(part, IMAGE_REPLACE_V2);
                        else
                            strImage = str;
                    }
                } else {
                    strImage = str;
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
