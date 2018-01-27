package com.anysoftkeyboard.ui.settings.setup;

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
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.menny.android.anysoftkeyboard.R;

public class SetupStepOne extends AppCompatActivity {

    private static final String TAG = "SetupStepOne";
    ImageView mImageViewPager;

    ImageView startInstallButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_base );

        startInstallButton =  (ImageView)findViewById(R.id.startButton);

        startInstallButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                Log.d(TAG, "onClick: Clicked");

                startInstall(view);


            }
        });

        ViewPager pager = (ViewPager) findViewById(R.id.viewPager);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
//
//        mImageViewPager = (ViewPager) findViewById(R.id.pager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(pager, true);

    }

    public void startInstall(View v)
    {

        Intent installIntent = new Intent(SetupStepOne.this, SetupStepTwo.class);
        SetupStepOne.this.startActivity(installIntent);

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
}
