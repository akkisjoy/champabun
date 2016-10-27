package champak.champabun.business.utilities.utilMethod;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import champak.champabun.R;
import champak.champabun.business.dataclasses.SongDetails;
import champak.champabun.business.definition.IConstant;

public class Utilities {
    final private static String vietnameseChars = "\u00E1\u00E0\u1EA3\u00E3\u1EA1\u0103\u1EAF\u1EB1\u1EB3\u1EB5\u1EB7\u00E2\u1EA5\u1EA7\u1EA9\u1EAB\u1EAD\u00E9\u00E8\u1EBB\u1EBD\u1EB9\u00EA\u1EBF\u1EC1\u1EC3\u1EC5\u1EC7\u00ED\u00EC\u1EC9\u0129\u1ECB\u00F3\u00F2\u1ECF\u00F5\u1ECD\u00F4\u1ED1\u1ED3\u1ED5\u1ED7\u1ED9\u01A1\u1EDB\u1EDD\u1EDF\u1EE1\u1EE3\u00FA\u00F9\u1EE7\u0169\u1EE5\u01B0\u1EE9\u1EEB\u1EED\u1EEF\u1EF1\u00FD\u1EF3\u1EF7\u1EF9\u1EF5\u0111\u00C1\u00C0\u1EA2\u00C3\u1EA0\u0102\u1EAE\u1EB0\u1EB2\u1EB4\u1EB6\u00C2\u1EA4\u1EA6\u1EA8\u1EAA\u1EAC\u00C9\u00C8\u1EBA\u1EBC\u1EB8\u00CA\u1EBE\u1EC0\u1EC2\u1EC4\u1EC6\u00CD\u00CC\u1EC8\u0128\u1ECA\u00D3\u00D2\u1ECE\u00D5\u1ECC\u00D4\u1ED0\u1ED2\u1ED4\u1ED6\u1ED8\u01A0\u1EDA\u1EDC\u1EDE\u1EE0\u1EE2\u00DA\u00D9\u1EE6\u0168\u1EE4\u01AF\u1EE8\u1EEA\u1EEC\u1EEE\u1EF0\u00DD\u1EF2\u1EF6\u1EF8\u1EF4\u0110";
    final private static String charMaps = "okkkkkkkkkk";

    public static String getTime(String millisecs) {
        String hms = null;
        try {
            int millis = Integer.parseInt(millisecs);
            hms = getTime(millis);
        } catch (Exception ignored) {
        }
        return hms;
    }

    public static String getTime(int millis) {
        String hms;
        if (millis > 3600000) {
            hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                    TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
        } else {
            hms = String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                    TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
        }
        return hms;
    }

    /**
     * Get time in millisecond
     *
     * @param time : format "hh:mm:ss"
     * @return time in millisecond
     */
    public static int parseTime(String time) {
        int reTime = 0;
        if (!isEmpty(time)) {
            time = time.trim();
            int hour = 0;
            int minute;
            int second;
            int firstIndex = time.indexOf(":");
            int lastIndex = time.lastIndexOf(":");
            if (firstIndex != lastIndex) {
                hour = Integer.parseInt(time.substring(0, firstIndex));
                minute = Integer.parseInt(time.substring(firstIndex + 1, lastIndex));
                second = Integer.parseInt(time.substring(lastIndex + 1));
            } else {
                minute = Integer.parseInt(time.substring(0, firstIndex));
                second = Integer.parseInt(time.substring(firstIndex + 1));
            }
            reTime = ((hour * 3600) + (minute * 60) + second) * 1000;
        }

        return reTime;
    }

    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static ArrayList<SongDetails> generatePlaylists(Context context) {
        ArrayList<SongDetails> Playlists = new ArrayList<SongDetails>();

        String[] columns = {MediaStore.Audio.Playlists._ID, MediaStore.Audio.Playlists.NAME};
        String criteria = MediaStore.Audio.Playlists.NAME.length() + " > 0 ";
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, columns, criteria, null,
                    MediaStore.Audio.Playlists.NAME + " COLLATE NOCASE ASC");
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Playlists.add(new SongDetails(cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Playlists._ID)), cursor.getString(0),
                            cursor.getString(0), cursor.getString(1)));
                }
                while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return Playlists;
    }

    public static boolean IsEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static String ToUpperCase(String str) {
        if (IsEmpty(str)) {
            return str;
        }

        String re = "";
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch >= 'a' && ch <= 'z') {
                ch -= ('a' - 'A');
            }
            re += ch;
        }

        return re;
    }

    public static String RemoveVietnameseStringSign(String vietnameseString) {
        if (IsEmpty(vietnameseString)) {
            return vietnameseString;
        }

        String re = "";
        for (int i = 0; i < vietnameseString.length(); i++) {
            char ch = vietnameseString.charAt(i);
            int index = vietnameseChars.indexOf(ch);
            if (index != -1) {
                ch = charMaps.charAt(index);
            }
            re += ch;
        }

        return re;
    }

    public static void HideSoftKeyboard(Context context, EditText edittext) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edittext.getWindowToken(), 0);
    }

    public static void SetSleepTimer(Context context, int hour, int minute) {
        Calendar cal = Calendar.getInstance();
        Intent intent = new Intent(IConstant.ACTION_SLEEP_TIMER);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, IConstant.ACTION_SLEEP_TIMER_CODE, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
        alarmMgr.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
    }

    public static void CancelSleepTimer(Context context) {
        Intent intent = new Intent(IConstant.ACTION_SLEEP_TIMER);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, IConstant.ACTION_SLEEP_TIMER_CODE, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        if (pendingIntent != null) {
            AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            pendingIntent.cancel();
            alarmMgr.cancel(pendingIntent);
        }
    }

    public static String GetStringFromInputStream(InputStream ims) {
        if (ims == null)
            return null;

        String re = null;
        byte[] bytes = new byte[1024];
        int read = 0;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            while ((read = ims.read(bytes)) != -1) {
                baos.write(bytes, 0, read);
            }

            baos.flush();
            ims.close();
            byte[] data = baos.toByteArray();
            baos.close();

            re = new String(data, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return re;
    }

    public static String GetStringFromAssets(Context context, String filename) {
        String res = null;
        AssetManager assetManager = context.getAssets();
        try {
            res = GetStringFromInputStream(assetManager.open(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static Dialog designdialog(int x, Context mActivity) {
        // TODO
        Dialog dialog2 = new Dialog(mActivity, R.style.AmuzeTheme);
        final Window window = dialog2.getWindow();
        float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, x, mActivity.getResources().getDisplayMetrics());
        dialog2.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, (int) pixels);
        window.setBackgroundDrawableResource(R.drawable.dialogbg);
        WindowManager.LayoutParams lp = dialog2.getWindow().getAttributes();
        lp.dimAmount = 0.8f; // Dim level. 0.0 - no dim, 1.0 - completely opaque
        dialog2.getWindow().setAttributes(lp);
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        return dialog2;
    }

}
