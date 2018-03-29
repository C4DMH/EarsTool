/*
 * Copyright (C)EARSTool
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Created by gwicks on 24/10/2017.
 */




package com.radicalninja.logger;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings.Secure;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.sevencupsoftea.ears.R;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

import javax.crypto.NoSuchPaddingException;

//import com.menny.android.anysoftkeyboard.R;


/* Class for the recording and uploading of the Video Diary. No longer used in the main tool
    Both the encryption and upload are handled by AsyncTask's
* */



public class VideoActivity extends AppCompatActivity  {


    private static final int VIDEO_CAPTURE = 101;
    public static String UserID;
    public final String TAG = "Encrypt";
    public String finalPath;
    public ProgressBar progressBar;
    public String theCurrentDate;
    public Encryption encryption;
    Long tsLong = System.currentTimeMillis();
    String timeStamp = tsLong.toString();
    String directoryName = "/videoDIARY/";
    Uri videoUri;
    private PopupWindow popupWindow;
    private TransferUtility transferUtility;

    public static String getSecureId(Context context) {
        String android_id = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
        return android_id;
    }

    public static String convertDate(String dateInMilliseconds, String dateFormat) {
        return DateFormat.format(dateFormat, Long.parseLong(dateInMilliseconds)).toString();
    }

    public static void deleteF(String pathToFile) throws IOException {
        File file = new File(pathToFile);
        if (file.delete() == false) {
            throw new IOException();
        }
    }



    public void setTheDate() {
        Calendar cal = Calendar.getInstance();
        long when = cal.getTimeInMillis();
        String timey = Long.toString(when);
        String theTime = convertDate(timey, "dd-MM-yyyy hh-mm-ss");
        theCurrentDate = theTime;
        System.out.println("The time changed into nice format is: " + theTime);
    }

    @TargetApi(19)

    void showDialog2() {

        MyDialogFragmentThree myDialogFragment = new MyDialogFragmentThree();
        myDialogFragment.show(getFragmentManager(), "WARNING");


    }
    @TargetApi(19)
    public void showDialog3(View v) {

        File fileCheck = new File(this.getExternalFilesDir(null) + "/videoDIARY/" + "CrashReport.txt");

        if (fileCheck.exists()) {

            Log.d(TAG, "showDialog3: Sending crash report to cloud");
            TransferObserver obvserver = transferUtility.upload(Constants.BUCKET_NAME, UserID + "/Crashreport.txt",
                    fileCheck);
        }
        Log.d("History", "In show Dialog3");
        //new ViewWeekStepCountTask().execute();
        MyDialogFragment myDialogFragment = new MyDialogFragment();
        myDialogFragment.show(getFragmentManager(), "INTO");
    }


    public boolean createDirectory(String path) {
        File mydir = new File(this.getExternalFilesDir(null).toString() + path);
        if (!mydir.exists()) {
            mydir.mkdirs();
        } else {
            Log.d("error", "dir already exisits");
        }
        return true;

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getActionBar().setTitle("Video Diary");
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(this));

        Log.d(TAG, "onCreate: ********************************** transfer Utility = " + transferUtility);
//
        getSupportActionBar().setTitle("EASE TOOL");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        UserID = getSecureId(this);
        setContentView(R.layout.activity_video);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setProgress(0);
        progressBar.setVisibility(View.GONE);

        setTheDate();

        transferUtility = Util.getTransferUtility(this);

        //showDialog2();
        encryption = new Encryption();

    }

    @Override
        protected void onResume() {

        super.onResume();

        Log.d(TAG, "onResume: before transfer utility");
        if(transferUtility == null){
            transferUtility = Util.getTransferUtility(this);

        }

        Log.d(TAG, "onResume: before encryption");
        if(encryption == null){
            encryption = new Encryption();
        }
        Log.d(TAG, "onResume: after encryption");
    }


    // this method is called from MyDialogFragment.
    // It starts the recorder, from the Instructions Fragment

    public void onRecordVideo(View v) {

        Log.d("VideoActivity", "1");

        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            Log.d("VideoActivity", "2");
            intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
            intent.putExtra("android.intent.extra.quickCapture", true);
            Log.d("VideoActivity", "3");
            createDirectory(directoryName);
            Log.d("VideoActivity", "4");
            File mediaFile = new File(this.getExternalFilesDir(null) + directoryName + timeStamp + ".mp4");
            Log.d("VideoActivity", "5");
            String newPath = this.getExternalFilesDir(null) + directoryName + timeStamp + ".mp4";
            Log.d("VideoActivity", "6");
            Log.d("VideoActivity", "This the the Video Uri in the on RECROD VIEW method using the newPath Variable: " + newPath);
            Log.d("VideoActivity", "This the the Video Uri in the on RECROD VIEW method using the mediafile Variable: " + mediaFile);

            finalPath = newPath;
            Log.d("VideoActivity", "7");
            videoUri = Uri.fromFile(mediaFile);
            Log.d("VideoActivity", "This the the Video Uri in the on RECROD VIEW method: " + videoUri);


            intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
            //intent.putExtra(MediaStore.EXTRA_OUTPUT, newPath);
            //intent.putExtra("STRING_I_NEED", newPath);
            Log.d("VideoActivity", "8");

            //intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
            startActivityForResult(intent, VIDEO_CAPTURE);
            //intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
        } else {
            Toast.makeText(this, "No camera on device", Toast.LENGTH_LONG).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VIDEO_CAPTURE) {
            if (resultCode == RESULT_OK) {

                showDialog2();

                new encryptAsyncTask().execute(finalPath);

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Video recording cancelled.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Failed to record video", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        startService(new Intent(this, MainActivity.class));
    }

    /* Async encryption class. Encrypted the file, then calls another Async Task to upload the video file to AWS.*/

    public class encryptAsyncTask extends AsyncTask<String, Void, String> {

        String idAndDate = UserID + "_" + theCurrentDate;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(VideoActivity.this, "File is encrypting...... ", Toast.LENGTH_LONG).show();
            Toast.makeText(VideoActivity.this, "File is encrypting...... ", Toast.LENGTH_LONG).show();


        }


        @Override
        protected String doInBackground(String... params) {
            String path = null;

            try {
                com.anysoftkeyboard.utils.Log.d("Video", "We are starting encrytopn 1 - in doInBackgound AsyncTask ENCRYTPTION!");
                path = encryption.encrypt(idAndDate, params[0],"/videoDIARY/");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            }
            //Toast.makeText(VideoActivity.this, "File has been sucessfully uploaded! ", Toast.LENGTH_LONG).show();

            return path;
        }


        @Override
        protected void onPostExecute(String path) {
            new uploadAsyncTask().execute(path);
            try {
                VideoActivity.deleteF(finalPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class uploadAsyncTask extends AsyncTask<String, Integer, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            Toast.makeText(VideoActivity.this, "File is uploading..... ", Toast.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(String... params) {
            com.anysoftkeyboard.utils.Log.d("Video", "We are starting eupload - In uploadtask");
            String filePath = params[0];
            // TRY NEW WAY OF GETTING PROGRESS BAR!!
            Log.d("Video", "started upload in beginupload");

            if (filePath == null) {
                Log.d("Video", "Could not find the filepath of the selected file");
                //return;
            }
            setTheDate();
            String newFilePath = UserID + "/" + theCurrentDate;
            Log.d("uploading, using: " + newFilePath, "");

            File file = new File(filePath);

            //Util.uploadFileToBucket(file, "new name", true,);


            try {

                final TransferObserver observer = transferUtility.upload(Constants.BUCKET_NAME, newFilePath,
                        file);

                Log.d("Progess", "The state of the observer1 is: " + observer.getState());


                /*
                * Transfer Listener implemented here to try and work out why AWS will randomly drop large file transfers
                * -> Anything bigger than 200MB will randomly return finished before it is actually finished. Still no sure why*/
                // TODO : Work out why AWS upload randomly drops large uploads


                observer.setTransferListener(new TransferListener() {

                    int identity = observer.getId();
                    @Override
                    public void onStateChanged(int id, TransferState state)
                    {
                        //observer.getState();
                        Log.d("Progess", "The state of the observer is: " + observer.getState());
                    switch (state) {
                            case WAITING:
                                break;
                            case IN_PROGRESS:
                                com.anysoftkeyboard.utils.Log.d(TAG, String.format("Transfer ID %d has begun", id));

                                break;
                            case PAUSED:
                                Log.d(TAG, "onStateChanged: ***********PAUSED");
                                break;
                            case RESUMED_WAITING:
                                Log.d(TAG, "onStateChanged: RESUME_WAITING**************************");
                                break;
                            case COMPLETED:
                                Log.d(TAG, "onStateChanged: **************completed ");
                                break;
                            case CANCELED:
                                com.anysoftkeyboard.utils.Log.d(TAG, String.format("Transfer ID %d has been cancelled", id));
                                Log.d(TAG, "onStateChanged: ****************CANCELD");
                                break;
                            case WAITING_FOR_NETWORK:
                                Log.d(TAG, "onStateChanged: **************waiting for networkd");
                                break;
                            case FAILED:
                                Log.d(TAG, "onStateChanged: ***************FAILED");
                                break;
                            case PART_COMPLETED:
                                Log.d(TAG, "onStateChanged: PARTCM***************************");
                                break;
                            case PENDING_CANCEL:
                                Log.d(TAG, "onStateChanged: ****************************PC");
                                break;
                            case PENDING_PAUSE:
                                Log.d(TAG, "onStateChanged: *****************************pp");
                                break;
                            case PENDING_NETWORK_DISCONNECT:
                                Log.d(TAG, "onStateChanged: *****************************PND");
                                break;
                            case UNKNOWN:
                                Log.d(TAG, "onStateChanged: **********************unkown");
                                break;
                            default:
                                break;
                        }
                    }

                    // Progress bar so user can see how much of the video file is uploaded

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                        double percentage = ((double) bytesCurrent / bytesTotal);
                        int one = (int) (percentage * 100);

                        Log.d("Progress", "Progress in upload is: " + percentage + "and identity is: " + identity);
                        Log.d("Progress", "Progress in upload for longOne is: " + one);
                        Log.d("Progess", "The state of the observer is: " + observer.getState());

                        int percentageInt = (int) (percentage);
                        publishProgress(one);
                        //progressBar.setProgress(one);

                        if (one == 100) {
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                    @Override
                    public void onError(int id, Exception ex) {
                        //String st = String.valueOf(ex.getStackTrace());
                        Log.d("Progess", "The state of the observer is: *****************************ERROR*************************************** " + observer.getState());
                        Log.d(TAG, "onError: the error was" +ex.getMessage());
                        Log.d(TAG, "onError: " + Log.getStackTraceString(ex));
                        Log.d(TAG, "onError: " + ex.toString());

                        //transferUtility.resume(identity);
                        //observer.get
                    }

                });
            } catch (AmazonS3Exception s3Exception) {
                Log.d("aws", "error contacting amazon");
            }

            Log.d("Video", "finshed upload in beginupload");
            return null;
        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values[0]);
            Log.d("Progress", "Progress = " + values[0]);

            progressBar.setProgress(values[0]);
            //progressBar.setProgress(100);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            com.anysoftkeyboard.utils.Log.d("Video", "We have finished upload - In onPostExecute" + " ");

        }

    }





}