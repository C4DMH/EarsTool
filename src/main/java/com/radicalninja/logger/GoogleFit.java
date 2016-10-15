package com.radicalninja.logger;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.result.DailyTotalResult;

import java.util.List;

/**
 * Created by gwicks on 13/09/2016.
 */
public class GoogleFit {

    private GoogleApiClient client;
    private PendingResult<DailyTotalResult> stepsResult;
    private int stepsTotal;




    public void buildFitnessClient(final Context context, final GoogleApiClient.ConnectionCallbacks connectionCallbacks, final GoogleApiClient.OnConnectionFailedListener connectionFailedListener) {
        client = new GoogleApiClient.Builder(context.getApplicationContext()).
                addApi(Fitness.HISTORY_API).
                addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ)).
                addConnectionCallbacks(connectionCallbacks).
                addOnConnectionFailedListener(connectionFailedListener)
                .build();

        client.connect();

        stepsResult = Fitness.HistoryApi.readDailyTotal(client, DataType.TYPE_STEP_COUNT_DELTA);
        stepsResult.setResultCallback(
                new ResultCallback<DailyTotalResult>() {
                    @Override
                    public void onResult(@NonNull DailyTotalResult dailyTotalResult) {
                        Log.d("Google", "Result: " + dailyTotalResult);
                        List<DataPoint> points = dailyTotalResult.getTotal().getDataPoints();
                        if(!points.isEmpty()){
                            stepsTotal = points.get(0).getValue(Field.FIELD_STEPS).asInt();
                        }
                        Log.d("Google", "StepsTotal = " + stepsTotal);
                    }
                }
        );
    }


//    public GoogleApiClient mClient = new GoogleApiClient.Builder()
//                .addApi(Fitness.HISTORY_API)
//                .setDefaultAccount()
//                .addConnectionCallbacks(...)
//                .addOnConnectionFailedListener(...)
//                .build();



}
