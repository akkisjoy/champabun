package champak.champabun.view.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;

import java.util.Calendar;

import champak.champabun.R;
import champak.champabun.business.dataclasses.AppDatabase;
import champak.champabun.business.dataclasses.SleepTime;
import champak.champabun.business.definition.IConstant;
import champak.champabun.business.utilities.utilClass.MyTimePickerDialog;
import champak.champabun.business.utilities.utilMethod.Utilities;

public class Settings extends BaseActivity implements OnClickListener {
    final private static String TAG = "Settings";
    private CheckBox auto_download_album_art_cb, fullscreen_cb;
    private Calendar calendar;
    private TextView sleepTimerView, durationFilterView;
    private ShimmerTextView titleHeader;
    private Dialog dialog;
    private boolean needRefresh = false;
    private boolean needRefreshFullscreen = false;
    private OnTimeSetListener callback = new OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            SetTime(hourOfDay, minute);
            SleepTime st = new SleepTime(hourOfDay, minute);
            appSettings.setSleepTimer(st);
            AppDatabase.SaveSleepTimer(Settings.this, st);
            Utilities.SetSleepTimer(Settings.this, hourOfDay, minute);
        }
    };

    @Override
    public int GetLayoutResID() {
        return R.layout.settings;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            needRefresh = savedInstanceState.getBoolean("needRefresh");
            needRefreshFullscreen = savedInstanceState.getBoolean("needRefreshFullscreen");
        } else if (getIntent().getExtras() != null) {
            needRefresh = getIntent().getExtras().getBoolean("needRefresh", false);
            needRefreshFullscreen = getIntent().getExtras().getBoolean("needRefreshFullscreen", false);
        }
        calendar = Calendar.getInstance();

        titleHeader = (ShimmerTextView) findViewById(R.id.titleHeader);
        new Shimmer().setRepeatCount(0)
                .setDuration(2000)
                .setStartDelay(100)
                .start(titleHeader);

        View sleep_timer = findViewById(R.id.sleep_timer_view);
        sleep_timer.setOnClickListener(this);

        sleepTimerView = (TextView) findViewById(R.id.sleep_timer_time);

        View dView = findViewById(R.id.duration_filter_view);
        durationFilterView = (TextView) dView.findViewById(R.id.duration_filter_time);
        dView.setOnClickListener(this);

        View auto_download_album_art = findViewById(R.id.auto_download_album_art);
        auto_download_album_art.setOnClickListener(this);
        auto_download_album_art_cb = (CheckBox) auto_download_album_art.findViewById(R.id.auto_download_album_art_cb);

        View fullscreen = findViewById(R.id.fullscreen);
        fullscreen.setOnClickListener(this);
        fullscreen_cb = (CheckBox) fullscreen.findViewById(R.id.fullscreen_cb);

        UpdateView();
    }

    private void UpdateView() {
        int curHour = calendar.get(Calendar.HOUR_OF_DAY);
        int curMin = calendar.get(Calendar.MINUTE);
        boolean set = false;
        if (appSettings.getSleepTimer() != null) {
            set = appSettings.getSleepTimer().getHour() > curHour
                    || (appSettings.getSleepTimer().getHour() == curHour && appSettings.getSleepTimer().getMinute() > curMin);
        }
        if (set) {
            SetTime(appSettings.getSleepTimer().getHour(), appSettings.getSleepTimer().getMinute());
        } else {
            sleepTimerView.setText(getString(R.string.setting_not_set));
        }

        durationFilterView.setText(String.valueOf(appSettings.getDurationFilterTime()));
        auto_download_album_art_cb.setChecked(appSettings.isAutoDownloadAlbumArt());
        auto_download_album_art_cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                OnAutoDownloadAlbumArt();
            }
        });

        fullscreen_cb.setChecked(appSettings.isAppFullscreen());
        fullscreen_cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                OnFullscreen(arg1);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("needRefresh", needRefresh);
        outState.putBoolean("needRefreshFullscreen", needRefreshFullscreen);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sleep_timer_view: {
                ShowTimePickerDialog();
                break;
            }
            case R.id.duration_filter_view: {
                ShowEditDurationDialog();
                break;
            }

            case R.id.auto_download_album_art: {
                auto_download_album_art_cb.setChecked(!auto_download_album_art_cb.isChecked());
                break;
            }
            case R.id.fullscreen: {
                fullscreen_cb.setChecked(!fullscreen_cb.isChecked());
                break;
            }

            default:
                break;
        }
    }

    @Override
    public String GetActivityID() {
        return "Settings";
    }

    @Override
    public void OnBackPressed() {
        if (needRefresh || needRefreshFullscreen) {
            Intent intent = getIntent();
            intent.putExtra("needRefreshFullscreen", needRefreshFullscreen);
            setResult(Activity.RESULT_OK, intent);
        } else {
            setResult(Activity.RESULT_CANCELED);
        }
        finish();
    }

    @Override
    protected String GetGAScreenName() {
        return null;
    }

    private void ShowTimePickerDialog() {
        MyTimePickerDialog dialog = new MyTimePickerDialog(calendar, Settings.this, getString(R.string.set_time), getString(R.string.cancel),
                callback);
        dialog.SetOnButtonClickListener(new MyTimePickerDialog.OnButtonClickListener() {

            @Override
            public void OnSet() {
            }

            @Override
            public void OnCancel() {
                sleepTimerView.setText(getString(R.string.setting_not_set));
                appSettings.setSleepTimer(null);
                AppDatabase.CancelSleepTimer(Settings.this);
                Utilities.CancelSleepTimer(Settings.this);
            }
        });
        dialog.show();
    }

    public void SetTime(int hourOfDay, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        String hour;
        if (hourOfDay < 10) {
            hour = "0" + hourOfDay;
        } else {
            hour = String.valueOf(hourOfDay);
        }
        String min = null;
        if (minute < 10) {
            min = "0" + minute;
        } else {
            min = String.valueOf(minute);
        }
        sleepTimerView.setText(hour + " : " + min);
    }

    private void ShowEditDurationDialog() {
        designdialog(210);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialoge_edit_duration_filter);
        EditText edittext = (EditText) dialog.findViewById(R.id.ti);
        String du = String.valueOf(appSettings.getDurationFilterTime());
        edittext.setText(du);
        edittext.requestFocus(du.length());

        Button bDOK = (Button) dialog.findViewById(R.id.dBOK);
        bDOK.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                EditText edittext = (EditText) dialog.findViewById(R.id.ti);
                String str = edittext.getText().toString();
                int duration = IConstant.DURATION_FILTER_DEFAULT;
                try {
                    duration = Integer.parseInt(str);
                } catch (NumberFormatException ignored) {
                }
                int oldDuration = appSettings.getDurationFilterTime();
                if (oldDuration != duration) {
                    durationFilterView.setText(String.valueOf(duration));
                    appSettings.setDurationFilterTime(duration);
                    AppDatabase.SaveDurationFilterTime(Settings.this, duration);

                    Intent intent = getIntent();
                    intent.putExtra("isDurationFilterChanged", true);
                    needRefresh = true;
                }
                dialog.dismiss();
            }
        });

        Button bDCancle = (Button) dialog.findViewById(R.id.dBCancel);
        bDCancle.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void designdialog(int x) {
        dialog = new Dialog(this, R.style.AmuzeTheme);
        final Window window = dialog.getWindow();
        float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, x, getResources().getDisplayMetrics());
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, (int) pixels);
        window.setBackgroundDrawableResource(R.drawable.miniback2);
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.dimAmount = 0.8f; // Dim level. 0.0 - no dim, 1.0 - completely opaque
        dialog.getWindow().setAttributes(lp);
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }


    private void OnAutoDownloadAlbumArt() {
        appSettings.setAutoDownloadAlbumArt(auto_download_album_art_cb.isChecked());
        AppDatabase.SetAutoDownloadAlbumArt(Settings.this, auto_download_album_art_cb.isChecked());
    }

    private void OnFullscreen(boolean isFullscreen) {
        appSettings.setAppFullscreen(isFullscreen);
        AppDatabase.SetAppFullscreen(Settings.this, isFullscreen);
        needRefreshFullscreen = true;
        // restart this activity
        Intent intent = getIntent();
        intent.putExtra("needRefresh", needRefresh);
        intent.putExtra("needRefreshFullscreen", needRefreshFullscreen);
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        setResult(Activity.RESULT_OK, intent);
        Settings.this.finish();
        overridePendingTransition(0, 0);
        Settings.this.startActivity(intent);
    }
}
