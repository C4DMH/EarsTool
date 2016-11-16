package com.radicalninja.logger;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import com.menny.android.anysoftkeyboard.R;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;


//public class MainActivity extends FragmentActivity
public class MainActivity extends AppCompatActivity {

	private Button videoButton;
	public Button nextOne;
	private TimePicker timePicker1;
	private PendingIntent alarmIntent;
	//public PendingIntent alarmIntent2;
	public static boolean alarmIsSet = false;
	public int timeHour;
	public int timeMinute;
	public static boolean gotLocation = false;
	public GoogleFit googleFit;

	public static boolean endRepeatingAlarm;

	private PendingIntent fitIntent;

	GPSTracker gps;
	double latitude;
	double longitude;
	String postCode;
	SharedPreferences wmbPreference;

	public static MainActivity instace;

	public boolean taskComplete;


//	public int hour = 13;
//	public int minute = 20;


	public String theCurrentDate;


	public void  SetTheCurrentDate(String date, String time)
	{
		theCurrentDate = date + time;
	}

	public String getTheCurrentDate()
	{
		return theCurrentDate;
	}

	//public Intent intentReminder = new Intent(this, AlarmReceiver.class);

	//public AlarmManager alarmMgr2;


//	public void cancelRepeatingAlarm(){
//		alarmMgr2.cancel(alarmIntent2);
//	}





	public void startAlarm(){



		Calendar cal = Calendar.getInstance();
		long when = cal.getTimeInMillis();
		String timey = Long.toString(when);
		String theTime = convertDate(timey, "dd/MM/yyyy hh:mm:ss");
		theCurrentDate = theTime;
		System.out.println("The time changed into nice format is: " + theTime);
		//Log.d(convertDate(timey, "dd/MM/yyyy hh:mm:ss"));

		Log.d("the time is: ", when+" ");
		//Log.d(theTime);

		cal.setTimeInMillis(System.currentTimeMillis());
		//cal.clear();
		cal.set(Calendar.HOUR_OF_DAY, 18);
		cal.set(Calendar.MINUTE, 30);

		AlarmManager alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

		Intent intent = new Intent(this, AlarmReceiver.class);
		//Intent intent = new Intent(this, Notification_reciever.class);
		alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
		//PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
		// cal.add(Calendar.SECOND, 5);
		//alarmMgr.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
		//alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,when, AlarmManager.INTERVAL_DAY, PendingIntent.getBroadcast(this,1,  intent, PendingIntent.FLAG_UPDATE_CURRENT));
		alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);
		//alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, when, 1000 * 60 * 1, alarmIntent);
		//Toast.makeText(this, "WE HAVE SET THE ALARM", Toast.LENGTH_LONG).show();
		alarmIsSet = true;

		//VideoActivity.encryptAsyncTask2()



//		Calendar cal = Calendar.getInstance();
//
//		long when = cal.getTimeInMillis();
//		String timey = Long.toString(when);
//		String theTime = convertDate(timey, "dd/MM/yyyy hh:mm:ss");
//		theCurrentDate = theTime;
//		System.out.println("The time changed into nice format is: " + theTime);
//		//Log.d(convertDate(timey, "dd/MM/yyyy hh:mm:ss"));
//
//		int hour = 11;
//		int minute = 20;
//		Log.d("the time is: ", when+" ");
//		//Log.d(theTime);
//		cal.setTimeInMillis(System.currentTimeMillis());
//		//cal.clear();
//		cal.set(Calendar.HOUR_OF_DAY, 11);
//		cal.set(Calendar.MINUTE, 25);
//		AlarmManager alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
//
//		Intent intent = new Intent(this, AlarmReceiver.class);
//		//Intent intent = new Intent(this, Notification_reciever.class);
//		PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
//
//		//PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
//		// cal.add(Calendar.SECOND, 5);
//		//alarmMgr.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
//		//alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,when, AlarmManager.INTERVAL_DAY, PendingIntent.getBroadcast(this,1,  intent, PendingIntent.FLAG_UPDATE_CURRENT));
//		alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);
//		//alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, when, 1000 * 60 * 1, alarmIntent);
//		Toast.makeText(this, "WE HAVE SET THE ALARM", Toast.LENGTH_LONG).show();
//		alarmIsSet = true;
	}

	public void startGoogleFit() {

		Log.d("Fit", "Start of startGoogle FIt");

		Calendar cal = Calendar.getInstance();
		long when = cal.getTimeInMillis();
		String timey = Long.toString(when);
		String theTime = convertDate(timey, "dd/MM/yyyy hh:mm:ss");
		theCurrentDate = theTime;
		System.out.println("The time changed into nice format is: " + theTime);
		//Log.d(convertDate(timey, "dd/MM/yyyy hh:mm:ss"));

		Log.d("the time is: ", when + " ");
		//Log.d(theTime);

		cal.setTimeInMillis(System.currentTimeMillis());
		//cal.clear();
		cal.set(Calendar.HOUR_OF_DAY, 18);
		cal.set(Calendar.MINUTE, 30);

		AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

		Intent intent = new Intent(this, GoogleFitUploadTask.class);
		fitIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
		alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, fitIntent);




	}




	public void startAlarm2(){
		Calendar cal = Calendar.getInstance();

		long when = cal.getTimeInMillis();
		String timey = Long.toString(when);
		String theTime = convertDate(timey, "dd/MM/yyyy hh:mm:ss");
		theCurrentDate = theTime;
		System.out.println("The time changed into nice format is: " + theTime);
		//Log.d(convertDate(timey, "dd/MM/yyyy hh:mm:ss"));
		int hour = 13;
		int minute = 20;
		Log.d("the time is: ", when+" ");
		//Log.d(theTime);
		cal.setTimeInMillis(System.currentTimeMillis());
		//cal.clear();
		cal.set(Calendar.HOUR_OF_DAY, 22);
		cal.set(Calendar.MINUTE, 40);

		AlarmManager alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(this, AlarmReceiver2.class);
		PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

		//PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
		// cal.add(Calendar.SECOND, 5);
		//alarmMgr.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
		//alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,when, AlarmManager.INTERVAL_DAY, PendingIntent.getBroadcast(this,1,  intent, PendingIntent.FLAG_UPDATE_CURRENT));
		alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, alarmIntent);

		//alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, when, 1000 * 60 * 1, alarmIntent);
		Toast.makeText(this, "WE HAVE SET THE ALARM REPEATING EVERY 15 MIN!!!!!!!!!!", Toast.LENGTH_LONG).show();

	}

	public static String convertDate(String dateInMilliseconds, String dateFormat) {
		return DateFormat.format(dateFormat, Long.parseLong(dateInMilliseconds)).toString();
	}

	@Override
	public Context getApplicationContext() {
		return super.getApplicationContext();
	}

	public static MainActivity getIntance() {
		return instace;
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(this));
		setContentView(R.layout.activity_main);
		instace = this;


		startAlarm();
		//googleFit = new GoogleFit();



		//alarmMgr2 = (AlarmManager)getSystemService(Context.ALARM_SERVICE);



		wmbPreference = PreferenceManager.getDefaultSharedPreferences(this);
		boolean alarmSet = wmbPreference.getBoolean("ALARMSET", true);
		//Toast.makeText(this, "THIS IS MAIN ACTIVIY, BEFORE (isfirstRun)", Toast.LENGTH_LONG).show();
		//Toast.makeText(this, "ALARM SET IS CURRENTLY: " + alarmSet, Toast.LENGTH_LONG).show();
		if (alarmSet)
		{
			//Toast.makeText(this, "THIS IS MAIN ACTIVIY, IN (isfirstRun)", Toast.LENGTH_LONG).show();
			// Code to run once
			//startActivity(new Intent(this, LauncherSettingsActivity.class));
			//startAlarm();
			SharedPreferences.Editor editor = wmbPreference.edit();
			editor.putBoolean("ALARMSET", false);
			//Toast.makeText(this, "ALARM SET IS CURRENTLY: " + alarmSet, Toast.LENGTH_LONG).show();
			editor.commit();
			//startAlarm();
			//startAlarm2();
			//finish();
		}
//		else{
//			startActivity(new Intent(this, VideoActivity.class));
//		}
//		Toast.makeText(this, "THIS IS MAIN ACTIVIY, AFTER (isfirstRun)", Toast.LENGTH_LONG).show();


//		if(alarmIsSet == false)

//		{
//			startAlarm();
//		}


		Geocoder geoCoder = new Geocoder(getApplicationContext(), Locale.getDefault());
		//List<Address> address = null;
		List<Address> address = null;

		if(gotLocation == false)
		{
			gps = new GPSTracker(this);
			latitude = gps.getLatitude();
			longitude = gps.getLongitude();

			//Toast.makeText(this, "WE HAVE GOT YOUR LOCATION: LATITUDE = " + latitude + "LONGITUDE = " + longitude, Toast.LENGTH_LONG).show();
			Log.d("GPS", "111 WE HAVE GOT YOUR LOCATION: LATITUDE = " + latitude + "LONGITUDE = " + longitude);


//			if (geoCoder != null) {
//				try {
//					address = geoCoder.getFromLocation(latitude, longitude, 1);
//					//Toast.makeText(this, "Address has been found" + address, Toast.LENGTH_LONG).show():;
//
//				} catch (IOException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
//				//if (address.size() > 0) {
//				if (address != null) {
//					try{
//						postCode = address.get(0).getPostalCode();
//					}
//					catch(IndexOutOfBoundsException e){
//						e.printStackTrace();
//					}
//				}
//				else{
//					Toast.makeText(this, "Address was null, maybe no GPS reception?", Toast.LENGTH_LONG).show();
//				}
//			}
//			//Toast.makeText(this, "WE HAVE GOT YOUR LOCATION: POSTCODE	 = "+ postCode , Toast.LENGTH_LONG).show();
//			if(postCode !=null){
//				gotLocation = true;
//
//			}


		}



		//videoButton = (Button) findViewById(R.id.videoButton);
		//nextOne = (Button) findViewById(R.id.nextOne);
//		nextOne.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				startActivity(new Intent(v.getContext(), VideoActivity.class));
//			}
//		});
        //broadcastIntent();
        //AlarmActivity alarmActivity = new AlarmActivity();
        //startActivity(new Intent(this, AlarmActivity.class));


//		videoButton.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				//startActivity(new Intent(MainActivity.this, VideoActivity.class));
//				Intent myIntent = new Intent(MainActivity.this, VideoActivity.class);
//				MainActivity.this.startActivity(myIntent);
//			}
//		});

		//startGoogleFit();

		startActivity(new Intent(this, VideoActivity.class));

	}

	@Override
	protected void onResume() {
		super.onResume();
		startActivity(new Intent(this, VideoActivity.class));
		//Toast.makeText(this, "On Resume called!" , Toast.LENGTH_LONG).show();
	}







	public void setReminder() {
        startActivity(new Intent(this, AlarmActivity.class));
	}

	public void onVideo(View v) {
		startActivity(new Intent(this, VideoActivity.class));
	}

	public void showTimePickerDialog(View v) {
		DialogFragment newFragment = new TimePickerFragment();
		newFragment.show(getFragmentManager(), "timePicker");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			//AnySoftKeyboard.launchsettings();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void broadcastIntent(){
		Intent intent = new Intent();
		intent.setAction("com.codepath.CUSTOM_INTENT");
        sendBroadcast(intent);
	}

	public static class TimePickerFragment extends DialogFragment
			implements TimePickerDialog.OnTimeSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current time as the default values for the picker
			final Calendar c = Calendar.getInstance();
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);

			// Create a new instance of TimePickerDialog and return it
			return new TimePickerDialog(getActivity(), this, hour, minute,
					DateFormat.is24HourFormat(getActivity()));
		}

		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			// Do something with the time chosen by the user
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		startService(new Intent(this, MainActivity.class));
	}
}


