package com.anysoftkeyboard.ui.settings.setup;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.sevencupsoftea.ears.R;

//import com.menny.android.anysoftkeyboard.R;

/**
 * Created by gwicks on 20/01/2018.
 */

public class SetupStepThreeOne extends AppCompatActivity {
    private static final String TAG = "SetupStepThreeOne";

    ImageView button;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.step_three_one);
        updateStatusBarColor("#1281e8");

        mContext = this;

        button = (ImageView) findViewById(R.id.imageView25);

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Clicked");

                // Switch 23rd Jan for Keyboard issue
                isKeyboardSelected();
                //Workaround:
//                Intent i = new Intent(SetupStepThreeOne.this, SetupStepThreeThree.class);
//
//                startActivity(i);


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


    public void isKeyboardSelected() {

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
                if (SetupSupport.isThisKeyboardSetAsDefaultIME(mContext)) {
                    Log.d(TAG, "run: 3");
                    //You have the permission, re-launch MainActivity
                    Intent i = new Intent(SetupStepThreeOne.this, SetupStepThreeThree.class);
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

        InputMethodManager imeManager = (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
        imeManager.showInputMethodPicker();
        handler.postDelayed(checkOverlaySetting, 1000);
//        try {
//            Log.d(TAG, "onClick: 5");
//            startActivity(startSettings);
//            handler.postDelayed(checkOverlaySetting, 1000);
//        } catch (ActivityNotFoundException notFoundEx) {
//            //weird.. the device does not have the IME setting activity. Nook?
//            Toast.makeText(this, "error", Toast.LENGTH_LONG).show();
//        }

    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

}
