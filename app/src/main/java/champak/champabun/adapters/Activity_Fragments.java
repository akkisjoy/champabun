package champak.champabun.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import champak.champabun.BaseActivity;
import champak.champabun.BaseFragment;
import champak.champabun.F_Albums;
import champak.champabun.F_Artists;
import champak.champabun.F_Folder;
import champak.champabun.F_Genres;
import champak.champabun.F_Playlists;
import champak.champabun.F_Songs;
import champak.champabun.GlobalSongList;
import champak.champabun.IConstant;
import champak.champabun.Logger;
import champak.champabun.R;
import champak.champabun.classes.AdsConfig;
import champak.champabun.classes.AppDatabase;
import champak.champabun.classes.PagerClickTitleStrip;
import champak.champabun.util.ActivityUtil;

public class Activity_Fragments extends BaseActivity {
    private List<BaseFragment> fragments;
    private Adapter_ViewPager adapter;
    private ViewPagerParallax pager;

    //private boolean isShowInterstitialAds;

    @Override
    public int GetLayoutResID() {
        return R.layout.adapter_fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //overridePendingTransition( R.anim.slide_in_left, R.anim.slide_out_left );
//		if ( savedInstanceState != null )
//		{
//			GlobalSongList.isShowInterstitialAds = savedInstanceState.getBoolean( "isShowInterstitialAds", false );
//		}

        fragments = new ArrayList<BaseFragment>();
        // fragments.add(Fragment.instantiate(this, F_Settings.class.getName()));
        fragments.add(new F_Genres());
        fragments.add(new F_Playlists());
        fragments.add(new F_Songs());
        fragments.add(new F_Artists());
        fragments.add(new F_Albums());
        fragments.add(new F_Folder());

        // registerReceiver(broadcastCoverReceiverpara, new IntentFilter(
        // Music_service.BROADCAST_COVERPARALLAX));
        String[] titles = getResources().getStringArray(R.array.tabs);
        adapter = new Adapter_ViewPager(getSupportFragmentManager(), titles, fragments);
        pager = (ViewPagerParallax) findViewById(R.id.viewpager);

        PagerClickTitleStrip _Title = (PagerClickTitleStrip) findViewById(R.id.__Weekly_pager_title_strip);
        Typeface font = Typeface.createFromAsset(this.getAssets(), "fonts/julius-sans-one.ttf");
        for (int counter = 0; counter < _Title.getChildCount(); counter++) {
            if (_Title.getChildAt(counter) instanceof TextView) {
                ((TextView) _Title.getChildAt(counter)).setTypeface(font);
            }
        }

        font = null;
        Random mRandom = new Random();
        int drawable_start = R.drawable.a1;
        int drawable_end = R.drawable.a9;
        int x = mRandom.nextInt(drawable_end - drawable_start + 1);
        pager.setBackgroundAsset(drawable_start + x);
        //pager.setBackgroundAsset(R.drawable.a14);
        //pager.setBackgroundAsset( appSettings.getAppBGResID( ) );
        pager.setPageTransformer(true, new ZoomOutPageTransformer());

        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(3);
        pager.setCurrentItem(2);

        // clear sleep timer
        AppDatabase.CancelSleepTimer(Activity_Fragments.this);
    }

    // private BroadcastReceiver broadcastCoverReceiverpara = new BroadcastReceiver() {
    // @Override
    // public void onReceive(Context context, Intent serviceIntent) {
    // pager.setBackgroundAsset(serviceIntent.getStringExtra("coverpara"));

    // }
    // };

    // public ViewPagerParallax hi()
    // {
    // return pager;
    // }

    @Override
    public void onResume() {
        /*
		 * int arr[]={R.raw.a1,R.raw.a2,R.raw.a3,R.raw.a4,R.raw.a5,R.raw.a6,R.raw.a7,R.raw.a8,
		 * R.raw.a9,R.raw.a10,R.raw.a11,R.raw.a12,R.raw.a13,R.raw.a14,R.raw.a15,R.raw.a16,
		 * R.raw.a17,R.raw.a18,R.raw.a19,R.raw.a20,R.raw.a21,R.raw.a22,R.raw.a23,
		 * R.raw.a24,R.raw.a25,R.raw.a26,R.raw.a27,R.raw.a28,R.raw.a29,R.raw.a30,
		 * R.raw.a31,R.raw.a32,R.raw.a33,R.raw.a34,R.raw.a35,R.raw.a36,R.raw.a37, R.raw.a38,R.raw.a39,R.raw.a40,R.raw.a41 };
		 */
        // int x=(int)(Math.random() * ((40) + 1));

        // pager.setCurrentItem(2);
        // pager.setBackgroundAsset(arr[x]);
        super.onResume();
//		if ( !IConstant.IS_PRO_VERSION && GlobalSongList.splashscreenopen==1)
//		{   GlobalSongList.splashscreenopen=0;
//		if ( !GlobalSongList.isShowInterstitialAds )
//		{
////			Appodeal.show( Activity_Fragments.this, Appodeal.INTERSTITIAL );
//			
//		}

        AdsConfig cfg = ((GlobalSongList) getApplication()).getPlayMeeConfig().getAdsConfig();
    }
//			if ( cfg.isShowFullScreenAdsAfterSplash( ) )
//			{
    // android.util.Log.d( "BaseActivity", "isShowFullScreenAdsAfterSplash.............." );
//				Appodeal.setInterstitialCallbacks( new InterstitialCallbacks( ) {
//
//					@Override
//					public void onInterstitialShown( )
//					{
//						GlobalSongList.isShowInterstitialAds = true;
//						
//						// android.util.Log.d( "BaseActivity", "onInterstitialShown.............." );
//					}
//
//					@Override
//					public void onInterstitialLoaded( boolean arg0 )
//					{
//						// android.util.Log.d( "BaseActivity", "onInterstitialLoaded.............." );
//						if ( !GlobalSongList.isShowInterstitialAds )
//						{
//							Appodeal.show( Activity_Fragments.this, Appodeal.INTERSTITIAL );
//							//GlobalSongList.isShowInterstitialAds = true;
//							
//						}
//					}
//
//					@Override
//					public void onInterstitialFailedToLoad( )
//					{
//						// android.util.Log.d( "BaseActivity", "onInterstitialFailedToLoad.............." );
//					}
//
//					@Override
//					public void onInterstitialClosed( )
//					{
//						//Appodeal.hide( Activity_Fragments.this, Appodeal.INTERSTITIAL );
//						// android.util.Log.d( "BaseActivity", "onInterstitialClosed.............." );
//					}
//
//					@Override
//					public void onInterstitialClicked( )
//					{
//					}
//					
//				} );
//				
//			}
//		}
//


    @Override
    public void finish() {
        super.finish();
        //overridePendingTransition( R.anim.slide_in_right, R.anim.slide_out_right );
    }

    @Override
    public void onPause() {
        // int x=(int)(Math.random() * ((28) + 1));
        // pager.setBackgroundAsset(arr[x]);
        //isShowInterstitialAds=false;
        super.onPause();
    }

    @Override
    public void OnBackPressed() {
        if (fragments.get(pager.getCurrentItem()) instanceof F_Folder) {
            F_Folder fragment = (F_Folder) fragments.get(pager.getCurrentItem());
            if (!fragment.canGoBack()) {
                pager.setCurrentItem(2);
            }
            return;
        }
        // super.onBackPressed();
        //GlobalSongList.isShowInterstitialAds=false;
        moveTaskToBack(true);
    }

    @Override
    public String GetActivityID() {
        return "Activity_Fragments";
    }

    @Override
    public void SetNullForCustomVariable() {
    }

    @Override
    public void onDestroy() {
        // Logger.d("Activity_Fragments", "................................onDestroy");
        ActivityUtil.CloseAllOpenActivities();
        if (IConstant.USE_SYSTEM_GC) {
            System.gc();
        }
        super.onDestroy();
    }

    @Override
    protected String GetGAScreenName() {
        return null;
    }

    @Override
    protected void onActivityResult(int requestcode, int resultCode, Intent intent) {
        super.onActivityResult(requestcode, resultCode, intent);
        Logger.d("Activity_Fragments", "onActivityResult requestcode = " + requestcode + " resultCode = " + resultCode);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestcode) {
                case IConstant.REQUEST_CODE_SEND_AUDIO: {
                    ActivityUtil.showCrouton(this, getString(R.string.song_has_been_sent));
                    break;
                }


                case IConstant.REQUEST_CODE_CHANGE_SETTINGS: {
                    boolean isAppBGChanged = false;
                    // Logger.d( "Activity_Fragments", "IConstant.REQUEST_CODE_CHANGE_SETTINGS" );
                    Bundle extras = intent.getExtras();
                    if (extras != null) {
                        isAppBGChanged = extras.getBoolean("isAppBGChanged", false);
                        boolean needRefreshFullscreen = extras.getBoolean("needRefreshFullscreen", false);
                        Logger.d("Activity_Fragments", "needRefreshFullscreen = " + needRefreshFullscreen);
                        if (needRefreshFullscreen) {
                            Intent intent2 = new Intent(Activity_Fragments.this, Activity_Fragments.class);
                            intent2.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            Activity_Fragments.this.startActivity(intent2);
                            overridePendingTransition(0, 0);
                        }
                    }
                    if (isAppBGChanged) {
                        int bgID = appSettings.getAppBGResID();
                        pager.setBackgroundAsset(bgID);
                    }
                    boolean isDurationFilterChanged = false;
                    try {
                        isDurationFilterChanged = intent.getBooleanExtra("isDurationFilterChanged", false);
                    } catch (NullPointerException e) {
                    }
                    if (isDurationFilterChanged) {
                        if (adapter != null) {
                            adapter.Update();
                        }
                    }
                    break;
                }
                default:
                    break;
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            if (requestcode == IConstant.REQUEST_CODE_SEND_AUDIO) {
                ActivityUtil.showCrouton(this, getString(R.string.sending_cancel));
            }
        }
    }

//	@Override
//	protected void onSaveInstanceState( Bundle outState )
//	{
//		outState.putBoolean( "isShowInterstitialAds", GlobalSongList.isShowInterstitialAds );
//		super.onSaveInstanceState( outState );
//	}

    // @Override
    // public boolean onCreateOptionsMenu(Menu menu)
    // {
    // MenuInflater inflater = getMenuInflater();
    // inflater.inflate(R.menu.main, menu);
    //
    // return super.onCreateOptionsMenu(menu);
    // }
    //
    // @Override
    // public boolean onOptionsItemSelected(MenuItem menuItem)
    // {
    // switch (menuItem.getItemId())
    // {
    // // case R.id.forget_password:
    // // {
    // // return true;
    // // }
    // // case R.id.signup:
    // // {
    // // return true;
    // // }
    // default:
    // return super.onOptionsItemSelected(menuItem);
    // }
    // }
}
