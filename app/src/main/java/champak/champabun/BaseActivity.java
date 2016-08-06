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
package champak.champabun;

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

import champak.champabun.classes.AppDatabase;
import champak.champabun.classes.AppSetting;
import champak.champabun.service.Music_service;
import champak.champabun.service.UpdateWidgetService;
import champak.champabun.util.ActivityUtil;
import champak.champabun.util.Utilities;

// import com.google.android.gms.analytics.HitBuilders;
// import com.google.android.gms.analytics.Tracker;

public abstract class BaseActivity extends AppCompatActivity {
    private boolean isRegisterReceiver;
    public AppSetting appSettings;
    // private com.google.android.gms.ads.AdView adView;
    // private ViewGroup fbAdsViewGrp;
    // private View adsLayout, closeAdsButton;
    // private View appodealBannerView;

    private boolean isAdsShown, isCloseAdsButtonShown;

    BroadcastReceiver stop = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            GlobalSongList.GetInstance().CLearNowPlaying();
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

        appSettings = ((GlobalSongList) getApplication()).getAppSettings();
        if (GetLayoutResID() > 0) {
            setContentView(GetLayoutResID());
        }

        // google analytics
        String screenName = GetGAScreenName();
        if (!Utilities.IsEmpty(screenName)) {
            // Get a Tracker (should auto-report)
            Tracker t = ((GlobalSongList) getApplication()).getTracker(GlobalSongList.TrackerName.APP_TRACKER);
            // Set screen name.
            t.setScreenName(screenName);
            // Send a screen view.
            t.send(new HitBuilders.AppViewBuilder().build());
        }

        if (!IConstant.IS_PRO_VERSION && isAdsOffer() && isAdsConfigOffer()) {
            // Appodeal
            android.util.Log.d("BaseActivity", "isAdsConfigOffer..............");
            // Appodeal.disableLocationPermissionCheck( );
            String appKey = getString(R.string.appodeal_ApiKey);
            if (isBannerAdsOffer()) {
                // Appodeal.initialize( BaseActivity.this, appKey, Appodeal.BANNER );
            }
            if (isInterstitialAdsOffer()) {
                // Appodeal.setAutoCache( Appodeal.INTERSTITIAL, false );
                // Appodeal.initialize( BaseActivity.this, appKey, Appodeal.INTERSTITIAL );
                // Appodeal.cache( this, Appodeal.INTERSTITIAL );
            }
            // closeAdsButton = findViewById( R.id.closeAdsView );
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        ActivityUtil.SaveOpenActivities(this);
    }

    @Override
    protected void onPause() {
        if (!IConstant.IS_PRO_VERSION && isAdsOffer()) {
            // Appodeal.hide( this, Appodeal.BANNER );
            isAdsShown = false;
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // if ( !IConstant.IS_PRO_VERSION && isAdsOffer( ) && isBannerAdsOffer( ) )
        // {
        // Appodeal.setBannerCallbacks( new BannerCallbacks( ) {
        //
        // @Override
        // public void onBannerShown( )
        // {
        // // android.util.Log.d( "BaseActivity", "onBannerShown.............." );
        // ShowCloseAdsButton( );
        // }
        //
        // @Override
        // public void onBannerLoaded( )
        // {
        // // android.util.Log.d( "BaseActivity", "onBannerLoaded....................." );
        // if ( !isAdsShown )
        // OnShowAds( );
        // }
        //
        // @Override
        // public void onBannerFailedToLoad( )
        // {
        // // android.util.Log.d( "BaseActivity", "onBannerFailedToLoad..............." );
        // if ( isAdsShown )
        // OnCloseAds( null );
        // }
        //
        // @Override
        // public void onBannerClicked( )
        // {
        // }
        // } );
        //
        // Appodeal.show( this, Appodeal.BANNER );
        // }
        ActivityUtil.SetCurrentActivity(this);
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
        } catch (Exception e) {
        }

        if (!IConstant.IS_PRO_VERSION && isAdsOffer())
            // Appodeal.hide( this, Appodeal.BANNER );

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
        } catch (Throwable mayHappen) {
        }
        try {
            view.setOnCreateContextMenuListener(null);
        } catch (Throwable mayHappen) {
        }
        try {
            view.setOnFocusChangeListener(null);
        } catch (Throwable mayHappen) {
        }
        try {
            view.setOnKeyListener(null);
        } catch (Throwable mayHappen) {
        }
        try {
            view.setOnLongClickListener(null);
        } catch (Throwable mayHappen) {
        }
        try {
            view.setOnClickListener(null);
        } catch (Throwable mayHappen) {
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
        //
        // // destroy webview
        // if (view instanceof WebView)
        // {
        // ((WebView) view).destroyDrawingCache();
        // ((WebView) view).destroy();
        // }
    }

    public abstract String GetActivityID();

    protected void SetNullForCustomVariable() {
    }

    public int GetRootViewID() {
        return R.id.rootview;
    }

    // private void ShowGoogleAdmob( )
    // {
    // if ( fbAdsViewGrp != null )
    // fbAdsViewGrp.setVisibility( View.GONE );
    //
    // if ( adView != null )
    // {
    // adView.loadAd( new com.google.android.gms.ads.AdRequest.Builder( ).build( ) );
    // adView.setAdListener( new AdListener( ) {
    //
    // @Override
    // public void onAdFailedToLoad( int errorCode )
    // {
    // super.onAdFailedToLoad( errorCode );
    // }
    //
    // @Override
    // public void onAdLoaded( )
    // {
    // super.onAdLoaded( );
    // if ( adLayout != null )
    // adLayout.setVisibility( View.VISIBLE );
    // adView.setVisibility( View.VISIBLE );
    // if ( fbAdsViewGrp != null )
    // fbAdsViewGrp.setVisibility( View.GONE );
    // ShowCloseAdsButton( );
    // }
    // } );
    // }
    // }

    public void OnShowAds() {
        // Appodeal.show( BaseActivity.this, Appodeal.BANNER );
        isAdsShown = true;
    }

    public void OnCloseAds(View view) {
        // View rootView = findViewById( GetMainRootLayoutID( ) );
        // if ( rootView instanceof RelativeLayout )
        // {
        // View mainView = findViewById( GetMainLayoutID( ) );
        // View adsView = findViewById( GetAdsLayoutID( ) );
        // if ( adsView != null )
        // {
        // adsView.setVisibility( View.GONE );
        // }
        // if ( mainView != null )
        // {
        // RelativeLayout.LayoutParams mainParams = ( RelativeLayout.LayoutParams ) mainView.getLayoutParams( );
        // mainParams.addRule( RelativeLayout.ALIGN_PARENT_TOP );
        // }
        // }
        // View closeButton = findViewById( R.id.closeAdsButton );
        // if ( closeButton != null )
        // closeButton.setVisibility( View.GONE );
        //
        isAdsShown = false;
        // isCloseAdsButtonShown = false;
    }

    private void ShowCloseAdsButton() {
        // adsLayout = findViewById( GetAdsLayoutID( ) );
        // if ( adsLayout != null )
        // {
        // // appodealBannerView = adsLayout.findViewById(R.id.appodealBannerView);
        // closeAdsButton = adsLayout.findViewById( R.id.closeAdsButton );
        // }

        // if ( closeAdsButton != null )
        // {
        // android.util.Log.d("BaseActivity","aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        // closeAdsButton.setVisibility( View.VISIBLE );
        // RelativeLayout.LayoutParams closeButtonParams = ( RelativeLayout.LayoutParams ) closeAdsButton.getLayoutParams( );
        // closeButtonParams.bottomMargin = getResources( ).getInteger( R.integer.appodeal_height );
        // closeAdsButton.bringToFront( );
        //
        // final android.view.ViewTreeObserver vto = appodealBannerView.getViewTreeObserver( );
        // vto.addOnGlobalLayoutListener( new android.view.ViewTreeObserver.OnGlobalLayoutListener( ) {
        // @SuppressWarnings( "deprecation" )
        // @Override
        // public void onGlobalLayout( )
        // {
        // if ( Build.VERSION.SDK_INT < 16 )
        // {
        // vto.removeGlobalOnLayoutListener( this );
        // }
        // else
        // {
        // vto.removeOnGlobalLayoutListener( this );
        // }
        // int width = appodealBannerView.getWidth( );
        // int height = appodealBannerView.getHeight( );
        // android.util.Log.d("BaseActivity","onGlobalLayout..............width = " + width + " height = " + height );
        // closeAdsButton.setVisibility( View.VISIBLE );
        // RelativeLayout.LayoutParams closeButtonParams = ( RelativeLayout.LayoutParams ) closeAdsButton.getLayoutParams( );
        // closeButtonParams.bottomMargin = height;
        // // closeButtonParams.addRule(RelativeLayout.ALIGN_TOP, R.id.appodealBannerView);
        // // closeButtonParams.addRule(RelativeLayout.ALIGN_RIGHT, R.id.appodealBannerView);
        // }
        // } );
        //// new Handler( ).postDelayed( new Runnable( ) {
        ////
        //// @Override
        //// public void run( )
        //// {
        //// }
        //// }, 150 );
        // }
        isCloseAdsButtonShown = true;
    }


    public boolean isAdsOffer() {
        return true;
    }

    protected boolean isBannerAdsOffer() {
        return ((GlobalSongList) getApplication()).getPlayMeeConfig().getAdsConfig().isShowBanner();
    }

    protected boolean isInterstitialAdsOffer() {
        return ((GlobalSongList) getApplication()).getPlayMeeConfig().getAdsConfig().isShowFullScreenAds();
    }

    protected boolean isAdsConfigOffer() {
        return isBannerAdsOffer() && isInterstitialAdsOffer();
    }

    public abstract int GetLayoutResID();

    public abstract void OnBackPressed();

    protected abstract String GetGAScreenName();
}
