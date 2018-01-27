package com.anysoftkeyboard.ui.settings.setup;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.menny.android.anysoftkeyboard.R;

/**
 * Created by gwicks on 20/01/2018.
 */

public class FourthFragment extends Fragment {

    private static final String TAG = "FourthFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.splash_three, container, false);
        ((SetupStepOne)getActivity()).updateStatusBarColor("#0075e1", this);
        Log.d(TAG, "onCreateView: update coulour in : 4");


//
//        TextView tv = (TextView) v.findViewById(R.id.tvFragThird);
//        tv.setText(getArguments().getString("msg"));

        return v;
    }

    public static FourthFragment newInstance(String text) {

        FourthFragment f = new FourthFragment();
//        Bundle b = new Bundle();
//        b.putString("msg", text);
//
//        f.setArguments(b);

        return f;
    }
}
