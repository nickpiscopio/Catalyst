package com.catalyst.catalyst.alarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;

import com.catalyst.catalyst.R;
import com.catalyst.catalyst.activity.MainActivity;
import com.catalyst.catalyst.util.Constant;

/**
 * Notification manager for Catalyst.
 *
 * Created by Nick Piscopio on 5/31/15.
 */
public class CatalystNotification extends BroadcastReceiver
{
    public static final int NOTIFICATION_ID = 1;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        SharedPreferences prefs = context.getSharedPreferences(Constant.SHARED_PREFERENCES,
                                                               Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(Constant.NEW_INSPIRATION, true);
        editor.apply();

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Intent mainActivityIntent = new Intent(context, MainActivity.class);
        mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pIntent = PendingIntent.getActivity(context, NOTIFICATION_ID, mainActivityIntent,
                                                          PendingIntent.FLAG_ONE_SHOT);

        Notification notification = new Notification.Builder(context)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText("New inspiration!")
                .setSmallIcon(R.mipmap.notification_icon)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_logo))
                .setContentIntent(pIntent).build();

        notification.flags =  Notification.FLAG_AUTO_CANCEL;
        notification.defaults = Notification.DEFAULT_ALL;

        notificationManager.notify(NOTIFICATION_ID, notification);

        // Creates the alarm to start sending notifications to the user.
        CatalystAlarm.getInstance(context);
    }
}