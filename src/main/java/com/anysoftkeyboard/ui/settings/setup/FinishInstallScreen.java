package com.anysoftkeyboard.ui.settings.setup;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.menny.android.anysoftkeyboard.AnyApplication;
import com.radicalninja.logger.MicRecordUploadAlarm;
import com.radicalninja.logger.MusicUploadReceiver;
import com.radicalninja.logger.PhotoUploadReceiver;
import com.radicalninja.logger.StatsAlarmReceiver;
import com.radicalninja.logger.StatsJobService;
import com.radicalninja.logger.UploadGPSAlarmReceiver;
import com.sevencupsoftea.ears.R;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

//import com.menny.android.anysoftkeyboard.R;

/**
 * Created by gwicks on 21/01/2018.
 */

public class FinishInstallScreen extends AppCompatActivity {

    private static final String TAG = "FinishInstallScreen";

    ImageView needToTalkClosed;
    TextView talkText;
    TextView mood;
    ImageView moodCheck;
    ImageView preferences;
    TextView prefText;


    // Main Activity variables added 8th Feb 2018

    private PendingIntent alarmIntent;
    private PendingIntent statsIntent;
    private PendingIntent GPSIntent;
    private PendingIntent MicIntent;
    private PendingIntent musicIntent;
    private PendingIntent photoIntent;
    public static boolean alarmIsSet = false;
    public static boolean statsAlarmIsSet = false;
    public static final String secureID = Settings.Secure.getString(
            AnyApplication.getInstance().getContentResolver(), Settings.Secure.ANDROID_ID);
    SharedPreferences wmbPreference;
    public String theCurrentDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.completed_base);
        updateStatusBarColor("#1281e8");


        //needToTalkOpen = (ImageView)findViewById(R.id.gr)


        moodCheck = (ImageView) findViewById(R.id.imageView41);
        needToTalkClosed = (ImageView) findViewById(R.id.imageView6);
        talkText = (TextView) findViewById(R.id.textViewTalk);
        talkText.setVisibility(View.GONE);

        preferences = findViewById(R.id.imageView42);
        //preferences.setTag(1);
        prefText = findViewById(R.id.textView2);
        prefText.setVisibility(View.GONE);
        prefText.setTag(1);


        needToTalkClosed.setTag(1);
        mood = (TextView) findViewById(R.id.textView1);
        mood.setTag(1);
        mood.setVisibility(View.GONE);


        SpannableString ss = new SpannableString("Get free, anonymous and confidential support at 7 Cups. Listeners available 24/7 to help you feel better\n\nGet the App");
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                launch7cups();
            }

        };
        //ss.setSpan(clickableSpan, 48, 55, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(clickableSpan, 106, 117, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        talkText.setText(ss);
        talkText.setMovementMethod(LinkMovementMethod.getInstance());


        needToTalkClosed.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Clicked");

                if (needToTalkClosed.getTag().equals(1)) {
                    talkText.setVisibility(View.VISIBLE);
                    needToTalkClosed.setTag(2);

                } else {
                    talkText.setVisibility(View.GONE);
                    needToTalkClosed.setTag(1);
                }
            }
        });

        preferences.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Clicked moodcheck");

                if (prefText.getTag().equals(1)) {
                    prefText.setVisibility(View.VISIBLE);
                    Log.d(TAG, "onClick: visable");
                    prefText.setTag(2);

                } else {
                    prefText.setVisibility(View.GONE);
                    Log.d(TAG, "onClick: invisible");
                    prefText.setTag(1);
                }
            }
        });

        moodCheck.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Clicked moodcheck");

                if (mood.getTag().equals(1)) {
                    mood.setVisibility(View.VISIBLE);
                    Log.d(TAG, "onClick: visable");
                    mood.setTag(2);

                } else {
                    mood.setVisibility(View.GONE);
                    Log.d(TAG, "onClick: invisible");
                    mood.setTag(1);
                }
            }
        });

        Toast.makeText(this, "THE SECURE DEVICE ID IS: " + secureID, Toast.LENGTH_LONG).show();
        if (!isAccessGranted()) {
            //Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            //startActivity(intent);
            showDialog();
        }

        if(!checkNotificationEnabled()){
            showMusicDialog();
        }

        startStatsAlarm();
        startMicUploadAlarm();
        startGPSUploadAlarm();
        startMusicUploadAlarm();
        startPhotoUploadAlarm();

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

        // remove this intent 30th october 2017
        //startActivity(new Intent(this, VideoActivity.class));


    }

    @Override
    protected void onResume() {
        super.onResume();

        // This is all added from MainActivity
        // 8th Feb 2018

//        if (!isAccessGranted()) {
//            //Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
//            //startActivity(intent);
//            showDialog();
//        }
//
//        if(!checkNotificationEnabled()){
//            showMusicDialog();
//        }
//
//        startStatsAlarm();
//        startMicUploadAlarm();
//        startGPSUploadAlarm();
//        startMusicUploadAlarm();
//        startPhotoUploadAlarm();
//
//        AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
//        String rate = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE);
//        String size = audioManager.getProperty(AudioManager.PROPERTY_OUTPUT_FRAMES_PER_BUFFER);
//        Log.d("Buffer Size and  rate", "Size :" + size + " & Rate: " + rate);
//
//
//        final JobInfo job = new JobInfo.Builder(1, new ComponentName(this, StatsJobService.class))
//                //.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
//                //.setRequiresCharging(true)
//                //.setMinimumLatency(10000)
//                .setPeriodic(TimeUnit.MINUTES.toMillis(15))
//                //.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
//                .build();
//        final JobScheduler jobScheduler =
//                (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
////
//        jobScheduler.schedule(job);
//        Log.d(TAG, "onCreate: Job Scehduled");
//
//        // remove this intent 30th october 2017
//        //startActivity(new Intent(this, VideoActivity.class));


    }

    public void updateStatusBarColor(String color) {// Color must be in hexadecimal fromat
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.d(TAG, "updateStatusBarColor: color change being called!");
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(color));
        }
    }

    public void launch7cups() {
        Log.d(TAG, "launch7cups: clicked");

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=com.sevencupsoftea.app"));
        startActivity(intent);
        //https://play.google.com/store/apps/details?id=com.sevencupsoftea.app
    }


    // 8th Feb 2018, this is first attempt to move the MainActivity and VideoActivity Classes into this final install Activity.

    public void startStatsAlarm() {
        Log.d(TAG, "startStatsAlarm: in start alarm");

        Calendar cal = Calendar.getInstance();
        long when = cal.getTimeInMillis();
        String timey = Long.toString(when);

        //System.out.println("The time changed into nice format is: " + theTime);

        Log.d("the time is: ", when + " ");

        cal.setTimeInMillis(System.currentTimeMillis());
        //cal.set(Calendar.HOUR_OF_DAY, 23);
        //cal.set(Calendar.MINUTE, 55);

        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 55);

        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, StatsAlarmReceiver.class);
        statsIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, statsIntent);

    }


    public void startMicUploadAlarm() {
        Log.d(TAG, "startStatsAlarm: in start alarm");

        Calendar cal = Calendar.getInstance();
        long when = cal.getTimeInMillis();
        String timey = Long.toString(when);

        //System.out.println("The time changed into nice format is: " + theTime);

        Log.d("the time is: ", when + " ");

        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 56);

        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, MicRecordUploadAlarm.class);
        //statsIntent = PendingIntent.getBroadcast(this, 2, intent, 0);
        MicIntent = PendingIntent.getBroadcast(this, 2, intent, 0);



        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, MicIntent);


    }

    public void startGPSUploadAlarm() {
        Log.d(TAG, "startGPSAlarm: in start alarm");

        Calendar cal = Calendar.getInstance();
        long when = cal.getTimeInMillis();
        String timey = Long.toString(when);

        //System.out.println("The time changed into nice format is: " + theTime);

        Log.d("the time is: ", when + " ");

        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE,57);
//        cal.set(Calendar.HOUR_OF_DAY, 12);
//        cal.set(Calendar.MINUTE,54);
        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, UploadGPSAlarmReceiver.class);
        //statsIntent = PendingIntent.getBroadcast(this, 3, intent, 0);
        GPSIntent = PendingIntent.getBroadcast(this, 3, intent, 0);
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, GPSIntent);

    }

    public void startMusicUploadAlarm() {
        Log.d(TAG, "startGPSAlarm: in start alarm");

        Calendar cal = Calendar.getInstance();
        long when = cal.getTimeInMillis();
        String timey = Long.toString(when);

        //System.out.println("The time changed into nice format is: " + theTime);

        Log.d("the time is: ", when + " ");

        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 55);

        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, MusicUploadReceiver.class);
        //statsIntent = PendingIntent.getBroadcast(this, 3, intent, 0);
        musicIntent = PendingIntent.getBroadcast(this, 4, intent, 0);
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, musicIntent);

    }

    // @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void startPhotoUploadAlarm() {
        Log.d(TAG, "startGPSAlarm: in start alarm");

        Calendar cal = Calendar.getInstance();
        long when = cal.getTimeInMillis();
        String timey = Long.toString(when);

        //System.out.println("The time changed into nice format is: " + theTime);

        Log.d("the time is: ", when + " ");

        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 54);
//        cal.set(Calendar.HOUR_OF_DAY, 12);
//        cal.set(Calendar.MINUTE, 56);

        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, PhotoUploadReceiver.class);
        //statsIntent = PendingIntent.getBroadcast(this, 3, intent, 0);
        photoIntent = PendingIntent.getBroadcast(this, 4, intent, 0);
        //alarmMgr.setExact();

        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, photoIntent);
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


    public boolean checkNotificationEnabled() {
        try{
            Log.d(TAG, "checkNotificationEnabled: in try");
            if(Settings.Secure.getString(this.getContentResolver(),
                    "enabled_notification_listeners").contains(this.getApplication().getPackageName()))
            {
                Log.d(TAG, "checkNotificationEnabled: in true");

                Log.d(TAG, "checkNotificationEnabled: true");
                return true;
            } else {

                Log.d(TAG, "checkNotificationEnabled: ruturn false");
                return false;
            }

        }catch(Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "checkNotificationEnabled: Did not get into settings?");
        return false;
    }

    public void showMusicDialog()
    {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Music Listening Habits")
                .setMessage("App will not run without usage access permissions. The app only collects information from installed music players, and ignores all other notifications.")
                .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                        startActivity(intent);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        startService(new Intent(this, FinishInstallScreen.class));
    }


}
