package champak.champabun.business.utilities.utilMethod;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import champak.champabun.R;
import champak.champabun.view.activity.BaseActivity;

public class ActivityUtil {
    private static ArrayList<BaseActivity> openActivities = new ArrayList<>();

    public static void SaveOpenActivities(BaseActivity activity) {
        openActivities.add(activity);
    }

    public static void RemoveOpenActivities(BaseActivity activity) {
        openActivities.remove(activity);
    }

    public static void SetCurrentActivity() {
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

//    public static void showCrouton(Activity mActivity, final String text) {
//        final Crouton _Crouton = Crouton.makeText(mActivity, text, Style.MY);
//        mActivity.runOnUiThread(new Runnable() {
//
//            @Override
//            public void run() {
//                Crouton.cancelAllCroutons();
//                _Crouton.show();
//            }
//        });
//    }

    public static void showCrouton(Activity mActivity, final String text) {
        LayoutInflater inflater = LayoutInflater.from(mActivity);
        View layout = inflater.inflate(R.layout.toast_layout, null);

        TextView textV = (TextView) layout.findViewById(R.id.toastText);
        textV.setText(text);

        Toast toast = new Toast(mActivity);
        toast.setGravity(Gravity.CENTER, 0, 0);
        boolean isLong = true;
        toast.setDuration((isLong) ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
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

    public static boolean IsNetworkAvailable(Context mContext) {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null)
            return false;

        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnectedOrConnecting();
    }

    public static void openPlaystore(Activity activity, String appPackage) {
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackage)));
        } catch (android.content.ActivityNotFoundException anfe) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackage)));
        }
    }

    public static boolean isAPKInstalled(Context context, String package_name) {
        PackageManager pm = context.getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(package_name, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    public static void startActivity(Context context, String package_name) throws Exception {
        if (Utilities.IsEmpty(package_name)) {
            throw new Exception("Stub!!! Package name cannot be null");
        }

        Intent LaunchIntent = context.getPackageManager().getLaunchIntentForPackage(package_name);
        context.startActivity(LaunchIntent);
    }
}
