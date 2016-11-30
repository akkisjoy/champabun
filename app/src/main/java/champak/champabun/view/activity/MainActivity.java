package champak.champabun.view.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.gigamole.navigationtabbar.ntb.NavigationTabBar;

import java.util.ArrayList;
import java.util.List;

import champak.champabun.AmuzicgApp;
import champak.champabun.R;
import champak.champabun.business.dataclasses.AppDatabase;
import champak.champabun.business.definition.IConstant;
import champak.champabun.business.definition.Logger;
import champak.champabun.business.utilities.utilMethod.ActivityUtil;
import champak.champabun.view.adapters.Adapter_ViewPager;
import champak.champabun.view.fragment.BaseFragment;
import champak.champabun.view.fragment.F_Albums;
import champak.champabun.view.fragment.F_Artists;
import champak.champabun.view.fragment.F_Folder;
import champak.champabun.view.fragment.F_Genres;
import champak.champabun.view.fragment.F_Playlists;
import champak.champabun.view.fragment.F_Songs;

public class MainActivity extends BaseActivity {
    protected static MainActivity mInstance;
    private List<BaseFragment> fragments;
    private Adapter_ViewPager adapter;
    private ViewPager pager;
    private View openactivity;
    private TextView songs;
    private TextView album2;
    private ImageView play2;
    private ImageView settings;
    private int whichanimation = 0;
    private Animation fadeOut, fadeIn;
    private String oldalbum, oldsong;
    public BroadcastReceiver broadcastCoverReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent serviceIntent) {
            // check that whether the audio file can play or not
            if (!serviceIntent.getBooleanExtra("setDataSourceFailed", false)) {
                // ok, the audio file can play
                inandout();
            }
        }
    };
    private BroadcastReceiver checkagain = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent serviceIntent) {
            checkbuttonplaypause();
        }
    };

    public static AppCompatActivity getMainActivity() {
        return mInstance;
    }

    @Override
    public int GetLayoutResID() {
        return R.layout.adapter_fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInstance = this;

        fragments = new ArrayList<>();
        fragments.add(new F_Genres());
        fragments.add(new F_Artists());
        fragments.add(new F_Songs());
        fragments.add(new F_Albums());
        fragments.add(new F_Playlists());
        fragments.add(new F_Folder());

        String[] titles = getResources().getStringArray(R.array.tabs);
        adapter = new Adapter_ViewPager(getSupportFragmentManager(), titles, fragments);
        pager = (ViewPager) findViewById(R.id.viewpager);

//        pager.setPageTransformer(true, new ZoomOutPageTransformer());

        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(3);
        pager.setCurrentItem(2);

        final String[] colors = getResources().getStringArray(R.array.vertical_ntb);

        final NavigationTabBar navigationTabBar = (NavigationTabBar) findViewById(R.id.ntb_vertical);
        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
        models.add(
                new NavigationTabBar.Model.Builder(
                        ResourcesCompat.getDrawable(getResources(), R.drawable.ic_genre, null),
                        Color.parseColor(colors[0]))
                        .selectedIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_genre, null))
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        ResourcesCompat.getDrawable(getResources(), R.drawable.ic_artists, null),
                        Color.parseColor(colors[1]))
                        .selectedIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_artists, null))
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        ResourcesCompat.getDrawable(getResources(), R.drawable.ic_songs, null),
                        Color.parseColor(colors[2]))
                        .selectedIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_songs, null))
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        ResourcesCompat.getDrawable(getResources(), R.drawable.ic_albums, null),
                        Color.parseColor(colors[3]))
                        .selectedIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_albums, null))
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        ResourcesCompat.getDrawable(getResources(), R.drawable.ic_playlists, null),
                        Color.parseColor(colors[4]))
                        .selectedIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_playlists, null))
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        ResourcesCompat.getDrawable(getResources(), R.drawable.ic_folder, null),
                        Color.parseColor(colors[5]))
                        .selectedIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_folder, null))
                        .build()
        );

        navigationTabBar.setModels(models);
        navigationTabBar.setViewPager(pager, 2);

        // clear sleep timer
        AppDatabase.CancelSleepTimer(MainActivity.this);

        settings = (ImageView) findViewById(R.id.settings);
        songs = (TextView) findViewById(R.id.songstop);
        album2 = (TextView) findViewById(R.id.albumtop);
        openactivity = findViewById(R.id.openactivity);
        play2 = (ImageView) findViewById(R.id.play2);


        openactivity.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (AmuzicgApp.GetInstance().GetNowPlayingSize() > 0) {
                    Intent intent = new Intent(MainActivity.this, Player.class);
                    intent.putExtra("shouldStartCoverOperation", true);
                    startActivity(intent);
                }
            }
        });

        play2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                buttonPlayStopClick();
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Settings.class);
                startActivityForResult(intent, IConstant.REQUEST_CODE_CHANGE_SETTINGS);
            }
        });

        registerReceiver(checkagain, new IntentFilter(IConstant.BROADCAST_CHECK_AGAIN));
        registerReceiver(broadcastCoverReceiver, new IntentFilter(IConstant.BROADCAST_COVER));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AmuzicgApp.GetInstance().GetNowPlayingSize() > 0) {
            play2.setVisibility(View.VISIBLE);
            checkbuttonplaypause();
        } else {
            songs.setText("No song in player queue");
            play2.setVisibility(View.GONE);
        }
    }

    private void checkbuttonplaypause() {
        if (AmuzicgApp.GetInstance().boolMusicPlaying1) {
            play2.setImageResource(R.drawable.pause);
        } else {
            play2.setImageResource(R.drawable.play2);
        }
        inandout();
    }

    protected void fadeout(int offset, int duration) {
        fadeOut = null;
        fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setFillAfter(true);
        fadeOut.setStartOffset(offset);
        fadeOut.setDuration(duration);
    }

    protected void fadein(int offset, int duration) {
        fadeIn = null;
        fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new AccelerateInterpolator()); // and this
        fadeIn.setFillAfter(true);
        fadeIn.setStartOffset(offset);
        fadeIn.setDuration(duration);
    }

    protected void inandout() {
        fadein(0, 500);
        fadeout(0, 500);

        if (AmuzicgApp.GetInstance().GetNowPlayingSize() != 0) {
            if (!AmuzicgApp.GetInstance().GetCurSongDetails().getSong().equals(oldsong)
                    || !AmuzicgApp.GetInstance().GetCurSongDetails().getAlbum().equals(oldalbum)) {
                if (!AmuzicgApp.GetInstance().GetCurSongDetails().getSong().equals(oldsong)
                        && AmuzicgApp.GetInstance().GetCurSongDetails().getAlbum().equals(oldalbum)) {
                    songs.startAnimation(fadeOut);
                    whichanimation = 1;
                } else if (AmuzicgApp.GetInstance().GetCurSongDetails().getSong().equals(oldsong)
                        && !AmuzicgApp.GetInstance().GetCurSongDetails().getAlbum().equals(oldalbum)) {
                    album2.startAnimation(fadeOut);
                    whichanimation = 2;
                } else if (!AmuzicgApp.GetInstance().GetCurSongDetails().getSong().equals(oldsong)
                        && !AmuzicgApp.GetInstance().GetCurSongDetails().getAlbum().equals(oldalbum)) {
                    songs.startAnimation(fadeOut);
                    album2.startAnimation(fadeOut);
                    whichanimation = 3;
                }
            }
            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    if (AmuzicgApp.GetInstance().GetNowPlayingSize() > 0) {
                        if (whichanimation == 1) {
                            songs.setText(AmuzicgApp.GetInstance().GetCurSongDetails().getSong());
                            songs.startAnimation(fadeIn);
                        } else if (whichanimation == 2) {
                            album2.setText(AmuzicgApp.GetInstance().GetCurSongDetails().getAlbum());
                            album2.startAnimation(fadeIn);
                        } else if (whichanimation == 3) {
                            album2.setText(AmuzicgApp.GetInstance().GetCurSongDetails().getAlbum());
                            album2.startAnimation(fadeIn);
                            songs.setText(AmuzicgApp.GetInstance().GetCurSongDetails().getSong());
                            songs.startAnimation(fadeIn);
                        }
                    }
                    oldsong = songs.getText().toString();
                    oldalbum = album2.getText().toString();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationStart(Animation animation) {
                }
            });
        }
    }

    private void buttonPlayStopClick() {
        if (!AmuzicgApp.GetInstance().boolMusicPlaying1) {
            Intent intentplaypause = new Intent(IConstant.BROADCAST_PLAYPAUSE);
            play2.setImageResource(R.drawable.pause);
            intentplaypause.putExtra("playpause", 1);
            sendBroadcast(intentplaypause);
            AmuzicgApp.GetInstance().boolMusicPlaying1 = true;
        } else {
            if (AmuzicgApp.GetInstance().boolMusicPlaying1) {
                play2.setImageResource(R.drawable.play2);
                AmuzicgApp.GetInstance().boolMusicPlaying1 = false;
                Intent intentplaypause = new Intent(IConstant.BROADCAST_PLAYPAUSE);
                intentplaypause.putExtra("playpause", 0);
                sendBroadcast(intentplaypause);
                AmuzicgApp.GetInstance().boolMusicPlaying1 = false;
            }
        }
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
        moveTaskToBack(true);
    }

    @Override
    public String GetActivityID() {
        return "MainActivity";
    }

    @Override
    public void SetNullForCustomVariable() {
    }

    @Override
    public void onDestroy() {
        try {
            unregisterReceiver(checkagain);
        } catch (Exception ignored) {
        }
        try {
            unregisterReceiver(broadcastCoverReceiver);
        } catch (Exception ignored) {
        }
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
        Logger.d("MainActivity", "onActivityResult requestcode = " + requestcode + " resultCode = " + resultCode);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestcode) {
                case IConstant.REQUEST_CODE_SEND_AUDIO: {
                    ActivityUtil.showCrouton(this, getString(R.string.song_has_been_sent));
                    break;
                }

                case IConstant.REQUEST_CODE_CHANGE_SETTINGS: {
                    Bundle extras = intent.getExtras();
                    if (extras != null) {
                        boolean needRefreshFullscreen = extras.getBoolean("needRefreshFullscreen", false);
                        Logger.d("MainActivity", "needRefreshFullscreen = " + needRefreshFullscreen);
                        if (needRefreshFullscreen) {
                            Intent intent2 = new Intent(MainActivity.this, MainActivity.class);
                            intent2.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            MainActivity.this.startActivity(intent2);
                            overridePendingTransition(0, 0);
                        }
                    }
                    boolean isDurationFilterChanged = false;
                    try {
                        isDurationFilterChanged = intent.getBooleanExtra("isDurationFilterChanged", false);
                    } catch (NullPointerException ignored) {
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
}