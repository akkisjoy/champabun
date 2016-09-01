package champak.champabun.framework.equalizer;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.audiofx.Equalizer;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;

public class Singleton extends Application {
    static final boolean SET_DEBUG = true;
    private final static String LOG_TAG = "Singleton";
    public static android.media.audiofx.BassBoost theBooster;
    public static Equalizer theEqualizer;
    public static android.media.audiofx.Virtualizer theVirtualizer;
    private static Singleton m_Instance;
    public float m_fFrameS = 0;
    public int m_nFrameW = 0, m_nFrameH = 0, m_nTotalW = 0, m_nTotalH = 0, m_nPaddingX = 0, m_nPaddingY = 0;
    /**
     * Current app's preferences.
     */
    private SharedPreferences preferences = null;
    private String presetKey = "PRESET_INDEX";

    public Singleton() {
        super();
        m_Instance = this;
    }

    public static Singleton getInstance() {
        if (m_Instance == null) {
            synchronized (Singleton.class) {
                if (m_Instance == null) new Singleton();
            }
        }
        return m_Instance;
    }

    public static void Debug(String tag, String message) {
        if (SET_DEBUG) {
            Log.d(tag, message);
        }
    }

    public void savePresetIndex(int presetIndex, Context mContext) {
        if (preferences == null)
            preferences = PreferenceManager.getDefaultSharedPreferences(mContext);


        SharedPreferences.Editor prefsEditor = preferences.edit();
        prefsEditor.putInt(presetKey, presetIndex);
        prefsEditor.apply();

    }

    public int getCurPresetIndex(Context mContext) {
        if (preferences == null)
            preferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        return preferences.getInt(presetKey, 0);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void InitGUIFrame(Activity context) {
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        m_nTotalW = dm.widthPixels;
        m_nTotalH = dm.heightPixels;
        // scale factor
        m_fFrameS = (float) m_nTotalW / 640.0f;
        // compute our frame
        m_nFrameW = m_nTotalW;
        m_nFrameH = (int) (960.0f * m_fFrameS);
        // compute padding for our frame inside the total screen size

        m_nPaddingY = 0;
        m_nPaddingX = (m_nTotalW - m_nFrameW) / 2;

        Debug(LOG_TAG, "InitGUIFrame: frame:" + m_nFrameW + "x" + m_nFrameH + " Scale:" + m_fFrameS);
    }

    public int Scale(int v) {
        float s = (float) v * m_fFrameS;
        int rs = 0;
        if (s - (int) s >= 0.5) rs = ((int) s) + 1;
        else rs = (int) s;
        return rs;
    }
}
