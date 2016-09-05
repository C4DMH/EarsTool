package com.radicalninja.logger;

//public class AlarmActivity extends Activity {

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        // TODO Auto-generated method stub
//        super.onCreate(savedInstanceState);
//        ImageView image = new ImageView(this);
//        image.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher));
//        setContentView(image);
//        Toast.makeText(getApplicationContext(),
//                "Do Something NOW",
//                Toast.LENGTH_LONG).show();
//    }
//
//}


import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import com.menny.android.anysoftkeyboard.R;

import java.util.Calendar;

public class AlarmActivity extends Activity {

    private PendingIntent alarmIntent;
    public static boolean alarmIsSet = false;
    /** Called when the activity is first created. */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //alarmIsSet = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startAlarm();



//        Calendar cal = Calendar.getInstance();
//        long when = cal.getTimeInMillis();
//        String timey = Long.toString(when);
//        String theTime = convertDate(timey, "dd/MM/yyyy hh:mm:ss");
//        System.out.println("The time changed into nice format is: " + theTime);
//        //Log.d(convertDate(timey, "dd/MM/yyyy hh:mm:ss"));
//
//        Log.d("the time is: ", when+" ");
//        //Log.d(theTime);
//
//        cal.setTimeInMillis(System.currentTimeMillis());
//        //cal.clear();
//        cal.set(Calendar.HOUR_OF_DAY, 21);
//        cal.set(Calendar.MINUTE, 10);
//
//        AlarmManager alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
//
//        Intent intent = new Intent(this, AlarmReceiver.class);
//        alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
//        //PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
//        // cal.add(Calendar.SECOND, 5);
//        //alarmMgr.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
//        //alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,when, AlarmManager.INTERVAL_DAY, PendingIntent.getBroadcast(this,1,  intent, PendingIntent.FLAG_UPDATE_CURRENT));
//        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);
//        //alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, when, 1000 * 60 * 1, alarmIntent);
    }

    public void startAlarm(){
        Calendar cal = Calendar.getInstance();
        long when = cal.getTimeInMillis();
        String timey = Long.toString(when);
        String theTime = convertDate(timey, "dd/MM/yyyy hh:mm:ss");
        System.out.println("The time changed into nice format is: " + theTime);
        //Log.d(convertDate(timey, "dd/MM/yyyy hh:mm:ss"));

        Log.d("the time is: ", when+" ");
        //Log.d(theTime);

        cal.setTimeInMillis(System.currentTimeMillis());
        //cal.clear();
        cal.set(Calendar.HOUR_OF_DAY, 21);
        cal.set(Calendar.MINUTE, 54);

        AlarmManager alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        //PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        // cal.add(Calendar.SECOND, 5);
        //alarmMgr.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
        //alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,when, AlarmManager.INTERVAL_DAY, PendingIntent.getBroadcast(this,1,  intent, PendingIntent.FLAG_UPDATE_CURRENT));
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);
        //alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, when, 1000 * 60 * 1, alarmIntent);
        Toast.makeText(this, "WE HAVE SET THE ALARM _ in arlarm activity class", Toast.LENGTH_LONG).show();
        alarmIsSet = true;
    }

    public static String convertDate(String dateInMilliseconds, String dateFormat) {
        return DateFormat.format(dateFormat, Long.parseLong(dateInMilliseconds)).toString();
    }
}