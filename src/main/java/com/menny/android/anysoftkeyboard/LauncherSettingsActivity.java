/*
 * Copyright (c) 2013 Menny Even-Danan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.menny.android.anysoftkeyboard;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.anysoftkeyboard.ui.settings.setup.Intro;
import com.radicalninja.logger.VideoActivity;

/*
 * Why is this class exists?
 * It is a forwarder activity that I can disable, thus not showing Settings in the launcher menu.
 */
public class LauncherSettingsActivity extends Activity {

    private final static String LAUNCHED_KEY = "LAUNCHED_KEY";
    private static final String TAG = "LauncherSettingsActivit";
    /**
     * This flag will help us keeping this activity inside the task, thus returning to the TASK when relaunching (and not to re-create the activity)
     */
    private boolean mLaunched = false;
    SharedPreferences wmbPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Launcer settings activity oNCREATE");
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
            mLaunched = savedInstanceState.getBoolean(LAUNCHED_KEY, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: SHOULD BE VERY FIRST THING I SEE");

        wmbPreference = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isFirstRun = wmbPreference.getBoolean("FIRSTRUN", true);
        //Toast.makeText(this, "THIS IS MAIN ACTIVIY, BEFORE (isfirstRun), and mLaunced  = " + mLaunched, Toast.LENGTH_LONG).show();
        if (isFirstRun || mLaunched == false) {
            Log.d(TAG, "onResume: either is first run or mlaunced false");
            Log.d(TAG, "onResume: isfirstrun: " + isFirstRun);
            Log.d(TAG, "onResume: mlaucned: " + mLaunched);
            //Toast.makeText(this, "THIS IS MAIN ACTIVIY, IN (isfirstRun)", Toast.LENGTH_LONG).show();
            // Code to run once
//            Toast.makeText(this, "inside if else [] (mLaunced)" , Toast.LENGTH_LONG).show();


            startActivity(new Intent(this, Intro.class));
            mLaunched = true;

//            if (SetupSupport.isThisKeyboardEnabled(getApplication())) {
//                Toast.makeText(this, "startActivity MainSettings)" , Toast.LENGTH_LONG).show();
//                startActivity(new Intent(this, MainSettingsActivity.class));
//            } else {
//                Toast.makeText(this, "startActivity MainSettings else thingy" , Toast.LENGTH_LONG).show();
//                Intent startSetupWizard = MainSettingsActivity.createStartActivityIntentForAddingFragmentToUi(
//                        this, MainSettingsActivity.class,
//                        new SetUpKeyboardWizardFragment(), TransitionExperiences.ROOT_FRAGMENT_EXPERIENCE_TRANSITION);
//                startActivity(startSetupWizard);
//                mLaunched = true;
            SharedPreferences.Editor editor = wmbPreference.edit();
            editor.putBoolean("FIRSTRUN", false);
            editor.commit();

            }
        else {
            Log.d(TAG, "onResume: not first time you fuckers");
            startActivity(new Intent(this, VideoActivity.class));
        }
        //Toast.makeText(this, "THIS IS MAIN ACTIVIY, AFTER (isfirstRun)", Toast.LENGTH_LONG).show();


//        if (mLaunched) {
//            Toast.makeText(this, "inside if (mLaunced)" , Toast.LENGTH_LONG).show();
//            //startActivity(new Intent(this, VideoActivity.class));
//            finish();
//        } else {
//            Toast.makeText(this, "inside if else [] (mLaunced)" , Toast.LENGTH_LONG).show();
//            if (SetupSupport.isThisKeyboardEnabled(getApplication())) {
//                Toast.makeText(this, "startActivity MainSettings)" , Toast.LENGTH_LONG).show();
//                startActivity(new Intent(this, MainSettingsActivity.class));
//            } else {
//                Toast.makeText(this, "startActivity MainSettings else thingy" , Toast.LENGTH_LONG).show();
//                Intent startSetupWizard = MainSettingsActivity.createStartActivityIntentForAddingFragmentToUi(
//                        this, MainSettingsActivity.class,
//                        new SetUpKeyboardWizardFragment(), TransitionExperiences.ROOT_FRAGMENT_EXPERIENCE_TRANSITION);
//                startActivity(startSetupWizard);
//            }
//        }

        mLaunched = true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(LAUNCHED_KEY, mLaunched);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mLaunched = savedInstanceState.getBoolean(LAUNCHED_KEY);
    }

}

//
//public class LauncherSettingsActivity extends Activity {
//
//    private final static String LAUNCHED_KEY = "LAUNCHED_KEY";
//    /**
//     * This flag will help us keeping this activity inside the task, thus returning to the TASK when relaunching (and not to re-create the activity)
//     */
//    private boolean mLaunched = false;
//    SharedPreferences wmbPreference;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (savedInstanceState != null)
//            mLaunched = savedInstanceState.getBoolean(LAUNCHED_KEY, false);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        wmbPreference = PreferenceManager.getDefaultSharedPreferences(this);
//        boolean isFirstRun = wmbPreference.getBoolean("FIRSTRUN", true);
////        Toast.makeText(this, "THIS IS MAIN ACTIVIY, BEFORE (isfirstRun)", Toast.LENGTH_LONG).show();
//        if (isFirstRun || mLaunched == false) {
////            Toast.makeText(this, "THIS IS MAIN ACTIVIY, IN (isfirstRun)", Toast.LENGTH_LONG).show();
//            // Code to run once
////            Toast.makeText(this, "inside if else [] (mLaunced)" , Toast.LENGTH_LONG).show();
//            if (SetupSupport.isThisKeyboardEnabled(getApplication())) {
////                Toast.makeText(this, "startActivity MainSettings)" , Toast.LENGTH_LONG).show();
//                startActivity(new Intent(this, MainSettingsActivity.class));
//            } else {
////                Toast.makeText(this, "startActivity MainSettings else thingy" , Toast.LENGTH_LONG).show();
//                Intent startSetupWizard = MainSettingsActivity.createStartActivityIntentForAddingFragmentToUi(
//                        this, MainSettingsActivity.class,
//                        new SetUpKeyboardWizardFragment(), TransitionExperiences.ROOT_FRAGMENT_EXPERIENCE_TRANSITION);
//                startActivity(startSetupWizard);
//                mLaunched = true;
//
//            }
//
//            SharedPreferences.Editor editor = wmbPreference.edit();
//            editor.putBoolean("FIRSTRUN", false);
//            editor.commit();
//
//            //finish();
//        } else {
//            startActivity(new Intent(this, VideoActivity.class));
//        }
////        Toast.makeText(this, "THIS IS MAIN ACTIVIY, AFTER (isfirstRun)", Toast.LENGTH_LONG).show();
//
//
////        if (mLaunched) {
////            Toast.makeText(this, "inside if (mLaunced)" , Toast.LENGTH_LONG).show();
////            //startActivity(new Intent(this, VideoActivity.class));
////            finish();
////        } else {
////            Toast.makeText(this, "inside if else [] (mLaunced)" , Toast.LENGTH_LONG).show();
////            if (SetupSupport.isThisKeyboardEnabled(getApplication())) {
////                Toast.makeText(this, "startActivity MainSettings)" , Toast.LENGTH_LONG).show();
////                startActivity(new Intent(this, MainSettingsActivity.class));
////            } else {
////                Toast.makeText(this, "startActivity MainSettings else thingy" , Toast.LENGTH_LONG).show();
////                Intent startSetupWizard = MainSettingsActivity.createStartActivityIntentForAddingFragmentToUi(
////                        this, MainSettingsActivity.class,
////                        new SetUpKeyboardWizardFragment(), TransitionExperiences.ROOT_FRAGMENT_EXPERIENCE_TRANSITION);
////                startActivity(startSetupWizard);
////            }
////        }
//
//        mLaunched = true;
//    }
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putBoolean(LAUNCHED_KEY, mLaunched);
//    }
//
//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        mLaunched = savedInstanceState.getBoolean(LAUNCHED_KEY);
//    }
//
//}
//
