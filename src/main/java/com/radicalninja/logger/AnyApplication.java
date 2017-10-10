package com.radicalninja.logger;

import android.app.Application;
import android.content.Context;

/**
 * Created by gwicks on 6/10/2017.
 */

public class AnyApplication extends Application {

    private static Context context;

    public void onCreate(){
        super.onCreate();
        AnyApplication.context = getApplicationContext();
    }

    public static Context getAppContext(){
        return AnyApplication.context;
    }
}
