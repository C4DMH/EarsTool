package com.anysoftkeyboard.ui.settings.setup;

/**
 * Created by gwicks on 24/10/2017.
 */

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;
import com.menny.android.anysoftkeyboard.R;
import com.radicalninja.logger.Encryption;
import com.radicalninja.logger.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.crypto.NoSuchPaddingException;

// TODO Class is upload both encrypted and unencrypted photo to AWS, need to fix, probably delete after encryption not working


public class FaceDetect extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    TransferUtility mTransferUtility;
    Encryption mEncryption;
    static String folder = "/ReferencePic/";




    ImageView imgTakePicture;
    Button btnTakePicture;
    TextView txtSampleDesc, txtTakenPicDesc;
    private FaceDetector detector;
    Bitmap editedBitmap;
    private Uri imageUri;
    private static final int REQUEST_WRITE_PERMISSION = 200;
    private static final int CAMERA_REQUEST = 101;

    private static final String SAVED_INSTANCE_URI = "uri";
    private static final String SAVED_INSTANCE_BITMAP = "bitmap";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect);

        mEncryption = new Encryption();
        mTransferUtility = Util.getTransferUtility(this);




        //imageArray = new int[]{R.drawable.sample_1, R.drawable.sample_2, R.drawable.sample_3};
        detector = new FaceDetector.Builder(getApplicationContext())
                .setTrackingEnabled(false)
                .setProminentFaceOnly(true)
                .setMode(FaceDetector.FAST_MODE)
                .setMinFaceSize((float) 0.60)
                .setLandmarkType(FaceDetector.ALL_CLASSIFICATIONS)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        initViews();

    }

    private void initViews() {
        //imageView = (ImageView) findViewById(R.id.imageView);
        imgTakePicture = (ImageView) findViewById(R.id.imgTakePic);
        //btnProcessNext = (Button) findViewById(R.id.btnProcessNext);
        btnTakePicture = (Button) findViewById(R.id.btnTakePicture);
        txtSampleDesc = (TextView) findViewById(R.id.txtSampleDescription);
        txtTakenPicDesc = (TextView) findViewById(R.id.textView);

        //processImage(imageArray[currentIndex]);
        //currentIndex++;

        //btnProcessNext.setOnClickListener(this);
        btnTakePicture.setOnClickListener(this);
        imgTakePicture.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.btnProcessNext:
//                imageView.setImageResource(imageArray[currentIndex]);
//                processImage(imageArray[currentIndex]);
//                if (currentIndex == imageArray.length - 1)
//                    currentIndex = 0;
//                else
//                    currentIndex++;
//
//                break;

            case R.id.btnTakePicture:
                ActivityCompat.requestPermissions(FaceDetect.this, new
                        String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
                break;

            case R.id.imgTakePic:
                ActivityCompat.requestPermissions(FaceDetect.this, new
                        String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
                break;
        }
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
            launchMediaScanIntent();
            try {
                processCameraPicture();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Failed to load Image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void launchMediaScanIntent() {
        Log.d(TAG, "launchMediaScanIntent: ");
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(imageUri);
        this.sendBroadcast(mediaScanIntent);
    }

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
        //Log.d(TAG, "startCamera: 4");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        //intent.putExtra("android.intent.extras.CAMERA_FACING", android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT);
        //intent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1);
        //intent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true);
        //intent.putExtra("android.intent.extras.CAMERA_FACING",1);
        //Log.d(TAG, "startCamera: 5");
        startActivityForResult(intent, CAMERA_REQUEST);
        //Log.d(TAG, "startCamera: 6");
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
        }
        Log.d(TAG, "onSaveInstanceState: 4");
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState: 5");
    }


    private void processCameraPicture() throws Exception {
        Log.d(TAG, "processCameraPicture: ");
        Bitmap bitmap = decodeBitmapUri(this, imageUri);
        Log.d(TAG, "processCameraPicture: 1");
        if(bitmap == null) {
            Log.d(TAG, "processCameraPicture: BITMAP IS NULL");
        }

        Log.d(TAG, "processCameraPicture: " + detector.isOperational());
        Log.d(TAG, "processCameraPicture:  " + bitmap.getByteCount());

        if (detector.isOperational() && bitmap != null) {
            editedBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                    .getHeight(), bitmap.getConfig());
            Log.d(TAG, "processCameraPicture: 2");
            float scale = getResources().getDisplayMetrics().density;
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.GREEN);
            paint.setTextSize((int) (16 * scale));
            paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(6f);
            Canvas canvas = new Canvas(editedBitmap);
            Log.d(TAG, "processCameraPicture: 3");
            canvas.drawBitmap(bitmap, 0, 0, paint);
            Log.d(TAG, "processCameraPicture: 4");
            Frame frame = new Frame.Builder().setBitmap(editedBitmap).build();
            Log.d(TAG, "processCameraPicture: 5");
            SparseArray<Face> faces = detector.detect(frame);
            Log.d(TAG, "processCameraPicture: 6");
            txtTakenPicDesc.setText(null);
            Log.d(TAG, "processCameraPicture: 7");

            Log.d(TAG, "processCameraPicture: the number of faces : " + faces.size());

            for (int index = 0; index < faces.size(); ++index) {
                Face face = faces.valueAt(index);
                canvas.drawRect(
                        face.getPosition().x,
                        face.getPosition().y,
                        face.getPosition().x + face.getWidth(),
                        face.getPosition().y + face.getHeight(), paint);


                canvas.drawText("Face " + (index + 1), face.getPosition().x + face.getWidth(), face.getPosition().y + face.getHeight(), paint);


                txtTakenPicDesc.setText(txtTakenPicDesc.getText() + "Number of Faces Detected: " + faces.size() + "\n");
                //txtTakenPicDesc.setText("FACE " + (index + 1) + "\n");
                txtTakenPicDesc.setText(txtTakenPicDesc.getText() + "Smile probability:" + " " + face.getIsSmilingProbability() + "\n");
                txtTakenPicDesc.setText(txtTakenPicDesc.getText() + "Left Eye Is Open Probability: " + " " + face.getIsLeftEyeOpenProbability() + "\n");
                txtTakenPicDesc.setText(txtTakenPicDesc.getText() + "Right Eye Is Open Probability: " + " " + face.getIsRightEyeOpenProbability() + "\n\n");
                //txtTakenPicDesc.setText( "Right Eye Is Open Probability: " + " " + face.getIsRightEyeOpenProbability() + "\n\n");

                Log.d(TAG, "processImage: face stuff: " + face.getWidth());


                for (Landmark landmark : face.getLandmarks()) {
                    int cx = (int) (landmark.getPosition().x);
                    int cy = (int) (landmark.getPosition().y);
                    canvas.drawCircle(cx, cy, 8, paint);

                }


            }

            Log.d(TAG, "processCameraPicture: faces size: " + faces.size());
            if (faces.size() == 0) {
                Toast.makeText(this, "No Face Detected, please make sure your your face is 50% width of screen", Toast.LENGTH_LONG).show();
            }

            if (faces.size() == 0) {
                txtTakenPicDesc.setText("Scan Failed: Found nothing to scan");
            } else {
                imgTakePicture.setImageBitmap(editedBitmap);
                //
                //
                // txtTakenPicDesc.setText(txtTakenPicDesc.getText() + "No of Faces Detected: " + " " + String.valueOf(faces.size()));
            }

            if(faces.size() == 1){

                Log.d(TAG, "processCameraPicture: the path is: " + imageUri.toString());
                Encrypt("referencePic.jpg", imageUri.toString());
                String path = getExternalFilesDir(null) + "/videoDIARY/ReferencePic/";
                File directory = new File(path);


                if(!directory.exists()){
                    directory.mkdirs();
                }
                ArrayList<File> encryptedFiles = new ArrayList<>(Arrays.asList(directory.listFiles()));


                Util.uploadFilesToBucket(encryptedFiles, true,logUploadCallback, this, folder);

                Toast.makeText(this, "Face Detection Successful", Toast.LENGTH_LONG).show();


                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        // Actions to do after 10 seconds
                        finish();
                    }
                }, 3000);



            }
        } else {
            txtTakenPicDesc.setText("Could not set up the detector!");
        }
    }

//    private Bitmap decodeBitmapUri(Context ctx, Uri uri) throws FileNotFoundException {
//        Log.d(TAG, "decodeBitmapUri: ");
//        int targetW = 300;
//        int targetH = 300;
//        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//        Log.d(TAG, "decodeBitmapUri: 1");
//        bmOptions.inJustDecodeBounds = true;
//        bmOptions.inPreferredConfig=Bitmap.Config.RGB_565;
//        Log.d(TAG, "decodeBitmapUri: 2");
//        BitmapFactory.decodeStream(ctx.getContentResolver().openInputStream(uri), null, bmOptions);
//        int photoW = bmOptions.outWidth;
//        int photoH = bmOptions.outHeight;
//        Log.d(TAG, "decodeBitmapUri: 4");
//
//        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
//        bmOptions.inJustDecodeBounds = false;
//        Log.d(TAG, "decodeBitmapUri: 5");
//        bmOptions.inSampleSize = scaleFactor;
//        Log.d(TAG, "decodeBitmapUri: 5");
//
//        return BitmapFactory.decodeStream(ctx.getContentResolver()
//                .openInputStream(uri), null, bmOptions);
//    }

    private Bitmap decodeBitmapUri(Context ctx, Uri uri) throws FileNotFoundException {
        Log.d(TAG, "decodeBitmapUri: ");
        //Toast.makeText(this, "1o" , Toast.LENGTH_LONG).show();
        Log.d(TAG, "initViews1: face detector is ============================ " + detector.isOperational());
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        detector.release();
    }

    public String Encrypt(String name, String path){
        Log.d(TAG, "Encrypt: 1");
        Log.d(TAG, "Encrypt: name = " + name);
        Log.d(TAG, "Encrypt: path = " + path);
        String mFileName = name;
        //String mFileName = "//storage/emulated/0/Android/data/com.menny.android.anysoftkeyboard/files/videoDIARY/ReferencePic/referencePhoto.jpg";
        Log.d(TAG, "Encrypt: mFIleName is: " + mFileName);
        String mFilePath = "/storage/emulated/0/Android/data/com.menny.android.anysoftkeyboard/files/videoDIARY/ReferencePic/referencePhoto.jpg";

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
