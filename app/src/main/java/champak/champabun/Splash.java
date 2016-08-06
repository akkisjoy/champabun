package champak.champabun;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import champak.champabun.adapters.Activity_Fragments;
import champak.champabun.ui.TypefaceTextView;

public class Splash extends BaseActivity {
    String isloaded;
    final private static long TIME_DELAY = 3 * 1000;
    private TypefaceTextView free, version;


    @Override
    public boolean isAdsOffer() {
        return false;
    }

    @Override
    public String GetActivityID() {
        return "Splash";
    }

    @Override
    public void OnBackPressed() {
        finish();
    }

    @Override
    protected String GetGAScreenName() {
        return "Splash";
    }

    @Override
    public int GetLayoutResID() {
        return R.layout.splash;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (!IConstant.IS_PRO_VERSION) {
//			Appodeal.disableLocationPermissionCheck( );
            String appKey = getResources().getString(R.string.appodeal_ApiKey);
//			// "3c5fde18ad307860236252941c0e7d6e0a9dcc6bb1d44971";
//			// Appodeal.setBannerViewId( R.id.appodealBannerView );
//			// Appodeal.disableNetwork(this, "yandex");
//			Appodeal.disableNetwork( this, "unity_ads" );
//			Appodeal.initialize( Splash.this, appKey, Appodeal.BANNER );
//			Appodeal.initialize( Splash.this, appKey, Appodeal.INTERSTITIAL );
        }

		/*
         * free = (TypefaceTextView) findViewById(R.id.free_version); if (IConstant.IS_PRO_VERSION) { free.setText(R.string.pro_version); } else {
		 * free.setText(R.string.free_version); }
		 */
        version = (TypefaceTextView) findViewById(R.id.version);
        // version.setText(String.format(getString(R.string.version),
        // ActivityUtil.GetVersionName(getApplicationContext())));
        // version.setText("www.IamAnkitSrivastava.com");
    }

    @Override
    protected void onStart() {
//		GlobalSongList.splashscreenopen=1;
//		GlobalSongList.isShowInterstitialAds=false;
        super.onStart();
        if (!(GlobalSongList.secondtimeplayershown == 0))
            GlobalSongList.secondtimeplayershown = 0;

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                startActivity(new Intent(Splash.this, Activity_Fragments.class));
                finish();
            }
        }, TIME_DELAY);
    }

}
