package com.catalyst.catalyst.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import com.catalyst.catalyst.R;
import com.catalyst.catalyst.util.Constant;
import com.catalyst.catalyst.util.DateUtil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

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

        catalystAlarm.setAlarm();

        return catalystAlarm;
    }

    private CatalystAlarm(Context context)
    {
        this.context = context;

        alarmManager = (AlarmManager) this.context.getSystemService(Context.ALARM_SERVICE);
    }

    /**
     * Sets the alarm in the application.
     */
    private void setAlarm()
    {
        Intent intent = new Intent(context, CatalystNotification.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
                                                                 intent,
                                                                 PendingIntent.FLAG_CANCEL_CURRENT);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        Resources res = context.getResources();

        //If the user has the notification preference on.
        if (prefs.getBoolean(res.getString(R.string.preference_notification),
                             Boolean.valueOf(res.getString(R.string.default_notification_flag))))
        {
            Set<String> interval = prefs.getStringSet(res.getString(R.string.preference_interval),
                                                      new HashSet<>(Arrays.asList(res.getStringArray(R.array.interval))));

            DateUtil dateUtil = new DateUtil(context);

            long defaultUTCTime = dateUtil.getDefaultTime();

            //This time is set from today in milliseconds and UTC.
            long storedTime = prefs.getLong(res.getString(R.string.preference_time), defaultUTCTime);
            long convertedTime = dateUtil.convertTime(storedTime, TimeZone.getDefault(),
                                                      TimeZone.getTimeZone(Constant.TIMEZONE_UTC));

            if (storedTime == defaultUTCTime)
            {
                dateUtil.storeDefaultTime(defaultUTCTime);
            }

            long alarmTime = dateUtil.getNextAlarm(interval, convertedTime);

            alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
        }
    }
}