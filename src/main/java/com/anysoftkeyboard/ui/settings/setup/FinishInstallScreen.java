package com.anysoftkeyboard.ui.settings.setup;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.menny.android.anysoftkeyboard.R;

/**
 * Created by gwicks on 21/01/2018.
 */

public class FinishInstallScreen extends AppCompatActivity {

    private static final String TAG = "FinishInstallScreen";

    ImageView needToTalkClosed;
    TextView talkText;
    TextView mood;
    ImageView moodCheck;
    ImageView preferences;
    TextView prefText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.completed_base);
        updateStatusBarColor("#1281e8");



        //needToTalkOpen = (ImageView)findViewById(R.id.gr)


        moodCheck = (ImageView)findViewById(R.id.imageView41);
        needToTalkClosed = (ImageView)findViewById(R.id.imageView6);
        talkText = (TextView)findViewById(R.id.textViewTalk);
        talkText.setVisibility(View.GONE);

        preferences = findViewById(R.id.imageView42);
        //preferences.setTag(1);
        prefText = findViewById(R.id.textView2);
        prefText.setVisibility(View.GONE);
        prefText.setTag(1);


        needToTalkClosed.setTag(1);
        mood = (TextView)findViewById(R.id.textView1);
        mood.setTag(1);
        mood.setVisibility(View.GONE);


        SpannableString ss = new SpannableString("Get free, anonymous and confidential support at 7 Cups. Listeners available 24/7 to help you feel better\n\nGet the App");
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                launch7cups();
            }

        };
        //ss.setSpan(clickableSpan, 48, 55, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(clickableSpan,106, 117, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        talkText.setText(ss);
        talkText.setMovementMethod(LinkMovementMethod.getInstance());



        needToTalkClosed.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                Log.d(TAG, "onClick: Clicked");

                if(needToTalkClosed.getTag().equals(1)){
                    talkText.setVisibility(View.VISIBLE);
                    needToTalkClosed.setTag(2);

                }else{
                    talkText.setVisibility(View.GONE);
                    needToTalkClosed.setTag(1);
                }
            }
        });

        preferences.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                Log.d(TAG, "onClick: Clicked moodcheck");

                if(prefText.getTag().equals(1)){
                    prefText.setVisibility(View.VISIBLE);
                    Log.d(TAG, "onClick: visable");
                    prefText.setTag(2);

                }else{
                    prefText.setVisibility(View.GONE);
                    Log.d(TAG, "onClick: invisible");
                    prefText.setTag(1);
                }
            }
        });

       moodCheck.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                Log.d(TAG, "onClick: Clicked moodcheck");

                if(mood.getTag().equals(1)){
                    mood.setVisibility(View.VISIBLE);
                    Log.d(TAG, "onClick: visable");
                    mood.setTag(2);

                }else{
                    mood.setVisibility(View.GONE);
                    Log.d(TAG, "onClick: invisible");
                    mood.setTag(1);
                }
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

    public void launch7cups(){
        Log.d(TAG, "launch7cups: clicked");

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=com.sevencupsoftea.app"));
        startActivity(intent);
        //https://play.google.com/store/apps/details?id=com.sevencupsoftea.app
    }


}
