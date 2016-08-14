package champak.champabun;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;

import java.util.Calendar;

import champak.champabun.adapters.LanguageAdapter;
import champak.champabun.classes.AppDatabase;
import champak.champabun.classes.ELanguage;
import champak.champabun.classes.SleepTime;
import champak.champabun.ui.MyTimePickerDialog;
import champak.champabun.ui.TypefaceTextView;
import champak.champabun.util.ActivityUtil;
import champak.champabun.util.Utilities;

public class Settings extends BaseActivity implements OnClickListener {
    final private static String TAG = "Settings";
    private CheckBox auto_download_album_art_cb, fullscreen_cb;
    private Calendar calendar;
    private TypefaceTextView sleepTimerView, languageView, durationFilterView, chooseBGView;
    private Dialog dialog;
    private ListView listview;
    //private boolean isOverridePendingTransition = false;
    private boolean needRefresh = false;
    private boolean needRefreshLanguage = false;
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
        //	if(!IConstant.IS_PRO_VERSION)
        //	   Appodeal.show(Settings.this, Appodeal.INTERSTITIAL);
        if (savedInstanceState != null) {
            needRefresh = savedInstanceState.getBoolean("needRefresh");
            needRefreshLanguage = savedInstanceState.getBoolean("needRefreshLanguage");
            needRefreshFullscreen = savedInstanceState.getBoolean("needRefreshFullscreen");
            //	isOverridePendingTransition = savedInstanceState.getBoolean( "isOverridePendingTransition" );
        } else if (getIntent().getExtras() != null) {
            needRefresh = getIntent().getExtras().getBoolean("needRefresh", false);
            needRefreshLanguage = getIntent().getExtras().getBoolean("needRefreshLanguage", false);
            needRefreshFullscreen = getIntent().getExtras().getBoolean("needRefreshFullscreen", false);
            //	isOverridePendingTransition = getIntent( ).getExtras( ).getBoolean( "isOverridePendingTransition", true );
        }
        //if ( isOverridePendingTransition )
        //{
        //	overridePendingTransition( R.anim.slide_in_left, R.anim.slide_out_left );
        //}
        calendar = Calendar.getInstance();

        View my_apps = findViewById(R.id.my_apps);
        my_apps.setOnClickListener(this);

        View sleep_timer = findViewById(R.id.sleep_timer_view);
        sleep_timer.setOnClickListener(this);

        sleepTimerView = (TypefaceTextView) findViewById(R.id.sleep_timer_time);

        View dView = findViewById(R.id.duration_filter_view);
        durationFilterView = (TypefaceTextView) dView.findViewById(R.id.duration_filter_time);
        dView.setOnClickListener(this);

        View langView = findViewById(R.id.language_view);
        languageView = (TypefaceTextView) langView.findViewById(R.id.language);
        languageView.setText(appSettings.getLanguage().getLanguageName());
        langView.setOnClickListener(this);

        TypefaceTextView cross_fading = (TypefaceTextView) findViewById(R.id.cross_fading);
        cross_fading.setOnClickListener(this);

        TypefaceTextView rescan_lib = (TypefaceTextView) findViewById(R.id.rescan_lib);
        rescan_lib.setOnClickListener(this);

        TypefaceTextView choose_folder = (TypefaceTextView) findViewById(R.id.choose_folder);
        choose_folder.setOnClickListener(this);

        View bgV = findViewById(R.id.choose_background_view);
        chooseBGView = (TypefaceTextView) bgV.findViewById(R.id.choose_background_value);
        bgV.setOnClickListener(this);

        View auto_download_album_art = findViewById(R.id.auto_download_album_art);
        auto_download_album_art.setOnClickListener(this);
        auto_download_album_art_cb = (CheckBox) auto_download_album_art.findViewById(R.id.auto_download_album_art_cb);

        View fullscreen = findViewById(R.id.fullscreen);
        fullscreen.setOnClickListener(this);
        fullscreen_cb = (CheckBox) fullscreen.findViewById(R.id.fullscreen_cb);

        // TypefaceTextView theme = ( TypefaceTextView ) findViewById( R.id.theme );
        // theme.setOnClickListener( this );

        // TypefaceTextView choose_album = ( TypefaceTextView ) findViewById( R.id.choose_album );
        // choose_album.setOnClickListener( this );

        // TypefaceTextView choose_color = ( TypefaceTextView ) findViewById( R.id.choose_color );
        // choose_color.setOnClickListener( this );

        // TypefaceTextView choose_font = ( TypefaceTextView ) findViewById( R.id.choose_font );
        // choose_font.setOnClickListener( this );

        TypefaceTextView request_feature = (TypefaceTextView) findViewById(R.id.request_feature);
        request_feature.setOnClickListener(this);

        TypefaceTextView report_bug = (TypefaceTextView) findViewById(R.id.report_bug);
        report_bug.setOnClickListener(this);

        TypefaceTextView remove_ads = (TypefaceTextView) findViewById(R.id.remove_ads);
        remove_ads.setOnClickListener(this);

        TypefaceTextView donate = (TypefaceTextView) findViewById(R.id.donate);
        donate.setOnClickListener(this);

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
                OnAutoDownloadAlbumArt(arg1);
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
        outState.putBoolean("needRefreshLanguage", needRefreshLanguage);
        outState.putBoolean("needRefreshFullscreen", needRefreshFullscreen);
        //outState.putBoolean( "isOverridePendingTransition", isOverridePendingTransition );
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
            case R.id.my_apps: {
                if (ActivityUtil.IsAPKInstalled(Settings.this, IConstant.CALCULATOR_APP_PACKAGE)) {
                    try {
                        ActivityUtil.StartActivity(this, IConstant.CALCULATOR_APP_PACKAGE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    ActivityUtil.OpenPlaystore(this, IConstant.CALCULATOR_APP_PACKAGE);
                }
                break;
            }
            case R.id.sleep_timer_view: {
                ShowTimePickerDialog();
                break;
            }
            case R.id.language_view: {
                ShowLanguageListDialog();
                break;
            }
            case R.id.duration_filter_view: {
                ShowEditDurationDialog();
                break;
            }
            case R.id.cross_fading: {
                break;
            }
            case R.id.rescan_lib: {
                break;
            }
            case R.id.choose_folder: {
                break;
            }
            case R.id.choose_background_view: {
                Intent intent = new Intent(Settings.this, ChooseBackground.class);
                startActivityForResult(intent, IConstant.REQUEST_CODE_CHANGE_BACKGROUND);
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
            // case R.id.theme:
            // {
            // break;
            // }
            // case R.id.choose_album:
            // {
            // break;
            // }
            // case R.id.choose_color:
            // {
            // break;
            // }
            // case R.id.choose_font:
            // {
            // break;
            // }
            case R.id.request_feature: {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(IConstant.REQUEST_FEATURE_URL));
                startActivity(intent);
                break;
            }
            case R.id.report_bug: {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(IConstant.REPORT_BUG_URL));
                startActivity(intent);
                break;
            }
            case R.id.remove_ads: {

                ActivityUtil.OpenPlaystore(this, IConstant.PLAYMEE_PRO_PACKAGE);
                break;
            }
            case R.id.donate: {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(IConstant.DONATE_URL));
                startActivity(intent);
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
//		if(!IConstant.IS_PRO_VERSION){
        //Appodeal.hide(Settings.this,Appodeal.INTERSTITIAL);
//		}
        if (needRefresh || needRefreshLanguage || needRefreshFullscreen) {
            Intent intent = getIntent();
            intent.putExtra("needRefreshLanguage", needRefreshLanguage);
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

    @Override
    protected void onActivityResult(int requestcode, int resultCode, Intent intent) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestcode) {
                case IConstant.REQUEST_CODE_CHANGE_BACKGROUND: {
                    Intent intent2 = getIntent();
                    intent2.putExtra("isAppBGChanged", true);
                    needRefresh = true;
                    break;
                }
                default:
                    break;
            }
        }
        // else if ( resultCode == Activity.RESULT_CANCELED )
        // {
        // }
        super.onActivityResult(requestcode, resultCode, intent);
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
        String hour = null;
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

    private void ShowLanguageListDialog() {
        designdialog(400);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.dialoge_listview);

        listview = (ListView) dialog.findViewById(R.id.listview);
        LanguageAdapter adapter = new LanguageAdapter(Settings.this);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                int language = position;
                dialog.dismiss();
                if (appSettings.getLanguage().getLanguageCode() != language) {
                    ELanguage elanguage = ELanguage.GetLanguage(language);
                    appSettings.setLanguage(elanguage);
                    AppDatabase.SetLanguage(getApplicationContext(), language);
                    ((GlobalSongList) getApplication()).ChangeLanguage();
                    needRefresh = true;
                    needRefreshLanguage = true;
                    //isOverridePendingTransition = false;
                    // restart this activity
                    Intent intent = getIntent();
                    intent.putExtra("needRefresh", needRefresh);
                    intent.putExtra("needRefreshLanguage", needRefreshLanguage);
                    //intent.putExtra( "isOverridePendingTransition", isOverridePendingTransition );
                    overridePendingTransition(0, 0);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    Settings.this.finish();
                    overridePendingTransition(0, 0);
                    Settings.this.startActivity(intent);
                }
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
                } catch (NumberFormatException e) {
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
        dialog = new Dialog(this, R.style.playmee);
        final Window window = dialog.getWindow();
        float pixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, x, getResources().getDisplayMetrics());
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, (int) pixels);
        window.setBackgroundDrawableResource(R.drawable.dialogbg);
        // window.setBackgroundDrawable(new ColorDrawable(0x99000000));
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.dimAmount = 0.8f; // Dim level. 0.0 - no dim, 1.0 - completely opaque
        dialog.getWindow().setAttributes(lp);
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }


    private void OnAutoDownloadAlbumArt(boolean value) {
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
        intent.putExtra("needRefreshLanguage", needRefreshLanguage);
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
