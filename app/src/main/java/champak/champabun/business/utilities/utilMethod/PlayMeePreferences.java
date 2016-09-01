package champak.champabun.business.utilities.utilMethod;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Build;

import champak.champabun.AmuzicgApp;
import champak.champabun.R;
import champak.champabun.business.dataclasses.PresetValue;
import champak.champabun.framework.equalizer.EqualizerValue;

public class PlayMeePreferences {
    final public static String key_uri_for_access = "key_uri_for_access";
    final public static String key_flag = "key_flag";
    final private static String filename = "playmee";
    final private static String key_equalizer = "equalizer";
    // final private static String key_equalizer_save_setting = "isSave";
    final private static String key_equalizer_band_id = "eqBandId";
    final private static String key_equalizer_band_min = "eqBandMin";
    final private static String key_equalizer_band_max = "eqBandMax";
    final private static String key_equalizer_band_value = "eqBandValue";
    final private static String key_equalizer_band_unit = "eqBandUnit";
    final private static String key_playlist_recent_add_name = "pl_re_name";
    final private static String key_player_repeat_mode = "repeat_mode";
    final private static String key_last_playlist = "last_playlist";
    final private static String key_last_playlist_index = "last_playlist_index";
    final private static String key_isFromRecentAdded = "isFromRecentAdded";
    final private static String key_click_no = "click_no";
    final private static String key_saved_presets = "saved_presets";
    final private static String key_prev_presets_index = "prev_presets_index";
    private Context context;

    public PlayMeePreferences(Context context) {
        this.context = context;
    }

    public void SaveEqualizerState(boolean value) {
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putBoolean(key_equalizer, value);
        editor.commit();
    }

    public boolean IsEqualizerOn() {
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        return prefs.getBoolean(key_equalizer, true);
    }

    // public void SaveEqualizerSetting(boolean value)
    // {
    // SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
    // Editor editor = prefs.edit();
    // editor.putBoolean(key_equalizer_save_setting, value);
    // editor.commit();
    // }
    //
    // public boolean IsEqualizerSettingOn()
    // {
    // SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
    // return prefs.getBoolean(key_equalizer_save_setting, true);
    // }

    public void SaveEqualizerValue(String presetName, EqualizerValue eqValue) {
        if (eqValue == null) {
            return;
        }
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putInt(presetName + "_" + key_equalizer_band_id + eqValue.getId(), eqValue.getId());
        // editor.putInt(presetName + "_" + key_equalizer_band_min + eqValue.getId(), eqValue.getMin());
        // editor.putInt(presetName + "_" + key_equalizer_band_max + eqValue.getId(), eqValue.getMax());
        editor.putInt(presetName + "_" + key_equalizer_band_value + eqValue.getId(), eqValue.getValue());
        // editor.putString(presetName + "_" + key_equalizer_band_unit + eqValue.getId(), eqValue.getUnit());
        editor.commit();
    }

    public EqualizerValue GetEqualizerValue(String presetName, int id) {
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        EqualizerValue eqValue = null;
        int _id = prefs.getInt(presetName + "_" + key_equalizer_band_id + id, -1);
        if (id == _id) {
            // int min = prefs.getInt(presetName + "_" + key_equalizer_band_min + id, 0);
            // int max = prefs.getInt(presetName + "_" + key_equalizer_band_max + id, 0);
            int value = prefs.getInt(presetName + "_" + key_equalizer_band_value + id, 0);
            // String unit = prefs.getString(presetName + "_" + key_equalizer_band_unit + id, "");
            eqValue = new EqualizerValue(_id, value);
        } else if (_id == -1) {
            eqValue = new EqualizerValue(id, 0);
        }
        return eqValue;
    }

    // public void ClearAllEqValues(int id)
    // {
    // SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
    // Editor editor = prefs.edit();
    // editor.remove(key_equalizer_band_id + id);
    // editor.remove(key_equalizer_band_value + id);
    // editor.commit();
    // }

    public void SetPlaylistRecentName(String recentName) {
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putString(key_playlist_recent_add_name, recentName);
        editor.commit();
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
        editor.commit();
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
        editor.commit();
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
        editor.commit();
    }

    public int GetLastPlaylistIndex() {
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        return prefs.getInt(key_last_playlist_index, 0);
    }

    public void SaveLastplayClickNo(String click_no) {
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putString(key_click_no, click_no);
        editor.commit();
    }

    public String GetLastplayClickNo() {
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        return prefs.getString(key_click_no, null);
    }

    public void SaveLastPlaylistIndex(int index) {
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putInt(key_last_playlist_index, index);
        editor.commit();
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
            editor.commit();
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

    public void SaveAllPresetValue(PresetValue[] presetValue) {
        if (presetValue == null) {
            return;
        }
        for (int i = 0; i < presetValue.length; i++) {
            SavePresetValue(presetValue[i]);
        }
    }

    public void SavePresetValue(PresetValue presetValue) {
        if (presetValue == null) {
            return;
        }
        for (int i = 0; i < presetValue.getEqualizerValue().length; i++) {
            SavePresetTitle(presetValue.getName());
            SaveEqualizerValue(presetValue.getName(), presetValue.getEqualizerValue()[i]);
        }
    }

//	private EqualizerValue [] GetPresetValue(String presetName)
//	{
//		EqualizerValue [] value = new EqualizerValue [ Equalizer2.NUM_BANDS ];
//		for (int i = 0; i < value.length; i ++)
//		{
//			value [ i ] = GetEqualizerValue(presetName, i);
//		}
//
//		return value;
//	}

//	public ArrayList < PresetValue > GetAllPresetValue()
//	{
//		String [] str = GetAllPresetName();
//		if (str == null)
//		{
//			return null;
//		}
//		ArrayList < PresetValue > value = new ArrayList < PresetValue >();
//		for (int i = 0; i < str.length; i ++)
//		{
//			value.add(new PresetValue(i, str [ i ], GetPresetValue(str [ i ])));
//		}
//		return value;
//	}

    public int GetPrevPresetIndex() {
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        return prefs.getInt(key_prev_presets_index, 1);
    }

    public void SavePrevPresetIndex(int index) {
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putInt(key_prev_presets_index, index);
        editor.commit();
    }

    public void SaveSharedPreferenceUri(String string, Uri treeUri) {
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putString(key_uri_for_access, treeUri.toString());
        editor.commit();

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

        } catch (Exception e) {
        }

        try {
            context.getContentResolver().releasePersistableUriPermission(treeUri, GetIntFlag());
            prefs.edit().clear().commit();
        } catch (Exception e) {
        }

    }

    public void SaveIntFlag(int flag) {
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        Editor editor = prefs.edit();
        editor.putLong(key_flag, flag);
        editor.commit();

    }

    public int GetIntFlag() {
        SharedPreferences prefs = context.getSharedPreferences(filename, Context.MODE_PRIVATE);
        return prefs.getInt(key_flag, 0);
    }
}
