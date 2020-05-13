package ru.ps.vlcatv.traktoauth.TraktTv.data;

import androidx.annotation.Keep;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.ps.vlcatv.utils.reflect.ReflectAttribute;
import ru.ps.vlcatv.utils.reflect.FieldReflect;
import ru.ps.vlcatv.utils.reflect.ObjectReflect;

@Keep
public class MediaObject extends ReflectAttribute {

    public enum MediaIdType {
        ID_TYPE_TRAKT,
        ID_TYPE_SLUG,
        ID_TYPE_IMDB,
        ID_TYPE_TMDB,
        ID_TYPE_NONE
    }
    public enum MediaObjectType {
        MEDIA_TYPE_MOVIE,
        MEDIA_TYPE_EPISODE,
        MEDIA_TYPE_NONE
    }

    public MediaObject(String title, int year, String slug) {
        mediaTitle = title;
        mediaYear = year;
        mediaIds = new MediaId(MediaIdType.ID_TYPE_SLUG, slug);
        mediaType = MediaObjectType.MEDIA_TYPE_MOVIE;
    }
    public MediaObject(String title, int year, String slug, int season, int episode) {
        mediaTitle = title;
        mediaYear = year;
        mediaISeason = season;
        mediaIEpisode = episode;
        mediaIds = new MediaId(MediaIdType.ID_TYPE_SLUG, slug);
        mediaType = MediaObjectType.MEDIA_TYPE_EPISODE;
    }
    public MediaObject(String title, int year, int season, int episode, MediaIdType t, int id, MediaObjectType ot) {
        mediaTitle = title;
        mediaYear = year;
        mediaISeason = season;
        mediaIEpisode = episode;
        mediaIds = new MediaId(t, id);
        mediaType = ot;
    }

    public MediaObject(MediaObjectType t, int id) {
        mediaIds = new MediaId(MediaIdType.ID_TYPE_TRAKT, id);
        mediaType = t;
    }
    public MediaObject(MediaIdType t, int id, MediaObjectType ot) {
        mediaIds = new MediaId(t, id);
        mediaType = ot;
    }

    // "{...,\"episode\":{\"ids\":{\"trakt\":75539}}}"
    // "{...,\"episode\":{\"season\":1,\"number\":2,\"title\":\"Моя Мертвая Няня\",\"ids\":{\"tmdb\":64811}}}"

    @FieldReflect("watched_at")
    private String mediaUpdate = null;
    @FieldReflect("title")
    private String mediaTitle = null;
    @FieldReflect("year")
    private int mediaYear = -1;

    @FieldReflect("season")
    private int mediaISeason = -1;
    @FieldReflect("number") // episode
    private int mediaIEpisode = -1;

    @ObjectReflect("ids")
    private MediaId mediaIds = null;
    private MediaObjectType mediaType = MediaObjectType.MEDIA_TYPE_NONE;

    public int getTraktId() {
        if (mediaIds == null)
            return -1;
        return mediaIds.traktId;
    }
    public JSONObject toJsonScrobble(JSONObject obj) throws JSONException {
        obj.put(objectType(false), toJson());
        return obj;
    }
    public JSONObject toJsonHistory() throws JSONException {
        mediaUpdate = OauthDataHolder.getUtc8601();
        JSONArray arr = new JSONArray();
        arr.put(toJson());
        JSONObject obj = new JSONObject();
        obj.put(objectType(true), arr);
        return obj;
    }

    private String objectType(boolean b) {
        switch (mediaType) {
            case MEDIA_TYPE_MOVIE:
                return ((b) ? "movies" : "movie");
            case MEDIA_TYPE_EPISODE:
                return ((b) ? "episodes" : "episode");
            default:
                return "";
        }
    }

    @Keep
    private static class MediaId extends ReflectAttribute {
        @FieldReflect("trakt")
        private int traktId = -1;
        @FieldReflect("tmdb")
        private int tmdbId = -1;
        @FieldReflect("slug")
        private String slugId = null;
        @FieldReflect("imdb")
        private String imdbId = null;

        boolean isempty = true;

        MediaId(MediaIdType t, String s) {
            switch (t) {
                case ID_TYPE_SLUG:
                    slugId = s;
                    isempty = false;
                    break;
                case ID_TYPE_IMDB:
                    imdbId = s;
                    isempty = false;
                    break;
                default:
                    break;
            }
        }
        MediaId(MediaIdType t, int n) {
            switch (t) {
                case ID_TYPE_TRAKT:
                    traktId = n;
                    isempty = false;
                    break;
                case ID_TYPE_TMDB:
                    tmdbId = n;
                    isempty = false;
                    break;
                case ID_TYPE_IMDB:
                    imdbId = Integer.toString(n);
                    isempty = false;
                    break;
                default:
                    break;
            }
        }
    }

}
