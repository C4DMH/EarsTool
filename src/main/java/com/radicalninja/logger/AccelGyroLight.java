package com.radicalninja.logger;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;

import java.io.*;
import java.util.List;

/**
 * Created by gwicks on 29/03/2018.
 */

public class AccelGyroLight implements SensorEventListener {

    private static final String TAG = "AccelGyroLight";

    Context mContext;

    private SensorManager sensorManager;
    private Sensor mAccelerometer;
    private Sensor mGyroscope;
    private Sensor mLight;
    String path;
    String path2;

    private static long LAST_TS_ACC = 0;
    private static long LAST_TS_GYRO = 0;
    private static long LAST_SAVE = 0;

    private static PowerManager.WakeLock wakeLock = null;



    StringBuilder accelBuffer;
    StringBuilder gryoBuffer;
    StringBuilder lightBuffer;

    private boolean writingAccelToFile = false;
    private boolean writingGyroToFile = false;
    private boolean writingLightToFile = false;

    float previousLightReading;

    private static Float[] LAST_VALUES_ACC = null;
    private static Float[] LAST_VALUES_GRYO = null;

    double THRESHOLD = 0.01;



    float lightReading = 0;

    long timeStampLight = 0;

    File AccelFile;
    File GyroFile;
    File LightFile;

    public void unregister(){
        sensorManager.unregisterListener(this);
        wakeLock.release();
        Log.d(TAG, "unregister: Unregister sensors called");
        
    }

    public AccelGyroLight(Context context){
        mContext = context;

        Log.d(TAG, "AccelGyroLight: starting accelgyrolight constructor");

        sensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGyroscope =  sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "My Tag");
        wakeLock.acquire();

        path = Environment.getExternalStorageDirectory() +"/VIDEODIARY";
        path2 = (mContext.getExternalFilesDir(null) + "/Sensors");
        Log.d(TAG, "AccelGyroLight:  the path to externalfilesdir is: " + path2);


        File directory = new File(path);
        File directory2 = new File(path2);

        if(!directory.exists()){
            Log.d(TAG, "onCreate: making directory");
            directory.mkdir();
        }

        if(!directory2.exists()){
            Log.d(TAG, "onCreate: making directory");
            directory.mkdir();
        }

        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        for (Sensor sensor : sensors) {
            Log.d("Sensors", "" + sensor.getName());
        }

        accelBuffer = new StringBuilder();
        gryoBuffer = new StringBuilder();
        lightBuffer = new StringBuilder();

        sensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        //sensorManager.registerListener(this, head, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {


        long TS = System.currentTimeMillis();
        //Log.d(TAG, "onSensorChanged: The time stamp check is:  " + TS +" + " + LAST_TS_ACC );

        // Filter to remove readings that come too often
        if (TS < LAST_TS_ACC + 100) {
            Log.d(TAG, "onSensorChanged: skipping");
            return;
        }

        if(LAST_VALUES_ACC != null && Math.abs(event.values[0] - LAST_VALUES_ACC[0]) < THRESHOLD
                && Math.abs(event.values[1] - LAST_VALUES_ACC[1]) < THRESHOLD
                && Math.abs(event.values[2] - LAST_VALUES_ACC[2]) < THRESHOLD) {
            return;
        }

        LAST_VALUES_ACC = new Float[]{event.values[0], event.values[1], event.values[2]};

        LAST_TS_ACC = System.currentTimeMillis();

        accelBuffer.append(LAST_TS_ACC + "," + event.values[0] + "," + event.values[1] + "," + event.values[2] + "\n");
        //Log.d(TAG, "onSensorChanged: \n the buffer length is: " + accelBuffer.length());
        //Log.d(TAG, "onSensorChanged: the buffer is: " + accelBuffer.toString());
        if((accelBuffer.length() > 500000) && (writingAccelToFile == false) ){
            writingAccelToFile = true;

            AccelFile = new File(path2 +"/Acc/"  + LAST_TS_ACC +".txt");
            Log.d(TAG, "onSensorChanged: accelfile created at : " + AccelFile.getPath());

            File parent = AccelFile.getParentFile();
            if(!parent.exists() && !parent.mkdirs()){
                throw new IllegalStateException("Couldn't create directory: " + parent);
            }

            //Try threading to take of UI thread

            new Thread(new Runnable() {
                @Override
                public void run() {
                    //Log.d(TAG, "onSensorChanged: in accelbuffer");
                   // Log.d(TAG, "run: in runnable");
                    //writeToStream(accelBuffer);
                    writeStringBuilderToFile(AccelFile, accelBuffer);
                    accelBuffer.setLength(0);
                    writingAccelToFile = false;

                }
            }).start();

        }
    }

    else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE){
        //Log.d(TAG, "onSensorChanged: 2");

        long TS = System.currentTimeMillis();
        if (TS < LAST_TS_GYRO + 100) {
            Log.d(TAG, "onSensorChanged: skipping");
            return;
        }
        // Filter to remove readings that have too small a change from previous reading.
        if(LAST_VALUES_GRYO != null && Math.abs(event.values[0] - LAST_VALUES_GRYO[0]) < THRESHOLD
                && Math.abs(event.values[1] - LAST_VALUES_GRYO[1]) < THRESHOLD
                && Math.abs(event.values[2] - LAST_VALUES_GRYO[2]) < THRESHOLD) {
            return;
        }

        LAST_VALUES_GRYO = new Float[]{event.values[0], event.values[1], event.values[2]};


        LAST_TS_GYRO = System.currentTimeMillis();


        gryoBuffer.append(LAST_TS_GYRO + "," + event.values[0] + "," + event.values[1] + "," + event.values[2] + "\n");
        //Log.d(TAG, "onSensorChanged: \n the buffer length is: " + gryoBuffer.length());
        //Log.d(TAG, "onSensorChanged: the buffer is: " + accelBuffer.toString());
        if((gryoBuffer.length() > 500000) && (writingGyroToFile == false) ){
            writingGyroToFile = true;

            GyroFile = new File(path2 +"/Gyro/"  + LAST_TS_GYRO +".txt");
            Log.d(TAG, "onSensorChanged: file created at: "+ GyroFile.getPath());

            File parent = GyroFile.getParentFile();
            if(!parent.exists() && !parent.mkdirs()){
                throw new IllegalStateException("Couldn't create directory: " + parent);
            }


            //Try threading to take of UI thread

            new Thread(new Runnable() {
                @Override
                public void run() {
                    //Log.d(TAG, "onSensorChanged: in accelbuffer");
                    //Log.d(TAG, "run: in runnable");
                    //writeToStream(accelBuffer);
                    writeStringBuilderToFile(GyroFile, gryoBuffer);
                    gryoBuffer.setLength(0);
                    writingGyroToFile = false;

                }
            }).start();

        }

    }

    else if(event.sensor.getType() == Sensor.TYPE_LIGHT){
        long lightTime = System.currentTimeMillis();

        lightReading = event.values[0];
        //Log.d(TAG, "onSensorChanged: \n the buffer length is: " + lightBuffer.length());

        if(lightReading > (previousLightReading + 3) || lightReading < (previousLightReading -3)){
            timeStampLight = System.currentTimeMillis();
            lightBuffer.append(lightTime + "," + event.values[0] + "\n");
            previousLightReading = lightReading;
        }


        if((lightBuffer.length() > 5000) && (writingLightToFile == false) ){

            timeStampLight = System.currentTimeMillis();
            LightFile = new File(path2 +"/Light/"  + timeStampLight +".txt");
            Log.d(TAG, "onSensorChanged: ligtfile created at: " + LightFile.getPath());
            File parent = LightFile.getParentFile();
            if(!parent.exists() && !parent.mkdirs()){
                throw new IllegalStateException("Couldn't create directory: " + parent);
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //Log.d(TAG, "onSensorChanged: in accelbuffer");
                    //Log.d(TAG, "run: in runnable");
                    //writeToStream(accelBuffer);
                    writeStringBuilderToFile(LightFile, lightBuffer);
                    lightBuffer.setLength(0);
                    writingLightToFile = false;

                }
            }).start();

        }
            }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void writeStringBuilderToFile(File file, StringBuilder builder){
        Log.d(TAG, "writeStringBuilderToFile: in stringbuilder to file");
        BufferedWriter writer = null;


        try {
            writer = new BufferedWriter(new java.io.FileWriter((file)));
            Log.d(TAG, "writeStringBuilderToFile: writiting");
            writer.append(builder);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(writer != null){
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
