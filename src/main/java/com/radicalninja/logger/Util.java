package com.radicalninja.logger;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.anysoftkeyboard.utils.Log;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * Created by gwicks on 7/08/2016.
 * Util class for uploading to Amazon AWS S3 Service
 */
public class Util {

    private static final String TAG = "Util";

    public interface FileTransferCallback {
        void onStart(final int id, final TransferState state);

        void onComplete(final int id, final TransferState state);

        void onCancel(final int id, final TransferState state);

        void onError(final int id, final Exception e);
    }


    // We only need one instance of the clients and credentials provider
    private static AmazonS3Client sS3Client;
    private static CognitoCachingCredentialsProvider sCredProvider;
    private static TransferUtility sTransferUtility;
    private static String userId;
    //Context mContext = MainActivity.getIntance();

    /**
     * Gets an instance of CognitoCachingCredentialsProvider which is
     * constructed using the given Context.
     *
     * @param context An Context instance.
     * @return A default credential provider.
     */
    private static CognitoCachingCredentialsProvider getCredProvider(Context context) {
        if (sCredProvider == null) {
            sCredProvider = new CognitoCachingCredentialsProvider(
                    context.getApplicationContext(),
                    Constants.COGNITO_POOL_ID,
                    Regions.US_WEST_2);
        }
        return sCredProvider;
    }

    private static void initUserId() {
        if (TextUtils.isEmpty(userId)) {
            userId = MainActivity.secureID;
//            userId = Settings.Secure.getString(
            //what? is this?
//                    AnyApplication.getInstance().getContentResolver(), Settings.Secure.ANDROID_ID);
        }
    }




    /**
     * Gets an instance of a S3 client which is constructed using the given
     * Context.
     *
     * @param context An Context instance.
     * @return A default S3 client.
     */
    public static AmazonS3Client getS3Client(Context context) {
        if (sS3Client == null) {
            ClientConfiguration config = new ClientConfiguration();
            config.setMaxErrorRetry(500);
            //config.
            config.setConnectionTimeout(50000);
            sS3Client = new AmazonS3Client(getCredProvider(context.getApplicationContext()), config);

        }


        return sS3Client;
    }

    /**
     * Gets an instance of the TransferUtility which is constructed using the
     * given Context
     *
     * @param context
     * @return a TransferUtility instance
     */
    public static TransferUtility getTransferUtility(Context context) {
        if (sTransferUtility == null) {
            sTransferUtility = new TransferUtility(getS3Client(context.getApplicationContext()),
                    context.getApplicationContext());
        }

        return sTransferUtility;
    }

    /**
     * Converts number of bytes into proper scale.
     *
     * @param bytes number of bytes to be converted.
     * @return A string that represents the bytes in a proper scale.
     */
    public static String getBytesString(long bytes) {
        String[] quantifiers = new String[]{
                "KB", "MB", "GB", "TB"
        };
        double speedNum = bytes;
        for (int i = 0; ; i++) {
            if (i >= quantifiers.length) {
                return "";
            }
            speedNum /= 1024;
            if (speedNum < 512) {
                return String.format("%.2f", speedNum) + " " + quantifiers[i];
            }
        }
    }

    /**
     * Copies the data from the passed in Uri, to a new file for use with the
     * Transfer Service
     *
     * @param context
     * @param uri
     * @return
     * @throws IOException
     */
    public static File copyContentUriToFile(Context context, Uri uri) throws IOException {
        InputStream is = context.getContentResolver().openInputStream(uri);
        File copiedData = new File(context.getDir("SampleImagesDir", Context.MODE_PRIVATE), UUID
                .randomUUID().toString());
        copiedData.createNewFile();

        FileOutputStream fos = new FileOutputStream(copiedData);
        byte[] buf = new byte[2046];
        int read = -1;
        while ((read = is.read(buf)) != -1) {
            fos.write(buf, 0, read);
        }

        fos.flush();
        fos.close();

        return copiedData;
    }

    /*
     * Fills in the map with information in the observer so that it can be used
     * with a SimpleAdapter to populate the UI
     */
    public static void fillMap(Map<String, Object> map, TransferObserver observer, boolean isChecked) {
        int progress = (int) ((double) observer.getBytesTransferred() * 100 / observer
                .getBytesTotal());
        map.put("id", observer.getId());
        map.put("checked", isChecked);
        map.put("fileName", observer.getAbsoluteFilePath());
        map.put("progress", progress);
        map.put("bytes",
                getBytesString(observer.getBytesTransferred()) + "/"
                        + getBytesString(observer.getBytesTotal()));
        map.put("state", observer.getState());
        map.put("percentage", progress + "%");
    }

    public static void uploadFilesToBucket(final List<File> files, final boolean deleteAfter,
                                            final Util.FileTransferCallback callback, Context context) {
        Log.d("Log", "This is in AWSUTIL upload files to bucket");

        if (sTransferUtility == null) {
            sTransferUtility = new TransferUtility(getS3Client(context.getApplicationContext()),
                    context.getApplicationContext());
        }



        for (final File file : files) {
            uploadFileToBucket(file, file.getName(), deleteAfter, callback, context);
        }
    }


    // Method overload attempt june 14th 2017

    public static void uploadFilesToBucket(final List<File> files, final boolean deleteAfter,
                                           final Util.FileTransferCallback callback, Context context, String folder) {
        Log.d("Log", "This is in AWSUTIL upload files to bucket");

        if (sTransferUtility == null) {
            sTransferUtility = new TransferUtility(getS3Client(context.getApplicationContext()),
                    context.getApplicationContext());
        }



        for (final File file : files) {
            uploadFileToBucket(file, file.getName(), deleteAfter, callback, folder);
        }
    }

    public static void uploadFileToBucket(final File file, final String filename,
                                          final boolean deleteAfter, final Util.FileTransferCallback callback, final Context context) {
        initUserId();
        Log.d("Log", "This is in AWSUTIL upload file to bucket");
        final String filePath = String.format("%s/%s", userId, filename);
        final TransferObserver observer =
                //transferUtility.upload(BuildConfig.AWS_BUCKET_NAME, filePath, file);
                sTransferUtility.upload(Constants.BUCKET_NAME, filePath, file);
        observer.setTransferListener(new TransferListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onStateChanged(int id, TransferState state) {
                final String logLine =
                        String.format("onStateChanged: Transfer ID: %d | New State: %s", id, state);
                Log.d(TAG, logLine);
                switch (state) {
                    case IN_PROGRESS:
                        Log.d(TAG, String.format("Transfer ID %d has begun", id));
                        callback.onStart(id, state);
                        break;
                    case COMPLETED:

                        long unixTime = System.currentTimeMillis() / 1000L;
                        String desination = context.getExternalFilesDir(null) + "/videoDIARY/buffered_" + unixTime + ".log";


                        File destination = new File(desination);
                        try {
                            FileUtils.copyFile(file, destination);
                            Log.d("LogUploadTask", "Copyting file to VideoDIARY");

                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        Log.d(TAG, String.format("Transfer ID %d has completed", id));
                        callback.onComplete(id, state);
                        if (deleteAfter) {
                            final String filename = file.getName();
                            final boolean deleted = file.delete();
                            Log.d(TAG, String.format("(%s)–File deleted: %s", filename, deleted));
                        }
                        break;
                    case CANCELED:
                        Log.d(TAG, String.format("Transfer ID %d has been cancelled", id));
                        callback.onCancel(id, state);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
            }

            @SuppressLint("DefaultLocale")
            @Override
            public void onError(int id, Exception ex) {
                Log.e(TAG, String.format("onError: Transfer ID: %d", id), ex);
                callback.onError(id, ex);
            }
        });
    }


    public static void uploadFileToBucket(final File file, final String filename,
                                          final boolean deleteAfter, final Util.FileTransferCallback callback, String folder) {
        initUserId();
        Log.d("Log", "This is in AWSUTIL upload file to bucket");
        final String filePath = String.format("%s%s", userId + folder, filename);
        final TransferObserver observer =
                //transferUtility.upload(BuildConfig.AWS_BUCKET_NAME, filePath, file);
                sTransferUtility.upload(Constants.BUCKET_NAME, filePath, file);
        observer.setTransferListener(new TransferListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onStateChanged(int id, TransferState state) {
                final String logLine =
                        String.format("onStateChanged: Transfer ID: %d | New State: %s", id, state);
                Log.d(TAG, logLine);
                switch (state) {
                    case IN_PROGRESS:
                        Log.d(TAG, String.format("Transfer ID %d has begun", id));
                        callback.onStart(id, state);
                        break;
                    case COMPLETED:

//                        long unixTime = System.currentTimeMillis() / 1000L;
//                        String desination = Environment.getExternalStorageDirectory().getAbsolutePath() + "/videoDIARY/buffered_" + unixTime + ".log";
//
//
//                        File destination = new File(desination);
//                        try {
//                            FileUtils.copyFile(file, destination);
//                            Log.d("LogUploadTask", "Copyting file to VideoDIARY");
//
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }


                        Log.d(TAG, String.format("Transfer ID %d has completed", id));
                        callback.onComplete(id, state);
                        if (deleteAfter) {
                            final String filename = file.getName();
                            final boolean deleted = file.delete();
                            Log.d(TAG, String.format("(%s)–File deleted: %s", filename, deleted));
                        }
                        break;
                    case CANCELED:
                        Log.d(TAG, String.format("Transfer ID %d has been cancelled", id));
                        callback.onCancel(id, state);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
            }

            @SuppressLint("DefaultLocale")
            @Override
            public void onError(int id, Exception ex) {
                Log.e(TAG, String.format("onError: Transfer ID: %d", id), ex);
                callback.onError(id, ex);
            }
        });
    }
}
