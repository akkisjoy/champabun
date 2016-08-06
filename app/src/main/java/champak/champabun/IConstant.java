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
package champak.champabun;


public interface IConstant {

    String urlContainingImageAndApk = "http://amuzicg.com/next.xml";
    /**
     * Enable/Disable Pro/Free version
     */
    final public static boolean IS_PRO_VERSION = false;
    final public static boolean IS_AMAZONVERSION = false;
    //final public static boolean SHOW_TONESHUB = false;
    /**
     * Determine on release product
     */
    final public static boolean BUILD_RELEASE = !true;// switch ON when release app

    /**
     * susuSUSU1234!@#$ password for key
     * Define use debug mode
     */
    final public static boolean USE_DEBUG = true && !BUILD_RELEASE;

    /**
     * Enable logcat
     */
    final public static boolean ENABLE_LOG = true && USE_DEBUG;

    /**
     * Enable memory log
     */
    final public static boolean ENABLE_MEMORY_LOG = true && USE_DEBUG;

    /**
     * Enable print exception stack traces
     */
    final public static boolean ENABLE_PRINT_STACK_TRACES = true && USE_DEBUG;

    /**
     * Enable/Disable calling System.gc();
     */
    final public static boolean USE_SYSTEM_GC = !true;

    /**
     * Enable/Disable optimize mem by recycle bitmap
     */
    final public static boolean OPTIMIZE_MEM_RECYCLE_BITMAP = false;

    final public static int arr[] = {R.drawable.album_art_1, R.drawable.album_art_2, R.drawable.album_art_3, R.drawable.album_art_4,
            R.drawable.album_art_5, R.drawable.album_art_6, R.drawable.album_art_7, R.drawable.album_art_8, R.drawable.album_art_9,
            R.drawable.album_art_10, R.drawable.album_art_11, R.drawable.album_art_12, R.drawable.album_art_13,};

    //public static final String LAST_PLAYLIST = 
    public static final int REQUEST_CODE_PLAYER = 1001;

    final public static class ACTION_FREE {
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

    final public static class ACTION_PRO {
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

    final public static String BROADCAST_PLAYPAUSE = IS_PRO_VERSION ? ACTION_PRO.BROADCAST_PLAYPAUSE : ACTION_FREE.BROADCAST_PLAYPAUSE;
    public final static String BROADCAST_SEEKBAR = IS_PRO_VERSION ? ACTION_PRO.BROADCAST_SEEKBAR : ACTION_FREE.BROADCAST_SEEKBAR;
    public final static String BROADCAST_SWAP = IS_PRO_VERSION ? ACTION_PRO.BROADCAST_SWAP : ACTION_FREE.BROADCAST_SWAP;
    public static final String BROADCAST_ACTION = IS_PRO_VERSION ? ACTION_PRO.BROADCAST_ACTION : ACTION_FREE.BROADCAST_ACTION;
    public static final String BROADCAST_COVER = IS_PRO_VERSION ? ACTION_PRO.BROADCAST_COVER : ACTION_FREE.BROADCAST_COVER;
    public static final String BROADCAST_STOP_NOW = IS_PRO_VERSION ? ACTION_PRO.BROADCAST_STOP_NOW : ACTION_FREE.BROADCAST_STOP_NOW;
    public static final String BROADCAST_STOP = IS_PRO_VERSION ? ACTION_PRO.BROADCAST_STOP : ACTION_FREE.BROADCAST_STOP;
    public static final String BROADCAST_STOP_WIDGET = IS_PRO_VERSION ? ACTION_PRO.BROADCAST_STOP_WIDGET : ACTION_FREE.BROADCAST_STOP_WIDGET;
    public static final String BROADCAST_OPEN_ACTIVITY = IS_PRO_VERSION ? ACTION_PRO.BROADCAST_OPEN_ACTIVITY : ACTION_FREE.BROADCAST_OPEN_ACTIVITY;
    public static final String BROADCAST_CHECK_AGAIN = IS_PRO_VERSION ? ACTION_PRO.BROADCAST_CHECK_AGAIN : ACTION_FREE.BROADCAST_CHECK_AGAIN;
    public static final String BROADCAST_SHUTDOWN = IS_PRO_VERSION ? ACTION_PRO.BROADCAST_SHUTDOWN : ACTION_FREE.BROADCAST_SHUTDOWN;
    public static final String BROADCAST_PREV = IS_PRO_VERSION ? ACTION_PRO.BROADCAST_PREV : ACTION_FREE.BROADCAST_PREV;
    // public static final String BROADCAST_COVERPARALLAX = IS_PRO_VERSION ? ACTION_PRO.BROADCAST_COVERPARALLAX : ACTION_FREE.BROADCAST_COVERPARALLAX;
    public static final String BROADCAST_ON_SONGDETAILS_UPDATED = IS_PRO_VERSION ? ACTION_PRO.BROADCAST_ON_SONGDETAILS_UPDATED
            : ACTION_FREE.BROADCAST_ON_SONGDETAILS_UPDATED;
    public static final String BROADCAST_EQ = IS_PRO_VERSION ? ACTION_PRO.BROADCAST_EQ : ACTION_FREE.BROADCAST_EQ;
    public static final int REQUEST_CODE = 0;

    public static final String SERVICECMD = IS_PRO_VERSION ? ACTION_PRO.SERVICECMD : ACTION_FREE.SERVICECMD;
    public static final String CMDNAME = IS_PRO_VERSION ? ACTION_PRO.CMDNAME : ACTION_FREE.CMDNAME;
    public static final String CMDTOGGLEPAUSE = IS_PRO_VERSION ? ACTION_PRO.CMDTOGGLEPAUSE : ACTION_FREE.CMDTOGGLEPAUSE;
    public static final String CMDSTOP = IS_PRO_VERSION ? ACTION_PRO.CMDSTOP : ACTION_FREE.CMDSTOP;
    public static final String CMDPLAY = IS_PRO_VERSION ? ACTION_PRO.CMDPLAY : ACTION_FREE.CMDPLAY;
    public static final String CMDPAUSE = IS_PRO_VERSION ? ACTION_PRO.CMDPAUSE : ACTION_FREE.CMDPAUSE;
    public static final String CMDPREVIOUS = IS_PRO_VERSION ? ACTION_PRO.CMDPREVIOUS : ACTION_FREE.CMDPREVIOUS;
    public static final String CMDNEXT = IS_PRO_VERSION ? ACTION_PRO.CMDNEXT : ACTION_FREE.CMDNEXT;

    public static final String TOGGLEPAUSE_ACTION = IS_PRO_VERSION ? ACTION_PRO.TOGGLEPAUSE_ACTION : ACTION_FREE.TOGGLEPAUSE_ACTION;
    public static final String PLAY_ACTION = IS_PRO_VERSION ? ACTION_PRO.PLAY_ACTION : ACTION_FREE.PLAY_ACTION;
    public static final String PAUSE_ACTION = IS_PRO_VERSION ? ACTION_PRO.PAUSE_ACTION : ACTION_FREE.PAUSE_ACTION;
    public static final String PREVIOUS_ACTION = IS_PRO_VERSION ? ACTION_PRO.PREVIOUS_ACTION : ACTION_FREE.PREVIOUS_ACTION;
    public static final String NEXT_ACTION = IS_PRO_VERSION ? ACTION_PRO.NEXT_ACTION : ACTION_FREE.NEXT_ACTION;

    public static final String ACTION_SLEEP_TIMER = "android.intent.action.ACTION_SLEEP_TIMER";
    public static final int ACTION_SLEEP_TIMER_CODE = 1234;
    public static final int DURATION_FILTER_DEFAULT = 10000;
    public static final int REQUEST_CODE_SEND_AUDIO = 1000;
    public static final int REQUEST_CODE_CHANGE_SETTINGS = 1111;
    public static final int REQUEST_CODE_CHANGE_BACKGROUND = 1112;
    public static final String REQUEST_CODE_INTENT = "intent.request.code";

    public static final String REQUEST_FEATURE_URL = "https://www.google.com/";
    public static final String REPORT_BUG_URL = "https://translate.google.com/";
    public static final String DONATE_URL = "http://vnexpress.net/";
    public static final String PLAYMEE_PRO_PACKAGE = "champa.pro";

    public static final String CALCULATOR_APP_PACKAGE = "champanator";
}
