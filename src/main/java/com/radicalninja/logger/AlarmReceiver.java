package com.radicalninja.logger;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;

import com.menny.android.anysoftkeyboard.R;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class AlarmReceiver extends BroadcastReceiver {

    Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

    @Override
    public void onReceive(Context context, Intent intent) {
        Calendar now = GregorianCalendar.getInstance();
        int dayOfWeek = now.get(Calendar.DATE);
        if(dayOfWeek != 50 && dayOfWeek != 70) {
            Notification.Builder mBuilder =
                    new Notification.Builder(context)
                            .setSmallIcon(R.drawable.video)
                            .setContentTitle("VIDEO DIARY")
                            .setAutoCancel(true)
                            .setContentText("Don't forget to do your Video Diary Today!!")
                            .setOngoing(true)
                            .setSound(uri);

            Intent resultIntent = new Intent(context, VideoActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addParentStack(AlarmActivity.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);

            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(1, mBuilder.build());

        }
    }
}

