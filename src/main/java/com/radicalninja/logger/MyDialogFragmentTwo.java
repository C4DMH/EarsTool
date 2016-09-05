package com.radicalninja.logger;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

/**
 * Created by gwicks on 9/08/2016.
 */
public class MyDialogFragmentTwo extends DialogFragment {


    private String messageToDisplay = "THis is the message to dispalt";


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle("Thank You :)")
                .setMessage("Your Video has been sucessfully recorded, and will be upload when a WiFi connection is available")
                .setPositiveButton("OK", null)
                .create();
    }
}