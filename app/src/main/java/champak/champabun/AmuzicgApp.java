package champak.champabun;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import champak.champabun.business.dataclasses.AppDatabase;
import champak.champabun.business.dataclasses.AppSetting;
import champak.champabun.business.dataclasses.PlayMeeConfig;
import champak.champabun.business.dataclasses.SongDetails;
import champak.champabun.business.utilities.utilMethod.AppConfigHelper;
import champak.champabun.business.utilities.utilMethod.PlayMeePreferences;
import champak.champabun.business.utilities.utilMethod.Utilities;

public class AmuzicgApp extends Application {
    final public static int REPEAT_NONE = 0;
    final public static int REPEAT_ONCE = 1;
    final public static int REPEAT_ALL = 2;

    public static boolean alreadyshuffled = false;
    //	public static boolean isShowInterstitialAds;
//	public static int  splashscreenopen=1;
    public static int secondtimeplayershown = 0;
    private static AmuzicgApp mInstance;
    public ArrayList<SongDetails> NP_List;// = new ArrayList < SongDetails >();
    public ArrayList<SongDetails> backup;
    // public Context c;
    public boolean boolMusicPlaying1 = false;
    public boolean boolshuffled = false;
    public String path;
    public int sizeofimage;
    // check for F_Playlist added
    public boolean isPlaylistAdded = false;
    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();
    private int position;
    private int check = 0;

    // public String song;
    // public String album;
    private int repeatmode;// 0 for no repeat 1 for repeat 1 or repeat song and 2 for repeat all
    private AppSetting appSettings;
    private PlayMeeConfig playMeeConfig;

    public static AmuzicgApp GetInstance() {
        return mInstance;
    }

    synchronized public Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            analytics.getLogger().setLogLevel(com.google.android.gms.analytics.Logger.LogLevel.VERBOSE);
            Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(getString(R.string.GA_ID))
                    : (trackerId == TrackerName.GLOBAL_TRACKER) ? analytics.newTracker(R.xml.global_tracker)
                    : analytics.newTracker(R.xml.ecommerce_tracker);
            t.enableAdvertisingIdCollection(true);
            mTrackers.put(trackerId, t);
        }
        return mTrackers.get(trackerId);
    }

    public void TrackPageview(String page) {
        Tracker t = getTracker(TrackerName.APP_TRACKER);
        t.setScreenName(page);
        t.send(new HitBuilders.AppViewBuilder().build());
    }

    public void TrackEvent(String category, String action, String label, long value) {
        Tracker t = getTracker(TrackerName.APP_TRACKER);
        t.send(new HitBuilders.EventBuilder().setCategory(category).setAction(action).setLabel(label).setValue(value).build());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        PlayMeePreferences prefs = new PlayMeePreferences(getApplicationContext());
        repeatmode = prefs.GetRepeatMode();
        prefs = null;

        sizeofimage = (int) mInstance.getResources().getDimension(R.dimen.player_image_size) + 20;

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = settings.edit();
        editor.remove("shuffle_setting");
        editor.commit();

        boolshuffled = false;

        appSettings = AppDatabase.GetAppSetting(getApplicationContext());
        ChangeLanguage();

        playMeeConfig = AppConfigHelper.getInstance().GetPlayMeeConfig(getApplicationContext());
        // fetch ads config updates
        AppConfigHelper.getInstance().CheckPlayMeeConfigUpdate(getApplicationContext(), playMeeConfig.getVersion(),
                new AppConfigHelper.OnGotPlayMeeConfigListener() {

                    @Override
                    public void OnGotPlayMeeConfig(PlayMeeConfig playMeeConfig) {
                        setPlayMeeConfig(playMeeConfig);
                    }
                });
    }

    public boolean isAdsConfigOffer() {
        return playMeeConfig.getAdsConfig().isAdsConfigOffer();
    }

    public void setArrayListMapData(ArrayList<SongDetails> setData) {
        NP_List = setData;
    }

    public int getCheck() {
        return check;
    }

    // public ArrayList < SongDetails > getArrayListMapData()
    // {
    // return NP_List;
    // }

    public void setCheck(int check) {
        this.check = check;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void increasePosition() {
        this.position++;
    }

    public void decreasePosition() {
        this.position--;
        if (this.position < 0) {
            this.position = 0;
        }
    }

    public void SetNowPlayingList(ArrayList<SongDetails> list) {
        NP_List = list;
    }

    public void Add2NowPlaying(SongDetails sd) {
        if (NP_List == null) {
            NP_List = new ArrayList<SongDetails>();
            position = 0;
        }
        NP_List.add(sd);
    }

    public void Add2CurSongDetails(SongDetails sd) {
        if (NP_List == null) {
            NP_List = new ArrayList<SongDetails>();
            position = 0;
        }
        NP_List.add(position, sd);
    }

    public void Add2NowPlaying(ArrayList<SongDetails> list) {
        if (NP_List == null) {
            NP_List = new ArrayList<SongDetails>();
            position = 0;
        }
        NP_List.addAll(list);
    }

    public void RemoveFromNowPlaying(int index) {
        if (NP_List != null && index < NP_List.size()) {
            NP_List.remove(index);
        }
    }

    public void RemoveCurSongDetails() {
        if (NP_List != null) {
            if (position < NP_List.size()) {
                try {
                    NP_List.remove(position);
                    if (position >= NP_List.size()) {
                        position = NP_List.size() - 1;
                    }
                } catch (IndexOutOfBoundsException e) {
                } catch (Exception e) {
                }


            }
        }
    }

    public void CLearNowPlaying() {
        if (NP_List != null) {
            NP_List.clear();
        }
    }

    public ArrayList<SongDetails> GetNowPlayingList() {
        return NP_List;
    }

    public int GetNowPlayingSize() {
        if (NP_List == null) {
            return 0;
        } else {
            return NP_List.size();
        }
    }

    public SongDetails GetCurSongDetails() {
        if (position >= 0 && NP_List != null && position < NP_List.size()) {
            return NP_List.get(position);
        } else {
            return null;
        }
    }

    public void SetRepeatMode(int mode) {
        repeatmode = mode;
        PlayMeePreferences prefs = new PlayMeePreferences(getApplicationContext());
        prefs.SetRepeatMode(mode);
        prefs = null;
    }

    public int GetRepeatMode() {
        return repeatmode;
    }

    public void SaveLastPlaylist(boolean isFromRecentAdded, String click_no) {
        PlayMeePreferences prefs = new PlayMeePreferences(getApplicationContext());
        if (Utilities.isEmpty(click_no)) {
            if (NP_List != null && NP_List.size() > 0) {
                String lastPlaylistIDs = "";
                for (int i = 0; i < NP_List.size(); i++) {
                    lastPlaylistIDs += String.valueOf(NP_List.get(i).getID());
                    if (i < NP_List.size() - 1) {
                        lastPlaylistIDs += ",";
                    }
                }
                prefs.SaveLastPlaylist(lastPlaylistIDs);
            }
        }
        prefs.SetFromRecentAdded(isFromRecentAdded);
        prefs.SaveLastPlaylistIndex(getPosition());
        prefs.SaveLastplayClickNo(click_no);
        prefs = null;
        isPlaylistAdded = true;
    }

    public AppSetting getAppSettings() {
        return appSettings;
    }

    public void setAppSettings(AppSetting appSettings) {
        this.appSettings = appSettings;
    }

    public PlayMeeConfig getPlayMeeConfig() {
        return playMeeConfig;
    }

    public void setPlayMeeConfig(PlayMeeConfig playMeeConfig) {
        this.playMeeConfig = playMeeConfig;
    }

    public void ChangeLanguage() {
        Locale locale = null;
        try {
            if (appSettings.getLanguage().getLocale().equals("pt_PT"))
                locale = new Locale("pt", "PT");
            else
                locale = new Locale(appSettings.getLanguage().getLocale());
        } catch (Exception e) {
            e.printStackTrace();
            locale = new Locale("en");
        }
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
        GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
        ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
    }
}
