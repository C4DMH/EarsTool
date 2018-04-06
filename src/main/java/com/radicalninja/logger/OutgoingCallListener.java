package com.radicalninja.logger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by gwicks on 2/04/2018.
 */

public class OutgoingCallListener extends BroadcastReceiver {

    private static final String TAG = "OutgoingCallListener";
    private static Boolean onCall = false;
    static MediaRecorder mediaRecorder;
    private Context mContext;
    TelephonyManager telephony;
    CustomPhoneStateListener customPhoneListener;
    
    @Override
    public void onReceive(Context context, Intent intent) {

        mContext = context;
        
        if(intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)){
            Log.d(TAG, "onReceive: this is after checking ACTION");
        }

        Bundle extras = intent.getExtras();
        if (extras != null) {
            String state = extras.getString(TelephonyManager.EXTRA_STATE);
            Log.d(TAG, "onReceive: the state is : " + state);
            for (String key : extras.keySet()) {
                Object value = extras.get(key);
                Log.d(TAG, String.format("%s %s (%s)", key,
                        value.toString(), value.getClass().getName()));
            }
        }
        telephony = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        customPhoneListener = new CustomPhoneStateListener();
        telephony.listen(customPhoneListener,   PhoneStateListener.LISTEN_CALL_STATE);

        Log.d(TAG, "onReceive: We have detected an outgoing call!");
        
    }

    public class CustomPhoneStateListener extends PhoneStateListener
    {
        private static final String TAG = "CustomPhoneListener2";
        Handler handler=new Handler();


       

        File dir;
        File f1;




        @Override
        public void onCallStateChanged(int state, String incomingNumber)
        {
            Log.d(TAG, "onCallStateChanged: entering on call state changed in customPhoneStateListener");
            Log.d(TAG, "onCallStateChanged: onCall = " + onCall);
            Log.d(TAG, "onCallStateChanged: the state is: " + state);
            switch (state)
            {
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.d(TAG, "onCallStateChanged: call state ringing");
                    if(!incomingNumber.equalsIgnoreCase(""))
                    {
                        //YOUR CODE HERE
                        Log.d(TAG, "onCallStateChanged: ringing");

                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.d(TAG, "onCallStateChanged: call state offhook");
                    //YOUR CODE HERE
                    Log.d(TAG, "onCallStateChanged: on call");
                    onCall = true;
                    Log.d(TAG, "onCallStateChanged: onCall = " + onCall);
                    mediaRecorder = new MediaRecorder();
                    //String path = Environment.getExternalStorageDirectory() + "/" + mContext.getString(R.string.app_name);
                    String path = mContext.getExternalFilesDir(null) + "/videoDIARY/MicRecord/";
                    dir = new File(path);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }

                    try {
                        f1 = File.createTempFile("Sound", ".aac", dir);
                        //f1 = new File(path + "/sound");

                        System.out.println("Path to file is: + " + f1.getAbsolutePath());
                    } catch (Exception e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }

                    Log.d(TAG, "onCallStateChanged: setting up mediarecorder");

                    // Various call recording options. Bitrate and Sample rate increasing will increase the quality of the call, but also
                    // increase the size of the files significantly.

                    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                    mediaRecorder.setAudioChannels(1);
                    mediaRecorder.setAudioSamplingRate(8000);
                    mediaRecorder.setAudioEncodingBitRate(64000);
                    //mediaRecorder.s
                    mediaRecorder.setOutputFile(f1.getAbsolutePath());
                    try {
                        Log.d(TAG, "onCallStateChanged: in try to prepare media recorder");
                        mediaRecorder.prepare();
                    } catch (IllegalStateException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    //mediaRecorder.start();


                    try {
                        Log.d(TAG, "onCallStateChanged: in try to start media recorder");
                        mediaRecorder.start();
                    } catch (Exception e) {
                        System.out.println("The stack trace is: ");
                        e.printStackTrace();
                    }



                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    Log.d(TAG, "onCallStateChanged: call state idle");
                    //YOUR CODE HERE
                    Log.d(TAG, "onCallStateChanged2: onCall = " + onCall);
                    Log.d(TAG, "onCallStateChanged: IDle");
                    if(onCall == true){
                        Log.d(TAG, "onCallStateChanged: oncall is true, recorder should be stopping!");

                        try{
                            Log.d(TAG, "onCallStateChanged: stopping recorder!");
                            mediaRecorder.stop();
                        }catch(RuntimeException e){
                            Log.d(TAG, "onReceive: could not stop recorder!");
                        }finally {
                            Log.d(TAG, "onCallStateChanged: in finally");
                            mediaRecorder.reset();
                            mediaRecorder.release();
                            mediaRecorder = null;
                            onCall = false;
                        }


                    }


                    break;
                
                default:
                    Log.d(TAG, "onCallStateChanged: in default");
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
            telephony.listen(customPhoneListener, PhoneStateListener.LISTEN_NONE);
        }


    }



}
