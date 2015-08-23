package com.catalyst.catalyst.activity;

import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.catalyst.catalyst.R;

/**
 * Catalyst about screen.
 *
 * Created by Nick Piscopio on 5/8/15.
 */
public class AboutActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null)
        {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setBackgroundDrawable(new ColorDrawable(getApplicationContext().getResources().getColor(R.color.light_blue)));
        }

        try
        {
            String version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;

            TextView textVersion = (TextView)findViewById(R.id.text_version);
            textVersion.setText(version);
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }
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
