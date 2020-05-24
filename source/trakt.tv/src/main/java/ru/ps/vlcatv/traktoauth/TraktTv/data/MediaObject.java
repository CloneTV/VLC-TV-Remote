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

    public MediaObject() {}
    public MediaObject setMovieTitle(String title) {
        mediaTitle = title;
        return this;
    }
    public MediaObject setMovieYear(int year) {
        mediaYear = year;
        return this;
    }
    public MediaObject setMovieSeason(int season, int episode) {
        mediaISeason = season;
        mediaIEpisode = episode;
        mediaType = MediaObjectType.MEDIA_TYPE_EPISODE;
        return this;
    }
    public MediaObject setMovieType(MediaObjectType t) {
        mediaType = t;
        return this;
    }
    public MediaObject setMovieId(MediaIdType t, int id) {
        mediaIds = new MediaId(t, id);
        return this;
    }
    public MediaObject setMovieId(int id) {
        mediaIds = new MediaId(MediaIdType.ID_TYPE_TRAKT, id);
        return this;
    }
    public MediaObject setMovieId(String slug) {
        mediaIds = new MediaId(MediaIdType.ID_TYPE_SLUG, slug);
        mediaType = MediaObjectType.MEDIA_TYPE_MOVIE;
        return this;
    }
    public MediaObject build() {
        return this;
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
