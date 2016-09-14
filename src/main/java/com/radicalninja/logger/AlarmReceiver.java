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

//public class AlarmReceiver extends BroadcastReceiver {
//
//    Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//
//
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        Calendar now = GregorianCalendar.getInstance();
//        int dayOfWeek = now.get(Calendar.DATE);
//        if(dayOfWeek != 50 && dayOfWeek != 70) {
//            Notification.Builder mBuilder =
//                    new Notification.Builder(context)
//                            .setSmallIcon(R.drawable.video)
//                            .setContentTitle("VIDEO DIARY")
//                            .setAutoCancel(true)
//                            .setContentText("Don't forget to do your Video Diary Today!!")
//                            .setOngoing(true)
//                            .setSound(uri);
//
//            int requestID = (int) System.currentTimeMillis();
//
////            Intent resultIntent = new Intent(context, VideoActivity.class);
////            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
////            stackBuilder.addParentStack(AlarmActivity.class);
////            stackBuilder.addNextIntent(resultIntent);
////            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
////            mBuilder.setContentIntent(resultPendingIntent);
////            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
////            Notification notification = mBuilder.build();
////            notification.flags = Notification.FLAG_NO_CLEAR;
////            mNotificationManager.notify(0, notification);
//
//
//            Intent resultIntent = new Intent(context, VideoActivity.class);
//            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
//            stackBuilder.addParentStack(AlarmActivity.class);
//            stackBuilder.addNextIntent(resultIntent);
//            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//            mBuilder.setContentIntent(resultPendingIntent);
//            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//            mNotificationManager.notify(requestID, mBuilder.build());
//
//        }
//    }
//}

//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.widget.Toast;
//
///**
// * Created by geord_000 on 7/07/2016.
// */
////import android.app.Notification;
////import android.app.NotificationManager;
////import android.app.PendingIntent;
////import android.content.BroadcastReceiver;
////import android.content.Context;
////import android.content.Intent;
////import android.support.v4.app.NotificationCompat;
////import android.widget.Toast;
////
//public class AlarmReceiver extends BroadcastReceiver {
//
//
//    @Override
//    public void onReceive(Context context, Intent intent){
//        Toast.makeText(context, "Alarm received!", Toast.LENGTH_LONG).show();
//    }
//}
////
////    private static final int MY_NOTIFICATION_ID=1;
////    NotificationManager notificationManager;
////    Notification myNotification;
////
////    @Override
////    public void onReceive(Context context, Intent intent) {
////        Toast.makeText(context, "Alarm received!", Toast.LENGTH_LONG).show();
////
////        Intent myIntent = new Intent(context, AlarmActivity.class);
////        PendingIntent pendingIntent = PendingIntent.getActivity(
////                context,
////                0,
////                myIntent,
////                Intent.FLAG_ACTIVITY_NEW_TASK);
////
////        myNotification = new NotificationCompat.Builder(context)
////                .setContentTitle("Exercise of Notification!")
////                .setContentText("Do Something...")
////                .setTicker("Notification!")
////                .setWhen(System.currentTimeMillis())
////                .setContentIntent(pendingIntent)
////                .setDefaults(Notification.DEFAULT_SOUND)
////                .setAutoCancel(true)
////                .setSmallIcon(R.drawable.ic_launcher)
////                .build();
////
////        notificationManager =
////                (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
////        notificationManager.notify(MY_NOTIFICATION_ID, myNotification);
////    }
////
////}
//
//
//
//
//
//
//
//
//
//
//
//
////public class AlarmReceiver extends BroadcastReceiver {
////
////    @Override
////    public void onReceive(Context context, Intent intent) {
////        // TODO Auto-generated method stub
////
////        Log.e("onReceive", "ladskjflsakjdflskjdflskjdfslkjdflasdf");
////        Toast.makeText(context, "OnReceive alarm test", Toast.LENGTH_SHORT).show();
////    }
////}

