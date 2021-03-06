package champak.champabun.view.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

import champak.champabun.view.fragment.BaseFragment;

public class Adapter_ViewPager extends FragmentStatePagerAdapter {
    private List<BaseFragment> fragments;
    private String[] titles;

    public Adapter_ViewPager(FragmentManager fm, String[] titles, List<BaseFragment> fragments) {
        super(fm);
        this.titles = titles;
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return this.fragments.get(position);
    }

    @Override
    public int getCount() {
        if (titles == null) {
            return 0;
        } else {
            return titles.length;
        }
    }

    @Override
    public String getPageTitle(int position) {
        return titles[position];
    }

    public void Update() {
        for (BaseFragment f : fragments) {
            if (f != null) {
                f.Update();
            }
        }
    }
}
