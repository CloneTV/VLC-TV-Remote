package ru.ps.vlcatv.utils.playlist.parse;

public class ParseUtils {

    public static String getType(int type) {
        switch (type) {
            case PlayListParseInterface.ID_FMT_MOVIE: return "Movie";
            case PlayListParseInterface.ID_FMT_SEASON: return "Series";
            case PlayListParseInterface.ID_FMT_EPISODE: return "Episode";
            default: return "";
        }
    }
    public static int getUriType(int type) {
        switch (type) {
            case PlayListParseInterface.ID_FMT_MOVIE: return 0;
            case PlayListParseInterface.ID_FMT_SEASON: return 1;
            case PlayListParseInterface.ID_FMT_EPISODE: return 2;
            default: return 3;
        }
    }
}
