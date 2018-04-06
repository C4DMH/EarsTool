package com.radicalninja.logger;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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
 * Created by gwicks on 2/04/2018.
 */

public class EMAUploadReceiver extends BroadcastReceiver {

    private static final String TAG = "EMAUploadReceiver";
    TransferUtility mTransferUtility;
    Encryption mEncryption;
    Context mContext;
    String encryptedPath;
    static String folder = "/EMA/";

    @Override
    public void onReceive(Context context, Intent intent) {

        mContext = context;
        mEncryption = new Encryption();
        mTransferUtility = Util.getTransferUtility(mContext);
        Log.d(TAG, "onReceive: we have received the broadcast recveiver for EMA Upload");

        EMAUpload();


//        Calendar c = Calendar.getInstance();
//
//        SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy_HHmmssSSS");
//        String formattedDate = df.format(c.getTime());
//
//        String path = mContext.getExternalFilesDir(null) + "/Sensors/";
//
//        File directory = new File(path);
//
//        if(!directory.exists()){
//            directory.mkdirs();
//        }
//
//        ArrayList<File> files = new ArrayList<>(Arrays.asList(directory.listFiles()));
//        int i = 1;
//        for(File each : files){
//
//            Log.d(TAG, "onReceive: path = " + each.getAbsolutePath());
//            Encrypt(formattedDate + "_" + i, each.getAbsolutePath());
//            i = i + 1;
//            Log.d(TAG, "onReceive: i is: " + i);
//            try{
//                each.delete();
//            }catch (Exception e){
//                Log.d(TAG, "onReceive: error deleting: " + e);
//            }
//        }
//        ArrayList<File> encryptedFiles = new ArrayList<>(Arrays.asList(directory.listFiles()));
//        Util.uploadFilesToBucket(encryptedFiles, true,logUploadCallback, mContext, folder);

    }

    public void EMAUpload(){

        String finalPath = folder;

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy_HHmmssSSS");
        String formattedDate = df.format(c.getTime());

        String path = mContext.getExternalFilesDir(null) + finalPath;

        File directory = new File(path);

        if(!directory.exists()){
            directory.mkdirs();
        }

        ArrayList<File> files = new ArrayList<>(Arrays.asList(directory.listFiles()));
        int i = 1;
        for(File each : files){

            Log.d(TAG, "onReceive: path = " + each.getAbsolutePath());
            Encrypt(formattedDate + "_" + i, each.getAbsolutePath());
            i = i + 1;
            Log.d(TAG, "onReceive: i is: " + i);
            try{
                each.delete();
            }catch (Exception e){
                Log.d(TAG, "onReceive: error deleting: " + e);
            }
        }
        ArrayList<File> encryptedFiles = new ArrayList<>(Arrays.asList(directory.listFiles()));
        Util.uploadFilesToBucket(encryptedFiles, true,logUploadCallback, mContext, finalPath);


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
            path2 = mEncryption.encrypt(mFileName, mFilePath, "/EMA/");
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