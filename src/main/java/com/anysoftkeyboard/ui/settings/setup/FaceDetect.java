package com.anysoftkeyboard.ui.settings.setup;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.radicalninja.logger.Encryption;
import com.radicalninja.logger.Util;
import com.sevencupsoftea.ears.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.crypto.NoSuchPaddingException;

//import static com.menny.android.anysoftkeyboard.R.id.imageView39;
//import static com.menny.android.anysoftkeyboard.R.id.imageView40;
//import static com.menny.android.anysoftkeyboard.R.id.profile_image;

//import com.menny.android.anysoftkeyboard.R;

//import static gwicks.com.a7cupstest.R.id.imageView39;
//import static gwicks.com.a7cupstest.R.id.imageView40;
//import static gwicks.com.a7cupstest.R.id.profile_image;

/**
 * Created by gwicks on 20/01/2018.
 */

public class FaceDetect extends AppCompatActivity {
//
//    private static final String TAG = "MainActivity";
//
//
//
//    public SharedPreferences photoTick;
//    public static final String TICK = "MyPrefFile";
//
//    ImageView button;
//    ImageView button2;
//    ImageView belowPic;
//
//    TextView abovePic;
//
//    boolean secondScreen = false;
//
//
//    Bitmap editedBitmap;
//    public Uri imageUri;
//    private static final int REQUEST_WRITE_PERMISSION = 200;
//    private static final int CAMERA_REQUEST = 101;
//
//    private static final String SAVED_INSTANCE_URI = "uri";
//    private static final String SAVED_INSTANCE_BITMAP = "bitmap";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.setup_six);
//        updateStatusBarColor("#1281e8");
//
//        Log.d(TAG, "onCreate: before set click");
//
//        abovePic = (TextView)findViewById(R.id.aboveImage);
//
//
//        button = (ImageView)findViewById(imageView40);
//        button2 = (ImageView)findViewById(profile_image);
//        belowPic = (ImageView)findViewById(imageView39);
//
//        button.setOnClickListener(new View.OnClickListener(){
//
//
//            @Override
//            public void onClick(View view){
//                Log.d(TAG, "onClick: Clicked");
//                Log.d(TAG, "onClick: secondscreen = " + secondScreen);
//
//                if(secondScreen == false){
//                    Log.d(TAG, "onClick: in false");
//
//
//                    ActivityCompat.requestPermissions(FaceDetect.this, new
//                            String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
//
//                }
//                Log.d(TAG, "onClick: second screen = " + secondScreen);
//                if(secondScreen == true){
//                    Log.d(TAG, "onClick: in true");
//                    goToFinish();
//                }
//
//
//
//
//
//
//            }
//        });
//        Log.d(TAG, "onCreate: after onclick set");
//        button2.setOnClickListener(new View.OnClickListener(){
//
//
//            @Override
//            public void onClick(View view){
//                Log.d(TAG, "onClick: Clicked");
//
//                ActivityCompat.requestPermissions(FaceDetect.this, new
//                        String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
//
//
//            }
//        });
//
//
//
//
//    }
//
//    public void updateStatusBarColor(String color){// Color must be in hexadecimal fromat
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Log.d(TAG, "updateStatusBarColor: color change being called!");
//            Window window = getWindow();
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.parseColor(color));
//        }
//    }
//
//    private void goToFinish(){
//        Log.d(TAG, "goToFinish: go to finish");
//        Intent intent = new Intent(FaceDetect.this, StepSeven.class);
//        FaceDetect.this.startActivity(intent);
//    }
//
//    private void initViews() {
//        //imageView = (ImageView) findViewById(R.id.imageView);
//        //imgTakePicture = (ImageView) findViewById(R.id.profile_image);
//        //btnProcessNext = (Button) findViewById(R.id.btnProcessNext);
//        //btnTakePicture = (ImageView) findViewById(imageView40);
//        //txtSampleDesc = (TextView) findViewById(R.id.txtSampleDescription);
//        //txtTakenPicDesc = (TextView) findViewById(R.id.textView);
//
//        //processImage(imageArray[currentIndex]);
//        //currentIndex++;
//
//        //btnProcessNext.setOnClickListener(this);
//        //btnTakePicture.setOnClickListener(this);
//        //imgTakePicture.setOnClickListener(this);
//    }
//
//
//
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case REQUEST_WRITE_PERMISSION:
//                startCamera();
////                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
////                    startCamera();
////                } else {
////                    Toast.makeText(getApplicationContext(), "Permission Denied!", Toast.LENGTH_SHORT).show();
////                }
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Log.d(TAG, "onActivityResult: this is resyult");
//        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
//            Log.d(TAG, "onActivityResult: IMAGWURI = " + imageUri);
//
//            //Failed on smj700
//
////
////            Bundle extras  = data.getExtras();
////
////            Bitmap bitmap = (Bitmap) extras.get("data");
////            button.setImageResource(R.drawable.complete_install);
////            button2.setImageBitmap(bitmap);
////            abovePic.setText("Great! Are you good with your picture?");
////            belowPic.setImageResource(R.drawable.take_a_different_pic);
//
//            // Try new
//            button.setImageResource(R.drawable.complete_install);
//            button2.setImageURI(null);
//            button2.setImageURI(imageUri);
//            abovePic.setText("Great! Are you good with your picture?");
//            belowPic.setImageResource(R.drawable.take_a_different_pic);
//
//            //finish
//
//            belowPic.setOnClickListener(new View.OnClickListener(){
//
//
//                @Override
//                public void onClick(View view){
//                    Log.d(TAG, "onClick: Clicked");
//                    startCamera();
//
//
//
//                }
//            });
//
//            Log.d(TAG, "onActivityResult: chagning secondScreen to true!");
//
//            secondScreen = true;
//            Log.d(TAG, "onActivityResult: Now changed to True!!");
//
//        }
//    }
//
////    private void launchMediaScanIntent() {
////        Log.d(TAG, "launchMediaScanIntent: ");
////        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
////        mediaScanIntent.setData(imageUri);
////        this.sendBroadcast(mediaScanIntent);
////    }
//
//    private void startCamera() {
//        Log.d(TAG, "startCamera: ");
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        //Log.d(TAG, "startCamera: 2");
//        File photo = new File(getExternalFilesDir(null), "/videoDIARY/ReferencePic/referencePhoto.jpg");
//        //Log.d(TAG, "startCamera: 3");
//        //File photo = new File(Environment.getExternalStorageDirectory(), "/videoDIARY/ReferencePic/photo.jpg");
//
//        try
//        {
//            if(photo.exists() == false)
//                Log.d(TAG, "startCamera:2 ");
//            {
//                photo.getParentFile().mkdirs();
//                Log.d(TAG, "startCamera: 1");
//                photo.createNewFile();
//            }
//        }
//        catch (IOException e)
//        {
//            Log.e(TAG, "Could not create file.", e);
//        }
//        imageUri = Uri.fromFile(photo);
//        Log.d(TAG, "startCamera: image uri - " + imageUri);
//        Log.d(TAG, "startCamera: 4");
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//        //intent.putExtra("android.intent.extras.CAMERA_FACING", android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT);
//        //intent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1);
//        //intent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true);
//        //intent.putExtra("android.intent.extras.CAMERA_FACING",1);
//        Log.d(TAG, "startCamera: 5");
//        startActivityForResult(intent, CAMERA_REQUEST);
//        Log.d(TAG, "startCamera: 6");
//    }
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        Log.d(TAG, "onSaveInstanceState: ");
//        if (imageUri != null) {
//            Log.d(TAG, "onSaveInstanceState: 1");
//            outState.putParcelable(SAVED_INSTANCE_BITMAP, editedBitmap);
//            Log.d(TAG, "onSaveInstanceState: 2");
//            outState.putString(SAVED_INSTANCE_URI, imageUri.toString());
//            Log.d(TAG, "onSaveInstanceState: 3");
//        }
//        Log.d(TAG, "onSaveInstanceState: 4");
//        super.onSaveInstanceState(outState);
//        Log.d(TAG, "onSaveInstanceState: 5");
//    }
//
//
//
//
//
//
//
//
//
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//
//    }
//
//
//
//
//}
    private static final String TAG = "FaceDetect";


    TransferUtility mTransferUtility;
    Encryption mEncryption;
    public SharedPreferences photoTick;
    public static final String TICK = "MyPrefFile";
    String path;
    File directory;

    ImageView button;
    ImageView button2;
    ImageView belowPic;
    //TextView belowPic;


    TextView abovePic;

    boolean secondScreen = false;


    Bitmap editedBitmap;
    public Uri imageUri;
    private static final int REQUEST_WRITE_PERMISSION = 200;
    private static final int CAMERA_REQUEST = 101;

    private static final String SAVED_INSTANCE_URI = "uri";
    private static final String SAVED_INSTANCE_BITMAP = "bitmap";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        path = getExternalFilesDir(null) + "/videoDIARY/ReferencePic/";

        File directory = new File(path);


        if(!directory.exists()){
            directory.mkdirs();
        }

        Log.d(TAG, "onCreate: directory = " +directory);


        setContentView(R.layout.setup_six);
        updateStatusBarColor("#1281e8");
        button = (ImageView)findViewById(R.id.imageView40);
        button2 = (ImageView)findViewById(R.id.profile_image);
        belowPic = (ImageView)findViewById(R.id.imageView39);
        abovePic = (TextView)findViewById(R.id.aboveImage);
        if(savedInstanceState != null){
            String stringImageUri = savedInstanceState.getString(SAVED_INSTANCE_URI);
            imageUri = Uri.parse(stringImageUri);
            Log.d(TAG, "onCreate: in savedInstance state the reloaded imageURI is: " + imageUri);
            Bitmap newBitmap = savedInstanceState.getParcelable(SAVED_INSTANCE_BITMAP);

            button.setImageResource(R.drawable.complete_install);
//            button2.setImageURI(null);
//            button2.setImageURI(imageUri);

            abovePic.setText("Great! Are you good with your picture?");
            belowPic.setImageResource(R.drawable.take_a_different_pic);
            //belowPic.setText("Take another PIC?");
            button2.setImageBitmap(newBitmap);
            secondScreen = true;
            //startActivityFor

        }
//        updateStatusBarColor("#1281e8");

        Log.d(TAG, "onCreate: before set click");

//        abovePic = (TextView)findViewById(R.id.aboveImage);


//        button = (ImageView)findViewById(R.id.imageView40);
//        button2 = (ImageView)findViewById(R.id.profile_image);
//        belowPic = (ImageView)findViewById(R.id.imageView39);
        //belowPic = (TextView)findViewById(imageView39);


        button.setOnClickListener(new View.OnClickListener(){


            @Override
            public void onClick(View view){
                Log.d(TAG, "onClick: Clicked");
                Log.d(TAG, "onClick: secondscreen = " + secondScreen);

                if(secondScreen == false){
                    Log.d(TAG, "onClick: in false");


                    ActivityCompat.requestPermissions(FaceDetect.this, new
                            String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);

                }
                Log.d(TAG, "onClick: second screen = " + secondScreen);
                if(secondScreen == true){
                    Log.d(TAG, "onClick: in true");
                    goToFinish();
                }






            }
        });
        Log.d(TAG, "onCreate: after onclick set");
        button2.setOnClickListener(new View.OnClickListener(){


            @Override
            public void onClick(View view){
                Log.d(TAG, "onClick: Clicked");

                ActivityCompat.requestPermissions(FaceDetect.this, new
                        String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);


            }
        });




    }

    public void updateStatusBarColor(String color){// Color must be in hexadecimal fromat
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.d(TAG, "updateStatusBarColor: color change being called!");
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(color));
        }
    }

    private void goToFinish(){

        mEncryption = new Encryption();
        mTransferUtility = Util.getTransferUtility(this);

        path = getExternalFilesDir(null) + "/videoDIARY/ReferencePic/";

        File directory = new File(path);


        if(!directory.exists()){
            directory.mkdirs();
        }

        Log.d(TAG, "onCreate: directory = " +directory);

        ArrayList<File> files = new ArrayList<>(Arrays.asList(directory.listFiles()));
        int i = 1;
        for(File each : files){

            Log.d(TAG, "onReceive: path = " + each.getAbsolutePath());
            Encrypt("ReferencePic_" + i, each.getAbsolutePath());
            i = i + 1;
            Log.d(TAG, "onReceive: i is: " + i);
            try{
                each.delete();
            }catch (Exception e){
                Log.d(TAG, "onReceive: error deleting: " + e);
            }

        }

        ArrayList<File> encryptedFiles = new ArrayList<>(Arrays.asList(directory.listFiles()));

        // END 9th JUne change


        Util.uploadFilesToBucket(encryptedFiles, true,logUploadCallback, this, "/ReferencePic/");



        Log.d(TAG, "goToFinish: go to finish");
        Intent intent = new Intent(FaceDetect.this, StepSeven.class);
        FaceDetect.this.startActivity(intent);
        //
        // finish();
    }

    private void initViews() {
        //imageView = (ImageView) findViewById(R.id.imageView);
        //imgTakePicture = (ImageView) findViewById(R.id.profile_image);
        //btnProcessNext = (Button) findViewById(R.id.btnProcessNext);
        //btnTakePicture = (ImageView) findViewById(imageView40);
        //txtSampleDesc = (TextView) findViewById(R.id.txtSampleDescription);
        //txtTakenPicDesc = (TextView) findViewById(R.id.textView);

        //processImage(imageArray[currentIndex]);
        //currentIndex++;

        //btnProcessNext.setOnClickListener(this);
        //btnTakePicture.setOnClickListener(this);
        //imgTakePicture.setOnClickListener(this);
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_WRITE_PERMISSION:
                startCamera();
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    startCamera();
//                } else {
//                    Toast.makeText(getApplicationContext(), "Permission Denied!", Toast.LENGTH_SHORT).show();
//                }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: this is resyult");
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Log.d(TAG, "onActivityResult: IMAGWURI = " + imageUri);

            //Failed on smj700

//
//            Bundle extras  = data.getExtras();
//
//            Bitmap bitmap = (Bitmap) extras.get("data");
//            button.setImageResource(R.drawable.complete_install);
//            button2.setImageBitmap(bitmap);
//            abovePic.setText("Great! Are you good with your picture?");
//            belowPic.setImageResource(R.drawable.take_a_different_pic);

            // Try new
            button.setImageResource(R.drawable.complete_install);
//            button2.setImageURI(null);
//            button2.setImageURI(imageUri);

            Bitmap bitmap = null;

            try {
                bitmap = decodeBitmapUri(this, imageUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            editedBitmap = bitmap;
            abovePic.setText("Great! Are you good with your picture?");
            belowPic.setImageResource(R.drawable.take_a_different_pic);
            //belowPic.setText("Take another PIC?");
            button2.setImageBitmap(bitmap);

            //finish

            belowPic.setOnClickListener(new View.OnClickListener(){


                @Override
                public void onClick(View view){
                    Log.d(TAG, "onClick: Clicked");
                    startCamera();



                }
            });

            Log.d(TAG, "onActivityResult: chagning secondScreen to true!");

            secondScreen = true;
            Log.d(TAG, "onActivityResult: Now changed to True!!");

        }
    }

    private Bitmap decodeBitmapUri(Context ctx, Uri uri) throws FileNotFoundException {
        Log.d(TAG, "decodeBitmapUri: " + uri);
        //Toast.makeText(this, "1o" , Toast.LENGTH_LONG).show();
        //Log.d(TAG, "initViews1: face detector is ============================ " + detector.isOperational());
        int targetW = 300;
        int targetH = 300;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        bmOptions.inPreferredConfig=Bitmap.Config.RGB_565;
        BitmapFactory.decodeStream(ctx.getContentResolver().openInputStream(uri), null, bmOptions);
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_FRONT, info);
        int rotation = this.getWindowManager().getDefaultDisplay().getRotation();
        int orientation = this.getResources().getConfiguration().orientation;
        Log.d(TAG, "decodeBitmapUri: OREINTATION is ==================== " + orientation);

        Log.d(TAG, "decodeBitmapUri: CAMERA ROTATION ========================= " + rotation);
        //Camera.Size size = android.hardware.Camera.get


        int photoW = bmOptions.outWidth;
        Log.d(TAG, "decodeBitmapUri: width: " + photoW );
        int photoH = bmOptions.outHeight;
        Log.d(TAG, "decodeBitmapUri:  height: " + photoH);
        Log.d(TAG, "decodeBitmapUri: 4");
        //Toast.makeText(this, "11" , Toast.LENGTH_LONG).show();

        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        /*this is because some phones default a camera Intent to landscape no matter how the phone is held
        * so we check for camera orienatation, then check to see if width is greater than height
        * */

        if(orientation == 1 && (photoW > photoH)){
            return rotate(BitmapFactory.decodeStream(ctx.getContentResolver()
                    .openInputStream(uri), null, bmOptions));
        }


        return BitmapFactory.decodeStream(ctx.getContentResolver()
                .openInputStream(uri), null, bmOptions);
    }

    public static Bitmap rotate(Bitmap bitmap){
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix mtx = new Matrix();
        mtx.postRotate(270);

        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }


//    private void launchMediaScanIntent() {
//        Log.d(TAG, "launchMediaScanIntent: ");
//        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//        mediaScanIntent.setData(imageUri);
//        this.sendBroadcast(mediaScanIntent);
//    }

    private void startCamera() {
        Log.d(TAG, "startCamera: ");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //Log.d(TAG, "startCamera: 2");
        File photo = new File(getExternalFilesDir(null), "/videoDIARY/ReferencePic/referencePhoto.jpg");
        //Log.d(TAG, "startCamera: 3");
        //File photo = new File(Environment.getExternalStorageDirectory(), "/videoDIARY/ReferencePic/photo.jpg");

        try
        {
            if(photo.exists() == false)
                Log.d(TAG, "startCamera:2 ");
            {
                photo.getParentFile().mkdirs();
                Log.d(TAG, "startCamera: 1");
                photo.createNewFile();
            }
        }
        catch (IOException e)
        {
            Log.e(TAG, "Could not create file.", e);
        }
        imageUri = Uri.fromFile(photo);
        Log.d(TAG, "startCamera: image uri - " + imageUri);
        Log.d(TAG, "startCamera: 4");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        //intent.putExtra("android.intent.extras.CAMERA_FACING", android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT);
        //intent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1);
        //intent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true);
        //intent.putExtra("android.intent.extras.CAMERA_FACING",1);
        Log.d(TAG, "startCamera: 5");
        startActivityForResult(intent, CAMERA_REQUEST);
        Log.d(TAG, "startCamera: 6");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState: ");
        if (imageUri != null) {
            Log.d(TAG, "onSaveInstanceState: 1");
            outState.putParcelable(SAVED_INSTANCE_BITMAP, editedBitmap);
            Log.d(TAG, "onSaveInstanceState: 2");
            outState.putString(SAVED_INSTANCE_URI, imageUri.toString());
            Log.d(TAG, "onSaveInstanceState: 3");
            Log.d(TAG, "onSaveInstanceState: the image uri saved is: " + imageUri + " also the outstate = " + outState.getString(SAVED_INSTANCE_URI));
        }
        Log.d(TAG, "onSaveInstanceState: 4");
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState: 5");
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }











    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: on destroy called");
        super.onDestroy();

    }

    public String Encrypt(String name, String path){
        Log.d(TAG, "Encrypt: 1");
        Log.d(TAG, "Encrypt: name = " + name);
        Log.d(TAG, "Encrypt: path = " + path);
        String mFileName = name;
        Log.d(TAG, "Encrypt: mFIleName is: " + mFileName);
        String mFilePath = path;

        String path2 = null;
        try {
            //com.anysoftkeyboard.utils.Log.d(TAG, "We are starting encrytopn 1 - in doInBackgound AsyncTask ENCRYTPTION!");
            path2 = mEncryption.encrypt(mFileName, mFilePath, "/videoDIARY/ReferencePic/");
            Log.d(TAG, "Encrypt: the path me get is: " + path2);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Encrypt: 2");
        Log.d(TAG, "Encrypt: path2 is: " + path2);
        //beginUpload2("STATS", path2);
        return path2;



    }



    final Util.FileTransferCallback logUploadCallback = new Util.FileTransferCallback() {
        @SuppressLint("DefaultLocale")

        private String makeLogLine(final String name, final int id, final TransferState state) {
            Log.d("LogUploadTask", "This is AWSBIT");
            return String.format("%s | ID: %d | State: %s", name, id, state.toString());
        }

        @Override
        public void onCancel(int id, TransferState state) {
            Log.d(TAG, makeLogLine("Callback onCancel()", id, state));
        }

        @Override
        public void onStart(int id, TransferState state) {
            Log.d(TAG, makeLogLine("Callback onStart()", id, state));

        }

        @Override
        public void onComplete(int id, TransferState state) {
            Log.d(TAG, makeLogLine("Callback onComplete()", id, state));
        }

        @Override
        public void onError(int id, Exception e) {
            Log.d(TAG, makeLogLine("Callback onError()", id, TransferState.FAILED), e);
        }
    };




}
