package champak.champabun.adapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.PagerTabStrip;
import android.util.AttributeSet;

import champak.champabun.R;

public class PagerTabStripCustom extends PagerTabStrip {
    int pagerID;

    public PagerTabStripCustom(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
        TypedArray types = arg0.obtainStyledAttributes(arg1, R.styleable.PagerTitleStripCustom, 0, 0);
        pagerID = types.getResourceId(R.styleable.PagerTitleStripCustom_parentViewPager, 0);

        types.recycle();
    }

    // @Override
    // public void onAttachedToWindow()
    // {
    // final ViewParent parent = getParent();
    //
    // if (!(parent instanceof ViewPagerParallax))
    // {
    // View theViewPager = ((View) parent).findViewById(R.id.viewpager);
    // final ViewPagerParallax pager = (ViewPagerParallax) theViewPager;
    // }
    // }
}
