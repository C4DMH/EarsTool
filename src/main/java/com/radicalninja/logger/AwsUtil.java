package com.radicalninja.logger;

import android.annotation.SuppressLint;
import android.provider.Settings;
import android.text.TextUtils;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.anysoftkeyboard.utils.Log;
import com.menny.android.anysoftkeyboard.AnyApplication;
import com.menny.android.anysoftkeyboard.BuildConfig;
import com.menny.android.anysoftkeyboard.R;
import java.io.File;
import java.util.List;

class AwsUtil {

    public interface FileTransferCallback {
        void onStart(final int id, final TransferState state);
        void onComplete(final int id, final TransferState state);
        void onCancel(final int id, final TransferState state);
        void onError(final int id, final Exception e);
    }

    private static final String TAG = AwsUtil.class.getCanonicalName();

    private static final Regions POOL_REGION = Regions.fromName(BuildConfig.AWS_POOL_REGION.toLowerCase());
    private static final Regions BUCKET_REGION = Regions.fromName(BuildConfig.AWS_BUCKET_REGION.toLowerCase());

    private static CognitoCachingCredentialsProvider credentialsProvider;
    private static String userId;

    private static void init() {
        initCredentialsProvider();
        initUserId();
       Log.d("AWS", "This is AWS in init");
    }

    private static void initCredentialsProvider() {
        if (credentialsProvider == null) {
            // Initialize the Amazon Cognito credentials provider
            credentialsProvider = new CognitoCachingCredentialsProvider(
                    AnyApplication.getInstance(),
                    BuildConfig.AWS_POOL_ID,
                    POOL_REGION
            );
        }
    }

    private static void initUserId() {
        if (TextUtils.isEmpty(userId)) {
            userId = Settings.Secure.getString(
                    AnyApplication.getInstance().getContentResolver(), Settings.Secure.ANDROID_ID);
        }
    }

    public static void uploadFilesToBucket(final List<File> files, final boolean deleteAfter,
                                           final FileTransferCallback callback) {
        for (final File file : files) {
            uploadFileToBucket(file, file.getName(), deleteAfter, callback);
        }
    }

    public static void uploadFileToBucket(final File file, final String filename,
                                          final boolean deleteAfter, final FileTransferCallback callback) {
        init();
        // S3 client
        final AmazonS3 s3 = new AmazonS3Client(credentialsProvider);
        s3.setRegion(Region.getRegion(BUCKET_REGION));
        // Transfer Utility
        final TransferUtility transferUtility =
                new TransferUtility(s3, AnyApplication.getInstance());
        // Upload the file
        final String filePath = String.format("%s/%s", userId, filename);
        final TransferObserver observer =
                transferUtility.upload(BuildConfig.AWS_BUCKET_NAME, filePath, file);
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
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) { }

            @SuppressLint("DefaultLocale")
            @Override
            public void onError(int id, Exception ex) {
                Log.e(TAG, String.format("onError: Transfer ID: %d", id), ex);
                callback.onError(id, ex);
            }
        });
    }

}
