package com.anysoftkeyboard.ui.settings.setup;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.menny.android.anysoftkeyboard.R;

/**
 * Created by gwicks on 20/01/2018.
 */

public class SetupStepTwo extends AppCompatActivity {

    private static final String TAG = "SetupStepTwo";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.step_two_zero );
        updateStatusBarColor("#1281e8");





    }

    public void startInstallTwo(View v)
    {

        Intent installIntent = new Intent(SetupStepTwo.this, SetupStepThree.class);
        SetupStepTwo.this.startActivity(installIntent);

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
