package com.radicalninja.logger;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import com.sevencupsoftea.ears.R;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by gwicks on 31/03/2018.
 */

public class TimeSpentWith extends DialogFragment {



    ArrayList mSelectedItems;
    String[] options;
    String[] selected;

    ArrayList<String> selectedNew;

    public interface NoticeDialogListener {
        //public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogPositiveClick(ArrayList results);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    NoticeDialogListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mSelectedItems = new ArrayList();  // Where we track the selected items
        options = getResources().getStringArray(R.array.time_spent);
        //selected = new String[];
        selectedNew = new ArrayList<String>();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Set the dialog title
        builder.setTitle(R.string.time_prompt)
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setMultiChoiceItems(R.array.time_spent, null,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which,
                                                boolean isChecked) {
                                if (isChecked) {
                                    // If the user checked the item, add it to the selected items
                                    mSelectedItems.add(which);
                                    selectedNew.add(options[which]);
                                    //Log.d(TAG, "onClick: mselected items which = " + mSelectedItems.get(which));
                                } else if (mSelectedItems.contains(which)) {
                                    // Else, if the item is already in the array, remove it
                                    mSelectedItems.remove(Integer.valueOf(which));
                                }
                            }
                        })
                // Set the action buttons
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Log.d(TAG, "onClick: mSelected items: " + mSelectedItems);
                        Log.d(TAG, "onClick: interger id = " + id);

                        for(String item : selectedNew){
                            Log.d(TAG, "onClick: item" + item);
                        }
                        // User clicked OK, so save the mSelectedItems results somewhere
                        // or return them to the component that opened the dialog
                        //Toast.makeText(getActivity(), "Hello", Toast.LENGTH_LONG).show();
                        NoticeDialogListener     activity = (NoticeDialogListener) getActivity();
                        //mListener.onDialogPositiveClick(mSelectedItems);
                        //activity.onDialogPositiveClick(mSelectedItems);
                        activity.onDialogPositiveClick(selectedNew);

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //Toast.makeText(getActivity(), "HelloHello", Toast.LENGTH_LONG).show();
                        mListener.onDialogNegativeClick(TimeSpentWith.this);
                    }
                });

        return builder.create();

    }





}
