package com.radicalninja.logger;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.Log;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.anysoftkeyboard.ui.dev.DeveloperUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import static com.menny.android.anysoftkeyboard.R.attr.appName;

/**
 * Created by gwicks on 20/10/2016.
 */
public class DefaultExceptionHandler implements Thread.UncaughtExceptionHandler {
    private Thread.UncaughtExceptionHandler defaultUEH;


    Activity activity;
    TransferUtility transferUtility;
    String android_id;


    //Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

    public DefaultExceptionHandler(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

        android_id = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);


        try {
            Log.d("Crash", "Crash 2.5");

            String CrashReportUpload = (Environment.getExternalStorageDirectory().getAbsolutePath() + "/videoDIARY/" + "CrashReport.txt");
            File file = new File(CrashReportUpload);

            String stackTrace = com.anysoftkeyboard.utils.Log.getStackTrace(ex);


            //String appName = DeveloperUtils.getAppDetails(activity);

            final CharSequence utcTimeDate = DateFormat.format(
                    "kk:mm:ss dd.MM.yyyy", new Date());
            final String newline = DeveloperUtils.NEW_LINE;

            writeToFile(file, "Hi. It seems that we have crashed.... Here are some details:" + newline
                    + "****** UTC Time: "
                    + utcTimeDate
                    + newline
                    + "****** Application name: "
                    + appName
                    + newline
                    + "******************************" + newline
                    + "****** Exception type: "
                    + ex.getClass().getName()
                    + newline
                    + "****** Exception message: "
                    + ex.getMessage() + newline + "****** Trace trace:" + newline + stackTrace + newline
                    + "******************************" + newline
                    + "****** Device information:" + newline
                    + DeveloperUtils.getSysInfo(activity), activity);

            writeToFile(file, stackTrace, activity);
            Log.d("Crash", "Crash 2");
            //beginUpload2(CrashReportUpload);
            Log.d("Crash", "Crash 3");


            Log.d("Exception", "Start of Exception handler class");

            Intent intent = new Intent(activity, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
            Log.d("Exception", "Start of Exception handler class 2");

            PendingIntent pendingIntent = PendingIntent.getActivity(
                    MainActivity.getIntance().getBaseContext(), 0, intent, intent.getFlags());

//            PendingIntent pendingIntent = PendingIntent.getActivity(
//                    MainActivity.getIntance().getApplicationContext(), 0, intent, intent.getFlags());
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
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void writeToFile(File file, String data, Context context) {
//        Calendar cal = Calendar.getInstance();
//        Date now = new Date();
//        cal.setTime(now);
//        long endTime = cal.getTimeInMillis();
//        File path = context.getExternalFilesDir(null);
//        File file = new File(path, "googleFit.txt");


//        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + directoryName + "GoogleFit_" + endTime + ".txt");
        FileOutputStream stream = null;
        //OutputStreamWriter stream = new OutputStreamWriter(openFileOutput(file), Context.MODE_APPEND);
        try {
            android.util.Log.e("Default Exception", "In try");
            stream = new FileOutputStream(file, true);
            stream.write(data.getBytes());
        } catch (FileNotFoundException e) {
            android.util.Log.e("Default exception", "In catch");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        try {
//            stream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        try {

            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void beginUpload2(String filePath) {
        if (filePath == null) {
            //Toast.makeText(this, "Could not find the filepath of the selected file",Toast.LENGTH_LONG).show();
            return;
        }

        Log.d("Crash", "Crash 1");

        //TransferUtility transferUtility;
        //transferUtility = Util.getTransferUtility(this);
        //transferUtility = Util.getTransferUtility(context);

        //setTheDate();
        String newFilePath = android_id + "/CrashReport.txt";
        //String newFilePath = UserID + "/" ;

        //Toast.makeText(this, "The file is uploading, using the name: " + newFilePath,Toast.LENGTH_LONG).show();
        //Log.d("uploading, using: " + newFilePath, "");
        File file = new File(filePath);
        TransferObserver observer = transferUtility.upload(Constants.BUCKET_NAME, newFilePath,
                file);
        /*
         * Note that usually we set the transfer listener after initializing the
         * transfer. However it isn't required in this sample app. The flow is
         * click upload button -> start an activity for image selection
         * startActivityForResult -> onActivityResult -> beginUpload -> onResume
         * -> set listeners to in progress transfers.
         */
        // observer.setTransferListener(new UploadListener());
    }


}
