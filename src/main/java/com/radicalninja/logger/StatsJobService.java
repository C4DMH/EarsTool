package com.radicalninja.logger;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by gwicks on 9/05/2017.
 */

public class StatsJobService extends JobService {





    static String folder = "/videoDIARY/";

    private static final String TAG = "StatsJobService";
    @Override
    public boolean onStartJob(JobParameters params) {
        GPSTracker mGPSTracker = new GPSTracker(this);
        Toast.makeText(this, "Job Starts", Toast.LENGTH_SHORT).show();

        //File directory;
        //File location;

        Calendar c = Calendar.getInstance();
        //System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("hh:mm:ss");
        String formattedDate = df.format(c.getTime());

        SimpleDateFormat df2 = new SimpleDateFormat("yyyy:MM:dd");
        String currentDate = df2.format(c.getTime());

        String path = Environment.getExternalStorageDirectory() + "/videoDIARY/Location/";




        Log.d(TAG, "onStartJob: path is: " + path);

        File directory = new File(path);
        if(!directory.exists()){
            Log.d(TAG, "onStartJob: making directory");
            directory.mkdir();
        }

        // Changed 7th June 2017

        //File location = new File(directory, formattedDate +".txt");
        File location = new File(directory, currentDate +".txt");

//        try{
//            location = File.createTempFile(formattedDate, ".txt", directory);
//            System.out.println("Path to file is: + " + location.getAbsolutePath());
//        } catch (Exception e1) {
//            Log.d(TAG, "onStartJob: in catch");
//            // TODO Auto-generated catch block
//            e1.printStackTrace();
//        }

        //File location = new File(path);

        double latitude = mGPSTracker.getLatitude();
        double longitude = mGPSTracker.getLongitude();

        Log.d(TAG, "onStartJob: Time: " + formattedDate + "  Latitude: " + latitude + "  Longitude: " + longitude);

        writeToFile(location, "Time: " + formattedDate + "  Latitude: " + latitude + "  Longitude: " + longitude +"\n");




        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "onStopJob: in on stop job");



        return false;
    }

    private static void writeToFile(File file, String data) {

        FileOutputStream stream = null;
        System.out.println("The state of the media is: " + Environment.getExternalStorageState());
        Log.d(TAG, "writeToFile: file location is:" + file.getAbsolutePath());

        //OutputStreamWriter stream = new OutputStreamWriter(openFileOutput(file), Context.MODE_APPEND);
        try {
            Log.e("History", "In try");
            Log.d(TAG, "writeToFile: ");
            stream = new FileOutputStream(file, true);
            Log.d(TAG, "writeToFile: 2");
            stream.write(data.getBytes());
            Log.d(TAG, "writeToFile: 3");
        } catch (FileNotFoundException e) {
            Log.e("History", "In catch");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();

        }


        try {

            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


//    Encryption mEncryption;
//    UsageStatsManager mUsageStatsManager;
//    private TransferUtility transferUtility;
//
//    public static String fileName;
//
//    Context context;
//
//    public static String time;
//
//
//    public void setFileName(String name){
//        fileName = name;
//    }
//
//
//
//    private static final String TAG = "StatsJobService";
//    @Override
//    public boolean onStartJob(JobParameters params) {
//        Log.d(TAG, "onStartJob: 1");
//        Toast.makeText(this, "Job Starts", Toast.LENGTH_SHORT).show();
//
//
//        new JobTask(this).execute(params);
//        Log.d(TAG, "onStartJob: ");
//        Calendar calendar = Calendar.getInstance();
//        long endTime = calendar.getTimeInMillis();
//        //calendar.add(Calendar.DAY_OF_YEAR, -1);
//        long startTime = calendar.getTimeInMillis() - 24*60*60*1000;
//        Log.d(TAG, "getUsageStatsList: endtime: " + endTime + "starttime: " + startTime);
//
//        Date one = new Date(startTime);
//        Date two = new Date(endTime);
//
//        Log.d(TAG, "getUsageStatsList: data start time: " + one);
//        Log.d(TAG, "getUsageStatsList: date endtime:  " + two);
//        time = two.toString();
//
//        context = VideoActivity.getIntance();
//        return true;
//    }
//
//    @Override
//    public boolean onStopJob(JobParameters params) {
//        Toast.makeText(this, "Job Stopped: criteria not met", Toast.LENGTH_SHORT).show();
//        //TODO cancel the running task
//        Log.d(TAG, "onStopJob: ");
//        return false;    }
//
//
//
//
//    private class JobTask extends AsyncTask<JobParameters, Void, JobParameters> {
//        private final JobService jobService;
//
//        public JobTask(JobService jobService) {
//            Log.d(TAG, "JobTask: ");
//            this.jobService = jobService;
//        }
//
//        @Override
//        protected JobParameters doInBackground(JobParameters... params) {
//            SystemClock.sleep(5000);
//            Log.d(TAG, "doInBackground: ");
//            UStats.printCurrentUsageStatus(this.jobService);
//
//            return params[0];
//        }
//
//        @Override
//        protected void onPostExecute(JobParameters jobParameters) {
//            jobService.jobFinished(jobParameters, false);
//            Log.d(TAG, "onPostExecute: ");
//            Toast.makeText(jobService, "Task Finished", Toast.LENGTH_SHORT).show();
//            transferUtility = Util.getTransferUtility(context);
//            //new encryptAsyncTask2.execute(fileName);
//            Log.d(TAG, "onPostExecute: 1");
//
//            String encryptedFilepath = Encrypt(time, fileName);
//            Log.d(TAG, "onPostExecute: 2");
//
//            beginUpload2(time, encryptedFilepath);
//            Log.d(TAG, "onPostExecute: 3");
//
//        }
//    }
//
//
//
//    public String Encrypt(String name, String path){
//        Log.d(TAG, "Encrypt: 1");
//        String mFileName = name;
//        String mFilePath = path;
//
//        String path2 = null;
//        try {
//            //com.anysoftkeyboard.utils.Log.d(TAG, "We are starting encrytopn 1 - in doInBackgound AsyncTask ENCRYTPTION!");
//            path2 = mEncryption.encrypt(name, path, "/videoDIARY/");
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (NoSuchPaddingException e) {
//            e.printStackTrace();
//        } catch (InvalidKeyException e) {
//            e.printStackTrace();
//        } catch (InvalidAlgorithmParameterException e) {
//            e.printStackTrace();
//        }
//        Log.d(TAG, "Encrypt: 2");
//
//        return path2;
//
//
//
//    }
//    private void beginUpload2(String name, String filePath) {
//        Log.d(TAG, "beginUpload2: start of beginupload2");
//        if (filePath == null) {
//            //Toast.makeText(this, "Could not find the filepath of the selected file", Toast.LENGTH_LONG).show();
//            Log.d(TAG, "beginUpload2: no file path found");
//            return;
//        }
//
//
//        Log.d(TAG, "beginUpload2: middle");
//
//        File file = new File(filePath);
//        TransferObserver observer = transferUtility.upload(Constants.BUCKET_NAME, name,
//                file);
//        Log.d(TAG, "beginUpload2: end");
//
//    }



}


