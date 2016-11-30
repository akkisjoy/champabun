package champak.champabun;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;

import champak.champabun.business.dataclasses.AppDatabase;
import champak.champabun.business.dataclasses.AppSetting;
import champak.champabun.business.dataclasses.SongDetails;
import champak.champabun.business.utilities.utilMethod.AmuzePreferences;
import champak.champabun.business.utilities.utilMethod.Utilities;

public class AmuzicgApp extends Application {
    final public static int REPEAT_NONE = 0;
    final public static int REPEAT_ONCE = 1;
    final public static int REPEAT_ALL = 2;

    public static boolean alreadyshuffled = false;
    public static int secondtimeplayershown = 0;
    private static AmuzicgApp mInstance;
    public ArrayList<SongDetails> NP_List;
    public ArrayList<SongDetails> backup;
    public boolean boolMusicPlaying1 = false;
    public boolean boolshuffled = false;
    public String path;
    public int sizeofimage;
    // check for F_Playlist added
    public boolean isPlaylistAdded = false;
    private int position;
    private int check = 0;

    private int repeatmode;// 0 for no repeat 1 for repeat 1 or repeat song and 2 for repeat all
    private AppSetting appSettings;

    public static AmuzicgApp GetInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        AmuzePreferences prefs = new AmuzePreferences(getApplicationContext());
        repeatmode = prefs.GetRepeatMode();

        sizeofimage = (int) mInstance.getResources().getDimension(R.dimen.player_image_size) + 50;

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = settings.edit();
        editor.remove("shuffle_setting");
        editor.apply();

        boolshuffled = false;

        appSettings = AppDatabase.GetAppSetting(getApplicationContext());

    }


    public int getCheck() {
        return check;
    }

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
            NP_List = new ArrayList<>();
            position = 0;
        }
        NP_List.add(sd);
    }

    public void Add2CurSongDetails(SongDetails sd) {
        if (NP_List == null) {
            NP_List = new ArrayList<>();
            position = 0;
        }
        NP_List.add(position, sd);
    }

    public void Add2NowPlaying(ArrayList<SongDetails> list) {
        if (NP_List == null) {
            NP_List = new ArrayList<>();
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
                } catch (Exception ignored) {
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
        AmuzePreferences prefs = new AmuzePreferences(getApplicationContext());
        prefs.SetRepeatMode(mode);
    }

    public int GetRepeatMode() {
        return repeatmode;
    }

    public void SaveLastPlaylist(boolean isFromRecentAdded, String click_no) {
        AmuzePreferences prefs = new AmuzePreferences(getApplicationContext());
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
        isPlaylistAdded = true;
    }

    public AppSetting getAppSettings() {
        return appSettings;
    }

}
