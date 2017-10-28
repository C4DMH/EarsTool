/*
 * Copyright (C)EARSTool
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package com.radicalninja.logger;

import android.app.Application;
import android.content.Context;

/**
 * Created by gwicks on 6/10/2017.
 * This class provides a static context if need anywhere in the Application. Currently this Class is NOT used
 * anywhere
 */

public class AnyApplication extends Application {

    private static Context context;
    private static final String TAG = "AnyApplication";

    public void onCreate(){

        super.onCreate();
        context = this ;
    }

    public static Context getAppContext(){
        return context;
    }
}
