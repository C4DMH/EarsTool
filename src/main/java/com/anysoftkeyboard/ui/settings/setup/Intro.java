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
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.menny.android.anysoftkeyboard.R;

/**
 * Created by gwicks on 21/01/2018.
 */

public class Intro extends AppCompatActivity {

    private static final String TAG = "Intro";

    public boolean appUsage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_step_one );
        updateStatusBarColor("#07dddd");

        if(isAccessGranted()) {
            moveToFinalStep();
        }else {
            moveToNextStep();
        }
    }


    public void moveToNextStep(){

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent(Intro.this, SetupStepOne.class);
                Intro.this.startActivity(intent);
            }
        }, 6000);

    }

    public void moveToFinalStep(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent(Intro.this, FinishInstallScreen.class);
                Intro.this.startActivity(intent);
            }
        }, 6000);

    }

    public void updateStatusBarColor(String color){// Color must be in hexadecimal fromat
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.d(TAG, "updateStatusBarColor: color change being called!");
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(color));
        }
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



}




