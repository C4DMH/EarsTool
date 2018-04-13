package com.garmin;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.garmin.health.DeviceManager;
import com.garmin.health.DeviceModel;
import com.garmin.health.GarminDeviceScanCallback;
import com.garmin.health.ScannedDevice;
import com.google.common.collect.ImmutableSet;
import com.sevencupsoftea.ears.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by gwicks on 11/04/2018.
 */

public class ScanningActivity extends BaseActivity implements ScannedDeviceAdapter.OnItemClickListener {

    private static final String TAG = ScanningActivity.class.getSimpleName();

    public static final int PAIRING_ACTIVITY = 1;

    private ScannedDeviceAdapter mDeviceAdapter;

    private ProgressBar mProgressBar;

    private GarminDeviceScanCallback mDeviceScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanning);

        setTitle(R.string.title_scanning);

        showBackInActionBar();

        mProgressBar = (ProgressBar) findViewById(R.id.scanned_progress_bar);

        RecyclerView mScannedDevices = (RecyclerView) findViewById(R.id.scanned_list);
        mScannedDevices.setHasFixedSize(true);

        mScannedDevices.setLayoutManager(new LinearLayoutManager(this));
        mDeviceAdapter = new ScannedDeviceAdapter(this, new ArrayList<ScannedDevice>(), this);

        List<ScannedDevice> externalPairedDevices = new ArrayList<>(0);

        try
        {
            // Get device already paired with GCM.
            externalPairedDevices = DeviceManager.getDeviceManager().getGcmPairedDevices();
        }
        catch(SecurityException e)
        {
            Log.e("BLE SCAN", "GCM Permissions not granted...");
        }

        for(ScannedDevice device : externalPairedDevices)
        {
            mDeviceAdapter.addDevice(device);
        }

        mScannedDevices.setAdapter(mDeviceAdapter);

        //Streaming data is only available on some models
        Set<DeviceModel> models = ImmutableSet.of(DeviceModel.VIVOSMARTHR, DeviceModel.FENIX_5, DeviceModel.FENIX_5S, DeviceModel.VIVOSMART_3, DeviceModel.VIVOACTIVEHR, DeviceModel.VIVOACTIVE_3, DeviceModel.VIVOSPORT, DeviceModel.VIVOMOVE_HR);

        mDeviceScanner = new GarminDeviceScanCallback(models) {
            @Override
            public void onScannedDevice(ScannedDevice device) {
                Log.d(TAG, "garminDeviceFound(device = " + device.friendlyName() + ")");

                mDeviceAdapter.addDevice(device);
            }

            @Override
            public void onScanFailed(int errorCode) {
                Log.d(TAG, "scanFailed(failure.name() = " + errorCode + ")");
                showErrorDialog(String.valueOf(errorCode));
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        };

        startBleScan();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        stopBleScan();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_scanned_devices, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.start_scan:
                mProgressBar.setVisibility(View.VISIBLE);
                mDeviceAdapter.clearList();
                startBleScan();
                return true;

            case R.id.stop_scan:
                mProgressBar.setVisibility(View.INVISIBLE);
                stopBleScan();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startBleScan() {
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null || !btAdapter.isEnabled()) {
            showErrorDialog("Bluetooth isn't available");
            return;
        }

        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        assert lm != null;
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) && !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            showErrorDialog("Location services are not enabled");
            return;
        }

        BluetoothLeScanner scanner = btAdapter.getBluetoothLeScanner();
        if (scanner == null) {
            showErrorDialog("Could not obtain BluetoothLeScanner to start scan.");
            return;
        }

        scanner.startScan(mDeviceScanner);

        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void stopBleScan() {
        BluetoothLeScanner scanner = BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
        if (scanner != null) {
            scanner.stopScan(mDeviceScanner);
        }

        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onClick(ScannedDevice scannedDevice) {
        Log.d(TAG, "onClick(scannedDevice = " + scannedDevice.friendlyName() + ")");

        stopBleScan();

        Intent intent = new Intent(this, PairingActivity.class);
        intent.putExtra(PairingActivity.SCANNED_DEVICE_EXTRA, scannedDevice);
        startActivityForResult(intent, PAIRING_ACTIVITY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PAIRING_ACTIVITY) {
            Log.d(TAG, "onActivityResult:"+resultCode);
            //Pairing was cancelled or failed, resume scanning for new devices
            if (resultCode != RESULT_OK) {
                startBleScan();
            }
            //Pairing succeeded, go back to main
            else {
                finishSuccessfully();
            }
        }
    }
}
