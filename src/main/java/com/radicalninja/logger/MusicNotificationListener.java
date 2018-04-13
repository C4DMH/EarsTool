package com.radicalninja.logger;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by gwicks on 13/06/2017.
 */

public class MusicNotificationListener extends NotificationListenerService {
    private static final String TAG = "MusicNotificationListen";

    Context mContext;
    String prevTitle;
    String currentTitle;

    @Override

    public void onCreate(){
        super.onCreate();
        mContext = getApplicationContext();
    }


    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        String pack = sbn.getPackageName();
        String ticker ="";
        if(sbn.getNotification().tickerText !=null) {
            ticker = sbn.getNotification().tickerText.toString();
        }
        Bundle extras = sbn.getNotification().extras;
        String title = "";
        try {
            //title = extras.getString("android.title");
            title = extras.getCharSequence("android.title").toString();

        }
        catch(NullPointerException e){
            e.printStackTrace();
        }

        String text = "";


        Calendar c = Calendar.getInstance();
        //System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("hh:mm:ss");
        String formattedTime = df.format(c.getTime());

        SimpleDateFormat df2 = new SimpleDateFormat("yyyy:MM:dd");
        String currentDate = df2.format(c.getTime());
        Log.d(TAG, "onNotificationPosted: current date is: " + currentDate);

        //String path = Environment.getExternalStorageDirectory() + "/videoDIARY/Music/";
        String path = mContext.getExternalFilesDir(null) + "/videoDIARY/Music/";


        Log.d(TAG, "onStartJob: path is: " + path);

        File directory = new File(path);
        if(!directory.exists()){
            Log.d(TAG, "onStartJob: making directory");
            directory.mkdirs();
        }

        File location = new File(directory, currentDate +".txt");





        try {
            text = extras.getCharSequence("android.text").toString();
        }
        catch(NullPointerException e){
            e.printStackTrace();
        }

        //int id1 = extras.getInt(Notification.EXTRA_SMALL_ICON);
        //Bitmap id = sbn.getNotification().largeIcon;

        Log.d(TAG, "onNotificationPosted: Package: " + pack);
        //Log.d(TAG, "onNotificationPosted: Ticker" + ticker  );
        //Log.d(TAG, "onNotificationPosted: Title " + title);
        //Log.d(TAG, "onNotificationPosted: text: " + text);

        currentTitle = title;

        Log.d(TAG, "onNotificationPosted: Previous = " + prevTitle + "current title = " + currentTitle);

        if(pack.contains("music") && (!currentTitle.equals(prevTitle))){

            Log.d(TAG, "onNotificationPosted: PREV = " + prevTitle);
            Log.d(TAG, "onNotificationPosted:  CURRENT = " + currentTitle);


            //Log.d(TAG, "onNotificationPosted: " + pack + " is a music package!!!!!!!!");
            //File location = new File(directory, currentDate +".txt");
            writeToFile(location, "Time: " + formattedTime + "\nPackage: " + pack + "\nTitle: " + title + "\nText: " + text + "\n\n");
            prevTitle = currentTitle;
        }


//        Log.i("Package",pack);
//        Log.i("Ticker",ticker);
//        Log.i("Title",title);
//        Log.i("Text",text);

//        Intent msgrcv = new Intent("Msg");
//        msgrcv.putExtra("package", pack);
//        msgrcv.putExtra("ticker", ticker);
//        msgrcv.putExtra("title", title);
//        msgrcv.putExtra("text", text);
//        if(id != null) {
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//            id.compress(Bitmap.CompressFormat.PNG, 100, stream);
//            byte[] byteArray = stream.toByteArray();
//            msgrcv.putExtra("icon",byteArray);
//        }
//        LocalBroadcastManager.getInstance(mContext).sendBroadcast(msgrcv);

    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
    }


    private static void writeToFile(File file, String data) {

        FileOutputStream stream = null;
        System.out.println("The state of the media is: " + Environment.getExternalStorageState());
        Log.d(TAG, "writeToFile: file location is:" + file.getAbsolutePath());

        //OutputStreamWriter stream = new OutputStreamWriter(openFileOutput(file), Context.MODE_APPEND);
        try {
            //Log.e("History", "In try");
            //Log.d(TAG, "writeToFile: ");
            stream = new FileOutputStream(file, true);
            //Log.d(TAG, "writeToFile: 2");
            stream.write(data.getBytes());
            //Log.d(TAG, "writeToFile: 3");
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



}