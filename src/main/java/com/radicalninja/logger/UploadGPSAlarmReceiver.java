package com.radicalninja.logger;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import javax.crypto.NoSuchPaddingException;

/**
 * Created by gwicks on 26/05/2017.
 */

public class UploadGPSAlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "MicRecordUploadAlarm";

    TransferUtility mTransferUtility;
    Encryption mEncryption;
    Context mContext;
    String encryptedPath;
    static String folder = "/GPS/";

    @Override
    public void onReceive(Context context, Intent intent) {

        //mContext = MainActivity.instance; - Changed 1st June 2017
        Log.d(TAG, "onReceive: ");

        mContext = context;
        mEncryption = new Encryption();
        mTransferUtility = Util.getTransferUtility(mContext);

        Calendar c = Calendar.getInstance();
        //System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy");
        String formattedDate = df.format(c.getTime());



        String path = Environment.getExternalStorageDirectory() + "/videoDIARY/Location/";

        File directory = new File(path);

        //String encryptedPath = Encrypt("MicRecord_" +formattedDate, path );
        //beginUpload2("MicRecord_" +formattedDate, encryptedPath);


        //File[] files = directory.listFiles();

        //Change 9th June 2017



        ArrayList<File> files = new ArrayList<>(Arrays.asList(directory.listFiles()));
        int i = 1;
        for(File each : files){

            Log.d(TAG, "onReceive: path = " + each.getAbsolutePath());
            Encrypt("GPS_ " + formattedDate + "_" + i, each.getAbsolutePath());
            i = i + 1;
            Log.d(TAG, "onReceive: i is: " + i);
            try{
                each.delete();
            }catch (Exception e){
                Log.d(TAG, "onReceive: error deleting: " + e);
            }

        }

        ArrayList<File> encryptedFiles = new ArrayList<>(Arrays.asList(directory.listFiles()));

        // END 9th JUne change


        Util.uploadFilesToBucket(encryptedFiles, true,logUploadCallback, mContext, folder);

        //DELETING FILE BEFORE UPLOAD COMPLETE!!!!

//        for(File each : encryptedFiles){
//            Log.d(TAG, "onReceive: encrypted file to be uploaded: " + each.getAbsolutePath());
//            beginUpload2("MicRecord_" +formattedDate, each.getAbsolutePath());
//            try{
//                each.delete();
//            }catch (Exception e){
//                Log.d(TAG, "onReceive: error deleting: " + e);
//            }
//        }


    }


    public String Encrypt(String name, String path){
        Log.d(TAG, "Encrypt: 1");
        Log.d(TAG, "Encrypt: name = " + name);
        Log.d(TAG, "Encrypt: path = " + path);
        String mFileName = name;
        Log.d(TAG, "Encrypt: mFIleName is: " + mFileName);
        String mFilePath = path;

        String path2 = null;
        try {
            //com.anysoftkeyboard.utils.Log.d(TAG, "We are starting encrytopn 1 - in doInBackgound AsyncTask ENCRYTPTION!");
            path2 = mEncryption.encrypt(mFileName, mFilePath, "/videoDIARY/Location/");
            Log.d(TAG, "Encrypt: the path me get is: " + path2);
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
        Log.d(TAG, "Encrypt: 2");
        Log.d(TAG, "Encrypt: path2 is: " + path2);
        //beginUpload2("STATS", path2);
        return path2;



    }

    private void beginUpload2(String name, String filePath) {
        Log.d(TAG, "beginUpload2: start of beginupload2");
        Log.d(TAG, "beginUpload2: the filepath is: " + filePath);
        if (filePath == null) {
            //Toast.makeText(this, "Could not find the filepath of the selected file", Toast.LENGTH_LONG).show();
            Log.d(TAG, "beginUpload2: no file path found");
            return;
        }


        Log.d(TAG, "beginUpload2: middle");

        File file = new File(filePath);
        Log.d(TAG, "beginUpload2: after new file");
        //TransferObserver observer = transferUtility.upload(Constants.BUCKET_NAME, name,
        mTransferUtility.upload(Constants.BUCKET_NAME, name,
                file);
        Log.d(TAG, "beginUpload2: end");

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