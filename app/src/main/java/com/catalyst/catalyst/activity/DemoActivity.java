package com.catalyst.catalyst.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.catalyst.catalyst.R;
import com.catalyst.catalyst.adapter.PagerAdapter;
import com.catalyst.catalyst.util.ColorUtil;
import com.catalyst.catalyst.util.Constant;

/**
 * Screens to demo the application before the user commits to Catalyst.
 *
 * Created by Nick Piscopio on 5/8/15.
 */
public class DemoActivity extends AppCompatActivity
{
    private static final int PAGER_SELECTED_RESOURCE = R.mipmap.pager_selected;
    private static final int PAGER_UNSELECTED_RESOURCE = R.mipmap.pager_unselected;

    private ViewPager mPager;

    private PagerAdapter mPagerAdapter;

    private ImageView pager0;
    private ImageView pager1;
    private ImageView pager2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null)
        {
            actionBar.setBackgroundDrawable(
                    new ColorDrawable(getResources().getColor(R.color.light_blue)));
        }

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager)findViewById(R.id.pager);
        mPagerAdapter = new PagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        mPager.setOnPageChangeListener(pageChangeListener);

        pager0 = (ImageView)findViewById(R.id.pager_0);
        pager1 = (ImageView)findViewById(R.id.pager_1);
        pager2 = (ImageView)findViewById(R.id.pager_2);
    }

    @Override
    public void onBackPressed()
    {
        if (mPager.getCurrentItem() == 0)
        {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        }
        else
        {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_demo, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_skip)
        {
            SharedPreferences prefs = getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(Constant.DEMO_FINISHED, true);
            editor.putInt(Constant.INSPIRATION_COLOR, ColorUtil.getRandomNumber(Constant.INSPIRATION_COLOR_MIN, Constant.INSPIRATION_COLOR_MAX));
            editor.apply();

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(MainActivity.NEW_INSPIRATION, true);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener()
    {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

        @Override
        public void onPageSelected(int position)
        {
            switch (position)
            {
                case 0:
                    pager0.setImageResource(PAGER_SELECTED_RESOURCE);
                    pager1.setImageResource(PAGER_UNSELECTED_RESOURCE);
                    pager2.setImageResource(PAGER_UNSELECTED_RESOURCE);
                    break;
                case 1:
                    pager0.setImageResource(PAGER_UNSELECTED_RESOURCE);
                    pager1.setImageResource(PAGER_SELECTED_RESOURCE);
                    pager2.setImageResource(PAGER_UNSELECTED_RESOURCE);
                    break;
                case 2:
                    pager0.setImageResource(PAGER_UNSELECTED_RESOURCE);
                    pager1.setImageResource(PAGER_UNSELECTED_RESOURCE);
                    pager2.setImageResource(PAGER_SELECTED_RESOURCE);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) { }
    };
}