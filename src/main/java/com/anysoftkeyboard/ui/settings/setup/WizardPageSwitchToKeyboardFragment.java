package com.anysoftkeyboard.ui.settings.setup;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.sevencupsoftea.ears.R;

//import com.menny.android.anysoftkeyboard.R;

public class WizardPageSwitchToKeyboardFragment extends WizardPageBaseFragment {
    private static final String TAG = "WizardPageSwitchToKeybo";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: 1");

        return inflater.inflate(R.layout.keyboard_setup_wizard_page_switch_to_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated: this is SwitchtoKeyboardFragmentPage");
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.go_to_switch_keyboard_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.showInputMethodPicker();
            }
        });
    }

    @Override
    protected boolean isStepCompleted(@NonNull Context context) {
        Log.d(TAG, "isStepCompleted: is step completed?");
        Log.d(TAG, "isStepCompleted: " + SetupSupport.isThisKeyboardSetAsDefaultIME(context));
        return SetupSupport.isThisKeyboardSetAsDefaultIME(context);
    }

    @Override
    protected boolean isStepPreConditionDone(@NonNull Context context) {
        Log.d(TAG, "isStepPreConditionDone: is last step completed? : " + SetupSupport.isThisKeyboardEnabled(context));
        return SetupSupport.isThisKeyboardEnabled(context);
    }
}
