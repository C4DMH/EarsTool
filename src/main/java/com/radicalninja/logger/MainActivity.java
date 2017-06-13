package com.radicalninja.logger;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TimePicker;

import com.menny.android.anysoftkeyboard.AnyApplication;
import com.menny.android.anysoftkeyboard.R;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private PendingIntent alarmIntent;
    private PendingIntent statsIntent;
    private PendingIntent GPSIntent;
    private PendingIntent MicIntent;
    public static boolean alarmIsSet = false;
    public static boolean gotLocation = false;
    public static boolean statsAlarmIsSet = false;
    public static final String secureID = Settings.Secure.getString(
            AnyApplication.getInstance().getContentResolver(), Settings.Secure.ANDROID_ID);

    GPSTracker gps;
    double latitude;
    double longitude;

    SharedPreferences wmbPreference;

    public static MainActivity instance;
    public String theCurrentDate;



//    private static void initUserId() {
//        if (TextUtils.isEmpty(secureID)) {
//            secureID = Settings.Secure.getString(
//                    AnyApplication.getInstance().getContentResolver(), Settings.Secure.ANDROID_ID);
//        }
//    }

    public void startAlarm() {

        Calendar cal = Calendar.getInstance();
        long when = cal.getTimeInMillis();
        String timey = Long.toString(when);
        String theTime = convertDate(timey, "dd/MM/yyyy hh:mm:ss");
        theCurrentDate = theTime;
        System.out.println("The time changed into nice format is: " + theTime);

        Log.d("the time is: ", when + " ");

        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.HOUR_OF_DAY, 18);
        cal.set(Calendar.MINUTE, 30);

        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);
        alarmIsSet = true;
        //instace = this;

    }




    public static String convertDate(String dateInMilliseconds, String dateFormat) {
        return DateFormat.format(dateFormat, Long.parseLong(dateInMilliseconds)).toString();
    }

    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }

    public static MainActivity getIntance() {
        return instance;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(this));
        setContentView(R.layout.activity_main);
//        secureID = Settings.Secure.getString(
//                AnyApplication.getInstance().getContentResolver(), Settings.Secure.ANDROID_ID);
        instance = this;
        startAlarm();
        wmbPreference = PreferenceManager.getDefaultSharedPreferences(this);
        boolean alarmSet = wmbPreference.getBoolean("ALARMSET", true);
        if (alarmSet) {
            SharedPreferences.Editor editor = wmbPreference.edit();
            editor.putBoolean("ALARMSET", false);
            editor.commit();

        }

        if (!isAccessGranted()) {
            //Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            //startActivity(intent);
            showDialog();

        }

        Geocoder geoCoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        //List<Address> address = null;
        List<Address> address = null;

        if (gotLocation == false) {
            gps = new GPSTracker(this);
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            Log.d("GPS", "111 WE HAVE GOT YOUR LOCATION: LATITUDE = " + latitude + "LONGITUDE = " + longitude);
        }
        startStatsAlarm();
        startMicUploadAlarm();
        startGPSUploadAlarm();

        AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        String rate = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE);
        String size = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER);
        Log.d("Buffer Size and  rate", "Size :" + size + " & Rate: " + rate);




        final JobInfo job = new JobInfo.Builder(1, new ComponentName(this, StatsJobService.class))
                //.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                //.setRequiresCharging(true)
                //.setMinimumLatency(10000)
                .setPeriodic(TimeUnit.MINUTES.toMillis(15))
                //.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .build();
        final JobScheduler jobScheduler =
                (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
//
        jobScheduler.schedule(job);
        Log.d(TAG, "onCreate: Job Scehduled");







        startActivity(new Intent(this, VideoActivity.class));

    }

    @Override
    protected void onResume() {
        super.onResume();
        startActivity(new Intent(this, VideoActivity.class));
        //Toast.makeText(this, "On Resume called!" , Toast.LENGTH_LONG).show();
    }


    public void onVideo(View v) {
        startActivity(new Intent(this, VideoActivity.class));
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            //AnySoftKeyboard.launchsettings();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    public void broadcastIntent() {
        Intent intent = new Intent();
        intent.setAction("com.codepath.CUSTOM_INTENT");
        sendBroadcast(intent);
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance = null;
        startService(new Intent(this, MainActivity.class));
    }

    public void startStatsAlarm() {
        Log.d(TAG, "startStatsAlarm: in start alarm");

        Calendar cal = Calendar.getInstance();
        long when = cal.getTimeInMillis();
        String timey = Long.toString(when);

        //System.out.println("The time changed into nice format is: " + theTime);

        Log.d("the time is: ", when + " ");

        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.HOUR_OF_DAY, 14);
        cal.set(Calendar.MINUTE, 59);

        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, StatsAlarmReceiver.class);
        statsIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, statsIntent);
        alarmIsSet = true;
        //instace = this;

    }


    public void startMicUploadAlarm() {
        Log.d(TAG, "startStatsAlarm: in start alarm");

        Calendar cal = Calendar.getInstance();
        long when = cal.getTimeInMillis();
        String timey = Long.toString(when);

        //System.out.println("The time changed into nice format is: " + theTime);

        Log.d("the time is: ", when + " ");

        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.HOUR_OF_DAY, 14);
        cal.set(Calendar.MINUTE, 35);

        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, MicRecordUploadAlarm.class);
        //statsIntent = PendingIntent.getBroadcast(this, 2, intent, 0);
        MicIntent = PendingIntent.getBroadcast(this, 2, intent, 0);



        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, MicIntent);
        alarmIsSet = true;
        //instace = this;

    }

    public void startGPSUploadAlarm() {
        Log.d(TAG, "startGPSAlarm: in start alarm");

        Calendar cal = Calendar.getInstance();
        long when = cal.getTimeInMillis();
        String timey = Long.toString(when);

        //System.out.println("The time changed into nice format is: " + theTime);

        Log.d("the time is: ", when + " ");

        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.HOUR_OF_DAY, 15);
        cal.set(Calendar.MINUTE, 8);

        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, UploadGPSAlarmReceiver.class);
        //statsIntent = PendingIntent.getBroadcast(this, 3, intent, 0);
        GPSIntent = PendingIntent.getBroadcast(this, 3, intent, 0);


        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, GPSIntent);
        alarmIsSet = true;
        //instace = this;

    }


    private boolean isAccessGranted() {
        try {
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            int mode = 0;
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.KITKAT) {
                mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                        applicationInfo.uid, applicationInfo.packageName);
            }
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public void showDialog()
    {

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Usage Access")
                .setMessage("App will not run without usage access permissions.")
                .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                        // intent.setComponent(new ComponentName("com.android.settings","com.android.settings.Settings$SecuritySettingsActivity"));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivityForResult(intent,0);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .create();

        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alertDialog.show();
    }

}


