package com.radicalninja.logger;

import android.app.Activity;
import android.os.Bundle;

import com.menny.android.anysoftkeyboard.R;

/**
 * Created by Aishwarya on 7/7/2015.
 */
public class TakeVideoActivity extends Activity {

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        setContentView(R.layout.take_video);
        //Setting content View

        getFragmentManager().beginTransaction()
                .replace(R.id.takeVideo_fragment, Camera2VideoFragment.newInstance()).commit();
        //Calling fragment

    }

}
