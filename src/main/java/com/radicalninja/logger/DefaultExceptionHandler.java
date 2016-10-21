package com.radicalninja.logger;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by gwicks on 20/10/2016.
 */
public class DefaultExceptionHandler implements Thread.UncaughtExceptionHandler {
    private Thread.UncaughtExceptionHandler defaultUEH;


    Activity activity;

    public DefaultExceptionHandler(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

        try {

            Log.d("Exception", "Start of Exception handler class");

            Intent intent = new Intent(activity, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
            Log.d("Exception", "Start of Exception handler class 2");

            PendingIntent pendingIntent = PendingIntent.getActivity(
                    MainActivity.getIntance().getBaseContext(), 0, intent, intent.getFlags());
            Log.d("Exception", "Start of Exception handler class 3");
            //Following code will restart your application after 2 seconds
            AlarmManager mgr = (AlarmManager) MainActivity.getIntance().getBaseContext()
                    .getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 5000,
                    pendingIntent);
            Log.d("Exception", "Start of Exception handler class 4");
            //This will finish your activity manually
            activity.finish();
            Log.d("Exception", "Start of Exception handler class 5");
            //This will stop your application and take out from it.
            System.exit(1);
        }catch(Exception e){
            e.printStackTrace();
        }

    }
}

