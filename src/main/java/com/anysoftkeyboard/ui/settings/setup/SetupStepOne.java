package com.anysoftkeyboard.ui.settings.setup;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.sevencupsoftea.ears.R;

//import android.support.v7.app.AlertDialog;

//import com.menny.android.anysoftkeyboard.R;

public class SetupStepOne extends AppCompatActivity {

    private static final String TAG = "SetupStepOne";
    ViewPager mImageViewPager;
    ImageView buttonContinue;
    CharSequence cs;
    String informedConsent;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_base );
        Log.d(TAG, "onCreate: called");

        mImageViewPager = (ViewPager) findViewById(R.id.viewPager);

        buttonContinue = (ImageView) findViewById(R.id.startButton);
        //buttonContinue.getBackground().setColorFilter("#0479cb", PorterDuff.Mode.MULTIPLY);
        informedConsent = getString(R.string.informed_consent);






//        startInstallButton =  (Button) findViewById(R.id.button_continue3);
//
//        startInstallButton.setOnClickListener(new View.OnClickListener(){
//
//            @Override
//            public void onClick(View view){
//                Log.d(TAG, "onClick: Clicked");
//
//                startInstall(view);
//
//
//            }
//        });

        ViewPager pager = (ViewPager) findViewById(R.id.viewPager);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
//
//        mImageViewPager = (ViewPager) findViewById(R.id.pager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(pager, true);


        /*
        * CONTINUE button does something different depending on your position. First two splash screens it moves
        * you forward, the final one starts the actual intstall ( ie starts Activity SetupStepTwo.java)
        * */


        buttonContinue.setOnClickListener(new View.OnClickListener(){





            @Override
            public void onClick(View view){
                Log.d(TAG, "onClick: get item : " + mImageViewPager.getCurrentItem());
                Log.d(TAG, "onClick: button Clicked");

                if(mImageViewPager.getCurrentItem() == 2){

                    AlertDialog alertDialog = new AlertDialog.Builder(SetupStepOne.this).create();
                    alertDialog.setTitle("7 Cups EARS: Informed Consent & Terms of Service Agreement");
                    alertDialog.setMessage(Html.fromHtml(informedConsent));
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "I Disagree",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.setButton("I Agree",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            startInstall();
                        }
                    });
                    alertDialog.show();

                    //startInstall(view);
                }
                else{
                    mImageViewPager.setCurrentItem(getItem(+1), true);
                }






            }
        });



    }

    public int getItem(int i){
        Log.d(TAG, "getItem: i:  " + 1);
        return mImageViewPager.getCurrentItem() + i;
    }

    public void next(View view){
        Log.d(TAG, "next: ");
        mImageViewPager.setCurrentItem(getItem(+1), true);
    }

    public void startInstall()
            
    {

        Log.d(TAG, "startInstall: ");
        Intent installIntent = new Intent(SetupStepOne.this, SetupStepTwo.class);
        SetupStepOne.this.startActivity(installIntent);
        finish();

    }

    public void updateStatusBarColor(String color, Fragment frag){// Color must be in hexadecimal fromat
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.d(TAG, "updateStatusBarColor: color change being called! From Fragment: " + frag);
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(color));
        }
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            Log.d(TAG, "getItem: ");
            switch(pos) {



                case 0: return SecondFragment.newInstance("SecondFragment, Instance 1");
                case 1: return ThirdFragment.newInstance("ThirdFragment, Instance 1");
                case 2: return FourthFragment.newInstance("ThirdFragment, Instance 2");


                default: return ThirdFragment.newInstance("ThirdFragment, Default");

//                case 0: return FirstFragment.newInstance("FirstFragment, Instance 1");
//                case 1: return SecondFragment.newInstance("SecondFragment, Instance 1");
//                case 2: return ThirdFragment.newInstance("ThirdFragment, Instance 1");
//                case 3: return FourthFragment.newInstance("ThirdFragment, Instance 2");
//                case 4: return ThirdFragment.newInstance("ThirdFragment, Instance 3");
//                default: return ThirdFragment.newInstance("ThirdFragment, Default");
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

}
