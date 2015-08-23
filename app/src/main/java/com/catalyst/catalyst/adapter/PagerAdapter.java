package com.catalyst.catalyst.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.catalyst.catalyst.fragment.DemoPagerFragment;

/**
 * A simple pager adapter that represents pages for the demo.
 *
 * Created by Nick Piscopio on 5/8/15.
 */
public class PagerAdapter extends FragmentStatePagerAdapter
{
    public static final int NUM_PAGES = 3;

    public PagerAdapter(FragmentManager fm)
    {
        super(fm);
    }

    @Override public Fragment getItem(int position)
    {
        Bundle bundle = new Bundle();
        bundle.putInt(DemoPagerFragment.DEMO_PAGE, position);

        DemoPagerFragment demoSliderFragment = new DemoPagerFragment();
        demoSliderFragment.setArguments(bundle);

        return demoSliderFragment;
    }

    @Override public int getCount()
    {
        return NUM_PAGES;
    }
}