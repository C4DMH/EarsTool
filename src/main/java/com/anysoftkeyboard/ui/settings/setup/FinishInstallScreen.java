package com.anysoftkeyboard.ui.settings.setup;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.menny.android.anysoftkeyboard.R;

/**
 * Created by gwicks on 21/01/2018.
 */

public class FinishInstallScreen extends AppCompatActivity {

    private static final String TAG = "FinishInstallScreen";

    ImageView needToTalkClosed;
    ImageView needToTalkOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.completed_base);
        updateStatusBarColor("#1281e8");



        //needToTalkOpen = (ImageView)findViewById(R.id.gr)

        needToTalkClosed = (ImageView)findViewById(R.id.imageView6);
        needToTalkClosed.setTag(1);


        needToTalkClosed.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                Log.d(TAG, "onClick: Clicked");

                if(needToTalkClosed.getTag().equals(1)){
                    needToTalkClosed.setImageResource(R.drawable.need_to_talk_open);
                    needToTalkClosed.setTag(2);

                }else{
                    needToTalkClosed.setImageResource(R.drawable.group_4);
                    needToTalkClosed.setTag(1);
                }

//
//                needToTalkClosed.setImageResource(R.drawable.need_to_talk_open);
                //needToTalkClosed.setImageResource(R.drawable.notif);



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

}
