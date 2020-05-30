package ru.ps.vlcatv.utils.playlist;

public class PlayListConstant {

    public static final int TYPE_MOVIE = 1000;
    public static final int TYPE_SEASON = 1001;
    public static final int TYPE_SERIES = 1002;
    public static final int TYPE_SHOWS = 1003;
    public static final int TYPE_FOLDER = 1004;
    public static final int TYPE_AUDIO = 1005;
    public static final int TYPE_VIDEO = 1006;
    public static final int TYPE_ONLINE = 1007;
    public static final int TYPE_NODE = 1021;
    public static final int TYPE_LEAF = 1022;
    public static final int TYPE_FOLDER_AUDIO = 1023;
    public static final int TYPE_FOLDER_MOVIE = 1024;
    public static final int TYPE_FOLDER_SHOWS = 1025;
    public static final int TYPE_CARD_MENU = 1026;
    public static final int TYPE_CARD_CTRL = 1027;
    public static final int TYPE_CARD_SEARCH_TAG = 1028;
    public static final int TYPE_SOURCE_OMDB = 1029;
    public static final int TYPE_NONE = -1;

    public static final String IDS_VLC = "vlc";
    public static final String IDS_EPG = "epg";
    public static final String IDS_IMDB = "imdb";
    public static final String IDS_TMDB = "tmdb";
    public static final String IDS_TRAKT = "trakt";
    public static final String IDS_META = "meta";
    public static final String IDS_RATED = "rated";
    public static final String IDS_RATING = "rating";
    public static final String IDS_VOTES = "votes";
    public static final String IDS_AWARDS = "awards";
    public static final String IDS_SERIES_ID = "seriesid";
    public static final String IDS_GRP_ID = "grpid";
    public static final String IDS_GRP_IDX = "grpidx";

    /// "https://clonetv.github.io/VLC-TV-Remote/media/radio.list"
    public static final String RADIO_ONLINE = "aHR0cHM6Ly9jbG9uZXR2LmdpdGh1Yi5pby9WTEMtVFYtUmVtb3RlL21lZGlhL3JhZGlvLmxpc3Q=";
    /// "https://clonetv.github.io/VLC-TV-Remote/media/films.list"
    public static final String FILMS_ONLINE = "aHR0cHM6Ly9jbG9uZXR2LmdpdGh1Yi5pby9WTEMtVFYtUmVtb3RlL21lZGlhL2ZpbG1zLmxpc3Q=";
    public static final String[] TV_ONLINE_ARRAY = new String[] {
            /// "https://smarttvnews.ru/apps/iptvchannels.m3u"
            "aHR0cHM6Ly9zbWFydHR2bmV3cy5ydS9hcHBzL2lwdHZjaGFubmVscy5tM3U=",
            /// "https://smarttvnews.ru/apps/Channels.m3u"
            "aHR0cHM6Ly9zbWFydHR2bmV3cy5ydS9hcHBzL0NoYW5uZWxzLm0zdQ==",
            /// "https://smarttvnews.ru/apps/AutoIPTV.m3u"
            "aHR0cHM6Ly9zbWFydHR2bmV3cy5ydS9hcHBzL0F1dG9JUFRWLm0zdQ==",
            /// "https://smarttvnews.ru/apps/iptvfreefull.m3u"
            "aHR0cHM6Ly9zbWFydHR2bmV3cy5ydS9hcHBzL2lwdHZmcmVlZnVsbC5tM3U="
    };
    ///
    public static final String TV_EPG_ONLINE_JTV_URI = "https://www.teleguide.info/download/old/jtv.zip";
    /// "23be17be"
    //public static final String OMDB_API_ID = "MjNiZTE3YmU=";

}
