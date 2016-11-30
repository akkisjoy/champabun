package champak.champabun.business.utilities.utilMethod;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Build;

import champak.champabun.AmuzicgApp;
import champak.champabun.R;

public class AmuzePreferences {
    private final static String key_uri_for_access = "key_uri_for_access";
    private final static String key_flag = "key_flag";
    final private static String filename = "amuzetheme";
    final private static String key_equalizer = "equalizer";
    final private static String key_playlist_recent_add_name = "pl_re_name";
    final private static String key_player_repeat_mode = "repeat_mode";
    final private static String key_last_playlist = "last_playlist";
    final private static String key_last_playlist_index = "last_playlist_index";
    final private static String key_isFromRecentAdded = "isFromRecentAdded";
    final private static String key_click_no = "click_no";
    final private static String key_saved_presets = "saved_presets";
    private Context context;

    public AmuzePreferences(Context context) {
        this.context = context;
    }

    public void SaveEqualizerState(boolean value) {
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putBoolean(key_equalizer, value);
        editor.apply();
    }

    public boolean IsEqualizerOn() {
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        return prefs.getBoolean(key_equalizer, true);
    }

    public void SetPlaylistRecentName(String recentName) {
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putString(key_playlist_recent_add_name, recentName);
        editor.apply();
    }

    public String GetPlaylistRecentName() {
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        return prefs.getString(key_playlist_recent_add_name, context.getResources().getString(R.string.recently_added));
    }

    /**
     * Set repeat mode
     *
     * @param mode 0: None, 1: Repeat once, 2: Repeat all
     */
    public void SetRepeatMode(int mode) {
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putInt(key_player_repeat_mode, mode);
        editor.apply();
    }

    /**
     * Get repeat mode
     *
     * @return 0: None, 1: Repeat once, 2: Repeat all
     */
    public int GetRepeatMode() {
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        return prefs.getInt(key_player_repeat_mode, AmuzicgApp.REPEAT_NONE);
    }

    public void SaveLastPlaylist(String lastPlaylistIDs) {
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putString(key_last_playlist, lastPlaylistIDs);
        editor.apply();
    }

    public String[] GetLastPlaylist() {
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        String str = prefs.getString(key_last_playlist, null);
        if (str != null) {
            return str.split(",");
        } else {
            return null;
        }
    }

    public boolean IsFromRecentAdded() {
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        return prefs.getBoolean(key_isFromRecentAdded, false);
    }

    public void SetFromRecentAdded(boolean isFromRecentAdded) {
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putBoolean(key_isFromRecentAdded, isFromRecentAdded);
        editor.apply();
    }

    public int GetLastPlaylistIndex() {
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        return prefs.getInt(key_last_playlist_index, 0);
    }

    public void SaveLastplayClickNo(String click_no) {
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putString(key_click_no, click_no);
        editor.apply();
    }

    public String GetLastplayClickNo() {
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        return prefs.getString(key_click_no, null);
    }

    public void SaveLastPlaylistIndex(int index) {
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putInt(key_last_playlist_index, index);
        editor.apply();
    }

    public boolean HasLastPlayed() {
        return GetLastPlaylist() != null;
    }

    private void SavePresetTitle(String presetTitle) {
        String addPreset = GetAllPresetTitle();
        if (addPreset == null || !addPreset.contains(presetTitle)) {
            if (addPreset != null && addPreset.length() > 0) {
                addPreset += ",";
            }
            addPreset += presetTitle;
            SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
            Editor editor = prefs.edit();
            editor.putString(key_saved_presets, addPreset);
            editor.apply();
        }
    }

    private String GetAllPresetTitle() {
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        return prefs.getString(key_saved_presets, "");
    }

    private String[] GetAllPresetName() {
        String ref = GetAllPresetTitle();
        if (Utilities.isEmpty(ref)) {
            return null;
        }
        return ref.split(",");
    }

    public void SaveSharedPreferenceUri(Uri treeUri) {
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putString(key_uri_for_access, treeUri.toString());
        editor.apply();

    }

    public String GetSharedPreferenceUri() {
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        return prefs.getString(key_uri_for_access, null);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void DeleteSharedPreferenceUri() {
        Uri treeUri = null;
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        try {
            treeUri = Uri.parse(prefs.getString(key_uri_for_access, null));

        } catch (Exception ignored) {
        }

        try {
            context.getContentResolver().releasePersistableUriPermission(treeUri, GetIntFlag());
            prefs.edit().clear().apply();
        } catch (Exception ignored) {
        }

    }

    public void SaveIntFlag(int flag) {
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putLong(key_flag, flag);
        editor.apply();

    }

    public int GetIntFlag() {
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        return prefs.getInt(key_flag, 0);
    }
}
