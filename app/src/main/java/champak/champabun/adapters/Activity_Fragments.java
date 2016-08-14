package champak.champabun.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;

import com.gigamole.navigationtabbar.ntb.NavigationTabBar;

import java.util.ArrayList;
import java.util.List;

import champak.champabun.BaseActivity;
import champak.champabun.BaseFragment;
import champak.champabun.F_Albums;
import champak.champabun.F_Artists;
import champak.champabun.F_Folder;
import champak.champabun.F_Genres;
import champak.champabun.F_Playlists;
import champak.champabun.F_Songs;
import champak.champabun.IConstant;
import champak.champabun.Logger;
import champak.champabun.R;
import champak.champabun.classes.AppDatabase;
import champak.champabun.util.ActivityUtil;

public class Activity_Fragments extends BaseActivity {
    private List<BaseFragment> fragments;
    private Adapter_ViewPager adapter;
    private ViewPager pager;

    @Override
    public int GetLayoutResID() {
        return R.layout.adapter_fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        pager.setPageTransformer(true, new ZoomOutPageTransformer());

        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(3);
        pager.setCurrentItem(2);

        final String[] colors = getResources().getStringArray(R.array.vertical_ntb);

        final NavigationTabBar navigationTabBar = (NavigationTabBar) findViewById(R.id.ntb_vertical);
        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_first),
                        Color.parseColor(colors[0]))
                        .selectedIcon(getResources().getDrawable(R.drawable.ic_eighth))
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_second),
                        Color.parseColor(colors[1]))
                        .selectedIcon(getResources().getDrawable(R.drawable.ic_eighth))
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_third),
                        Color.parseColor(colors[2]))
                        .selectedIcon(getResources().getDrawable(R.drawable.ic_eighth))
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        ResourcesCompat.getDrawable(getResources(), R.drawable.ic_fourth, null),
                        Color.parseColor(colors[3]))
                        .selectedIcon(getResources().getDrawable(R.drawable.ic_eighth))
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_fifth),
                        Color.parseColor(colors[4]))
                        .selectedIcon(getResources().getDrawable(R.drawable.ic_eighth))
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_sixth),
                        Color.parseColor(colors[5]))
                        .selectedIcon(getResources().getDrawable(R.drawable.ic_eighth))
                        .build()
        );

        navigationTabBar.setModels(models);
        navigationTabBar.setViewPager(pager, 2);

        // clear sleep timer
        AppDatabase.CancelSleepTimer(Activity_Fragments.this);
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
        return "Activity_Fragments";
    }

    @Override
    public void SetNullForCustomVariable() {
    }

    @Override
    public void onDestroy() {
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
                    Bundle extras = intent.getExtras();
                    if (extras != null) {
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
}