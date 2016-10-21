package com.radicalninja.logger;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings.Secure;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.model.AmazonS3Exception;
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
import com.radicalninja.logger.ui.MapsActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.crypto.NoSuchPaddingException;

//import com.example.aishwarya.thirdapplication.R;
//import com.example.aishwarya.thirdapplication.viewactivity.TakeImageActivity;

public class VideoActivity extends AppCompatActivity implements
		GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener,
		View.OnClickListener{

//public class VideoActivity extends AppCompatActivity {

	private GoogleApiClient mGoogleApiClient;

	Long tsLong = System.currentTimeMillis();
	String timeStamp = tsLong.toString();
	//String dir = Environment.getExternalStorageDirectory().getAbsolutePath();
	String directoryName = "/videoDIARY/";
	private PopupWindow popupWindow;
	String awsAccessKey = "Insert Key here";
	String awsSecretKey = "Insert Key here";
	String MY_BUCKET = "adaptvideos";
	String fileLocation = "something_goes_here";
	String OBJECT_KEY = "nameOfTheFile, once storeed on S3";
	public final String TAG = "Encrypt";
	public String finalPath;
	public ProgressBar progressBar;

	private TransferUtility transferUtility;

	public static String UserID;
	public String theCurrentDate;

	//private Thread.UncaughtExceptionHandler androidDefaultUEH;

	private File testRoot;
	private final static int REQUEST_RESULT_IMAGE = 1;
	private final static int REQUEST_RESULT_VIDEO = 7;
	public Encryption encryption;
	public static boolean dialogShown = false;

	GPSTracker gps;
	double latitude;
	double longitude;





	//public String deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

	public static String getSecureId(Context context) {
		String android_id = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
		return android_id;
	}

	public static String convertDate(String dateInMilliseconds, String dateFormat) {
		return DateFormat.format(dateFormat, Long.parseLong(dateInMilliseconds)).toString();
	}

	public void setTheDate() {
		Calendar cal = Calendar.getInstance();
		long when = cal.getTimeInMillis();
		String timey = Long.toString(when);
		String theTime = convertDate(timey, "dd-MM-yyyy hh:mm:ss");
		theCurrentDate = theTime;
		System.out.println("The time changed into nice format is: " + theTime);
	}

	void showDialog() {


		MyDialogFragment myDialogFragment = new MyDialogFragment();
		myDialogFragment.show(getFragmentManager(), "");
//		mStackLevel++;
//
//		// DialogFragment.show() will take care of adding the fragment
//		// in a transaction.  We also want to remove any currently showing
//		// dialog, so make our own transaction and take care of that here.
//		FragmentTransaction ft = getFragmentManager().beginTransaction();
//		Fragment prev = getFragmentManager().findFragmentByTag("dialog");
//		if (prev != null) {
//			ft.remove(prev);
//		}
//		ft.addToBackStack(null);
//
//		// Create and show the dialog.
//		DialogFragment newFragment = MyDialogFragment.newInstance(mStackLevel);
//		newFragment.show(ft, "dialog");
	}

	void showDialog2() {

		MyDialogFragmentThree myDialogFragment = new MyDialogFragmentThree();
		myDialogFragment.show(getFragmentManager(), "WARNING");


	}

	public void showDialog3(View v) {

		//startActivity(new Intent(this, mGoogleFit.class));

		//mGoogleFit mgf = new mGoogleFit();

		new ViewWeekStepCountTask().execute();


		MyDialogFragment myDialogFragment = new MyDialogFragment();
		myDialogFragment.show(getFragmentManager(), "INTO");
	}


//	public void credentialsProvider(){
//		CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
//				getApplicationContext(),
//				us-east-1:dbacd6aa-9393-475e-b687-xxxxxxxxx&quot;
//		)
//	}

//	CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
//			getApplicationContext(),
//			"us-west-2:41d228a6-292a-4ebb-9f37-cf96c33063a2", // Identity Pool ID
//			Regions.US_WEST_2 // Region
//	);
//
//	AmazonS3 s3 = new AmazonS3Client(credentialsProvider);
//	TransferUtility transferUtility = new TransferUtility(s3, getApplicationContext());

//	TransferObserver observer = transferUtility.upload(
//			MY_BUCKET,     /* The bucket to upload to */
//			OBJECT_KEY,    /* The key for the uploaded object */
//			MY_FILE        /* The file where the data to upload exists */
//	);

	private void beginUpload(String filePath) {

		Log.d("Video", "started upload in beginupload");

		if (filePath == null) {
			Log.d("Video", "Could not find the filepath of the selected file");
			return;
		}
		setTheDate();
		String newFilePath = UserID + "/" + theCurrentDate;
		//Toast.makeText(this, "The file is uploading, using the name: " + newFilePath,Toast.LENGTH_LONG).show();
		Log.d("uploading, using: " + newFilePath, "");

		File file = new File(filePath);
		try{

			TransferObserver observer = transferUtility.upload(Constants.BUCKET_NAME, newFilePath,
					file);
			observer.setTransferListener(new TransferListener() {
				@Override
				public void onStateChanged(int id, TransferState state) {

				}

				@Override
				public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
					int percentage = (int) (bytesCurrent/bytesTotal);



				}

				@Override
				public void onError(int id, Exception ex) {

				}
			});
		}catch (AmazonS3Exception s3Exception)
		{
			Log.d("aws","error contacting amazon");
		}
		Log.d("Video", "finshed upload in beginupload");
		}





//		File file = new File(filePath);
//		TransferObserver observer = transferUtility.upload(Constants.BUCKET_NAME, newFilePath,
//				file);
        /*
         * Note that usually we set the transfer listener after initializing the
         * transfer. However it isn't required in this sample app. The flow is
         * click upload button -> start an activity for image selection
         * startActivityForResult -> onActivityResult -> beginUpload -> onResume
         * -> set listeners to in progress transfers.
         */
		// observer.setTransferListener(new UploadListener());


	private void beginUpload2(String filePath) {
		if (filePath == null) {
			Toast.makeText(this, "Could not find the filepath of the selected file",Toast.LENGTH_LONG).show();
			return;
		}

		setTheDate();
		String newFilePath = UserID + "/" +"GoogleFit_" + theCurrentDate;
		//String newFilePath = UserID + "/" ;

		//Toast.makeText(this, "The file is uploading, using the name: " + newFilePath,Toast.LENGTH_LONG).show();
		//Log.d("uploading, using: " + newFilePath, "");
		File file = new File(filePath);
		TransferObserver observer = transferUtility.upload(Constants.BUCKET_NAME, newFilePath,
				file);
        /*
         * Note that usually we set the transfer listener after initializing the
         * transfer. However it isn't required in this sample app. The flow is
         * click upload button -> start an activity for image selection
         * startActivityForResult -> onActivityResult -> beginUpload -> onResume
         * -> set listeners to in progress transfers.
         */
		// observer.setTransferListener(new UploadListener());
	}


	public class encryptAsyncTask extends AsyncTask<String, Void, String> {

		String idAndDate = UserID + "_" + theCurrentDate;


		@Override
		protected String doInBackground(String... params) {
			String path = null;
			try {
				com.anysoftkeyboard.utils.Log.d("Video", "We are starting encrytopn 1 - in doInBackgound AsyncTask ENCRYTPTION!");
				path = encryption.encrypt(idAndDate, params[0]);
				//Toast.makeText(VideoActivity.this, "Encrypting.",  Toast.LENGTH_LONG).show();
			} catch (IOException e) {
				//Toast.makeText(VideoActivity.this, "Encrypting.1",  Toast.LENGTH_LONG).show();
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				//Toast.makeText(VideoActivity.this, "Encrypting.2",  Toast.LENGTH_LONG).show();
				e.printStackTrace();
			} catch (NoSuchPaddingException e) {
				//Toast.makeText(VideoActivity.this, "Encrypting.3",  Toast.LENGTH_LONG).show();
				e.printStackTrace();
			} catch (InvalidKeyException e) {
				//Toast.makeText(VideoActivity.this, "Encrypting.4",  Toast.LENGTH_LONG).show();
				e.printStackTrace();
			} catch (InvalidAlgorithmParameterException e) {
				e.printStackTrace();
			}
			//Toast.makeText(VideoActivity.this, "File has been sucessfully uploaded! ", Toast.LENGTH_LONG).show();

			return path;
		}





//

		@Override
		protected void onPostExecute(String path) {

			Toast.makeText(VideoActivity.this, "File is encrypting...... ", Toast.LENGTH_LONG).show();

			new uploadAsyncTask().execute(path);
			//showDialog2();
//			uploadAsynTask upload = new uploadAsynTask();
//			upload.execute()

		}
	}

	public class encryptAsyncTask2 extends AsyncTask<String, Void, String> {

		String idAndDate = UserID + "_" + theCurrentDate;




		@Override
		protected String doInBackground(String... params) {
			String path = null;
			try {
				com.anysoftkeyboard.utils.Log.d(TAG, "We are starting encrytopn 1 - in doInBackgound AsyncTask ENCRYTPTION!");
				path = encryption.encrypt(idAndDate, params[0]);
				//Toast.makeText(VideoActivity.this, "Encrypting.",  Toast.LENGTH_LONG).show();
			} catch (IOException e) {
				//Toast.makeText(VideoActivity.this, "Encrypting.1",  Toast.LENGTH_LONG).show();
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				//Toast.makeText(VideoActivity.this, "Encrypting.2",  Toast.LENGTH_LONG).show();
				e.printStackTrace();
			} catch (NoSuchPaddingException e) {
				//Toast.makeText(VideoActivity.this, "Encrypting.3",  Toast.LENGTH_LONG).show();
				e.printStackTrace();
			} catch (InvalidKeyException e) {
				//Toast.makeText(VideoActivity.this, "Encrypting.4",  Toast.LENGTH_LONG).show();
				e.printStackTrace();
			} catch (InvalidAlgorithmParameterException e) {
				e.printStackTrace();
			}
			//Toast.makeText(VideoActivity.this, "File has been sucessfully uploaded! ", Toast.LENGTH_LONG).show();

			return path;
		}



//

		@Override
		protected void onPostExecute(String path) {

			//Toast.makeText(VideoActivity.this, "File is encrypting..... ", Toast.LENGTH_LONG).show();
			//showDialog2();

			new uploadAsyncTask2().execute(path);
//			uploadAsynTask upload = new uploadAsynTask();
//			upload.execute()

		}
	}





	public class uploadAsyncTask extends AsyncTask<String, Integer, Void> {

		//private ProgressBar progressBar;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressBar.setVisibility(View.VISIBLE);
		}

		@Override
		protected Void doInBackground(String... params) {
			com.anysoftkeyboard.utils.Log.d("Video", "We are starting eupload - In uploadtask");
			// TRY NEW WAY OF GETTING PROGRESS BAR!!


			String filePath = params[0];
			// TRY NEW WAY OF GETTING PROGRESS BAR!!
			Log.d("Video", "started upload in beginupload");

			if (filePath == null) {
				Log.d("Video", "Could not find the filepath of the selected file");
				//return;
			}
			setTheDate();
			String newFilePath = UserID + "/" + theCurrentDate;
			//Toast.makeText(this, "The file is uploading, using the name: " + newFilePath,Toast.LENGTH_LONG).show();
			Log.d("uploading, using: " + newFilePath, "");

			File file = new File(filePath);
			try{

				TransferObserver observer = transferUtility.upload(Constants.BUCKET_NAME, newFilePath,
						file);
				observer.setTransferListener(new TransferListener() {
					@Override
					public void onStateChanged(int id, TransferState state) {

					}

					@Override
					public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
						double percentage = ((double)bytesCurrent/bytesTotal);
						int one = (int)(percentage * 100);

						Log.d("Progress", "Progress in upload is: " + percentage);
						Log.d("Progress", "Progress in upload for longOne is: " + one);

						int percentageInt = (int)(percentage);
						publishProgress(one);
						//progressBar.setProgress(one);

						if(one == 100)
						{
							progressBar.setVisibility(View.GONE);
						}

					}

					@Override
					public void onError(int id, Exception ex) {

					}
				});
			}catch (AmazonS3Exception s3Exception)
			{
				Log.d("aws","error contacting amazon");
			}
			Log.d("Video", "finshed upload in beginupload");




			return null;
		}





			// TRY NEW WAY OF GETTING PROGRESS BAR!!
//			beginUpload(params[0]);
//			return null;
//		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values[0]);
			Log.d("Progress", "Progress = " + values[0]);
			progressBar.setProgress(values[0]);
			//progressBar.setProgress(100);
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			//progressBar.setVisibility(View.GONE);
			Toast.makeText(VideoActivity.this, "File is uploading..... ", Toast.LENGTH_LONG).show();
			com.anysoftkeyboard.utils.Log.d("Video", "We have finished upload - In onPostExecute" +
					" ");

			//THIS CAN CRASH APP IF USER CLOSES APP BEFORE IT IS RUN!!
			//showDialog2();
	}

		//		@Override
//		protected void onPostExecute(Void... params){
//			Toast.makeText(VideoActivity.this, "File has been sucessfully uploaded! ", Toast.LENGTH_LONG).show();
//
//
//		}


	}

	public class uploadAsyncTask2 extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			com.anysoftkeyboard.utils.Log.d(TAG, "Beginning Upload");
			Log.d("Fit", "Params[0] = " + params[0]);
			beginUpload2(params[0]);
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			//Toast.makeText(VideoActivity.this, "File has been sucessfully uploaded! - FIT ", Toast.LENGTH_LONG).show();
			com.anysoftkeyboard.utils.Log.d(TAG, "Fit - We are starting encrytopn 5 - Upload finishedk" + " ");

		}

		//		@Override
//		protected void onPostExecute(Void... params){
//			Toast.makeText(VideoActivity.this, "File has been sucessfully uploaded! ", Toast.LENGTH_LONG).show();
//
//
//		}


	}


	public boolean createDirectory(String path) {
		File mydir = new File(Environment.getExternalStorageDirectory().toString() + path);
		if (!mydir.exists()) {
			mydir.mkdirs();
		} else {
			Log.d("error", "dir already exisits");
		}
		return true;

	}

	private void initiatePopupWindow() {
		try {
			// We need to get the instance of the LayoutInflater
			LayoutInflater inflater = (LayoutInflater) this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View layout = inflater.inflate(R.layout.screen_popup,
					(ViewGroup) findViewById(R.id.popup_element));
			popupWindow = new PopupWindow(layout, 300, 370, true);
			popupWindow.showAtLocation(layout, Gravity.CENTER, 0, 0);


		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		//getActionBar().setTitle("Video Diary");
		super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(this));
//
		getSupportActionBar().setTitle("UO Video Diary App");
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		//ProgressBar progressBar;

		//progressBar = (ProgressBar) findViewById(R.id.progressBar);
		//progressBar.setProgress(0);
		//getSupportActionBar().setHomeButtonEnabled(false);


		UserID = getSecureId(this);
		setContentView(R.layout.activity_video);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		progressBar.setProgress(0);
		progressBar.setVisibility(View.GONE);


		//initiatePopupWindow();

		setTheDate();
		//showDialog();


//		if(!dialogShown)
//		{
//			Toast.makeText(this, "User ID = " + UserID, Toast.LENGTH_LONG).show();
//			showDialog2();
//			dialogShown = true;
//
//		}

		//showDialog2();
		encryption = new Encryption();
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addApi(Fitness.HISTORY_API)
				.addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
				.addConnectionCallbacks(this)
				.enableAutoManage(this, 0, this)
				.build();
//
////        DailyTotalResult result = Fitness.HistoryApi.readDailyTotal( mGoogleApiClient, DataType.TYPE_STEP_COUNT_DELTA ).await(0, TimeUnit.MINUTES);
////        showDataSet(result.getTotal());
////        Toast.makeText(this, "THe steps for todays is: " + result, Toast.LENGTH_LONG).show();
//
//		new ViewWeekStepCountTask().execute();
//		startGoogleFit();


	}

//	public void startGoogleFit() {
//
//		Calendar cal = Calendar.getInstance();
//		long when = cal.getTimeInMillis();
//		String timey = Long.toString(when);
//		String theTime = convertDate(timey, "dd/MM/yyyy hh:mm:ss");
//		theCurrentDate = theTime;
//		System.out.println("The time changed into nice format is: " + theTime);
//		//Log.d(convertDate(timey, "dd/MM/yyyy hh:mm:ss"));
//
//		Log.d("the time is: ", when + " ");
//		//Log.d(theTime);
//
//		cal.setTimeInMillis(System.currentTimeMillis());
//		//cal.clear();
//		cal.set(Calendar.HOUR_OF_DAY, 18);
//		cal.set(Calendar.MINUTE, 30);
//
//		AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//
//		Intent intent = new Intent(this, GoogleFitUploadTask.class);
//		fitIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
//		alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, fitIntent);
//
//
//	}

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

	}

	public void handleUncaughtException (Thread thread, Throwable e)
	{



		e.printStackTrace(); // not all Android versions will print the stack trace automatically

		Log.d("Video", "The app caught a unhandled error!");

		//System.exit(1); // kill off the crashed app
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

		java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance();
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
		Log.d("History", "Number of buckets : " + dataReadResult.getBuckets().size());

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

		String uri = (Environment.getExternalStorageDirectory().getAbsolutePath() + directoryName + "GoogleFit_" + endTime + ".txt");
		//String uri = (Environment.getExternalStorageDirectory().getAbsolutePath() + directoryName + "GoogleFit_" + ".txt");

		Log.e("History", "URI+ " + uri);
		//File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + directoryName + "GoogleFit_" + endTime + ".txt");
		File file = new File(uri);
		FileOutputStream stream = null;
		Log.e("History", "Start showDataset");
		Log.e("History", "Data returned for Data type: " + dataSet.getDataType().getName());
		java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance();
		java.text.DateFormat timeFormat = java.text.DateFormat.getTimeInstance();

		gps = new GPSTracker(this);
		latitude = gps.getLatitude();
		longitude = gps.getLongitude();

		writeToFile(file, "\nLatitude is: " + latitude,this.getApplicationContext() );
		writeToFile(file, "\nLongitude is: " + longitude,this.getApplicationContext() );

		Log.e("History", "Latitude is: " + latitude);
		Log.e("History", "Longitude is: " + longitude);

		for (DataPoint dp : dataSet.getDataPoints()) {
			Log.e("History", "Data point:");

			//writeToFile(file, "Data point:\n",this.getApplicationContext() );


			writeToFile(file, "\n" +
					"Data point:\n",this.getApplicationContext() );
			Log.e("History", "\tType: " + dp.getDataType().getName());
			Log.e("History", "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
			writeToFile(file, "\tStart: "  + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)), this.getApplicationContext());
			Log.e("History", "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
			writeToFile(file, "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)), this.getApplicationContext());
			for(Field field : dp.getDataType().getFields()) {
				Log.e("History", "\tField: " + field.getName() +
						" Value: " + dp.getValue(field));
				writeToFile(file, "\n" + field.getName() +
						" Value: " +"\n" + dp.getValue(field), this.getApplicationContext());
			}
		}
		Log.e("History", "End show data set");

		String newFilePath = UserID + "/" ;

		//Toast.makeText(this, "The file is uploading, using the name: " + newFilePath,Toast.LENGTH_LONG).show();


		Log.e("Fit","uploading, using: " + newFilePath);
//		File file2 = new File(uri);
//		TransferObserver observer = transferUtility.upload(Constants.BUCKET_NAME, newFilePath,file2);
		transferUtility = Util.getTransferUtility(this);
		new encryptAsyncTask2().execute(uri);
		//new uploadAsyncTask2().execute(uri);

	}

	private class ViewWeekStepCountTask extends AsyncTask<Void, Void, Void> {
		protected Void doInBackground(Void... params) {
			displayLastWeeksData();
			return null;
		}
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

//        try {
//            stream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
		try {

			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

//

	private static final int VIDEO_CAPTURE = 101;
	Uri videoUri;

	public void onRecordVideo(View v) {

		//showDialog2();


//		Intent i;
//
//		//isVideo=((CompoundButton)findViewById(R.id.is_video)).isChecked();
//
//
//		Toast.makeText(this, "NUMBER 15", Toast.LENGTH_LONG).show();
//		i=new VideoRecorderActivity.IntentBuilder(this)
//				.facing(Facing.FRONT)
//				.facingExactMatch()
//				.to(new File(testRoot, "portrait-front.mp4"))
//				.updateMediaStore()
//				.durationLimit(10000)
//				.debug()
//				//.flashModes(FLASH_MODES)
//				.build();
//
//		startActivityForResult(i, VIDEO_CAPTURE);
		if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
			Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
			intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
			intent.putExtra("android.intent.extra.quickCapture", true);
			createDirectory(directoryName);
			File mediaFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + directoryName + timeStamp + ".mp4");
			String newPath = Environment.getExternalStorageDirectory().getAbsolutePath() + directoryName + timeStamp + ".mp4";
			Log.d("VideoActivity", "This the the Video Uri in the on RECROD VIEW method using the newPath Variable: " + newPath);
			Log.d("VideoActivity", "This the the Video Uri in the on RECROD VIEW method using the mediafile Variable: " + mediaFile);

			finalPath = newPath;
			videoUri = Uri.fromFile(mediaFile);
			Log.d("VideoActivity", "This the the Video Uri in the on RECROD VIEW method: " + videoUri);

			intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
			//intent.putExtra(MediaStore.EXTRA_OUTPUT, newPath);
			//intent.putExtra("STRING_I_NEED", newPath);

			//intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
			startActivityForResult(intent, VIDEO_CAPTURE);
			//intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
		} else {
			Toast.makeText(this, "No camera on device", Toast.LENGTH_LONG).show();
		}
	}

	public void onRecordVideo2(View v) {
		//startActivity(new Intent(this, CameraActivity.class));

		Intent takeVideoActivity = new Intent(this, TakeVideoActivity.class);
		startActivityForResult(takeVideoActivity, REQUEST_RESULT_VIDEO);


//		getFragmentManager().beginTransaction()
//				.replace(R.id.LinearLayout1, Camera2VideoFragment.newInstance())
//				.commit();
	}

	public void openMaps(View v) {
		Intent i = new Intent(getApplicationContext(), MapsActivity.class);
		startActivity(i);
	}


	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == VIDEO_CAPTURE) {
			if (resultCode == RESULT_OK) {

				//THIS IS THE ONE THAT WORKS ON ALL PHONES BUT SD CARD PHONES
				String path = data.getData().getPath();
				Log.d("Video", "the path variable is " + path);

				// END
				//String newPath = data.getExtras().get("videoUri");


				// THIS IS WHAT PUTS IT IN VIDEO DIRECTORY 3/10/16
				//String path = videoUri.toString();


//			  if(data.getExtras().getString("STRING_I_NEED") == null)
//			  {
//				  Log.d("VideoActivity", "String is null");
//			  }

				//String string = data.getExtras().get("STRING_I NEED");
				Log.d("VideoActivity", "The next attempt is: " + videoUri.toString());
				Log.d("VideoActivity", "The old method was: " + data.getData().getPath());
				//Log.d("VideoActivity", "The old method was: " + string);

				//Log.d("VideoActivity", "This the the getting extra info from intent method: " + data.getExtras().get("STRING_PATH"));
				//Log.d("VideoActivity", "This the the getting extra info from intent method 2 : " + data.getExtras().get("data"));


				Log.d("VideoActivity", "The path returned by the intent data obbject was: " + path);
				Log.d("VideoActivity", "The path returned by absolute path external storage was : " + Environment.getExternalStorageDirectory().getAbsolutePath());
				Log.d("VideoActivity", "The path returned by  external storage was : " + Environment.getExternalStorageDirectory());
				Uri path2 = data.getData();
				//VideoView mVideoView = (VideoView) findViewById(R.id.video_view);
				//mVideoView.setVideoURI(videoUri);
				Toast.makeText(this, "Video has been saved to:\n" + data.getData(), Toast.LENGTH_LONG).show();


				//Toast.makeText(this, "Path used to find encrypted file is :\n" + path, Toast.LENGTH_LONG).show();


				//TRY CANCEL PENDING INTENT HERE:

				//AlarmManager alarmMgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
//			  Intent intentReminder = new Intent(this, AlarmReceiver2.class);
//			  PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intentReminder, 0);
//			  alarmIntent.cancel();
//			  alarmMgr.cancel(alarmIntent);


				// NEW TRY
				//NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
				//notificationManager.cancel(1);

				//Toast.makeText(this, "We have cancelled the repeating every 15 alarm!", Toast.LENGTH_LONG).show();

				// END TRY CANCEL PENDING INTENT HERE!


				//MainActivity.endRepeatingAlarm = true;


				//mVideoView.setMediaController(new MediaController(this));
				//mVideoView.requestFocus();
				//mVideoView is the replay after video
				//mVideoView.start();

				// TURN OFF WHILE TESTING ENCRYPTION!!

				transferUtility = Util.getTransferUtility(this);
				//new encryptAsyncTask().execute(path);
				showDialog2();
				new encryptAsyncTask().execute(finalPath);
//			  try {
//				  encryption.encrypt(UserID, path);
//				  Toast.makeText(this, "Encrypting.",  Toast.LENGTH_LONG).show();
//			  } catch (IOException e) {
//				  Toast.makeText(this, "Encrypting.1",  Toast.LENGTH_LONG).show();
//				  e.printStackTrace();
//			  } catch (NoSuchAlgorithmException e) {
//				  Toast.makeText(this, "Encrypting.2",  Toast.LENGTH_LONG).show();
//				  e.printStackTrace();
//			  } catch (NoSuchPaddingException e) {
//				  Toast.makeText(this, "Encrypting.3",  Toast.LENGTH_LONG).show();
//				  e.printStackTrace();
//			  } catch (InvalidKeyException e) {
//				  Toast.makeText(this, "Encrypting.4",  Toast.LENGTH_LONG).show();
//				  e.printStackTrace();
//			  }
				//beginUpload(path);
			} else if (resultCode == RESULT_CANCELED) {
				Toast.makeText(this, "Video recording cancelled.", Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(this, "Failed to record video", Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.video, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		startService(new Intent(this, MainActivity.class));
	}


//	public class mGoogleFit implements
//			GoogleApiClient.ConnectionCallbacks,
//			GoogleApiClient.OnConnectionFailedListener,
//			View.OnClickListener {
//
//		private GoogleApiClient mGoogleApiClient;
//		private Context context;
//
//
//
//
//		public void mGoogleFit(){
//
//			Log.d("Fit", "In mGoogleFit Constrcutor");
//
//			//this.context = context;
//			context = getApplicationContext();
//
//
//			mGoogleApiClient = new GoogleApiClient.Builder(context)
//				.addApi(Fitness.HISTORY_API)
//				.addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
//				.addConnectionCallbacks(this)
//				.enableAutoManage((FragmentActivity) context, 0, this)
//				.build();
//
//
//
//			new ViewWeekStepCountTask().execute();
//		}
//
//
//		@Override
//		public void onConnectionSuspended(int i) {
//			Log.e("HistoryAPI", "onConnectionSuspended");
//	}
//
//		@Override
//		public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//			Log.e("HistoryAPI", "onConnectionFailed");
//	}
//
//		public void onConnected(@Nullable Bundle bundle) {
//			Log.e("HistoryAPI", "onConnected");
//	}
//
//
//		@Override
//		public void onClick(View v) {
//
//	}
//
//	public void displayLastWeeksData() {
//		Calendar cal = Calendar.getInstance();
//		Date now = new Date();
//		cal.setTime(now);
//		long endTime = cal.getTimeInMillis();
//		//cal.add(Calendar.WEEK_OF_YEAR, -1);
//		//cal.add(Calendar.HOUR_OF_DAY, -2);
//		cal.add(Calendar.DAY_OF_WEEK, -1);
//		long startTime = cal.getTimeInMillis();
//
//		java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance();
//		Log.e("History", "Range Start: " + dateFormat.format(startTime));
//		Log.e("History", "Range End: " + dateFormat.format(endTime));
//
//		//Check how many steps were walked and recorded in the last 7 days
//		DataReadRequest readRequest = new DataReadRequest.Builder()
//				//.aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
//				.aggregate(DataType.TYPE_ACTIVITY_SEGMENT, DataType.AGGREGATE_ACTIVITY_SUMMARY)
//				.bucketByTime(1, TimeUnit.DAYS)
//				//.bucketByTime(2, TimeUnit.HOURS)
//				.setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
//				.build();
//
//		DataReadResult dataReadResult = Fitness.HistoryApi.readData(mGoogleApiClient, readRequest).await(1, TimeUnit.MINUTES);
//		Log.d("History", "Number of buckets : " + dataReadResult.getBuckets().size());
//
//		//Used for aggregated data
//		if (dataReadResult.getBuckets().size() > 0) {
//			Log.e("History", "Number of buckets: " + dataReadResult.getBuckets().size());
//			for (Bucket bucket : dataReadResult.getBuckets()) {
//				List<DataSet> dataSets = bucket.getDataSets();
//				for (DataSet dataSet : dataSets) {
//					showDataSet(dataSet);
//				}
//			}
//		}
//		//Used for non-aggregated data
//		else if (dataReadResult.getDataSets().size() > 0) {
//			Log.e("History", "Number of returned DataSets: " + dataReadResult.getDataSets().size());
//			for (DataSet dataSet : dataReadResult.getDataSets()) {
//				showDataSet(dataSet);
//			}
//		}
//	}
//
//	private String showDataSet(DataSet dataSet) {
//
//		Calendar cal = Calendar.getInstance();
//		Date now = new Date();
//		cal.setTime(now);
//		long endTime = cal.getTimeInMillis();
//
//		String uri = (Environment.getExternalStorageDirectory().getAbsolutePath() + directoryName + "GoogleFit_" + endTime + ".txt");
//
//		//File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + directoryName + "GoogleFit_" + endTime + ".txt");
//		File file = new File(uri);
//		FileOutputStream stream = null;
//		Log.e("History", "Start showDataset");
//		Log.e("History", "Data returned for Data type: " + dataSet.getDataType().getName());
//		java.text.DateFormat dateFormat = java.text.DateFormat.getDateInstance();
//		java.text.DateFormat timeFormat = java.text.DateFormat.getTimeInstance();
//
//		gps = new GPSTracker(context);
//		latitude = gps.getLatitude();
//		longitude = gps.getLongitude();
//
//		writeToFile(file, "\nLatitude is: " + latitude,context );
//		writeToFile(file, "\nLongitude is: " + longitude,context );
//
//		Log.e("History", "Latitude is: " + latitude);
//		Log.e("History", "Longitude is: " + longitude);
//
//		for (DataPoint dp : dataSet.getDataPoints()) {
//			Log.e("History", "Data point:");
//
//			//writeToFile(file, "Data point:\n",this.getApplicationContext() );
//
//
//			writeToFile(file, "\n" +
//					"Data point:\n",context );
//			Log.e("History", "\tType: " + dp.getDataType().getName());
//			Log.e("History", "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
//			writeToFile(file, "\tStart: "  + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)), context);
//			Log.e("History", "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
//			writeToFile(file, "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)), context);
//			for(Field field : dp.getDataType().getFields()) {
//				Log.e("History", "\tField: " + field.getName() +
//						" Value: " + dp.getValue(field));
//				writeToFile(file, "\n" + field.getName() +
//						" Value: " +"\n" + dp.getValue(field), context);
//			}
//		}
//		Log.e("History", "End show data set");
//
//		return uri;
//
//	}
//
//	private class ViewWeekStepCountTask extends AsyncTask<Void, Void, Void> {
//		protected Void doInBackground(Void... params) {
//			displayLastWeeksData();
//			return null;
//		}
//	}
//
//	private void writeToFile(File file, String data, Context context) {
////        Calendar cal = Calendar.getInstance();
////        Date now = new Date();
////        cal.setTime(now);
////        long endTime = cal.getTimeInMillis();
////        File path = context.getExternalFilesDir(null);
////        File file = new File(path, "googleFit.txt");
//
//
////        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + directoryName + "GoogleFit_" + endTime + ".txt");
//		FileOutputStream stream = null;
//		//OutputStreamWriter stream = new OutputStreamWriter(openFileOutput(file), Context.MODE_APPEND);
//		try {
//			Log.e("History", "In try");
//			stream = new FileOutputStream(file, true);
//			stream.write(data.getBytes());
//		} catch (FileNotFoundException e) {
//			Log.e("History", "In catch");
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
////        try {
////            stream.close();
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//		try {
//
//			stream.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//	}
//
//
//	}
}