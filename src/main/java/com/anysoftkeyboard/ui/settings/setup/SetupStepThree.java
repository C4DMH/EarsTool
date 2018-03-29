package com.anysoftkeyboard.ui.settings.setup;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.sevencupsoftea.ears.R;

import java.util.List;

//import com.menny.android.anysoftkeyboard.R;

/**
 * Created by gwicks on 20/01/2018.
 */

public class SetupStepThree extends AppCompatActivity {

    private static final String TAG = "SetupStepThree";

    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.step_three_zero);
        updateStatusBarColor("#1281e8");

        mContext = this;
    }

    public void skipThisStep(View v){
        Intent intent = new Intent(SetupStepThree.this, SetupStepThreeOne.class);
        SetupStepThree.this.startActivity(intent);

    }

    public void updateStatusBarColor(String color){// Color must be in hexadecimal fromat
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.d(TAG, "updateStatusBarColor: color change being called!");
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(color));
        }
    }


    public void installKeyboard(View v){

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
                if (isStepCompleted(mContext)) {
                    Log.d(TAG, "run: 3");
                    //You have the permission, re-launch MainActivity
                    Intent i = new Intent(SetupStepThree.this, SetupStepThreeOne.class);
                    Log.d(TAG, "run: 4");
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    return;
                }


//
//            if (isAccessGranted()) {
//                Log.d(TAG, "run: 3");
//                //You have the permission, re-launch MainActivity
//                //Intent i = new Intent(MainActivity.this, MainActivity.class);
//                Log.d(TAG, "run: 4");
//                //i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                //startActivity(i);
//                dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
//                //dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
//                return;
//            }
                handler.postDelayed(this, 200);
            }
        };

        Intent startSettings = new Intent(android.provider.Settings.ACTION_INPUT_METHOD_SETTINGS);
        startSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startSettings.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startSettings.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        try {
            Log.d(TAG, "onClick: 5");
            startActivity(startSettings);
            handler.postDelayed(checkOverlaySetting, 1000);
        } catch (ActivityNotFoundException notFoundEx) {
            //weird.. the device does not have the IME setting activity. Nook?
            Toast.makeText(this, "error", Toast.LENGTH_LONG).show();
        }



    }

    protected boolean isStepCompleted(@NonNull Context context) {
        Log.d(TAG, "isStepCompleted: is step completed?");
        InputMethodManager imeManager = (InputMethodManager)getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
        List<InputMethodInfo> InputMethods = imeManager.getEnabledInputMethodList();
        for(InputMethodInfo model : InputMethods) {
            System.out.println(model.getPackageName());
        }

        return SetupSupport.isThisKeyboardEnabled(context);
    }
}
