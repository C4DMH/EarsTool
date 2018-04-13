package com.garmin;

import android.content.Context;
import android.util.Log;

import com.garmin.health.Device;
import com.garmin.health.DeviceManager;
import com.garmin.health.realtime.RealTimeAccelerometer;
import com.garmin.health.realtime.RealTimeDataListener;
import com.garmin.health.realtime.RealTimeDataManager;
import com.garmin.health.realtime.RealTimeDataType;
import com.garmin.health.realtime.RealTimeHeartRate;
import com.garmin.health.realtime.RealTimeResult;
import com.garmin.health.realtime.RealTimeStress;
import com.garmin.health.settings.AccelerometerSamplingRate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.HashMap;

/**
 * Created by gwicks on 11/04/2018.
 */

public class RealTimeDataHandler implements RealTimeDataListener {

    private static final String TAG = RealTimeDataHandler.class.getSimpleName();

    private static RealTimeDataHandler sInstance;

    private final HashMap<String, HashMap<RealTimeDataType, RealTimeResult>> mLatestData;

    private static StringBuilder garminBuffer;
    private static String path;
    private Context mContext;
    private static boolean writingToBuffer;

    public synchronized static RealTimeDataHandler getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new RealTimeDataHandler(context);
            //mContext = context;
        }

        return sInstance;
    }

    private RealTimeDataHandler(Context context) {
        mLatestData = new HashMap<>();
        mContext = context;
        garminBuffer = new StringBuilder();
        path = mContext.getExternalFilesDir(null) + "/Garmin";
        File directory = new File(path);
        if(!directory.exists()){
            Log.d(TAG, "onCreate: making directory");
            directory.mkdir();
        }


    }

    public HashMap<RealTimeDataType, RealTimeResult> getLatestData(String deviceAddress) {
        return mLatestData.get(deviceAddress);
    }

    /**
     * Start listening for real time data of all paired devices.
     */
    public void startListening() {

        RealTimeDataManager manager = DeviceManager.getDeviceManager().getRealTimeDataManager();
        manager.addRealTimeDataListener(this, EnumSet.allOf(RealTimeDataType.class));

        for (Device device : DeviceManager.getDeviceManager().getPairedDevices()) {
            Log.d(TAG, "startListening: real time data");
            device.samplingFrequency(AccelerometerSamplingRate.ONE_HUNDRED_HERTZ);
            manager.enableRealTimeData(device, EnumSet.allOf(RealTimeDataType.class));
        }
    }

    /**
     * Start listening for real time data of a specific device.
     *
     * @param device to listen for real time data.
     */
    public void startListening(Device device) {
        Log.d(TAG, "startListening: real time data 2");

        RealTimeDataManager manager = DeviceManager.getDeviceManager().getRealTimeDataManager();
        manager.addRealTimeDataListener(this, EnumSet.allOf(RealTimeDataType.class));
        manager.enableRealTimeData(device, EnumSet.allOf(RealTimeDataType.class));
    }

    /**
     * Stop listening for real time data of all paired devices.
     */
    public void stopListening() {

        RealTimeDataManager manager = DeviceManager.getDeviceManager().getRealTimeDataManager();
        manager.removeRealTimeDataListener(this, EnumSet.allOf(RealTimeDataType.class));

        for (Device device : DeviceManager.getDeviceManager().getPairedDevices()) {
            Log.d(TAG, "stopListening: stop called");
            manager.disableRealTimeData(device, EnumSet.allOf(RealTimeDataType.class));
        }
    }

    /**
     * Stop listening for real time data of a specific device. Don't call this if streaming while the app is in background is required.
     *
     * @param device to listen for real time data.
     */
    public void stopListening(Device device) {

        Log.d(TAG, "stopListening: stop two called");

        RealTimeDataManager manager = DeviceManager.getDeviceManager().getRealTimeDataManager();
        manager.removeRealTimeDataListener(this, EnumSet.allOf(RealTimeDataType.class));
        manager.disableRealTimeData(device, EnumSet.allOf(RealTimeDataType.class));
    }

    @Override
    public void onDataUpdate(Device device, RealTimeDataType dataType, RealTimeResult result) {
        Log.d(TAG, "onDataUpdate: calling log readtime data from real time data handler");
        logRealTimeData(TAG, device, dataType, result);

        //Cache last received data of each type
        //Used to display values if device loses connection
        HashMap<RealTimeDataType, RealTimeResult> latestData = mLatestData.get(device.address());
        if (latestData == null) {
            latestData = new HashMap<>();
            mLatestData.put(device.address(), latestData);
        }
        latestData.put(dataType, result);
    }

    public static void logRealTimeData(String tag, Device device, RealTimeDataType dataType, RealTimeResult result) {
//        StringBuilder garminBuffer = new StringBuilder();
        writingToBuffer = false;
        //long previousTime = 0;
        String value = "";
        //Log.d(TAG, "logRealTimeData: in real time data ");
        //Log out the main value for each data type
        switch (dataType) {
            case STEPS:
                value = String.valueOf(result.getSteps().getCurrentStepCount());
                break;
            case HEART_RATE_VARIABILITY:
                value = String.valueOf(result.getHeartRateVariability().getHeartRateVariability());
                break;
            case CALORIES:
                value = String.valueOf(result.getCalories().getCurrentTotalCalories());
                break;
            case FLOORS:
                value = String.valueOf(result.getFloors().getCurrentFloorsClimbed());
                break;
            case INTENSITY_MINUTES:
                value = String.valueOf(result.getIntensityMinutes().getTotalWeeklyMinutes());
                break;
            case HEART_RATE:
                RealTimeHeartRate heartRate = result.getHeartRate();
                value = heartRate.getCurrentHeartRate() + " " + heartRate.getCurrentRestingHeartRate() + " " + heartRate.getDailyHighHeartRate() + " " + heartRate.getDailyLowHeartRate() + " " + heartRate.getHeartRateSource().name();
                break;
            case STRESS:
                RealTimeStress stress = result.getStress();
                value = String.valueOf(stress.getStressScore());
                break;
            case ACCELEROMETER:
                RealTimeAccelerometer realTimeAccelerometer = result.getRealTimeAccelerometer();
                long CT = System.currentTimeMillis();

                int x = realTimeAccelerometer.getAccelerometerSamples().get(0).getX();
                int y = realTimeAccelerometer.getAccelerometerSamples().get(0).getY();
                int z = realTimeAccelerometer.getAccelerometerSamples().get(0).getZ();

                //double magnitude = Math.sqrt(x * x + y * y + z * z) / 1000;
                value =  "," + x + "," + y + "," + z;
                break;
        }
        Long currentTime = System.currentTimeMillis();
        Log.i(tag, "Real Time (" + device.address() + ") " + dataType.name() + ": " + value);
        Log.d(TAG, "logRealTimeData: the size of the buffer is: " + garminBuffer.length());
        garminBuffer.append(currentTime + "  " +  dataType.name() + " : " + value + "\n");
        if((garminBuffer.length() > 500000) && writingToBuffer == false){
            writingToBuffer = true;
            long LAST_TS_ACC = System.currentTimeMillis();
            File garminFile = new File(path +"/" + LAST_TS_ACC + ".txt");
            Log.d(TAG, "logRealTimeData: inside write to file");

            File parent = garminFile.getParentFile();
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
                    writeStringBuilderToFile(garminFile, garminBuffer);
                    garminBuffer.setLength(0);
                    writingToBuffer = false;

                }
            }).start();



        }
    }

    private static void writeStringBuilderToFile(File file, StringBuilder builder){
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