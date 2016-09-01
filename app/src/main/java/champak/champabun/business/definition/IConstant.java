/**
 * IConstant.java
 * <p/>
 * <br>
 * <b>Purpose</b> : Interface that define constant value
 * <p/>
 * <br>
 * <b>Optional info</b> :
 * <p/>
 * <br>
 * <b>author</b> : chquay@gmail.com
 * <p/>
 * <br>
 * <b>date</b> : Jul 10, 2014
 * <p/>
 * <br>
 * <b>lastChangedRevision</b> :
 * <p/>
 * <br>
 * <b>lastChangedDate</b> : Jul 10, 2014
 */
package champak.champabun.business.definition;


import champak.champabun.R;

public interface IConstant {

    String urlContainingImageAndApk = "http://amuzicg.com/next.xml";
    /**
     * Enable/Disable Pro/Free version
     */
    boolean IS_PRO_VERSION = false;
    boolean IS_AMAZONVERSION = false;
    //final public static boolean SHOW_TONESHUB = false;
    /**
     * Determine on release product
     */
    boolean BUILD_RELEASE = !true;// switch ON when release app

    /**
     * susuSUSU1234!@#$ password for key
     * Define use debug mode
     */
    boolean USE_DEBUG = true && !BUILD_RELEASE;

    /**
     * Enable logcat
     */
    boolean ENABLE_LOG = true && USE_DEBUG;

    /**
     * Enable memory log
     */
    boolean ENABLE_MEMORY_LOG = true && USE_DEBUG;

    /**
     * Enable print exception stack traces
     */
    boolean ENABLE_PRINT_STACK_TRACES = true && USE_DEBUG;

    /**
     * Enable/Disable calling System.gc();
     */
    boolean USE_SYSTEM_GC = !true;

    /**
     * Enable/Disable optimize mem by recycle bitmap
     */
    boolean OPTIMIZE_MEM_RECYCLE_BITMAP = false;

    int arr[] = {R.drawable.album_art_1, R.drawable.album_art_2, R.drawable.album_art_3, R.drawable.album_art_4,
            R.drawable.album_art_5, R.drawable.album_art_6, R.drawable.album_art_7, R.drawable.album_art_8, R.drawable.album_art_9,
            R.drawable.album_art_10, R.drawable.album_art_11, R.drawable.album_art_12, R.drawable.album_art_13,};

    //public static final String LAST_PLAYLIST = 
    int REQUEST_CODE_PLAYER = 1001;
    String BROADCAST_PLAYPAUSE = IS_PRO_VERSION ? ACTION_PRO.BROADCAST_PLAYPAUSE : ACTION_FREE.BROADCAST_PLAYPAUSE;
    String BROADCAST_SEEKBAR = IS_PRO_VERSION ? ACTION_PRO.BROADCAST_SEEKBAR : ACTION_FREE.BROADCAST_SEEKBAR;
    String BROADCAST_SWAP = IS_PRO_VERSION ? ACTION_PRO.BROADCAST_SWAP : ACTION_FREE.BROADCAST_SWAP;
    String BROADCAST_ACTION = IS_PRO_VERSION ? ACTION_PRO.BROADCAST_ACTION : ACTION_FREE.BROADCAST_ACTION;
    String BROADCAST_COVER = IS_PRO_VERSION ? ACTION_PRO.BROADCAST_COVER : ACTION_FREE.BROADCAST_COVER;
    String BROADCAST_STOP_NOW = IS_PRO_VERSION ? ACTION_PRO.BROADCAST_STOP_NOW : ACTION_FREE.BROADCAST_STOP_NOW;
    String BROADCAST_STOP = IS_PRO_VERSION ? ACTION_PRO.BROADCAST_STOP : ACTION_FREE.BROADCAST_STOP;
    String BROADCAST_STOP_WIDGET = IS_PRO_VERSION ? ACTION_PRO.BROADCAST_STOP_WIDGET : ACTION_FREE.BROADCAST_STOP_WIDGET;
    String BROADCAST_OPEN_ACTIVITY = IS_PRO_VERSION ? ACTION_PRO.BROADCAST_OPEN_ACTIVITY : ACTION_FREE.BROADCAST_OPEN_ACTIVITY;
    String BROADCAST_CHECK_AGAIN = IS_PRO_VERSION ? ACTION_PRO.BROADCAST_CHECK_AGAIN : ACTION_FREE.BROADCAST_CHECK_AGAIN;
    String BROADCAST_SHUTDOWN = IS_PRO_VERSION ? ACTION_PRO.BROADCAST_SHUTDOWN : ACTION_FREE.BROADCAST_SHUTDOWN;
    String BROADCAST_PREV = IS_PRO_VERSION ? ACTION_PRO.BROADCAST_PREV : ACTION_FREE.BROADCAST_PREV;
    // public static final String BROADCAST_COVERPARALLAX = IS_PRO_VERSION ? ACTION_PRO.BROADCAST_COVERPARALLAX : ACTION_FREE.BROADCAST_COVERPARALLAX;
    String BROADCAST_ON_SONGDETAILS_UPDATED = IS_PRO_VERSION ? ACTION_PRO.BROADCAST_ON_SONGDETAILS_UPDATED
            : ACTION_FREE.BROADCAST_ON_SONGDETAILS_UPDATED;
    String BROADCAST_EQ = IS_PRO_VERSION ? ACTION_PRO.BROADCAST_EQ : ACTION_FREE.BROADCAST_EQ;
    int REQUEST_CODE = 0;
    String SERVICECMD = IS_PRO_VERSION ? ACTION_PRO.SERVICECMD : ACTION_FREE.SERVICECMD;
    String CMDNAME = IS_PRO_VERSION ? ACTION_PRO.CMDNAME : ACTION_FREE.CMDNAME;
    String CMDTOGGLEPAUSE = IS_PRO_VERSION ? ACTION_PRO.CMDTOGGLEPAUSE : ACTION_FREE.CMDTOGGLEPAUSE;
    String CMDSTOP = IS_PRO_VERSION ? ACTION_PRO.CMDSTOP : ACTION_FREE.CMDSTOP;
    String CMDPLAY = IS_PRO_VERSION ? ACTION_PRO.CMDPLAY : ACTION_FREE.CMDPLAY;
    String CMDPAUSE = IS_PRO_VERSION ? ACTION_PRO.CMDPAUSE : ACTION_FREE.CMDPAUSE;
    String CMDPREVIOUS = IS_PRO_VERSION ? ACTION_PRO.CMDPREVIOUS : ACTION_FREE.CMDPREVIOUS;
    String CMDNEXT = IS_PRO_VERSION ? ACTION_PRO.CMDNEXT : ACTION_FREE.CMDNEXT;
    String TOGGLEPAUSE_ACTION = IS_PRO_VERSION ? ACTION_PRO.TOGGLEPAUSE_ACTION : ACTION_FREE.TOGGLEPAUSE_ACTION;
    String PLAY_ACTION = IS_PRO_VERSION ? ACTION_PRO.PLAY_ACTION : ACTION_FREE.PLAY_ACTION;
    String PAUSE_ACTION = IS_PRO_VERSION ? ACTION_PRO.PAUSE_ACTION : ACTION_FREE.PAUSE_ACTION;
    String PREVIOUS_ACTION = IS_PRO_VERSION ? ACTION_PRO.PREVIOUS_ACTION : ACTION_FREE.PREVIOUS_ACTION;
    String NEXT_ACTION = IS_PRO_VERSION ? ACTION_PRO.NEXT_ACTION : ACTION_FREE.NEXT_ACTION;
    String ACTION_SLEEP_TIMER = "android.intent.action.ACTION_SLEEP_TIMER";
    int ACTION_SLEEP_TIMER_CODE = 1234;
    int DURATION_FILTER_DEFAULT = 10000;
    int REQUEST_CODE_SEND_AUDIO = 1000;
    int REQUEST_CODE_CHANGE_SETTINGS = 1111;
    int REQUEST_CODE_CHANGE_BACKGROUND = 1112;
    String REQUEST_CODE_INTENT = "intent.request.code";
    String REQUEST_FEATURE_URL = "https://www.google.com/";
    String REPORT_BUG_URL = "https://translate.google.com/";
    String DONATE_URL = "http://vnexpress.net/";
    String PLAYMEE_PRO_PACKAGE = "champa.pro";
    String CALCULATOR_APP_PACKAGE = "champanator";

    final class ACTION_FREE {
        final public static String BROADCAST_PLAYPAUSE = "champa.action.BROADCAST_PLAYPAUSE";
        public final static String BROADCAST_SEEKBAR = "source.justanothermusicplayer.sendseekbar";
        public final static String BROADCAST_SWAP = "champa.action.BROADCAST_SWAP";
        public static final String BROADCAST_ACTION = "source.justanothermusicplayer.seekprogress";
        public static final String BROADCAST_COVER = "source.justanothermusicplayer.cover";
        public static final String BROADCAST_STOP_NOW = "source.juststop";
        public static final String BROADCAST_STOP = "stop";
        public static final String BROADCAST_STOP_WIDGET = "stop.widget";
        public static final String BROADCAST_OPEN_ACTIVITY = "champa.action.BROADCAST_OPEN_ACTIVITY";
        public static final String BROADCAST_CHECK_AGAIN = "xyz";
        public static final String BROADCAST_SHUTDOWN = "shutdown";
        public static final String BROADCAST_PREV = "champa.action.BROADCAST_PREV";
        // public static final String BROADCAST_COVERPARALLAX = "source.justanothermusicplayer.coverparallax";
        public static final String BROADCAST_ON_SONGDETAILS_UPDATED = "songdetails.updated";
        public static final String BROADCAST_EQ = "eq";
        public static final String SERVICECMD = "nu.staldal.djdplayer.musicservicecommand";
        public static final String CMDNAME = "command";
        public static final String CMDTOGGLEPAUSE = "togglepause";
        public static final String CMDSTOP = "stop";
        public static final String CMDPLAY = "play";
        public static final String CMDPAUSE = "pause";
        public static final String CMDPREVIOUS = "previous";
        public static final String CMDNEXT = "next";

        public static final String TOGGLEPAUSE_ACTION = "champa.musicservicecommand.togglepause";
        public static final String PLAY_ACTION = "champa.musicservicecommand.play";
        public static final String PAUSE_ACTION = "champa.musicservicecommand.pause";
        public static final String PREVIOUS_ACTION = "champa.musicservicecommand.previous";
        public static final String NEXT_ACTION = "champa.musicservicecommand.next";
    }

    final class ACTION_PRO {
        final public static String BROADCAST_PLAYPAUSE = "champa.action.BROADCAST_PLAYPAUSE";
        public final static String BROADCAST_SEEKBAR = "pro.source.justanothermusicplayer.sendseekbar";
        public final static String BROADCAST_SWAP = "champa.action.BROADCAST_SWAP";
        public static final String BROADCAST_ACTION = "pro.source.justanothermusicplayer.seekprogress";
        public static final String BROADCAST_COVER = "pro.source.justanothermusicplayer.cover";
        public static final String BROADCAST_STOP_NOW = "pro.source.juststop";
        public static final String BROADCAST_STOP = "pro.stop";
        public static final String BROADCAST_STOP_WIDGET = "pro.stop.widget";
        public static final String BROADCAST_OPEN_ACTIVITY = "champa.action.BROADCAST_OPEN_ACTIVITY";
        public static final String BROADCAST_CHECK_AGAIN = "pro.xyz";
        public static final String BROADCAST_SHUTDOWN = "pro.shutdown";
        public static final String BROADCAST_PREV = "champa.action.BROADCAST_PREV";
        // public static final String BROADCAST_COVERPARALLAX = "pro.source.justanothermusicplayer.coverparallax";
        public static final String BROADCAST_ON_SONGDETAILS_UPDATED = "pro.songdetails.updated";
        public static final String BROADCAST_EQ = "eq";
        public static final String SERVICECMD = "pro.nu.staldal.djdplayer.musicservicecommand";
        public static final String CMDNAME = "pro.command";
        public static final String CMDTOGGLEPAUSE = "pro.togglepause";
        public static final String CMDSTOP = "pro.stop";
        public static final String CMDPLAY = "pro.play";
        public static final String CMDPAUSE = "pro.pause";
        public static final String CMDPREVIOUS = "pro.previous";
        public static final String CMDNEXT = "pro.next";

        public static final String TOGGLEPAUSE_ACTION = "champa.pro.musicservicecommand.togglepause";
        public static final String PLAY_ACTION = "champa.pro.musicservicecommand.play";
        public static final String PAUSE_ACTION = "champa.pro.musicservicecommand.pause";
        public static final String PREVIOUS_ACTION = "champa.pro.musicservicecommand.previous";
        public static final String NEXT_ACTION = "champa.pro.musicservicecommand.next";
    }
}
