package com.anysoftkeyboard.ui.settings.setup;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
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

public class SetupStepFive extends AppCompatActivity {

    private static final String TAG = "SetupStepFive";

    ImageView button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.step_five);
        updateStatusBarColor("#1281e8");



        button = (ImageView)findViewById(R.id.imageView35);

        button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                Log.d(TAG, "onClick: Clicked");

                appUsage();


            }
        });




    }

    public void appUsage(){


        final Handler handler = new Handler();

        Runnable checkOverlaySetting = new Runnable() {

            @Override
            //@TargetApi(23)
            public void run() {
                Log.d(TAG, "run: 1");
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    Log.d(TAG, "run: 2");
                    //return;
                }

                // 18th Jan 2018, below works, trying to stop using the intent ( ie try back button below).
                if (isAccessGranted()) {
                    Log.d(TAG, "run: 3");
                    Intent i = new Intent(SetupStepFive.this, FaceDetect.class);
                    Log.d(TAG, "run: 42");
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    SetupStepFive.this.startActivity(i);
                    //Log.d(TAG, "the activity is: " + getActivity());
                    return;
                }

                handler.postDelayed(this, 200);
            }
        };


        Log.d(TAG, "appUsage: before intnet");
        Intent intentTwo = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        intentTwo.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intentTwo.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intentTwo.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        Log.d(TAG, "appUsage: after intent");
        handler.postDelayed(checkOverlaySetting, 1000);
        Log.d(TAG, "appUsage: after starting checkoverlay");
        //intentTwo.putExtra("finishActivityOnSaveCompleted", true);

        startActivity(intentTwo);



    }

    public boolean isAccessGranted() {
        try {
            Log.d(TAG, "isAccessGranted: in isaccessgranted");
            PackageManager packageManager = this.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(this.getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) this.getSystemService(Context.APP_OPS_SERVICE);
            int mode = 0;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                Log.d(TAG, "isAccessGranted: ??");
                mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                        applicationInfo.uid, applicationInfo.packageName);
            }
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            Log.d(TAG, "isAccessGranted: name not found");
            return false;
        }
    }

    public void updateStatusBarColor(String color){// Color must be in hexadecimal fromat
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.d(TAG, "updateStatusBarColor: color change being called!");
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(color));
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

}
