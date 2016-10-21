package com.radicalninja.logger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;

/**
 * Created by gwicks on 15/10/2016.
 */
public class GoogleFitUploadTask extends BroadcastReceiver{
    String directoryName = "/videoDIARY/";
    public static String UserID;
    public String theCurrentDate;
    private TransferUtility transferUtility;
    private String path;



//
//
//    public static String getSecureId() {
//        String android_id = Settings.Secure.getString(AnyApplication.getInstance().getContentResolver(), Settings.Secure.ANDROID_ID);
//        return android_id;
//    }




    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("Fit", "Start of startGoogleFItUplaodTask");



        Intent i = new Intent();
        i.setClassName("com.radicalninja.logger","com.radicalninja.logger.GoogleFit" );
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);

//        UserID = getSecureId();
//        setTheDate();

//
//        beginUpload(path);
    }

//    private void beginUpload(String filePath) {
//        if (filePath == null) {
//            //Toast.makeText(this, "Could not find the filepath of the selected file",Toast.LENGTH_LONG).show();
//            return;
//        }
//        String newFilePath = UserID +"/" +  theCurrentDate;
//        //Toast.makeText(this, "The file is uploading, using the name: " + newFilePath,Toast.LENGTH_LONG).show();
//        Log.d("uploading, using: " + newFilePath, "");
//        File file = new File(filePath);
//        TransferObserver observer = transferUtility.upload(Constants.BUCKET_NAME, newFilePath,
//                file);
//        /*
//         * Note that usually we set the transfer listener after initializing the
//         * transfer. However it isn't required in this sample app. The flow is
//         * click upload button -> start an activity for image selection
//         * startActivityForResult -> onActivityResult -> beginUpload -> onResume
//         * -> set listeners to in progress transfers.
//         */
//        // observer.setTransferListener(new UploadListener());
//    }
//
//    public static String convertDate(String dateInMilliseconds, String dateFormat) {
//        return DateFormat.format(dateFormat, Long.parseLong(dateInMilliseconds)).toString();
//    }
//
//    public void setTheDate()
//    {
//        Calendar cal = Calendar.getInstance();
//        long when = cal.getTimeInMillis();
//        String timey = Long.toString(when);
//        String theTime = convertDate(timey, "dd-MM-yyyy hh:mm:ss");
//        theCurrentDate = theTime;
//        System.out.println("The time changed into nice format is: " + theTime);
//    }





}
