package com.radicalninja.logger;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.sevencupsoftea.ears.BuildConfig;

import java.io.File;
import java.util.List;

public class LogUploadTask extends BroadcastReceiver {


    //public static  int n = 1;


    private static final String TAG = LogUploadTask.class.getSimpleName();
    private static final long FIRST_TASK_DELAY_MILLIS = 30000;  // 30 seconds

    static long getTaskInterval() {
        return BuildConfig.LOG_UPLOAD_INTERVAL_MINUTES * 60 * 1000;
    }

    static long getFirstTaskDelay() {
        return SystemClock.elapsedRealtime() + FIRST_TASK_DELAY_MILLIS;
    }

    @SuppressWarnings("PointlessBooleanExpression")
    static void registerTasks(final Context context) {
        Log.d("LogUploadtask", "This is LogUplaodTask in registerTasks 1");

        int upload_interval = BuildConfig.LOG_UPLOAD_INTERVAL_MINUTES;
        Log.d("LogUploadtask", "Upload interval minutes in registerTasks = " + upload_interval);

        int x = BuildConfig.LOG_UPLOAD_INTERVAL_MINUTES;
        boolean y = BuildConfig.USE_AUTO_UPLOAD;

        Log.d("LogUploadtask", "The value of LOG_UPLOAD_INTERVAL_MINUTES IS: " + x + "the value of boolean USE_AUTO_UPLOAD IS: " + y);

        if (BuildConfig.LOG_UPLOAD_INTERVAL_MINUTES < 1 || !BuildConfig.USE_AUTO_UPLOAD) {
            //if (BuildConfig.LOG_UPLOAD_INTERVAL_MINUTES < 1) {
            return;
        }

        Log.d(TAG, String.format("Registering repeating upload tasks. Runs every %d minutes.",
                BuildConfig.LOG_UPLOAD_INTERVAL_MINUTES));
        Log.d("LogUploadtask", "This is LogUplaodTask in registerTasks 2");
        final long interval = getTaskInterval();
        final long delay = getFirstTaskDelay();
        final AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        final Intent intent = new Intent(context, LogUploadTask.class);
        Log.d("LogUploadtask", "This is LogUplaodTask in registerTasks 3");
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
//        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, delay, interval, pendingIntent);
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, delay, interval, pendingIntent);
        //alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,10000,240000, pendingIntent);

    }//java.io.FileNotFoundException: Source '/data/user/0/com.menny.android.anysoftkeyboard/files/buffered.log' does not exist

    static void unregisterTasks(final Context context) {
        Log.d(TAG, "Unregistering the LogUploadTasks.");
        final AlarmManager alarmManager =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        final Intent intent = new Intent(context, LogUploadTask.class);
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        alarmManager.cancel(pendingIntent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Executing log upload task.");
        Log.d("LogUploadTask", "This is loguplaodtask on Recieve, right before awsutil");


        final LogManager logManager = LogManager.getInstance();
        final List<File> files = logManager.getExportFiles();


        Util.uploadFilesToBucket(files, true, logUploadCallback, context);

        // Log.d("LogUploadTask", "This is loguplaodtask on Recieve, right after awsutil and the number of files is: " + length);

        File path = context.getFilesDir();


    }

    final Util.FileTransferCallback logUploadCallback = new Util.FileTransferCallback() {
        @SuppressLint("DefaultLocale")

        private String makeLogLine(final String name, final int id, final TransferState state) {
            Log.d("LogUploadTask", "This is AWSBIT");
            return String.format("%s | ID: %d | State: %s", name, id, state.toString());
        }

        @Override
        public void onCancel(int id, TransferState state) {
            Log.d(TAG, makeLogLine("Callback onCancel()", id, state));
        }

        @Override
        public void onStart(int id, TransferState state) {
            Log.d(TAG, makeLogLine("Callback onStart()", id, state));

        }

        @Override
        public void onComplete(int id, TransferState state) {
            Log.d(TAG, makeLogLine("Callback onComplete()", id, state));
        }

        @Override
        public void onError(int id, Exception e) {
            Log.d(TAG, makeLogLine("Callback onError()", id, TransferState.FAILED), e);
        }
    };

}
