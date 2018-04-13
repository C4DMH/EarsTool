package com.radicalninja.logger;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by gwicks on 4/04/2018.
 */

public class AccGryLgt extends Service implements SensorEventListener {

    private static final String TAG = "AccGryLgt";

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
    double ACCEL_THRESHOLD = 0.05;



    float lightReading = 0;

    long timeStampLight = 0;

    File AccelFile;
    File GyroFile;
    File LightFile;
    File DestroyFile;



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "AccelGyroLight: starting accelgyrolight constructor");

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mGyroscope =  sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "AccGryLgt - Service");
        wakeLock.acquire();

        path = Environment.getExternalStorageDirectory() +"/VIDEODIARY";
        path2 = (getExternalFilesDir(null) + "/Sensors");
        Log.d(TAG, "AccGryLgt:  the path to externalfilesdir is: " + path2);


        File directory = new File(path);
        File directory2 = new File(path2);

        if(!directory.exists()){
            Log.d(TAG, "onCreate: making directory");
            directory.mkdir();
        }

        if(!directory2.exists()){
            Log.d(TAG, "onCreate: making directory");
            directory2.mkdir();
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
    public void onDestroy() {
        Log.d(TAG, "onDestroy: the sensor service has been destroyed");
        sensorManager.unregisterListener(this);
        wakeLock.release();
        DestroyFile = new File(path2 +"/Acc/Destroy_Service.txt");
        writeToFile(DestroyFile, "the file was destroyed at: ");
        super.onDestroy();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            long TS = System.currentTimeMillis();
            //Log.d(TAG, "onSensorChanged: The time stamp check is:  " + TS +" + " + LAST_TS_ACC );

            // Filter to remove readings that come too often
            if (TS < LAST_TS_ACC + 100) {
                //Log.d(TAG, "onSensorChanged: skipping");
                return;
            }

            if(LAST_VALUES_ACC != null && Math.abs(event.values[0] - LAST_VALUES_ACC[0]) < ACCEL_THRESHOLD
                    && Math.abs(event.values[1] - LAST_VALUES_ACC[1]) < ACCEL_THRESHOLD
                    && Math.abs(event.values[2] - LAST_VALUES_ACC[2]) < ACCEL_THRESHOLD) {
                return;
            }

            LAST_VALUES_ACC = new Float[]{event.values[0], event.values[1], event.values[2]};

            LAST_TS_ACC = System.currentTimeMillis();

            accelBuffer.append(LAST_TS_ACC + "," + event.values[0] + "," + event.values[1] + "," + event.values[2] + "\n");
            //Log.d(TAG, "onSensorChanged: \n the buffer length is: " + accelBuffer.length());
            //Log.d(TAG, "onSensorChanged: the buffer is: " + accelBuffer.toString());
            if((accelBuffer.length() > 500000) && (writingAccelToFile == false) ){
                writingAccelToFile = true;

                AccelFile = new File(path2 +"/Acc/"  + LAST_TS_ACC +"_Service.txt");
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


            long TS = System.currentTimeMillis();
            if (TS < LAST_TS_GYRO + 100) {
                //Log.d(TAG, "onSensorChanged: skipping");
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

                GyroFile = new File(path2 +"/Gyro/"  + LAST_TS_GYRO +"_Service.txt");
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

            //Log.d(TAG, "onSensorChanged: light");
            long lightTime = System.currentTimeMillis();

            lightReading = event.values[0];
            //Log.d(TAG, "onSensorChanged: \n the buffer length is: " + lightBuffer.length());

            if(lightReading > (previousLightReading + 3) || lightReading < (previousLightReading -3)){
                timeStampLight = System.currentTimeMillis();
                lightBuffer.append(lightTime + "," + event.values[0] + "\n");
                //Log.d(TAG, "onSensorChanged: appending to light buffer");
                previousLightReading = lightReading;
            }


            if((lightBuffer.length() > 50000) && (writingLightToFile == false) ){

                timeStampLight = System.currentTimeMillis();
                LightFile = new File(path2 +"/Light/"  + timeStampLight +"_Service.txt");
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
        }catch(NullPointerException e){
            e.printStackTrace();
        }

    }
}
