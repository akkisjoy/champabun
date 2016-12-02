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


public interface IConstant {

    /**
     * Enable/Disable Pro/Free version
     */
    boolean IS_PRO_VERSION = false;
    boolean IS_AMAZONVERSION = false;
    /**
     * Determine on release product
     */
    boolean BUILD_RELEASE = false;// switch ON when release app

    /**
     * susuSUSU1234!@#$ password for key
     * Define use debug mode
     */
    boolean USE_DEBUG = true;

    /**
     * Enable logcat
     */
    boolean ENABLE_LOG = true;

    /**
     * Enable memory log
     */
    boolean ENABLE_MEMORY_LOG = true;

    /**
     * Enable print exception stack traces
     */
    boolean ENABLE_PRINT_STACK_TRACES = true;

    /**
     * Enable/Disable calling System.gc();
     */
    boolean USE_SYSTEM_GC = false;

    /**
     * Enable/Disable optimize mem by recycle bitmap
     */
    boolean OPTIMIZE_MEM_RECYCLE_BITMAP = false;

    String BROADCAST_PLAYPAUSE = ACTION_FREE.BROADCAST_PLAYPAUSE;
    String BROADCAST_SEEKBAR = ACTION_FREE.BROADCAST_SEEKBAR;
    String BROADCAST_SWAP = ACTION_FREE.BROADCAST_SWAP;
    String BROADCAST_ACTION = ACTION_FREE.BROADCAST_ACTION;
    String BROADCAST_COVER = ACTION_FREE.BROADCAST_COVER;
    String BROADCAST_STOP_NOW = ACTION_FREE.BROADCAST_STOP_NOW;
    String BROADCAST_STOP = ACTION_FREE.BROADCAST_STOP;
    String BROADCAST_OPEN_ACTIVITY = ACTION_FREE.BROADCAST_OPEN_ACTIVITY;
    String BROADCAST_CHECK_AGAIN = ACTION_FREE.BROADCAST_CHECK_AGAIN;
    String BROADCAST_PREV = ACTION_FREE.BROADCAST_PREV;
    String BROADCAST_ON_SONGDETAILS_UPDATED = ACTION_FREE.BROADCAST_ON_SONGDETAILS_UPDATED;
    String BROADCAST_EQ = ACTION_FREE.BROADCAST_EQ;
    int REQUEST_CODE = 0;
    String SERVICECMD = ACTION_FREE.SERVICECMD;
    String CMDNAME = ACTION_FREE.CMDNAME;
    String CMDTOGGLEPAUSE = ACTION_FREE.CMDTOGGLEPAUSE;
    String CMDSTOP = ACTION_FREE.CMDSTOP;
    String CMDPLAY = ACTION_FREE.CMDPLAY;
    String CMDPAUSE = ACTION_FREE.CMDPAUSE;
    String CMDPREVIOUS = ACTION_FREE.CMDPREVIOUS;
    String CMDNEXT = ACTION_FREE.CMDNEXT;
    String TOGGLEPAUSE_ACTION = ACTION_FREE.TOGGLEPAUSE_ACTION;
    String PLAY_ACTION = ACTION_FREE.PLAY_ACTION;
    String PAUSE_ACTION = ACTION_FREE.PAUSE_ACTION;
    String PREVIOUS_ACTION = ACTION_FREE.PREVIOUS_ACTION;
    String NEXT_ACTION = ACTION_FREE.NEXT_ACTION;
    String ACTION_SLEEP_TIMER = "android.intent.action.ACTION_SLEEP_TIMER";
    int ACTION_SLEEP_TIMER_CODE = 1234;
    int DURATION_FILTER_DEFAULT = 10000;
    int REQUEST_CODE_SEND_AUDIO = 1000;
    int REQUEST_CODE_CHANGE_SETTINGS = 1111;
    String REQUEST_FEATURE_URL = "https://www.google.com/";
    String REPORT_BUG_URL = "https://translate.google.com/";
    String CALCULATOR_APP_PACKAGE = "champanator";

    final class ACTION_FREE {
        final public static String BROADCAST_PLAYPAUSE = "champa.action.BROADCAST_PLAYPAUSE";
        public final static String BROADCAST_SEEKBAR = "champa.action.sendseekbar";
        public final static String BROADCAST_SWAP = "champa.action.BROADCAST_SWAP";
        public static final String BROADCAST_ACTION = "champa.action.seekprogress";
        public static final String BROADCAST_COVER = "champa.action.cover";
        public static final String BROADCAST_STOP_NOW = "champa.action.juststop";
        public static final String BROADCAST_STOP = "champa.action.stop";
        public static final String BROADCAST_OPEN_ACTIVITY = "champa.action.BROADCAST_OPEN_ACTIVITY";
        public static final String BROADCAST_CHECK_AGAIN = "xyz";
        public static final String BROADCAST_PREV = "champa.action.BROADCAST_PREV";
        public static final String BROADCAST_ON_SONGDETAILS_UPDATED = "songdetails.updated";
        public static final String BROADCAST_EQ = "champa.action.eq";
        public static final String SERVICECMD = "champa.action.musicservicecommand";
        public static final String CMDNAME = "command";
        public static final String CMDTOGGLEPAUSE = "togglepause";
        public static final String CMDSTOP = "stop";
        public static final String CMDPLAY = "play";
        public static final String CMDPAUSE = "act_pause";
        public static final String CMDPREVIOUS = "previous";
        public static final String CMDNEXT = "next";

        public static final String TOGGLEPAUSE_ACTION = "champa.musicservicecommand.togglepause";
        public static final String PLAY_ACTION = "champa.musicservicecommand.play";
        public static final String PAUSE_ACTION = "champa.musicservicecommand.act_pause";
        public static final String PREVIOUS_ACTION = "champa.musicservicecommand.previous";
        public static final String NEXT_ACTION = "champa.musicservicecommand.next";
    }
}
