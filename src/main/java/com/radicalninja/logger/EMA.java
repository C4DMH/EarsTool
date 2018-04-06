package com.radicalninja.logger;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.anysoftkeyboard.ui.settings.setup.FinishInstallScreen;
import com.sevencupsoftea.ears.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

/**
 * Created by gwicks on 31/03/2018.
 */

public class EMA extends Activity implements SeekBar.OnSeekBarChangeListener, TimeSpentWith.NoticeDialogListener{

    private static final String TAG = "EMA";
    private ArrayList<String> mSelectedTimeWith;

    public TextView t1;
    public TextView t2;
    public TextView t3;
    public TextView t4;
    public TextView t5;
    public TextView t6;
    public TextView t7;

    public SeekBar s1;
    public SeekBar s2;
    public SeekBar s3;
    public SeekBar s4;
    public SeekBar s5;
    public SeekBar s6;
    public SeekBar s7;

    public int answer1;
    public int answer2;
    public int answer3;
    public int answer4;
    public int answer5;
    public int answer6;
    public int answer7;

    public Button timeSpentButton;
    public Button finishedButton;





    public ArrayList<String> questions;

    //public static NotificationManager notificationManager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.survey_layout);

        mSelectedTimeWith = new ArrayList();
        Log.d(TAG, "onCreate: IN MAIN ACTIVITY FUCKER");


        String path = getExternalFilesDir(null) + "/EMA/";

        File directory = new File(path);

        if(!directory.exists()){
            directory.mkdirs();
        }





        Calendar c = Calendar.getInstance();
        //System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy_HHmm");
        final String formattedDate = df.format(c.getTime());
        Log.d(TAG, "onCreate: formated date  = " + formattedDate);

//        if(notificationManager == null){
//            Log.d(TAG, "onCreate: EMA loading the notificataion manager");
//            notificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
//            Log.d(TAG, "onCreate: noti manager should be loaded");
//        }
//        Log.d(TAG, "onCreate: just past noti manager");


        questions = new ArrayList<String>();
        questions.add("How confident do you feel right now?");
        questions.add("How happy do you feel right now?");
        questions.add("How calm do you feel right now?");
        questions.add("How anxious do you feel right now?");
        questions.add("How stressed do you feel right now?");
        questions.add("How sad do you feel right now?");
        questions.add("How angry do you feel right now?");
        Log.d(TAG, "onCreate: 1");

        Collections.shuffle(questions);

        t1 = findViewById(R.id.question1);
        t1.setText(questions.get(0));

        s1 = findViewById(R.id.seekBar);
        s1.setOnSeekBarChangeListener(this);

        t2 = findViewById(R.id.question2);
        t2.setText(questions.get(1));

        s2 = findViewById(R.id.seekBar2);
        s2.setOnSeekBarChangeListener(this);

        t3 = findViewById(R.id.question3);
        t3.setText(questions.get(2));

        s3 = (SeekBar)findViewById(R.id.seekBar3);
        s3.setOnSeekBarChangeListener(this);

        t4 = (TextView)findViewById(R.id.question4);
        t4.setText(questions.get(3));


        s4 = (SeekBar)findViewById(R.id.seekBar4);
        s4.setOnSeekBarChangeListener(this);

        t5 = (TextView)findViewById(R.id.question5);
        t5.setText(questions.get(4));

        s5 = (SeekBar)findViewById(R.id.seekBar5);
        s5.setOnSeekBarChangeListener(this);

        t6 = (TextView)findViewById(R.id.question6);
        t6.setText(questions.get(5));

        s6 = (SeekBar)findViewById(R.id.seekBar6);
        s6.setOnSeekBarChangeListener(this);

        t7 = (TextView)findViewById(R.id.question7);
        t7.setText(questions.get(6));

        s7 = (SeekBar)findViewById(R.id.seekBar7);
        s7.setOnSeekBarChangeListener(this);

        timeSpentButton = findViewById(R.id.button_time_spent);

        timeSpentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchTimeSpentDialog();
            }
        });

        finishedButton = (Button)findViewById(R.id.button);
        finishedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String uri = (getExternalFilesDir(null) + "/EMA/"+ formattedDate + ".txt");
                File file = new File(uri);
                Log.d(TAG, "onClick: the file url is : " + uri);

                writeToFile(file, "Question,Value\n");
                writeToFile(file, t1.getText().toString() +"," + Integer.toString(answer1) + "\n");
                writeToFile(file, t2.getText().toString() +"," + Integer.toString(answer2) + "\n");
                writeToFile(file, t3.getText().toString() +"," + Integer.toString(answer3) + "\n");
                writeToFile(file, t4.getText().toString() +"," + Integer.toString(answer4) + "\n");
                writeToFile(file, t5.getText().toString() +"," + Integer.toString(answer5) + "\n");
                writeToFile(file, t6.getText().toString() +"," + Integer.toString(answer6) + "\n");
                writeToFile(file, t7.getText().toString() +"," + Integer.toString(answer7) + "\n");
                writeToFile(file, "Time Spent with:\n");
                writeToFile(file, mSelectedTimeWith.toString());

                Log.d(TAG, "onClick: 2");



//                Intent startMain = new Intent(Intent.ACTION_MAIN);
//                startMain.addCategory(Intent.CATEGORY_HOME);
//                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(startMain);
//                Log.d(TAG, "onClick: 3");


                Toast.makeText(getBaseContext(), "Thank you for completing the questionaire, the next one will be in roughly two hours  :)", Toast.LENGTH_LONG).show();
                Intent returnToFinish = new Intent(EMA.this, FinishInstallScreen.class);

                startActivity(returnToFinish);

            }
        });

        //startEMAAlarm();
        Log.d(TAG, "onCreate: 4");

    }

//    public NotificationManager getNotificationManager(){
//        if(notificationManager == null){
//            notificationManager = (NotificationManager)this.getSystemService(Context.NOTIFICATION_SERVICE);
//        }
//        return notificationManager;
//    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if(seekBar.equals(s1)){
            Log.d(TAG, "onProgressChanged: the questions was: " + questions.get(0) +  " the progress is: " + i);
            answer1 = i;
        }
        else if(seekBar.equals(s2)){
            Log.d(TAG, "onProgressChanged: the questions was: " + questions.get(1) + " the progress is: " + i);
            answer2 = i;
        }
        else if(seekBar.equals(s3)){
            Log.d(TAG, "onProgressChanged: the questions was: " + questions.get(2) + " the progress is: " + i);
            answer3 = i;
        }
        else if(seekBar.equals(s4)){
            Log.d(TAG, "onProgressChanged: the questions was: " + questions.get(3) + " the progress is: " + i);
            answer4 = i;
        }
        else if(seekBar.equals(s5)){
            Log.d(TAG, "onProgressChanged: the questions was: " + questions.get(4) + " the progress is: " + i);
            answer5 = i;
        }
        else if(seekBar.equals(s6)){
            Log.d(TAG, "onProgressChanged: the questions was: " + questions.get(5) + " the progress is: " + i);
            answer6 = i;
        }
        else if(seekBar.equals(s7)){
            Log.d(TAG, "onProgressChanged: the questions was: " + questions.get(6) + " the progress is: " + i);
            answer7 = i;
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private static void writeToFile(File file, String data) {

        FileOutputStream stream = null;
        System.out.println("The state of the media is: " + Environment.getExternalStorageState());

        //OutputStreamWriter stream = new OutputStreamWriter(openFileOutput(file), Context.MODE_APPEND);
        try {
            Log.e("History", "In try");
            Log.d(TAG, "writeToFile: ");
            stream = new FileOutputStream(file, true);
            Log.d(TAG, "writeToFile: 2");
            stream.write(data.getBytes());
            Log.d(TAG, "writeToFile: 3");
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

    @Override
    public void onDialogPositiveClick(ArrayList items) {
        // User touched the dialog's positive button
        //Toast.makeText(this, "Clicked positive array saved", Toast.LENGTH_LONG).show();
        mSelectedTimeWith = items;
        Log.d(TAG, "onDialogPositiveClick: mselected ittems");
        for(String group : mSelectedTimeWith){
            Log.d(TAG, "\nonDialogPositiveClick: The current item is: " + group);

        }

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button
        Toast.makeText(this,"Clicked negative nothing saved", Toast.LENGTH_LONG).show();

    }

    public void launchTimeSpentDialog(){
        DialogFragment newFragment = new TimeSpentWith();
        newFragment.show(getFragmentManager(), "timeSpent");
    }

    @Override
    public boolean isDestroyed() {
        Log.d(TAG, "isDestroyed: in EMA on destroy");
        return super.isDestroyed();
    }
}
