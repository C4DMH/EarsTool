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
import java.util.Date;

import javax.crypto.NoSuchPaddingException;

import static java.lang.System.exit;

/**
 * Created by gwicks on 15/06/2017.
 */

public class PhotoUploadReceiver extends BroadcastReceiver {


    private static final String TAG = "PhotoUploadReceiver";

    TransferUtility mTransferUtility;
    Encryption mEncryption;
    Context mContext;
    String encryptedPath;
    static String folder = "/Photos/";

    @Override
    public void onReceive(Context context, Intent intent) {

        //mContext = MainActivity.instance; - changed 1st June 2017
        mContext = context;
        mEncryption = new Encryption();
        mTransferUtility = Util.getTransferUtility(mContext);

        Calendar c = Calendar.getInstance();
        //System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy_HHmmssSSS");
        String formattedDate = df.format(c.getTime());

        String CameraFolder="Camera";
        String CameraD = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/Camera/";
        File CameraDirectory = new File(CameraD);
        Long currentTime = System.currentTimeMillis();
        int dayTime = 24*60*60*1000;
        Long finalTime = currentTime - dayTime;
        Log.d(TAG, "getPhotos: the current time is: " + currentTime);
        //File CameraDirectory = (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM));

        ArrayList<File> photos = new ArrayList<>();



        Log.d(TAG, "getPhotos: the cameraDirectory is: " + CameraDirectory.toString());
        File[] files = CameraDirectory.listFiles();
        if(!CameraDirectory.isDirectory()){
            Log.d(TAG, "getPhotos: not a directory!! ");
            exit(0);
        }

        if(CameraDirectory.isDirectory()){
            Log.d(TAG, "getPhotos: directory!! ");

        }

        if(files == null){
            Log.d(TAG, "getPhotos: NULLLLLLL");
        }

        Log.d(TAG, "getPhotos: size of array is: " + files.length);

        for (File CurFile : files) {
            Log.d(TAG, "getPhotos: the current file is: " + CurFile);
            Log.d(TAG, "getPhotos: MODIFIED: " + CurFile.lastModified());
            Log.d(TAG, "getPhotos: HTE NAME IS: " + CurFile.getName());
            Date d = new Date(CurFile.lastModified());
            Log.d(TAG, "getPhotos: MODIFIED NUMBER 2: " + d);
            if(CurFile.lastModified() > finalTime){
                Log.d(TAG, "***************************getPhotos: The phtot: " + CurFile + " was taken in the last 24 hours");
                photos.add(CurFile);
            }
            if (CurFile.isDirectory()) {
                Log.d(TAG, "getPhotos: is a directory");
                //CameraDirectory=CurFile.getName();
                break;
            }
        }



//        String path = Environment.getExternalStorageDirectory() + "/videoDIARY/Photos/";
//
        //File directory = new File(CameraD);

        //String encryptedPath = Encrypt("MicRecord_" +formattedDate, path );
        //beginUpload2("MicRecord_" +formattedDate, encryptedPath);


        int i = 1;
        for(File each : photos){

            String filename=each.getName();

            Log.d(TAG, "onReceive: path = " + each.getAbsolutePath());
            //String finalPath = Encrypt(filename, each.getAbsolutePath());
            Encrypt(filename, each.getAbsolutePath());
            //File encryptedFile = new File(finalPath);
//            encryptedPhotos.add(encryptedFile);
//            i = i + 1;
//            Log.d(TAG, "onReceive: i is: " + i);
//            try{
//                each.delete();
//            }catch (Exception e){
//                Log.d(TAG, "onReceive: error deleting: " + e);
//            }

        }

        String path = Environment.getExternalStorageDirectory() + "/videoDIARY/Photos/";

        File directory = new File(path);


        if(!directory.exists()){
            directory.mkdir();
        }



        //File[] files = directory.listFiles();
        ArrayList<File> encryptedPhotos = new ArrayList<>(Arrays.asList(directory.listFiles()));

        //ArrayList<File> encryptedFiles = new ArrayList<>(Arrays.asList(directory.listFiles()));


        Util.uploadFilesToBucket(encryptedPhotos, true,logUploadCallback, mContext, folder);

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
            path2 = mEncryption.encrypt(mFileName, mFilePath, "/videoDIARY/Photos/");
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

//    private void beginUpload2(String name, String filePath) {
//        Log.d(TAG, "beginUpload2: start of beginupload2");
//        Log.d(TAG, "beginUpload2: the filepath is: " + filePath);
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
//        Log.d(TAG, "beginUpload2: after new file");
//        //TransferObserver observer = transferUtility.upload(Constants.BUCKET_NAME, name,
//        mTransferUtility.upload(Constants.BUCKET_NAME, name,
//                file);
//        Log.d(TAG, "beginUpload2: end");
//
//    }

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
