package com.anysoftkeyboard.ui.settings.setup;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.sevencupsoftea.ears.R;

//import com.menny.android.anysoftkeyboard.R;

/**
 * Created by gwicks on 20/01/2018.
 */

public class SetupStepFour extends AppCompatActivity {

    private static final String TAG = "SetupStepFour";

    ImageView button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.step_four_one);
        updateStatusBarColor("#1281e8");




        button = (ImageView)findViewById(R.id.imageView30);

        button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                Log.d(TAG, "onClick: Clicked");

                showDialog3(view);


            }
        });


    }

    public void updateStatusBarColor(String color){// Color must be in hexadecimal fromat
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.d(TAG, "updateStatusBarColor: color change being called!");
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(color));
        }
    }

    public void showDialog3(View v) {


        Log.d("History", "In show Dialog3");


        final Handler handler2 = new Handler();

        Runnable checkOverlaySetting2 = new Runnable() {

            @Override
            //@TargetApi(23)
            public void run() {
                Log.d(TAG, "run: 1");
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    Log.d(TAG, "run: 2");
                    //return;
                }

                // 18th Jan 2018, below works, trying to stop using the intent ( ie try back button below).
                if (checkNotificationEnabled()) {
                    Log.d(TAG, "run: 41");
                    Log.d(TAG, "run: Notificiation Enabled moterfucker");
                    //You have the permission, re-launch MainActivity
                    Intent i = new Intent(SetupStepFour.this, SetupStepFive.class);
                    Log.d(TAG, "run: 42");
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    SetupStepFour.this.startActivity(i);
                    //Log.d(TAG, "the activity is: " + getActivity());
                    return;
                }

                handler2.postDelayed(this, 200);
            }
        };


                Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                //Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                //intent.putExtra("finishActivityOnSaveCompleted", true);
                // intent.setComponent(new ComponentName("com.android.settings","com.android.settings.Settings$SecuritySettingsActivity"));
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

        handler2.postDelayed(checkOverlaySetting2, 1000);
    }

        public boolean checkNotificationEnabled() {
            try{
                Log.d(TAG, "checkNotificationEnabled: in try");
                if(Settings.Secure.getString(this.getContentResolver(),
                        "enabled_notification_listeners").contains(this.getApplication().getPackageName()))
                {
                    Log.d(TAG, "checkNotificationEnabled: in true");

                    Log.d(TAG, "checkNotificationEnabled: true");
                    return true;
                } else {

                    Log.d(TAG, "checkNotificationEnabled: ruturn false");
                    return false;
                }

            }catch(Exception e) {
                e.printStackTrace();
            }
            Log.d(TAG, "checkNotificationEnabled: Did not get into settings?");
            return false;
        }
}
