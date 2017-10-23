package com.radicalninja.logger;

import android.app.Application;
import android.content.Context;
import android.util.Log;

/**
 * Created by gwicks on 6/10/2017.
 */

public class AnyApplication extends Application {

    private static Context context;
    private static final String TAG = "AnyApplication";

    public void onCreate(){

        Log.d(TAG, "onCreate: We hit ititititititititititititit");
        super.onCreate();
        //AnyApplication.context = getApplicationContext();
        context = this ;
    }

    public static Context getAppContext(){
        return context;
    }
}
