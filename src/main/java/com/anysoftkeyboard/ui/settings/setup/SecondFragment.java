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

public class SecondFragment extends Fragment {

    private static final String TAG = "SecondFragment";
//
//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        if (isVisibleToUser) {
//            ((SetupStepOne)getActivity()).updateStatusBarColor("#0075e1", this);
//            Log.d(TAG, "onCreateView: update coulour in : 2");
//
//        }
//        else {
//            Log.d(TAG, "setUserVisibleHint: in else");
//        }
//    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.splash_one, container, false);
        ((SetupStepOne)getActivity()).updateStatusBarColor("#0075e1", this);
        Log.d(TAG, "onCreateView: update coulour in : 2");






//        TextView tv = (TextView) v.findViewById(R.id.tvFragSecond);
//        tv.setText(getArguments().getString("msg"));

        return v;
    }

    public static SecondFragment newInstance(String text) {

        Log.d(TAG, "newInstance: second");

        SecondFragment f = new SecondFragment();


//        Bundle b = new Bundle();
//        b.putString("msg", text);
//
//        f.setArguments(b);

        return f;
    }


}
