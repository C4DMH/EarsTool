package com.radicalninja.logger;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.sevencupsoftea.ears.R;

import static android.app.Notification.EXTRA_NOTIFICATION_ID;

/**
 * Created by gwicks on 31/03/2018.
 */

public class EMASleepReceiver extends BroadcastReceiver {

    private NotificationManager mNotificationManager;
    private static final String TAG = "EMASleepReceiver";
    private String CHANNEL_DI = "EMasleep";
    Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

    Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {

        mContext = context;
        Log.d(TAG, "onReceive: in second one FUCKER");



        //mNotificationManager.cancel(1);
        Log.d(TAG, "onReceive: in sleep 1 intent");
        Log.d(TAG, "onReceive: mnotif: " + mNotificationManager);



        if(mNotificationManager == null){
            Log.d(TAG, "onReceive: in notification manager = null");
                //mNotificationManager = FinishInstallScreen.notificationManager;
            mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE) ;
            Log.d(TAG, "onReceive: notificiation manager  = " + mNotificationManager);
                //mNotificationManager = EMA.
                //mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
            //mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        clearNotfication();
        String message = intent.getAction();
        if(message.equals("SNOOZE")){
            Log.d(TAG, "onReceive: SNOOZE!!!!");

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            String name = "Oreo_Notificaitons";
            NotificationChannel mChannel = mNotificationManager.getNotificationChannel(CHANNEL_DI);
            if(mChannel == null){
                mChannel = new NotificationChannel(CHANNEL_DI, name, importance);
                Log.d(TAG, "onReceive: mchannel: " + mChannel);
                Log.d(TAG, "onReceive: mnotif: " + mNotificationManager);
                mChannel.setDescription("blah_1");
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                Log.d(TAG, "onReceive: mchannel: " + mChannel);

                mNotificationManager.createNotificationChannel(mChannel);

            }
            Log.d(TAG, "onReceive: fuck");
            if(message.equals("SNOOZE")){
                Log.d(TAG, "onReceive: SNOOZE!!!!");

            }

            Intent snoozeIntent = new Intent(context, EMASleepTwoReceiver.class);
            snoozeIntent.setAction("SNOOZE");
            snoozeIntent.putExtra(EXTRA_NOTIFICATION_ID, 0);
            PendingIntent snoozePendingIntent =
                    PendingIntent.getBroadcast(context, 0, snoozeIntent, 0);

            NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.cdmh_small, "SNOOZE", snoozePendingIntent).build();

//            Intent snoozeIntent = new Intent(this, MyBroadCastReceiver.class);
//            snoozeIntent.setAction(ACTION_SNOOZE);
//            snoozeIntent.putExtra(EXTRA_NOTIFICATION_ID, 0);
//            PendingIntent snoozePendingIntent =
//                    PendingIntent.getBroadcast(this, 0, snoozeIntent, 0);
            Intent resultIntent = new Intent(context, EMA.class);
            //resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, resultIntent, 0);



            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context, CHANNEL_DI)
                            .setSmallIcon(R.drawable.noti_icon)
                            .setContentTitle("Quick Survey Reminder")
                            .setAutoCancel(true)
                            .setContentText("Quick Survey  1st Reminder")
                            .setOngoing(true)
                            .setChannelId(CHANNEL_DI)
                            .addAction(action)

                            .setContentIntent(pendingIntent);
            //.build();


            //TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            //stackBuilder.addParentStack(AlarmActivity.class);
            //stackBuilder.addNextIntent(resultIntent);
            //PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            //mBuilder.setContentIntent(resultPendingIntent);

            //NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            //mNotificationManager.notify(1, mBuilder);
            Log.d(TAG, "onReceive OREO: should be notification built now");
            createNotification(mBuilder);

        }else{


            Intent resultIntent = new Intent(context, EMA.class);
            //TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            //stackBuilder.addParentStack(AlarmActivity.class);
            //stackBuilder.addNextIntent(resultIntent);
            //PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);

            //mBuilder.setContentIntent(resultPendingIntent);

            Intent snoozeIntent = new Intent(context, EMASleepTwoReceiver.class);
            snoozeIntent.setAction("SNOOZE");
            snoozeIntent.putExtra(EXTRA_NOTIFICATION_ID, 0);
            PendingIntent snoozePendingIntent =
                    PendingIntent.getBroadcast(context, 0, snoozeIntent, 0);
            NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.cdmh_small, "SNOOZE", snoozePendingIntent).build();



            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context, CHANNEL_DI)
                            .setSmallIcon(R.drawable.noti_icon)
                            .setContentTitle("Quick Survey Reminder")
                            .setAutoCancel(true)
                            .setContentText("Quick Survey 1st Reminder")
                            .setOngoing(true)
                            .addAction(action)
                            .setContentIntent(resultPendingIntent)
                            .setSound(uri);


            //NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            //mNotificationManager.notify(2, mBuilder.build());
            createNotification(mBuilder);
            Log.d(TAG, "onReceive not ored: should be notification built now");

        }



    }

    private void createNotification(NotificationCompat.Builder builder){

        final NotificationCompat.Builder mBuilder;

        mBuilder = builder;


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: runnalbe in");
                mNotificationManager.notify("second", 2, mBuilder.build());


            }
        }, 1000*60*10);

        Log.d(TAG, "createNotification: runable out");
        //mNotificationManager.notify(1, builder.build());

    }

    public void clearNotfication(){

        Log.d(TAG, "clearNotfication: in cancel");

        //NotificationManager notificationManager = (NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        //notificationManager.cancel(id);
        mNotificationManager.cancelAll();
        mNotificationManager.cancel("first", 1);
        mNotificationManager.cancel("second", 2);
        mNotificationManager.cancel("third", 3);


    }
}