package com.radicalninja.logger;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.sevencupsoftea.ears.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.app.Notification.EXTRA_NOTIFICATION_ID;

/**
 * Created by gwicks on 31/03/2018.
 */

public class EMAAlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "EMAAlarmReciever";
    private NotificationManager mNotificationManager;

    Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    String CHANNEL_DI = "EMA_notfication";
    Context mContext;
    String stringExtra = null;

    //public String myString;

    @Override
    public void onReceive(Context context, Intent intent) {

        // This next part is to ensure the notification does not go off during sleep.
        // Note may still be firing at night, which is bad for battery life, and should be fixed
        //TODO: Change time checking method to no longer fire during sleep, and be discarded ( bad for battery)
        mContext = context;

        Log.d(TAG, "onReceive: THe intent extra is: "+ intent.getStringExtra("EMA"));
        Log.d(TAG, "onReceive: the intent action is : " + intent.getAction());

        if(stringExtra != null){
            Log.d(TAG, "onReceive: stringExtra is already set to EMA1");
            return;
        }



        stringExtra = intent.getStringExtra("EMA");




        Log.d(TAG, "onReceive: IN FIRST 1 FUCKER");
        String dateFormat = "HH:mm:ss";
        String endTime= "23:30:00";
        String startTime = "08:00:00";
        String currentTime = new SimpleDateFormat(dateFormat).format(new Date());

        Calendar cStart = setTimeToCalendar(dateFormat, startTime, false);
        Calendar cEnd = setTimeToCalendar(dateFormat, endTime, false);
        Calendar cNow = setTimeToCalendar(dateFormat, currentTime, false );
        Date curDate = cNow.getTime();

        Log.d(TAG, "onReceive: cStart: " + cStart.getTime() + " cEnd: " + cEnd.getTime());
        Log.d(TAG, "onReceive: curdate.after(cStart: " + curDate.after(cStart.getTime()));
        Log.d(TAG, "onReceive: curDate.before(Cend: " + curDate.before(cEnd.getTime()));

        if (curDate.after(cStart.getTime()) && curDate.before(cEnd.getTime())) {
            System.out.println("Time is out of range time is: " + currentTime + " curDate: " + curDate);
            Log.d(TAG, "Date is in range, firing notification:");

        } else {
            System.out.println("Date is out of range there fore skippingf: ");
            return;

        }

        Calendar currentTime2 = Calendar.getInstance();








        Log.d(TAG, "onReceive: in receive ");

        if(mNotificationManager == null){
            Log.d(TAG, "onReceive: in notification manager = null");
            //mNotificationManager = FinishInstallScreen.notificationManager;
            mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE) ;
            Log.d(TAG, "onReceive: notificiation manager  = " + mNotificationManager);
            //mNotificationManager = EMA.
            //mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        Log.d(TAG, "onReceive: before clear notifications");

        clearNotfications();
        Log.d(TAG, "onReceive: after clear notifications");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            String name = "Oreo_Notificaitons";
            NotificationChannel mChannel = mNotificationManager.getNotificationChannel(CHANNEL_DI);
            if(mChannel == null){
                mChannel = new NotificationChannel(CHANNEL_DI, name, importance);
                mChannel.setDescription("blah_1");
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                mNotificationManager.createNotificationChannel(mChannel);

            }

            Intent snoozeIntent = new Intent(context, EMASleepReceiver.class);
            snoozeIntent.setAction("SNOOZE");
            snoozeIntent.putExtra(EXTRA_NOTIFICATION_ID, 0);
            PendingIntent snoozePendingIntent =
                    PendingIntent.getBroadcast(context, 0, snoozeIntent, 0);


            Intent resultIntent = new Intent(context, EMA.class);
            //resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, resultIntent, 0);

            NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.cdmh_small, "SNOOZE", snoozePendingIntent).build();

            Notification mBuilder =
                    new NotificationCompat.Builder(context, CHANNEL_DI)
                            .setSmallIcon(R.drawable.noti_icon)
                            .setContentTitle("Quick Survey")
                            .setAutoCancel(true)
                            .setContentText("Time for another quick survey :)")
                            .setOngoing(true)
                            .setChannelId(CHANNEL_DI)
                            .setSound(uri)
                            .setContentIntent(pendingIntent)
                            .addAction(action)
                            .build();


            //TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            //stackBuilder.addParentStack(AlarmActivity.class);
            //stackBuilder.addNextIntent(resultIntent);
            //PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            //mBuilder.setContentIntent(resultPendingIntent);

            //NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify("first",1, mBuilder);
            Log.d(TAG, "onReceive OREO: should be notification built now");

        }else{
            Log.d(TAG, "onReceive: non oreo, reight before building pending intent");

            Intent resultIntent = new Intent(context, EMA.class);
//            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
//            //stackBuilder.addParentStack(AlarmActivity.class);
//            stackBuilder.addNextIntent(resultIntent);
//            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//            //mBuilder.setContentIntent(resultPendingIntent);

//
//            Log.d(TAG, "onReceive: before snooze intent");
//            Intent snoozeIntent = new Intent(context, EMASleepReceiver.class);
//            snoozeIntent.setAction("SNOOZE");
//            snoozeIntent.putExtra(EXTRA_NOTIFICATION_ID, 0);
//            PendingIntent snoozePendingIntent =
//                    PendingIntent.getBroadcast(context, 0, snoozeIntent, 0);
//            NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.cdmh_small, "SNOOZE", snoozePendingIntent).build();

            //6/4/18 - maybe the stackbuilder is causing me problems?

            //TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            //stackBuilder.addParentStack(AlarmActivity.class);
            //stackBuilder.addNextIntent(resultIntent);
            //PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);

            //mBuilder.setContentIntent(resultPendingIntent);


            Log.d(TAG, "onReceive: before snooze intent");
            Intent snoozeIntent = new Intent(context, EMASleepReceiver.class);
            snoozeIntent.setAction("SNOOZE");
            snoozeIntent.putExtra(EXTRA_NOTIFICATION_ID, 0);
            PendingIntent snoozePendingIntent =
                    PendingIntent.getBroadcast(context, 0, snoozeIntent, 0);
            NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.cdmh_small, "SNOOZE", snoozePendingIntent).build();


            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context, CHANNEL_DI)
                            .setSmallIcon(R.drawable.noti_icon)
                            .setContentTitle("Quick Survey")
                            .setAutoCancel(true)
                            .setContentText("Time for another quick survey")
                            .setOngoing(true)
                            .setContentIntent(resultPendingIntent)
                            .addAction(action)
                            .setSound(uri);



            //NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify("first",1, mBuilder.build());
            Log.d(TAG, "onReceive not ored: should be notification built now");

        }




//
//                        .setSmallIcon(R.drawable.cdmh_small)
//                        .setContentTitle("EMA")
//                        .setAutoCancel(true)
//                        .setContentText("Time for another EMA :)")
//                        .setOngoing(true)
//                        .setSound(uri);
//
//        Intent resultIntent = new Intent(context, MainActivity.class);
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
//        //stackBuilder.addParentStack(AlarmActivity.class);
//        stackBuilder.addNextIntent(resultIntent);
//        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//        mBuilder.setContentIntent(resultPendingIntent);
//
//        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        mNotificationManager.notify(1, mBuilder.build());
//        Log.d(TAG, "onReceive: should be notification built now");

    }

    private Calendar setTimeToCalendar(String dateFormat, String date, boolean addADay) {
        Date time = null;
        try {
            time = new SimpleDateFormat(dateFormat).parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(time );

        if(addADay) {
            cal.add(Calendar.DATE, 1);
        }
        return cal;
    }

    public void clearNotfications(){

        Log.d(TAG, "clearNotfication: in cancel");

        //NotificationManager notificationManager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        //Log.d(TAG, "clearNotfications: " + notificationManager.getActiveNotifications().toString());
        //mNotificationManager.

        mNotificationManager.cancel("first", 1);
        mNotificationManager.cancel("second", 2);
        mNotificationManager.cancel("third", 3);

        if(mNotificationManager != null){
            mNotificationManager.cancelAll();
        }
    }
}
