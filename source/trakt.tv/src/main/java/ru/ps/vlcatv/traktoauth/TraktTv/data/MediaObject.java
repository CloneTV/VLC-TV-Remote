package ru.ps.vlcatv.traktoauth.TraktTv.data;

import androidx.annotation.Keep;
import ru.ps.vlcatv.utils.reflect.ReflectAttribute;
import ru.ps.vlcatv.utils.reflect.annotation.IFieldReflect;
import ru.ps.vlcatv.utils.reflect.annotation.IObjectReflect;
import ru.ps.vlcatv.utils.json.JSONObject;
import ru.ps.vlcatv.utils.json.JSONArray;
import ru.ps.vlcatv.utils.json.JSONException;

@Keep
public class MediaObject extends ReflectAttribute {

    public enum MediaIdType {
        ID_TYPE_TRAKT,
        ID_TYPE_SLUG,
        ID_TYPE_IMDB,
        ID_TYPE_TMDB,
        ID_TYPE_OMDB,
        ID_TYPE_TITLE,
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
    public MediaObject setMovieId(String slug, MediaIdType type) {
        mediaIds = new MediaId(type, slug);
        return this;
    }
    public MediaObject build() {
        return this;
    }

    // "{...,\"episode\":{\"ids\":{\"trakt\":75539}}}"
    // "{...,\"episode\":{\"season\":1,\"number\":2,\"title\":\"Моя Мертвая Няня\",\"ids\":{\"tmdb\":64811}}}"

    @IFieldReflect("watched_at")
    private String mediaUpdate = null;
    @IFieldReflect("title")
    private String mediaTitle = null;
    @IFieldReflect("year")
    private int mediaYear = -1;

    @IFieldReflect("season")
    private int mediaISeason = -1;
    @IFieldReflect("number") // episode
    private int mediaIEpisode = -1;

    @IObjectReflect("ids")
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
        @IFieldReflect("trakt")
        private int traktId = -1;
        @IFieldReflect("tmdb")
        private int tmdbId = -1;
        @IFieldReflect("slug")
        private String slugId = null;
        @IFieldReflect("imdb")
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
