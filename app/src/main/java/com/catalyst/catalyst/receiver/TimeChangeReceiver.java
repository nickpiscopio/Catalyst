package com.catalyst.catalyst.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.catalyst.catalyst.alarm.CatalystAlarm;

/**
 * Listens for time being changed on the device.
 *
 * Created by Nick Piscopio on 6/13/15.
 */
public class TimeChangeReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        String action = intent.getAction();

        if (action.equals(Intent.ACTION_TIME_CHANGED) ||
            action.equals(Intent.ACTION_TIMEZONE_CHANGED))
        {
            CatalystAlarm.getInstance(context);
        }
    }
}