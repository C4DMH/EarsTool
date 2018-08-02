package com.garmin;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.garmin.health.ConnectionState;
import com.garmin.health.Device;
import com.garmin.health.DeviceConnectionStateListener;
import com.garmin.health.DeviceManager;
import com.garmin.health.DevicePairedStateListener;
import com.garmin.health.GarminHealth;
import com.garmin.health.GarminHealthInitializationException;
import com.garmin.health.bluetooth.FailureCode;
import com.garmin.health.realtime.RealTimeDataListener;
import com.garmin.health.realtime.RealTimeDataType;
import com.garmin.health.realtime.RealTimeResult;
import com.garmin.health.settings.AccelerometerSamplingRate;
import com.sevencupsoftea.ears.R;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by gwicks on 11/04/2018.
 */

public class DeviceListActivity extends BaseActivity implements View.OnClickListener, DeviceConnectionStateListener, DevicePairedStateListener, RealTimeDataListener {

    private static final String TAG = DeviceListActivity.class.getSimpleName();

    public final static String DEVICE_ADDRESS_EXTRA = "device.address";

    private static final String GCM_PERMISSION = "com.garmin.android.apps.connectmobile.permission.READ_DEVICE_RECORDS";
    private static final int REQUEST = 0;

    private String licence;

    private static final int REQUEST_COURSE_LOCATION = 0;

    private DeviceAdapter mConnectedAdapter;
    private DeviceManager mDeviceManager;
    private Device mDevice;

    private String mAddress;
    private Context mContext;
    private TextView statusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);

        if (!initializeGarminHealth()) {
            return;
        }

        mContext = this;

        Log.d(TAG, "onCreate: mAddress is: " + mAddress);

        mDeviceManager = DeviceManager.getDeviceManager();
        mDeviceManager.addConnectionStateListener(this);
        mDeviceManager.addPairedStateListener(this);

        mAddress = DEVICE_ADDRESS_EXTRA;
        //mDeviceManager.getRealTimeDataManager().addRealTimeDataListener(this, EnumSet.allOf(RealTimeDataType.class));


        Log.d(TAG, "onCreate: mAddress is: " + mAddress);



        setContentView(R.layout.activity_main_garmin);

        setTitle(R.string.title_paired_devices);

        findViewById(R.id.add_device_button).setOnClickListener(this);
        statusText = (TextView)findViewById(R.id.connectstatus);

        //Will need location service for ble scanning
        requestLocationPermission();

        if(!verifyGcmPermissions() && Build.VERSION.SDK_INT > 23)
        {
            requestPermissions(
                    new String[]{GCM_PERMISSION},
                    REQUEST);
        }

        initPairedDeviceList();
        statusText.setTextColor(Color.RED);

    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "onStart()");

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop()");

        Device device = null;
        Iterator<Device> iterator = DeviceManager.getDeviceManager().getPairedDevices().iterator();
        if (iterator.hasNext()) {
            device = iterator.next();
        }
    }

    private boolean initializeGarminHealth() {
        try {
            // Using system BLE bonding is very unreliable on phones version lower than LOLLIPOP,
            // so it is safer to turn it off entirely.
            boolean systemBonding = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;

            //GarminHealth.initialize(this, systemBonding, getString(R.string.streaming_license));
            GarminHealth.initialize(this, systemBonding, licence);

            return true;
        }
        catch (Error | Exception e) {
            Log.e(TAG, "Failed to initialize GarminHealth", e);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            //Standard initialization exception, should be license related
            if (GarminHealthInitializationException.class.isInstance(e)) {
                builder.setTitle(R.string.init_failed_title)
                        .setMessage(R.string.init_failed_message)
                        .setPositiveButton(R.string.alert_ok_button, null);

            }
            //Unexpected crash, usually AssertionError
            else {
                builder.setTitle(R.string.init_failed_title)
                        .setMessage(R.string.init_failed_generic_message)
                        .setPositiveButton(R.string.alert_ok_button, null);
            }
            AlertDialog dialog = builder.create();
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog)
                {
                    finish();
                }
            });
            dialog.show();
            return false;
        }
    }

    private void initPairedDeviceList() {
        RecyclerView connectedDevicesRecyclerView = (RecyclerView)findViewById(R.id.paired_list);
        connectedDevicesRecyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        connectedDevicesRecyclerView.setLayoutManager(layoutManager);

        List<Device> pairedDevices = new ArrayList<>();
        pairedDevices.addAll(mDeviceManager.getPairedDevices());

        mConnectedAdapter = new DeviceAdapter(this, pairedDevices,
                new DeviceAdapter.OnItemClickListener() {
                    @Override
                    public void onForgetDeviceClick(final Device device) {
                        Log.d(TAG, "onForgetDeviceClick(device = " + device.friendlyName() + ")");
                        ConfirmationDialog dialog = new ConfirmationDialog(DeviceListActivity.this, null, getString(R.string.connected_forget_device_message),
                                getString(R.string.button_yes), getString(R.string.button_no),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == DialogInterface.BUTTON_POSITIVE) {
                                            mDeviceManager.forget(device.address());
                                            mConnectedAdapter.removeDevice(device.address());
                                        }
                                    }
                                });
                        dialog.show();
                    }

                    @Override
                    public void onConnectionClick(Device device)
                    {
                        Log.d(TAG, "onConnectionClick: 12");
                        if(device.connectionState() == ConnectionState.CONNECTED)
                        {
                            mDeviceManager.stopCommunication(device.address());
                        }
                        else
                        {
                            mDeviceManager.restartCommunication(device.address());
                        }
                    }

                    @Override
                    public void onItemClick(Device device) {
                        Log.d(TAG, "onItemClick: clicked");

                        // Cut out activity

//                        intent.putExtra(DEVICE_ADDRESS_EXTRA, device.address());
//                        startActivity(intent);
//
                        mAddress = device.address();
                        mDevice = device;
                        Log.d(TAG, "onItemClick: 1");
                        device.samplingFrequency(AccelerometerSamplingRate.TWENTY_FIVE_HERTZ);
                        Log.d(TAG, "onItemClick: 2");
                        // mDeviceManager.getRealTimeDataManager().enableRealTimeData(device, EnumSet.allOf(RealTimeDataType.class));
                        Log.d(TAG, "onItemClick: 3");
                        initRealTimeData();
                        //mConnectedAdapter.onBindViewHolder().;
                        Toast.makeText(mContext, "YOU ARE NOW CONNECTED, THANK YOU", Toast.LENGTH_LONG).show();
                        statusText.setText("CONNECTED");
                        statusText.setTextColor(Color.GREEN);
                    }
                });

        connectedDevicesRecyclerView.setAdapter(mConnectedAdapter);
    }

    private void refreshPairedDevices() {
        Set<Device> devices = mDeviceManager.getPairedDevices();
        for (Device device : devices) {
            mConnectedAdapter.addDevice(device);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_device_button:
                Intent intent = new Intent(this, ScanningActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void requestLocationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return;
        }

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_COURSE_LOCATION);
    }

    private boolean verifyGcmPermissions()
    {
        boolean buildCondition = Build.VERSION.SDK_INT < Build.VERSION_CODES.M;

        int permission = checkCallingOrSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        boolean permCondition = (permission == PackageManager.PERMISSION_GRANTED);

        return buildCondition || permCondition;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case REQUEST_COURSE_LOCATION:
                //if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                break;
        }
    }

    @Override
    public void onDeviceConnected(Device device) {
        mConnectedAdapter.addDevice(device);
    }

    @Override
    public void onDeviceDisconnected(Device device) {
        Log.d(TAG, "onDeviceDisconnected: device disconneted");
        mConnectedAdapter.refreshDevice(device);
    }

    @Override
    public void onDeviceConnectionFailed(Device device, FailureCode failure) {
        Log.d(TAG, "onDeviceConnectionFailed: device connection failed");
        try{
            showErrorDialog(getString(R.string.error_failed_to_connect_to_device, device.address(), failure.name()));

        }catch(Exception e){
            Log.e(TAG, "onDeviceConnectionFailed: " + e);
        }
        //showErrorDialog(getString(R.string.error_failed_to_connect_to_device, device.address(), failure.name()));
        mConnectedAdapter.refreshDevice(device);
    }

    @Override
    public void onDevicePaired(Device device) {
        mConnectedAdapter.addDevice(device);
    }

    @Override
    public void onDeviceUnpaired(String deviceId) {
        Log.d(TAG, "onDeviceUnpaired: device unpaided");
        mConnectedAdapter.removeDevice(deviceId);
    }

    //Trying to cut out the real time data activity

    private void initRealTimeData() {
        Log.d(TAG, "initRealTimeData: starting real time data.");









        mDeviceManager.getRealTimeDataManager().addRealTimeDataListener(this, EnumSet.allOf(RealTimeDataType.class));
        //Data may have been received when page wasn't in the foreground
        HashMap<RealTimeDataType, RealTimeResult> latestData = RealTimeDataHandler.getInstance(this).getLatestData(mAddress);
        mDeviceManager.getRealTimeDataManager().enableRealTimeData(mDevice, EnumSet.allOf(RealTimeDataType.class));

        Log.d(TAG, "initRealTimeData: 1");
        //RealTimeDataHandler.logRealTimeData();
        if (latestData != null) {
            for (RealTimeDataType type : latestData.keySet()) {

                Log.d(TAG, "initRealTimeData: " + latestData.get(type));
                //updateData(type, latestData.get(type));
            }
            Log.d(TAG, "initRealTimeData: 2");
        }
    }

    @Override
    public void onDataUpdate(Device device, final RealTimeDataType dataType, final RealTimeResult result) {
        if (!device.address().equals(mAddress)) {
            //Real time data came from different device
            return;
        }

        //Use same logging as single instance real time listener for sample
        //Log.d(TAG, "onDataUpdate: logging read time data from real time activity");
        RealTimeDataHandler.logRealTimeData(TAG, device, dataType, result);
    }
}
