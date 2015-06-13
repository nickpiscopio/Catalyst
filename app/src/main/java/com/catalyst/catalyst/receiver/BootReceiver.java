package com.catalyst.catalyst.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.catalyst.catalyst.alarm.CatalystAlarm;

/**
 * Starts the app when the device boots up.
 *
 * Created by Nick Piscopio on 6/13/15.
 */
public class BootReceiver extends BroadcastReceiver
{
    private static final String BOOT_COMPLETE = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent.getAction().equals(BOOT_COMPLETE))
        {
            CatalystAlarm.getInstance(context);
        }
    }
}