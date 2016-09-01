package champak.champabun.business.utilities.utilMethod;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.ArrayList;

import champak.champabun.business.utilities.crouton.Crouton;
import champak.champabun.business.utilities.crouton.Style;
import champak.champabun.view.activity.BaseActivity;

public class ActivityUtil {
    private static ArrayList<BaseActivity> openActivities = new ArrayList<BaseActivity>();
    private static BaseActivity currentActivity;

    public static void SaveOpenActivities(BaseActivity activity) {
        openActivities.add(activity);
    }

    public static void RemoveOpenActivities(BaseActivity activity) {
        openActivities.remove(activity);
    }

    public static void SetCurrentActivity(BaseActivity activity) {
        currentActivity = activity;
    }

    public static BaseActivity GetCurrentActivity() {
        return currentActivity;
    }

    public static boolean IsActivityCreated(String activity_id) {
        if (openActivities == null || openActivities.size() == 0) {
            return false;
        }
        for (int i = 0; i < openActivities.size(); i++) {
            if (openActivities.get(i).GetActivityID().equals(activity_id)) {
                return true;
            }
        }
        return false;
    }

    public static void CloseAllOpenActivities() {
        for (BaseActivity activity : openActivities) {
            activity.finish();
        }
        openActivities.clear();
    }

    public static void showCrouton(Activity mActivity, final String text) {
        final Crouton _Crouton = Crouton.makeText(mActivity, text, Style.MY);
        mActivity.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Crouton.cancelAllCroutons();
                _Crouton.show();
            }
        });
    }

    /**
     * Get app version code
     */
    public static String GetPackageName(Context applicationContext) {
        try {
            PackageInfo packageInfo = applicationContext.getPackageManager().getPackageInfo(applicationContext.getPackageName(), 0);
            return packageInfo.packageName;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Implicit show soft keyboard
     */
    public static void ShowKeyboard(EditText editText) {
        InputMethodManager imm = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    public static boolean IsMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check whether network is available or not
     */
    public static boolean IsNetworkAvailable(Context mContext) {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null)
            return false;

        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnectedOrConnecting();
    }

    /**
     * Get app version name
     */
    public static String GetVersionName(Context context) {
        String app_ver = null;
        try {
            app_ver = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
        }
        return app_ver;
    }

    /**
     * Get app version code
     */
    public static int GetVersionCode(Context applicationContext) {
        try {
            PackageInfo packageInfo = applicationContext.getPackageManager().getPackageInfo(applicationContext.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * Get devices' screen width
     */
    public static int GetScreenWidth(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    public static void OpenPlaystore(Activity activity) {
        OpenPlaystore(activity, GetPackageName(activity.getApplicationContext()));
    }

    public static void OpenPlaystore(Activity activity, String appPackage) {
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackage)));
        } catch (android.content.ActivityNotFoundException anfe) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackage)));
        }
    }

    public static boolean IsAPKInstalled(Context context, String package_name) {
        PackageManager pm = context.getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(package_name, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    public static void StartActivity(Context context, String package_name) throws Exception {
        if (Utilities.IsEmpty(package_name)) {
            throw new Exception("Stub!!! Package name cannot be null");
        }

        Intent LaunchIntent = context.getPackageManager().getLaunchIntentForPackage(package_name);
        context.startActivity(LaunchIntent);
    }
}
