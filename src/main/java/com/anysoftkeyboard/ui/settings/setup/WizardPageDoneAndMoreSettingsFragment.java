package com.anysoftkeyboard.ui.settings.setup;

import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.anysoftkeyboard.ui.settings.KeyboardAddOnBrowserFragment;
import com.anysoftkeyboard.ui.settings.KeyboardThemeSelectorFragment;
import com.anysoftkeyboard.ui.settings.MainSettingsActivity;
import com.menny.android.anysoftkeyboard.R;
import com.radicalninja.logger.MainActivity;

import net.evendanan.chauffeur.lib.experiences.TransitionExperiences;

public class WizardPageDoneAndMoreSettingsFragment extends WizardPageBaseFragment implements View.OnClickListener {

    private static final String TAG = "WizardPageDoneAndMoreSe";
    ImageView imageView3;
    ImageView imageView1;
    ImageView imageView2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.keyboard_setup_wizard_page_additional_settings_layout, container, false);


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {


        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.notificationSettings).setOnClickListener(this);
        view.findViewById(R.id.AppUsageSettings).setOnClickListener(this);
        view.findViewById(R.id.go_to_home_fragment_action).setOnClickListener(this);
        view.findViewById(R.id.go_to_languages_action).setOnClickListener(this);
        view.findViewById(R.id.go_to_theme_action).setOnClickListener(this);
        view.findViewById(R.id.go_to_all_settings_action).setOnClickListener(this);
        view.findViewById(R.id.TakeReferencePic).setOnClickListener(this);



        imageView1 = (ImageView)view.findViewById(R.id.imageView1);
        if(isAccessGranted()){
            Log.d(TAG, "onViewCreated: access granted");
            imageView1.setImageResource(R.drawable.green_tick);
        }

        imageView2 = (ImageView)view.findViewById(R.id.imageView2);
        if(checkNotificationEnabled()){
            Log.d(TAG, "onViewCreated: notifications");
            imageView2.setImageResource(R.drawable.green_tick);
        }

        imageView3 = (ImageView)view.findViewById(R.id.imageView3);

    }

    @Override
    protected boolean isStepCompleted(@NonNull Context context) {
        return false;//this step is never done! You can always configure more :)

    }

    @Override
    protected boolean isStepPreConditionDone(@NonNull Context context) {
        return SetupSupport.isThisKeyboardSetAsDefaultIME(context);
    }

    @Override
    public void onClick(View v) {
        MainSettingsActivity activity = (MainSettingsActivity) getActivity();
        switch (v.getId()) {
//            case R.id.show_keyboard_view_action:
//                InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
//                if (inputMethodManager != null) {
//                    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
//                }
//                break;
//            case R.id.go_to_home_fragment_action:
//                activity.onNavigateToRootClicked(v);
//                break;

            case R.id.go_to_home_fragment_action:
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.go_to_languages_action:
                activity.addFragmentToUi(new KeyboardAddOnBrowserFragment(), TransitionExperiences.DEEPER_EXPERIENCE_TRANSITION);
                break;
            case R.id.go_to_theme_action:
                activity.addFragmentToUi(new KeyboardThemeSelectorFragment(), TransitionExperiences.DEEPER_EXPERIENCE_TRANSITION);
                break;
            case R.id.go_to_all_settings_action:
                activity.onNavigateToRootClicked(v);
                activity.openDrawer();
                break;

            case R.id.notificationSettings:
                Log.d(TAG, "onClick: 2");
                showDialog3(v);
//                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
//                //intent.putExtra("finishActivityOnSaveCompleted", true);
//                // intent.setComponent(new ComponentName("com.android.settings","com.android.settings.Settings$SecuritySettingsActivity"));
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivityForResult(intent,0);
                break;

            case R.id.AppUsageSettings:
                Log.d(TAG, "onClick: 1");
                Intent intentTwo = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                //intentTwo.putExtra("finishActivityOnSaveCompleted", true);

                startActivity(intentTwo);
                break;

            case R.id.TakeReferencePic:
                Log.d(TAG, "onClick: I clicked it");
                Intent intentThree = new Intent(getActivity(), FaceDetect.class);
                startActivity(intentThree);
                imageView3.setImageResource(R.drawable.green_tick);

                break;

        }
    }

//
    public void showDialog3(View v) {


        Log.d("History", "In show Dialog3");
        //new ViewWeekStepCountTask().execute();
//        DialogFragment dialog = (DialogFragment) DialogFragment.instantiate(getActivity(), "MyDialogFragment" );
//        dialog.show(getFragmentManager(), "dialog");
        //myDialogFragment.show(getFragmentManager(), "INTO");

//        MyDialogFragment myDialogFragment = new MyDialogFragment();
//        myDialogFragment.show(getFragmentManager(), "INTO");

        AlertDialog ad = new AlertDialog.Builder(getActivity())
                .create();
        ad.setCancelable(false);
        ad.setTitle("IMPORTANT");
        ad.setMessage("After changing setting for EARS Tool to ON, please press back button until " +
                "you return to this menu!");
        ad.setButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                //intent.putExtra("finishActivityOnSaveCompleted", true);
                // intent.setComponent(new ComponentName("com.android.settings","com.android.settings.Settings$SecuritySettingsActivity"));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        ad.show();
    }

    public boolean isAccessGranted() {
        try {
            PackageManager packageManager = getActivity().getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getActivity().getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) getActivity().getSystemService(Context.APP_OPS_SERVICE);
            int mode = 0;
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.KITKAT) {
                mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                        applicationInfo.uid, applicationInfo.packageName);
            }
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public boolean checkNotificationEnabled() {
        try{
            Log.d(TAG, "checkNotificationEnabled: in try");
            if(Settings.Secure.getString(getActivity().getContentResolver(),
                    "enabled_notification_listeners").contains(getActivity().getApplication().getPackageName()))
            {
                Log.d(TAG, "checkNotificationEnabled: in true");

                Log.d(TAG, "checkNotificationEnabled: true");
                return true;
            } else {

                Log.d(TAG, "checkNotificationEnabled: ruturn false");
                return false;
            }

        }catch(Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "checkNotificationEnabled: Did not get into settings?");
        return false;
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if(requestCode == 1){
//            if(requestCode == RESULT_OK){
//
//                //imageView3 = (ImageView)findViewById(imageView2);
//                Log.d(TAG, "onActivityResult: setting image view for picture");
//
//                imageView3.setImageResource(R.drawable.green_tick);
//                }
//
//            }
//        }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ONONONONONONONONONONONON");
        imageView3.invalidate();
        imageView1.invalidate();
        imageView2.invalidate();


        if(isAccessGranted()){
            Log.d(TAG, "onViewCreated: access granted");
            imageView1.setImageResource(R.drawable.green_tick);
        }


        if(checkNotificationEnabled()){
            Log.d(TAG, "onViewCreated: notifications");
            imageView2.setImageResource(R.drawable.green_tick);
        }


    }
}
