package com.catalyst.catalyst;

/**
 * Created by nickpiscopio on 5/8/15.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
 * sequence.
 */
public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter
{
    /**
     * The number of pages (wizard steps) to show in this demo.
     */
    private static final int NUM_PAGES = 3;

    public ScreenSlidePagerAdapter(FragmentManager fm)
    {
        super(fm);
    }

    @Override
    public Fragment getItem(int position)
    {
        DemoSliderFragment demoSliderFragment = new DemoSliderFragment();

        Bundle bundle = new Bundle();
        bundle.putInt(DemoSliderFragment.DEMO_PAGE, position);

        demoSliderFragment.setArguments(bundle);

        return demoSliderFragment;
    }

    @Override
    public int getCount()
    {
        return NUM_PAGES;
    }
}
