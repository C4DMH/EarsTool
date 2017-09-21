package com.anysoftkeyboard.ui.settings.setup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anysoftkeyboard.ui.settings.KeyboardAddOnBrowserFragment;
import com.anysoftkeyboard.ui.settings.KeyboardThemeSelectorFragment;
import com.anysoftkeyboard.ui.settings.MainSettingsActivity;
import com.menny.android.anysoftkeyboard.R;

import net.evendanan.chauffeur.lib.experiences.TransitionExperiences;

public class WizardPageDoneAndMoreSettingsFragment extends WizardPageBaseFragment implements View.OnClickListener {
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
            case R.id.go_to_home_fragment_action:
                activity.onNavigateToRootClicked(v);
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
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                // intent.setComponent(new ComponentName("com.android.settings","com.android.settings.Settings$SecuritySettingsActivity"));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent,0);
                break;

            case R.id.AppUsageSettings:
                Intent intentTwo = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                startActivity(intentTwo);
                break;

        }
    }
}
