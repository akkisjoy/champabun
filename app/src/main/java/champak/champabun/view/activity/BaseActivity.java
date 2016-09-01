/**
 * BaseActivity.java
 * <p/>
 * <br>
 * <b>Purpose</b> : Base class for all inheritance activities
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
 * <b>lastChangedDate</b> : Sep 24, 2015
 */
package champak.champabun.view.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import champak.champabun.AmuzicgApp;
import champak.champabun.R;
import champak.champabun.business.dataclasses.AppDatabase;
import champak.champabun.business.dataclasses.AppSetting;
import champak.champabun.business.definition.IConstant;
import champak.champabun.business.definition.Logger;
import champak.champabun.business.utilities.utilMethod.ActivityUtil;
import champak.champabun.business.utilities.utilMethod.Utilities;
import champak.champabun.framework.service.Music_service;
import champak.champabun.framework.service.UpdateWidgetService;

public abstract class BaseActivity extends AppCompatActivity {
    public AppSetting appSettings;
    BroadcastReceiver stop = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            AmuzicgApp.GetInstance().CLearNowPlaying();
            Intent updateWidgetService = new Intent(BaseActivity.this, UpdateWidgetService.class);
            updateWidgetService.putExtra("action", UpdateWidgetService.ACTION_STOP);
            context.startService(updateWidgetService);
            if (IConstant.USE_SYSTEM_GC) {
                System.gc();
            }
            BaseActivity.this.stopService(new Intent(BaseActivity.this, Music_service.class));
            ActivityUtil.CloseAllOpenActivities();
        }
    };
    private boolean isRegisterReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (AppDatabase.IsAppFullscreen(this)) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        }
        if (!isRegisterReceiver) {
            registerReceiver(stop, new IntentFilter(IConstant.BROADCAST_STOP));
            isRegisterReceiver = true;
        }

        appSettings = ((AmuzicgApp) getApplication()).getAppSettings();
        if (GetLayoutResID() > 0) {
            setContentView(GetLayoutResID());
        }

        // google analytics
        String screenName = GetGAScreenName();
        if (!Utilities.IsEmpty(screenName)) {
            // Get a Tracker (should auto-report)
            Tracker t = ((AmuzicgApp) getApplication()).getTracker(AmuzicgApp.TrackerName.APP_TRACKER);
            // Set screen name.
            t.setScreenName(screenName);
            // Send a screen view.
            t.send(new HitBuilders.AppViewBuilder().build());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        ActivityUtil.SaveOpenActivities(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ActivityUtil.SetCurrentActivity();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            OnBackPressed();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        try {
            unregisterReceiver(stop);
            isRegisterReceiver = false;
        } catch (Exception ignored) {
        }

        MyGarbageCollection();
        ActivityUtil.RemoveOpenActivities(this);

        super.onDestroy();
    }

    private void MyGarbageCollection() {
        try {
            SetNullForCustomVariable();
            UnbindReferences(findViewById(GetRootViewID()));
        } catch (Exception e) {
            Logger.e("ERROR", "Unbind References" + e.toString());
        }
    }

    private void UnbindReferences(View view) {
        try {
            if (view != null) {
                if (view instanceof ViewGroup) {
                    UnbindViewGroupReferences((ViewGroup) view);
                }
            }
            if (IConstant.USE_SYSTEM_GC) {
                System.gc();
            }
        } catch (Throwable e) {
            // whatever exception is thrown just ignore it because a crash is always worse than this method not doing what it's supposed to do
        }
    }

    protected void UnbindViewGroupReferences(ViewGroup viewGroup) {
        if (viewGroup == null)
            return;

        int nrOfChildren = viewGroup.getChildCount();
        for (int i = 0; i < nrOfChildren; i++) {
            View view = viewGroup.getChildAt(i);
            UnbindViewReferences(view);
            if (view instanceof ViewGroup)
                UnbindViewGroupReferences((ViewGroup) view);
        }
        try {
            viewGroup.removeAllViews();
        } catch (Throwable mayHappen) {
            // AdapterViews, ListViews and potentially other ViewGroups don't support the removeAllViews operation
        }
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    private void UnbindViewReferences(View view) {
        if (view == null)
            return;
        // set all listeners to null (not every view and not every API level supports the methods)
        try {
            view.setOnClickListener(null);
        } catch (Throwable ignored) {
        }
        try {
            view.setOnCreateContextMenuListener(null);
        } catch (Throwable ignored) {
        }
        try {
            view.setOnFocusChangeListener(null);
        } catch (Throwable ignored) {
        }
        try {
            view.setOnKeyListener(null);
        } catch (Throwable ignored) {
        }
        try {
            view.setOnLongClickListener(null);
        } catch (Throwable ignored) {
        }
        try {
            view.setOnClickListener(null);
        } catch (Throwable ignored) {
        }

        // set background to null
        Drawable d = view.getBackground();
        if (d != null)
            d.setCallback(null);
        if (view instanceof ImageView) {
            ImageView imageView = (ImageView) view;
            d = imageView.getDrawable();
            if (d != null)
                d.setCallback(null);
            imageView.setImageDrawable(null);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                imageView.setBackgroundDrawable(null);
            } else {
                imageView.setBackground(null);
            }
        }
    }

    public abstract String GetActivityID();

    protected void SetNullForCustomVariable() {
    }

    public int GetRootViewID() {
        return R.id.rootview;
    }

    public abstract int GetLayoutResID();

    public abstract void OnBackPressed();

    protected abstract String GetGAScreenName();
}
