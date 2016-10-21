package com.radicalninja.logger;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResult;
import com.menny.android.anysoftkeyboard.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by gwicks on 13/09/2016.
 */




public class GoogleFit extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private GoogleApiClient mGoogleApiClient;

//
//    private Button mButtonViewWeek;
//    private Button mButtonViewToday;
//    private Button mButtonAddSteps;
//    private Button mButtonUpdateSteps;
//    private Button mButtonDeleteSteps;


    String directoryName = "/videoDIARY/";

    GPSTracker gps;
    double latitude;
    double longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        initViews();


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addConnectionCallbacks(this)
                .enableAutoManage(this, 0, this)
                .build();

//        DailyTotalResult result = Fitness.HistoryApi.readDailyTotal( mGoogleApiClient, DataType.TYPE_STEP_COUNT_DELTA ).await(0, TimeUnit.MINUTES);
//        showDataSet(result.getTotal());
//        Toast.makeText(this, "THe steps for todays is: " + result, Toast.LENGTH_LONG).show();
        Log.d("Fit", "Start of Google Fit");
        new ViewWeekStepCountTask().execute();

    }

//    private void initViews() {
//        mButtonViewWeek = (Button) findViewById(R.id.btn_view_week);
//        mButtonViewToday = (Button) findViewById(R.id.btn_view_today);
//        mButtonAddSteps = (Button) findViewById(R.id.btn_add_steps);
//        mButtonUpdateSteps = (Button) findViewById(R.id.btn_update_steps);
//        mButtonDeleteSteps = (Button) findViewById(R.id.btn_delete_steps);
//
//        mButtonViewWeek.setOnClickListener(this);
//        mButtonViewToday.setOnClickListener(this);
//        mButtonAddSteps.setOnClickListener(this);
//        mButtonUpdateSteps.setOnClickListener(this);
//        mButtonDeleteSteps.setOnClickListener(this);
//    }


    @Override
    public void onConnectionSuspended(int i) {
        Log.e("HistoryAPI", "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("HistoryAPI", "onConnectionFailed");
    }

    public void onConnected(@Nullable Bundle bundle) {
        Log.e("HistoryAPI", "onConnected");
    }


    @Override
    public void onClick(View v) {
//
    }


    private void writeToFile(File file, String data, Context context) {
//        Calendar cal = Calendar.getInstance();
//        Date now = new Date();
//        cal.setTime(now);
//        long endTime = cal.getTimeInMillis();
//        File path = context.getExternalFilesDir(null);
//        File file = new File(path, "googleFit.txt");


//        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + directoryName + "GoogleFit_" + endTime + ".txt");
        FileOutputStream stream = null;
        //OutputStreamWriter stream = new OutputStreamWriter(openFileOutput(file), Context.MODE_APPEND);
        try {
            Log.e("History", "In try");
            stream = new FileOutputStream(file, true);
            stream.write(data.getBytes());
        } catch (FileNotFoundException e) {
            Log.e("History", "In catch");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void displayLastWeeksData() {
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        //cal.add(Calendar.WEEK_OF_YEAR, -1);
        //cal.add(Calendar.HOUR_OF_DAY, -2);
        cal.add(Calendar.DAY_OF_WEEK, -1);
        long startTime = cal.getTimeInMillis();

        java.text.DateFormat dateFormat = DateFormat.getDateInstance();
        Log.e("History", "Range Start: " + dateFormat.format(startTime));
        Log.e("History", "Range End: " + dateFormat.format(endTime));

        //Check how many steps were walked and recorded in the last 7 days
        DataReadRequest readRequest = new DataReadRequest.Builder()
                //.aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .aggregate(DataType.TYPE_ACTIVITY_SEGMENT, DataType.AGGREGATE_ACTIVITY_SUMMARY)
                .bucketByTime(1, TimeUnit.DAYS)
                //.bucketByTime(2, TimeUnit.HOURS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        DataReadResult dataReadResult = Fitness.HistoryApi.readData(mGoogleApiClient, readRequest).await(1, TimeUnit.MINUTES);

        //Used for aggregated data
        if (dataReadResult.getBuckets().size() > 0) {
            Log.e("History", "Number of buckets: " + dataReadResult.getBuckets().size());
            for (Bucket bucket : dataReadResult.getBuckets()) {
                List<DataSet> dataSets = bucket.getDataSets();
                for (DataSet dataSet : dataSets) {
                    showDataSet(dataSet);
                }
            }
        }
        //Used for non-aggregated data
        else if (dataReadResult.getDataSets().size() > 0) {
            Log.e("History", "Number of returned DataSets: " + dataReadResult.getDataSets().size());
            for (DataSet dataSet : dataReadResult.getDataSets()) {
                showDataSet(dataSet);
            }
        }
    }

    private void showDataSet(DataSet dataSet) {

        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();

        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + directoryName + "GoogleFit_" + endTime + ".txt");
        FileOutputStream stream = null;
        Log.e("History", "Start showDataset");
        Log.e("History", "Data returned for Data type: " + dataSet.getDataType().getName());
        DateFormat dateFormat = DateFormat.getDateInstance();
        DateFormat timeFormat = DateFormat.getTimeInstance();

        gps = new GPSTracker(this);
        latitude = gps.getLatitude();
        longitude = gps.getLongitude();

        writeToFile(file, "\nLatitude is: " + latitude, this.getApplicationContext());
        writeToFile(file, "\nLongitude is: " + longitude, this.getApplicationContext());

        Log.e("History", "Latitude is: " + latitude);
        Log.e("History", "Longitude is: " + longitude);

        for (DataPoint dp : dataSet.getDataPoints()) {
            Log.e("History", "Data point:");

            //writeToFile(file, "Data point:\n",this.getApplicationContext() );


            writeToFile(file, "\n" +
                    "Data point:\n", this.getApplicationContext());
            Log.e("History", "\tType: " + dp.getDataType().getName());
            Log.e("History", "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            writeToFile(file, "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)), this.getApplicationContext());
            Log.e("History", "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            writeToFile(file, "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)), this.getApplicationContext());
            for (Field field : dp.getDataType().getFields()) {
                Log.e("History", "\tField: " + field.getName() +
                        " Value: " + dp.getValue(field));
                writeToFile(file, "\n" + field.getName() +
                        " Value: " + "\n" + dp.getValue(field), this.getApplicationContext());
            }
        }
        Log.e("History", "End show data set");

    }

    private class ViewWeekStepCountTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            displayLastWeeksData();
            return null;
        }
    }

}