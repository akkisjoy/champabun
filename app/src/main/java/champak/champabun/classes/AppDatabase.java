package champak.champabun.classes;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import champak.champabun.IConstant;
import champak.champabun.R;

public class AppDatabase {
    private static final String APP_DB_KEY = "appsettings";
    private static final String KEY_SLEEP_TIMER = "sleep_timer";
    private static final String KEY_SLEEP_TIMER_HOUR = "sleep_timer_hour";
    private static final String KEY_SLEEP_TIMER_MINUTE = "sleep_timer_min";
    private static final String KEY_DURATION_FILTER = "duration_filter";
    private static final String KEY_APP_BG_RES_ID = "bg_res_id";
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

        int resID = pref.getInt(KEY_APP_BG_RES_ID, -1);
        if (resID == -1) {
            int[] arr = new int[]{R.drawable.a1, R.drawable.a2, R.drawable.a3, R.drawable.a4, R.drawable.a5, R.drawable.a6, R.drawable.a7,
                    R.drawable.a8};
            Random mRandom = new Random();
            int x = mRandom.nextInt(arr.length);
            resID = R.drawable.a1 + x;
            arr = null;
        }
        settings.setAppBGResID(resID);

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
            editor.commit();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void CancelSleepTimer(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(APP_DB_KEY, Context.MODE_PRIVATE).edit();
        editor.remove(KEY_SLEEP_TIMER);
        editor.commit();
    }

    public static void SaveDurationFilterTime(Context context, int duration) {
        SharedPreferences.Editor editor = context.getSharedPreferences(APP_DB_KEY, Context.MODE_PRIVATE).edit();
        editor.putInt(KEY_DURATION_FILTER, duration);
        editor.commit();
    }

    public static int GetDurationFilterTime(Context context) {
        SharedPreferences pref = context.getSharedPreferences(APP_DB_KEY, Context.MODE_PRIVATE);
        return pref.getInt(KEY_DURATION_FILTER, IConstant.DURATION_FILTER_DEFAULT);
    }

    public static void SaveAppBGResID(Context context, int resID) {
        SharedPreferences.Editor editor = context.getSharedPreferences(APP_DB_KEY, Context.MODE_PRIVATE).edit();
        editor.putInt(KEY_APP_BG_RES_ID, resID);
        editor.commit();
    }

    public static int GetAppBGResID(Context context) {
        SharedPreferences pref = context.getSharedPreferences(APP_DB_KEY, Context.MODE_PRIVATE);
        int resID = pref.getInt(KEY_APP_BG_RES_ID, -1);
        if (resID == -1) {
            int[] arr = new int[]{R.drawable.a1, R.drawable.a2, R.drawable.a3, R.drawable.a4, R.drawable.a5, R.drawable.a6, R.drawable.a7,
                    R.drawable.a8};
            Random mRandom = new Random();
            int x = mRandom.nextInt(arr.length);
            resID = R.drawable.a1 + x;
            arr = null;
        }
        return resID;
    }

    public static String GetAlbumSortKey(Context context) {
        SharedPreferences pref = context.getSharedPreferences(APP_DB_KEY, Context.MODE_PRIVATE);
        return pref.getString(KEY_ALBUM_SORT, null);
    }

    public static void SaveAlbumSortKey(Context context, String key) {
        SharedPreferences.Editor editor = context.getSharedPreferences(APP_DB_KEY, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_ALBUM_SORT, key);
        editor.commit();
    }

    public static String GetArtistSortKey(Context context) {
        SharedPreferences pref = context.getSharedPreferences(APP_DB_KEY, Context.MODE_PRIVATE);
        return pref.getString(KEY_ARTIST_SORT, null);
    }

    public static void SaveArtistSortKey(Context context, String key) {
        SharedPreferences.Editor editor = context.getSharedPreferences(APP_DB_KEY, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_ARTIST_SORT, key);
        editor.commit();
    }

    public static String GetSongSortKey(Context context) {
        SharedPreferences pref = context.getSharedPreferences(APP_DB_KEY, Context.MODE_PRIVATE);
        return pref.getString(KEY_SONG_SORT, null);
    }

    public static void SaveSongSortKey(Context context, String key) {
        SharedPreferences.Editor editor = context.getSharedPreferences(APP_DB_KEY, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_SONG_SORT, key);
        editor.commit();
    }

    public static String GetGenreSortKey(Context context) {
        SharedPreferences pref = context.getSharedPreferences(APP_DB_KEY, Context.MODE_PRIVATE);
        return pref.getString(KEY_GENRE_SORT, null);
    }

    public static void SaveGenreSortKey(Context context, String key) {
        SharedPreferences.Editor editor = context.getSharedPreferences(APP_DB_KEY, Context.MODE_PRIVATE).edit();
        editor.putString(KEY_GENRE_SORT, key);
        editor.commit();
    }

    public static boolean IsAutoDownloadAlbumArt(Context context) {
        SharedPreferences pref = context.getSharedPreferences(APP_DB_KEY, Context.MODE_PRIVATE);
        return pref.getBoolean(KEY_AUTO_DOWNLOAD_ALBUM_ART, true);
    }

    public static void SetAutoDownloadAlbumArt(Context context, boolean value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(APP_DB_KEY, Context.MODE_PRIVATE).edit();
        editor.putBoolean(KEY_AUTO_DOWNLOAD_ALBUM_ART, value);
        editor.commit();
    }

    public static boolean IsAppFullscreen(Context context) {
        SharedPreferences pref = context.getSharedPreferences(APP_DB_KEY, Context.MODE_PRIVATE);
        return pref.getBoolean(KEY_APP_FULLSCREEN, false);
    }

    public static void SetAppFullscreen(Context context, boolean value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(APP_DB_KEY, Context.MODE_PRIVATE).edit();
        editor.putBoolean(KEY_APP_FULLSCREEN, value);
        editor.commit();
    }

    public static ELanguage GetLanguage(Context context) {
        SharedPreferences pref = context.getSharedPreferences(APP_DB_KEY, Context.MODE_PRIVATE);
        return ELanguage.GetLanguage(pref.getInt(KEY_LANGUAGE, ELanguage.EN.getLanguageCode()));
    }

    public static void SetLanguage(Context context, ELanguage language) {
        SharedPreferences.Editor editor = context.getSharedPreferences(APP_DB_KEY, Context.MODE_PRIVATE).edit();
        editor.putInt(KEY_LANGUAGE, language.getLanguageCode());
        editor.commit();
    }

    public static void SetLanguage(Context context, int language) {
        SharedPreferences.Editor editor = context.getSharedPreferences(APP_DB_KEY, Context.MODE_PRIVATE).edit();
        editor.putInt(KEY_LANGUAGE, language);
        editor.commit();
    }
}
