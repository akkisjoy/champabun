package champak.champabun.business.dataclasses;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import champak.champabun.business.definition.IConstant;

public class AppDatabase {
    private static final String APP_DB_KEY = "appsettings";
    private static final String KEY_SLEEP_TIMER = "sleep_timer";
    private static final String KEY_SLEEP_TIMER_HOUR = "sleep_timer_hour";
    private static final String KEY_SLEEP_TIMER_MINUTE = "sleep_timer_min";
    private static final String KEY_DURATION_FILTER = "duration_filter";
    private static final String KEY_ALBUM_SORT = "Album_Sort";
    private static final String KEY_ARTIST_SORT = "Artist_Sort";
    private static final String KEY_SONG_SORT = "Song_Sort";
    private static final String KEY_GENRE_SORT = "Genre_Sort";
    private static final String KEY_AUTO_DOWNLOAD_ALBUM_ART = "auto_download_albumart";
    private static final String KEY_APP_FULLSCREEN = "app_fullscreen";
    private static final String KEY_LANGUAGE = "language";

    public static AppSetting GetAppSetting(Context context) {
        AppSetting settings = new AppSetting();

        SharedPreferences pref = context.getSharedPreferences(APP_DB_KEY, Context.MODE_PRIVATE);
        String appsettings = pref.getString(KEY_SLEEP_TIMER, "");
        if (appsettings.length() > 0) {
            try {
                JSONObject sleep_time = new JSONObject(appsettings);
                int hour = sleep_time.getInt(KEY_SLEEP_TIMER_HOUR);
                int minute = sleep_time.getInt(KEY_SLEEP_TIMER_MINUTE);
                settings.setSleepTimer(new SleepTime(hour, minute));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        int duration = pref.getInt(KEY_DURATION_FILTER, IConstant.DURATION_FILTER_DEFAULT);
        settings.setDurationFilterTime(duration);

        // album sort
        settings.setAlbumSortKey(pref.getString(KEY_ALBUM_SORT, null));

        // artist sort
        settings.setArtistSortKey(pref.getString(KEY_ARTIST_SORT, null));

        // song sort
        settings.setSongSortKey(pref.getString(KEY_SONG_SORT, null));

        // genre sort
        settings.setGenreSortKey(pref.getString(KEY_GENRE_SORT, null));

        // genre sort
        settings.setLanguage(pref.getInt(KEY_LANGUAGE, ELanguage.EN.getLanguageCode()));

        settings.setAutoDownloadAlbumArt(pref.getBoolean(KEY_AUTO_DOWNLOAD_ALBUM_ART, true));
        settings.setAppFullscreen(pref.getBoolean(KEY_APP_FULLSCREEN, false));

        return settings;
    }

    public static void SaveSleepTimer(Context context, SleepTime st) {
        JSONObject jobj = new JSONObject();
        try {
            jobj.accumulate(KEY_SLEEP_TIMER_HOUR, st.getHour());
            jobj.accumulate(KEY_SLEEP_TIMER_MINUTE, st.getMinute());

            SharedPreferences.Editor editor = context.getSharedPreferences(APP_DB_KEY, Context.MODE_PRIVATE).edit();
            editor.putString(KEY_SLEEP_TIMER, jobj.toString());
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void CancelSleepTimer(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(APP_DB_KEY, Context.MODE_PRIVATE).edit();
        editor.remove(KEY_SLEEP_TIMER);
        editor.apply();
    }

    public static void SaveDurationFilterTime(Context context, int duration) {
        SharedPreferences.Editor editor = context.getSharedPreferences(APP_DB_KEY, Context.MODE_PRIVATE).edit();
        editor.putInt(KEY_DURATION_FILTER, duration);
        editor.apply();
    }

    public static void SaveAlbumSortKey(Context context, String key) {
        SharedPreferences.Editor editor = context.getSharedPreferences(APP_DB_KEY, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_ALBUM_SORT, key);
        editor.apply();
    }

    public static void SaveArtistSortKey(Context context, String key) {
        SharedPreferences.Editor editor = context.getSharedPreferences(APP_DB_KEY, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_ARTIST_SORT, key);
        editor.apply();
    }

    public static void SaveSongSortKey(Context context, String key) {
        SharedPreferences.Editor editor = context.getSharedPreferences(APP_DB_KEY, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_SONG_SORT, key);
        editor.apply();
    }

    public static void SaveGenreSortKey(Context context, String key) {
        SharedPreferences.Editor editor = context.getSharedPreferences(APP_DB_KEY, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_GENRE_SORT, key);
        editor.apply();
    }

    public static void SetAutoDownloadAlbumArt(Context context, boolean value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(APP_DB_KEY, Context.MODE_PRIVATE).edit();
        editor.putBoolean(KEY_AUTO_DOWNLOAD_ALBUM_ART, value);
        editor.apply();
    }

    public static boolean IsAppFullscreen(Context context) {
        SharedPreferences pref = context.getSharedPreferences(APP_DB_KEY, Context.MODE_PRIVATE);
        return pref.getBoolean(KEY_APP_FULLSCREEN, false);
    }

    public static void SetAppFullscreen(Context context, boolean value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(APP_DB_KEY, Context.MODE_PRIVATE).edit();
        editor.putBoolean(KEY_APP_FULLSCREEN, value);
        editor.apply();
    }

    public static void SetLanguage(Context context, int language) {
        SharedPreferences.Editor editor = context.getSharedPreferences(APP_DB_KEY, Context.MODE_PRIVATE).edit();
        editor.putInt(KEY_LANGUAGE, language);
        editor.apply();
    }
}
