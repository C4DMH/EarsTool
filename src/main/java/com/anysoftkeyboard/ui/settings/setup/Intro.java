package com.anysoftkeyboard.ui.settings.setup;

import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_step_one );
        updateStatusBarColor("#07dddd");

        moveToNextStep();


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

    public void updateStatusBarColor(String color){// Color must be in hexadecimal fromat
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.d(TAG, "updateStatusBarColor: color change being called!");
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(color));
        }
    }



}




