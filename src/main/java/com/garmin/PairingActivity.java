package com.garmin;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.garmin.health.AuthCompletion;
import com.garmin.health.Device;
import com.garmin.health.DeviceManager;
import com.garmin.health.PairingCallback;
import com.garmin.health.PairingCompletion;
import com.garmin.health.ScannedDevice;
import com.garmin.health.bluetooth.PairingFailedException;
import com.garmin.health.settings.Gender;
import com.garmin.health.settings.UserSettings;
import com.sevencupsoftea.ears.R;

import java.util.concurrent.Future;

/**
 * Created by gwicks on 11/04/2018.
 */

public class PairingActivity extends BaseActivity {

    private final static String TAG = PairingActivity.class.getSimpleName();

    public static final String SCANNED_DEVICE_EXTRA = "PairingActivity.ScannedDevice";

    private ScannedDevice mScannedDevice;

    private DeviceManager mDeviceManager;

    private ProgressBar mProgressBar;

    private Future mPairingFuture;
    private boolean mHasReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pairing);

        setTitle(R.string.title_pairing_device);

        showBackInActionBar();

        Intent intent = getIntent();
        mScannedDevice = intent.getParcelableExtra(SCANNED_DEVICE_EXTRA);

        mDeviceManager = DeviceManager.getDeviceManager();

        TextView deviceName = (TextView) findViewById(R.id.pair_device_text);
        deviceName.setText(mScannedDevice.friendlyName());

        mProgressBar = (ProgressBar) findViewById(R.id.pair_progress);

        pair();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //Cancel pairing if it is still in progress
        if (mPairingFuture != null && !mPairingFuture.isDone()) {
            mPairingFuture.cancel(true);
        }
    }

    private void pair() {
        Log.d(TAG, "pair()");

        //General user profile data is required for pairing
        UserSettings.Builder builder = new UserSettings.Builder();
        builder.setGender(Gender.FEMALE);
        builder.setAge(25);
        builder.setHeight((float)1.72);
        builder.setWeight(70);

        //Save future to cancel on back
        mPairingFuture = mDeviceManager.pair(mScannedDevice, builder.build(), new PairingListener());
    }

    private class PairingListener implements PairingCallback {

        @Override
        public void connectionReady(Device device, final PairingCompletion completion) {
            Log.d(TAG, "pairingComplete(device = " + device.friendlyName() + ")");
            //If app cancelled pairing, don't need to show message
            if (mPairingFuture.isCancelled()) {
                return;
            }

            //Device settings can be updated while connection remains open from pairing
            //Finish pairing, will start sync if necessary
            runOnUiThread(new Runnable() {
                @Override
                public void run()
                {
                    //Already did reset, don't ask again
                    if (mHasReset) {
                        completion.complete();
                    }
                    else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(PairingActivity.this);
                        builder.setTitle(R.string.reset_device)
                                .setMessage(R.string.reset_device)
                                .setPositiveButton(R.string.button_yes, new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        completion.requestReset();
                                        mHasReset = true;
                                    }
                                })
                                .setNegativeButton(R.string.button_no, new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        completion.complete();
                                    }
                                });

                        builder.create().show();
                    }
                }
            });
        }

        @Override
        public void pairingSucceeded(final Device device) {
            Log.d(TAG, "PairingSucceeded(device = " + device.friendlyName() + ")");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mProgressBar.setVisibility(View.INVISIBLE);

                    finishSuccessfully();
                }
            });
        }

        @Override
        public void pairingFailed(PairingFailedException cause) {
            Log.e(TAG, "pairingFailed", cause);
            //If app cancelled pairing, don't need to show message
            if (mPairingFuture.isCancelled()) {
                return;
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mProgressBar.setVisibility(View.INVISIBLE);
                    ConfirmationDialog dialog = new ConfirmationDialog(PairingActivity.this, getString(R.string.alert_error_title),
                            getString(R.string.pair_screen_device_pairing_failed), getString(R.string.alert_ok_button));
                    dialog.show();
                }
            });
        }

        @Override
        public void authRequested(final AuthCompletion completion)
        {
            //If app cancelled pairing, don't need to show message
            if (mPairingFuture.isCancelled()) {
                return;
            }

            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PairingActivity.this);
                    builder.setTitle(R.string.enter_passkey);

                    // Set up the input
                    final EditText input = new EditText(PairingActivity.this);
                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.setInputType(InputType.TYPE_CLASS_NUMBER);
                    builder.setView(input);

                    // Set up the buttons
                    builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            String text = input.getText().toString();
                            int passkey = Integer.parseInt(text);
                            completion.setPasskey(passkey);
                        }
                    });
                    builder.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.cancel();
                        }
                    });

                    builder.show();
                }
            });
        }
    }
}