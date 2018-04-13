package com.radicalninja.logger.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

/**
 * Created by gwicks on 3/04/2018.
 */

public class EmailSecureDeviceID extends DialogFragment {

//
//
//
//    public interface NoticeDialogListener {
//        //public void onDialogPositiveClick(DialogFragment dialog);
//        public void onDialogPositiveClick(ArrayList results);
//
//        public void onDialogNegativeClick(DialogFragment dialog);
//    }

    //TimeSpentWith.NoticeDialogListener mListener;
    private String secureDeviceID;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        secureDeviceID = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);

    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Set the dialog title
        builder.setTitle("Please press Send Email to connect your email to your Device ID\nThis will enable us to connect your Survey answers to your phone data.")

                .setPositiveButton("Send Email", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //Log.d(TAG, "onClick: mSelected items: " + mSelectedItems);
                        //Log.d(TAG, "onClick: interger id = " + id);

//                        for (String item : selectedNew) {
//                            Log.d(TAG, "onClick: item" + item);
//                        }
                        // User clicked OK, so save the mSelectedItems results somewhere
                        // or return them to the component that opened the dialog
                        //Toast.makeText(getActivity(), "Hello", Toast.LENGTH_LONG).show();
                        //TimeSpentWith.NoticeDialogListener activity = (TimeSpentWith.NoticeDialogListener) getActivity();
                        //mListener.onDialogPositiveClick(mSelectedItems);
                        //activity.onDialogPositiveClick(mSelectedItems);
                        //activity.onDialogPositiveClick(selectedNew);
                        sendEmailDeviceID();

                    }
                })

                // Not sure we want a cancel button, so going to comment this out 10th April 2018
//                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int id) {
//                        //Toast.makeText(getActivity(), "HelloHello", Toast.LENGTH_LONG).show();
//                        //mListener.onDialogNegativeClick(EmailSecureDeviceID.this);
//                        dismiss();
//                    }
//                });

        ;
        return builder.create();

    }

    public void sendEmailDeviceID() {
        String[] recipient = new String[]{"adaptlab@uoregon.edu"};

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto","adaptlab@uoregon.edu", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, secureDeviceID);
        emailIntent.putExtra(Intent.EXTRA_TEXT, "No Need to put anything here, just press send :)");
        startActivity(Intent.createChooser(emailIntent, "Select your most used email provider"));

//        Intent sendMail = new Intent();
//        sendMail.setAction(Intent.ACTION_SENDTO);
//        //sendMail.addCategory(Intent.CATEGORY_APP_EMAIL);
//        //sendMail.setType("message/rfc822");
//        sendMail.setType("text/plain");
////        sendMail.putExtra(Intent.EXTRA_EMAIL, recipient);
////        sendMail.putExtra(Intent.EXTRA_SUBJECT, secureDeviceID);
//        //sendMail.putExtra(Intent.EXTRA_TEXT, mCrashReportDetails.crashReportText);
//
//        try {
//            Intent sender = Intent.createChooser(sendMail, "Select your most used email provider");
//            //sender.addCategory(Intent.CATEGORY_APP_EMAIL);
//            sender.putExtra(Intent.EXTRA_EMAIL, sendMail.getStringArrayExtra(Intent.EXTRA_EMAIL));
//            sender.putExtra(Intent.EXTRA_SUBJECT, sendMail.getStringExtra(Intent.EXTRA_SUBJECT));
//            //sender.putExtra(Intent.EXTRA_TEXT, mCrashReportDetails.crashReportText);
//
//            //com.anysoftkeyboard.utils.Log.i(TAG, "Will send crash report using " + sender);
//            startActivity(sender);
//        } catch (android.content.ActivityNotFoundException ex) {
//            Toast.makeText(getActivity(), "Unable to send bug report via e-mail!", Toast.LENGTH_LONG).show();
//        }

        //finish();
    }
}