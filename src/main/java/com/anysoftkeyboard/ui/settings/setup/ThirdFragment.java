package com.anysoftkeyboard.ui.settings.setup;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sevencupsoftea.ears.R;

//import com.menny.android.anysoftkeyboard.R;

/**
 * Created by gwicks on 20/01/2018.
 */

public class ThirdFragment extends Fragment {

    private static final String TAG = "ThirdFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.splash_two, container, false);
        //((SetupStepOne)getActivity()).updateStatusBarColor("#0075e1", this);
        Log.d(TAG, "onCreateView: update coulour in : 3");


//
//        TextView tv = (TextView) v.findViewById(R.id.tvFragThird);
//        tv.setText(getArguments().getString("msg"));

        return v;
    }

    public static ThirdFragment newInstance(String text) {

        Log.d(TAG, "newInstance: third");

        ThirdFragment f = new ThirdFragment();
//        Bundle b = new Bundle();
//        b.putString("msg", text);
//
//        f.setArguments(b);

        return f;
    }
}