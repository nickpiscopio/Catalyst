package com.catalyst.catalyst.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import com.catalyst.catalyst.util.Constant;

/**
 * Listens for the app to be upgraded.
 *
 * Created by Nick Piscopio on 8/30/15.
 */
public class UpgradeReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Uri packageName = intent.getData();

        if(packageName.toString().equals("package:" + context.getPackageName()))
        {
            SharedPreferences prefs = context.getSharedPreferences(Constant.SHARED_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();
        }
    }
}