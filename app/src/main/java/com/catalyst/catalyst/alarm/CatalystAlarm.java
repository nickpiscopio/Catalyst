package com.catalyst.catalyst.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Singleton class for the alarm in Catalyst.
 *
 * Created by Nick Piscopio on 5/31/15.
 */
public class CatalystAlarm
{
    private static CatalystAlarm catalystAlarm;

    private Context context;

    private AlarmManager alarmManager;

    public static CatalystAlarm getInstance(Context context)
    {
        if (catalystAlarm == null)
        {
            catalystAlarm = new CatalystAlarm(context);
        }

        return catalystAlarm;
    }

    private CatalystAlarm(Context context)
    {
        this.context = context;

        alarmManager = (AlarmManager) this.context.getSystemService(Context.ALARM_SERVICE);

        setAlarm();
    }

    /**
     * Sets the alarm in the application.
     */
    private void setAlarm()
    {
        Intent intent = new Intent(context, CatalystNotification.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
                                                                 intent, PendingIntent.FLAG_CANCEL_CURRENT);
        long currentTime = System.currentTimeMillis() + (20 * 1000);
        alarmManager.set(AlarmManager.RTC_WAKEUP, currentTime, pendingIntent);
        Log.i("Alarm", "Alarm set for: " + currentTime);
    }
}