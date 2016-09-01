/**
 * Logger.java
 * <p/>
 * <br>
 * <b>Purpose</b> : CLog This class is used for print out log base on it's tag. Depend on flag ENABLE_LOG is true/false to print out log. Switch off
 * ENABLE_LOG in release build
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

import android.os.Debug;
import android.util.Log;

import java.text.DecimalFormat;

@SuppressWarnings("unused")
public class Logger {
    public static void d(String tag, String msg) {
        if (IConstant.ENABLE_LOG) {
            Log.d(tag, msg);
        }
    }

    public static int d(String tag, String msg, Throwable tr) {
        if (IConstant.ENABLE_LOG) {
            return Log.d(tag, msg, tr);
        }
        return 0;
    }

    public static int e(String tag, String msg) {
        if (IConstant.ENABLE_LOG) {
            return Log.e(tag, msg);
        }
        return 0;
    }

    public static int e(String tag, String msg, Throwable tr) {
        if (IConstant.ENABLE_LOG) {
            return Log.e(tag, msg, tr);
        }
        return 0;
    }

    public static String getStackTraceString(Throwable tr) {
        if (IConstant.ENABLE_LOG) {
            return Log.getStackTraceString(tr);
        }
        return "";
    }

    public static int i(String tag, String msg) {
        if (IConstant.ENABLE_LOG) {
            return Log.i(tag, msg);
        }
        return 0;
    }

    public static int i(String tag, String msg, Throwable tr) {
        if (IConstant.ENABLE_LOG) {
            return Log.i(tag, msg, tr);
        }
        return 0;
    }

    public static boolean isLoggable(String tag, int level) {
        return Log.isLoggable(tag, level);
    }

    public static int println(int priority, String tag, String msg) {
        if (IConstant.ENABLE_LOG) {
            return Log.println(priority, tag, msg);
        }
        return 0;
    }

    public static int v(String tag, String msg) {
        if (IConstant.ENABLE_LOG) {
            return Log.v(tag, msg);
        }
        return 0;
    }

    public static int v(String tag, String msg, Throwable tr) {
        if (IConstant.ENABLE_LOG) {
            return Log.v(tag, msg, tr);
        }
        return 0;
    }

    public static int w(String tag, Throwable tr) {
        if (IConstant.ENABLE_LOG) {
            return Log.w(tag, tr);
        }
        return 0;
    }

    public static int w(String tag, String msg, Throwable tr) {
        if (IConstant.ENABLE_LOG) {
            return Log.w(tag, msg, tr);
        }
        return 0;
    }

    public static int w(String tag, String msg) {
        if (IConstant.ENABLE_LOG) {
            return Log.w(tag, msg);
        }
        return 0;
    }

    public static void logHeap(String prefix, Class<?> clazz) {

        Double allocated = (double) (Debug.getNativeHeapAllocatedSize()) / (double) (1048576);
        Double available = Debug.getNativeHeapSize() / 1048576.0;
        Double free = Debug.getNativeHeapFreeSize() / 1048576.0;
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);

        Log.w("logHeap", prefix + " allocated " + df.format(allocated) + "MB in ["
                + clazz.getName().replaceAll("fr.playsoft.assurland.", "") + "]");
    }

    public static void printStackTraces(Exception e) {
        if (IConstant.ENABLE_PRINT_STACK_TRACES) {
            e.printStackTrace();
        }
    }
}
