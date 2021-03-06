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
 * Created by gwicks on 23/05/2017.
 * Class to record the user's MIC during a phone call
 * ************* WARNING ************************
 * Despite only recording the microphone and not the earpiece, the other party on the call
 * can be heard faintly. This is illegal in many States, Countries and Jurisdictions
 */

public class IncomingCallListener extends BroadcastReceiver
{
    private Context mContext;
    static Boolean onCall = false;
    static MediaRecorder mediaRecorder;
    private static final String TAG = "CustomBroadcastReceiver";
    TelephonyManager telephony;
    CustomPhoneStateListener customPhoneListener ;
    //Context mContext;
    @Override
    public void onReceive(Context context, Intent intent)
    {

        Log.d(TAG, "onReceive: Incoming call listener1");
        mContext = context;
        Bundle extras = intent.getExtras();
        if (extras != null) {
            String state = extras.getString(TelephonyManager.EXTRA_STATE);
            Log.w("DEBUG", state);

            telephony = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
            customPhoneListener = new   CustomPhoneStateListener();
            telephony.listen(customPhoneListener,   PhoneStateListener.LISTEN_CALL_STATE);
            Bundle bundle = intent.getExtras();
            String phoneNr= bundle.getString("incoming_number");
            Log.d(TAG, "onReceive: incoming number is: " + phoneNr);



        }


    }

    /*
    * Method starts recording to file if:
     * a) User makes an outgoing call. Note this will record the ringing time before other party picks up
     * b) User receives an incoming call,
    */
    public class CustomPhoneStateListener extends PhoneStateListener
    {
        private static final String TAG = "CustomPhoneListener";
        Handler handler=new Handler();



        File dir;
        File f1;




        @Override
        public void onCallStateChanged(int state, String incomingNumber)
        {
            Log.d(TAG, "onCallStateChanged: onCall = " + onCall);
            switch (state)
            {
                case TelephonyManager.CALL_STATE_RINGING:
                    if(!incomingNumber.equalsIgnoreCase(""))
                    {
                        //YOUR CODE HERE
                        Log.d(TAG, "onCallStateChanged1: ringing");

                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    //YOUR CODE HERE
                    Log.d(TAG, "onCallStateChanged1: on call");
                    onCall = true;
                    Log.d(TAG, "onCallStateChanged1: onCall = " + onCall);
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

                    Log.d(TAG, "onCallStateChanged1: setting up mediarecorder");

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
                        Log.d(TAG, "onCallStateChanged1: in try to prepare media recorder");
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
                        Log.d(TAG, "onCallStateChanged1: in try to start media recorder");
                        mediaRecorder.start();
                    } catch (Exception e) {
                        System.out.println("The stack trace is: ");
                        e.printStackTrace();
                    }



                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    //YOUR CODE HERE
                    Log.d(TAG, "onCallStateChanged1: onCall = " + onCall);
                    Log.d(TAG, "onCallStateChanged1: IDle");
                    if(onCall == true){
                        Log.d(TAG, "onCallStateChanged1: oncall is true, recorder should be stopping!");

                        try{
                            Log.d(TAG, "onCallStateChanged1: stopping recorder!");
                            mediaRecorder.stop();
                        }catch(RuntimeException e){
                            Log.d(TAG, "onReceive: could not stop recorder!");
                        }finally {
                            Log.d(TAG, "onCallStateChanged1: in finally");
                            mediaRecorder.reset();
                            mediaRecorder.release();
                            mediaRecorder = null;
                            onCall = false;
                        }


                    }


                    break;
                default:
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
            telephony.listen(customPhoneListener, PhoneStateListener.LISTEN_NONE);
        }


    }
}
