package com.catalyst.catalyst.activity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.catalyst.catalyst.R;
import com.catalyst.catalyst.fragment.SettingsFragment;

/**
 * Settings screen for Catalyst.
 *
 * Created by Nick Piscopio on 5/8/15.
 */
public class SettingsActivity extends AppCompatActivity
{
    @Override protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setBackgroundDrawable(new ColorDrawable(getApplicationContext().getResources().getColor(R.color.green)));
        }

        getFragmentManager().beginTransaction().replace(android.R.id.content,
                                                        new SettingsFragment()).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
