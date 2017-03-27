package com.radicalninja.logger;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.EditorInfo;

import com.menny.android.anysoftkeyboard.BuildConfig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class LogManager {

    private static final String TAG = LogManager.class.getSimpleName();
    private static final String FORMAT_LINE_PREFIX = "[yyyy-MM-dd HH:mm:ss]";
    private static final String FORMAT_EXPORT_FILE_PREFIX = "yyyyMMddHHmmss";

    private static LogManager instance;

    GPSTracker gps;
    double latitude;
    double longitude;
    //private final Context context;
    public final Context context;

    private final PreferencesManager preferencesManager;
    private final List<Buffer> buffers = new ArrayList<>();

    private Date startTime;
    private boolean privacyModeEnabled;
    private FileUploadLog fileUploadLog;
    private String prevBuffer = null;

    public static void init(final Context context) {

        Log.d("LogManager", "This is LogManager before init");


        if (instance == null) {
            Log.d("LogManager", "This is LogManager in init");
            instance = new LogManager(context);
            LogUploadTask.registerTasks(context);
        }
    }

    public static void destroy() {
        if (instance != null) {
            Log.d(TAG, "LogManager – destroy");
            instance.destroyBuffers();
            instance.clearUploadTasks();
            instance = null;
        }
    }

    public static void startLine(@NonNull final EditorInfo attribute) {
        if (instance != null) {
            instance.startNewLine(attribute, true);
        }
    }

    public static void finishLine() {
        Log.d(TAG, "finishLine: ");
        if (instance != null) {
            Log.d(TAG, "finishLine: 2");
            instance.clearBuffers(true);
        }
    }

    static LogManager getInstance() throws LogManagerNotStartedException {
        if (instance == null) {
            //init(context);
            throw new LogManagerNotStartedException();
        }
        return instance;
    }

    boolean isPrivacyModeEnabled() {
        return privacyModeEnabled;
    }

    @SuppressWarnings("NewApi")
    private LogManager(final Context context) {
        this.context = context;
        preferencesManager = PreferencesManager.getInstance(context);
    }

    boolean registerBuffer(final Buffer buffer) {
        try {
            return buffer.isBufferAllowed() && buffer.getFileWriter() != null && buffers.add(buffer);
        } catch (IOException e) {
            return false;
        }
    }

    boolean unregisterBuffer(final Buffer buffer) {
        return buffers.remove(buffer);
    }

    @SuppressWarnings("PointlessBooleanExpression")
    FileWriter createFileWriter(final String logFilename) throws IOException {
        FileWriter fileWriter = null;
        Log.d("Log", "This is LogManager 1");

        Exception exception = null;

        try {
            if (!BuildConfig.USE_SDCARD) {
                fileWriter = openPrivateStorage(logFilename);
            } else {
                // Try opening log files on the SD card, fall back to alternate locations on failures.
                try {
                    fileWriter = openExternalPublicStorage(logFilename);
                } catch (final FileNotFoundException e2) {
                    exception = e2;
                    try {
                        fileWriter = openFallbackPublicStorage(logFilename);
                    } catch (final FileNotFoundException e3) {
                        exception = e3;
                        // Final exception defaults to private storage in /data/data.
                        fileWriter = openPrivateStorage(logFilename);
                    }
                }
            }
        } catch (final FileNotFoundException e1) {
            exception = e1;
        }

        if (fileWriter == null) {
            throw new IOException("Could not open private or public storage!");
        }

        if (exception != null) {
            CrashReportUtility.throwCrashReportNotification(context, exception);
        }
        CrashReportUtility.displayLoggingAlertNotification(context,
                CrashReportUtility.TAG_LOG_LOCATION, fileWriter.filePath);
        return fileWriter;
    }

    private FileWriter openPrivateStorage(final String filename) throws IOException {
        final File logDir = context.getFilesDir();
        final String filePath = String.format("%s/%s", logDir, filename);
        Log.d("Log", "This is LogManager 2");

        return new FileWriter(filePath, true);
    }

    private FileWriter openExternalPublicStorage(final String filename) throws IOException {
        final File logDir = context.getExternalFilesDir(null);
        if (logDir == null) {
            Log.d("Log", "context.getExternalFilesDir() returned null");

            throw new FileNotFoundException("context.getExternalFilesDir() returned null.");
        }
        Log.d("Log", "This is LogManager 3");

        final String filePath = String.format("%s/%s", logDir, filename);
        return new FileWriter(filePath, true);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private FileWriter openFallbackPublicStorage(final String filename) throws IOException {
        final File logDir = new File(getFallbackPublicStoragePath());
        logDir.mkdirs();
        final String filePath = String.format("%s/%s", logDir, filename);
        Log.d("Log", "This is LogManager 4");

        return new FileWriter(filePath, true);
    }

    private String getFallbackPublicStoragePath() {
        // Strip any extraneous backslash prefix/suffix
        final String cleanedPath = StringUtil.rlTrim(BuildConfig.FALLBACK_LOG_DIRECTORY.trim(), '/');
        return String.format("%s/%s/",
                Environment.getExternalStorageDirectory(), cleanedPath);
    }

    /**
     * Reset the buffers for a new line session.
     *
     * @param attribute The EditorInfo object of the current text input view.
     * @param logBuffer Whether or not the current buffer contents should be logged
     *                  before being cleared.
     */
    private void startNewLine(@NonNull final EditorInfo attribute, final boolean logBuffer) {
        clearBuffers(logBuffer);
        Log.d(TAG, "startNewLine: ");
        setupPrivacyMode(attribute);
        startTime = new Date();
        for (final Buffer buffer : buffers) {
            buffer.startNewLine();
        }
    }

    /**
     * Check the current EditorInfo object for the potential of sensitive data entry.
     * If detected, the log will be disabled for this line session.
     *
     * @param attribute the attributes object of the focused text-input view.
     */
    private void setupPrivacyMode(final EditorInfo attribute) {

        final int editorClass = attribute.inputType & EditorInfo.TYPE_MASK_CLASS;
        switch (editorClass) {
            case EditorInfo.TYPE_CLASS_DATETIME:
            case EditorInfo.TYPE_CLASS_NUMBER:
            case EditorInfo.TYPE_CLASS_PHONE:
                privacyModeEnabled = true;
                return;
        }

        final int variation = attribute.inputType & EditorInfo.TYPE_MASK_VARIATION;
        switch (variation) {
            case EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS:
            case EditorInfo.TYPE_TEXT_VARIATION_EMAIL_SUBJECT:
            case EditorInfo.TYPE_TEXT_VARIATION_FILTER:
            case EditorInfo.TYPE_TEXT_VARIATION_PASSWORD:
            case EditorInfo.TYPE_TEXT_VARIATION_PERSON_NAME:
            case EditorInfo.TYPE_TEXT_VARIATION_POSTAL_ADDRESS:
            case EditorInfo.TYPE_TEXT_VARIATION_URI:
            case EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD:
            case EditorInfo.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS:
            case EditorInfo.TYPE_TEXT_VARIATION_WEB_PASSWORD:
                privacyModeEnabled = true;
                return;
        }
        privacyModeEnabled = false;
    }

    private void saveBuffers() {

        gps = new GPSTracker(context);

        latitude = gps.getLatitude();
        longitude = gps.getLongitude();
        for (final Buffer buffer : buffers) {
            try {
                Log.d("Log", "We are in saveBuffer line 235");
                String bufferContents = buffer.getBufferContents();
                Log.d(TAG, "saveBuffers: buffer : " + bufferContents);
                writeToFile(buffer);
            } catch (final IOException | NullPointerException e) {
                final String msg = String.format(
                        "Unable to save %s contents to disk.", buffer.getDebugTag());
                Log.e(TAG, msg, e);
            }
        }
    }

    /**
     * Clear the buffers out and start fresh. Has the option to write the current buffer contents
     * to the log if it's not empty.
     *
     * @param logBuffer If the buffer is not empty, write the contents to the log before clearing.
     */
    private synchronized
    void  clearBuffers(final boolean logBuffer) {
        Log.d("Log", "clearBuffers 1");

        if (logBuffer) {
            Log.d("Log", "clearBuffers 2");
            saveBuffers();
        }
        for (final Buffer buffer : buffers) {
            Log.d("Log", "clearBuffers 3");
            String bufferContents = buffer.getBufferContents();
            Log.d(TAG, "saveBuffers: buffer 2 :" + bufferContents);
            Log.d(TAG, "clearBuffers: the number of buffers is: " + buffers.size());
            buffer.clearBuffer();
            Log.d(TAG, "clearBuffers: the number of buffers 2 is: " + buffers.size());
            String bufferContents2 = buffer.getBufferContents();
            Log.d(TAG, "saveBuffers: buffer 3 :" + bufferContents2);

        }
    }



    private void destroyBuffers() {
        final Iterator<Buffer> iterator = buffers.iterator();
        while (iterator.hasNext()) {
            final Buffer buffer = iterator.next();
            try {
                buffer.getFileWriter().close();
            } catch (final IOException e) {
                Log.e(TAG, String.format("Error closing FileOutputStream for %s",
                        buffer.getDebugTag()), e);
            } catch (final NullPointerException e) {
                Log.e(TAG, "Error closing FileOutputStream for null buffer reference", e);
            }
            iterator.remove();
        }
    }

    private void clearUploadTasks() {
        LogUploadTask.unregisterTasks(context);
    }

    private void writeToFile(final Buffer buffer)
            throws IOException, NullPointerException {
        final String bufferContents = buffer.getBufferContents();

        if(bufferContents.equals(prevBuffer)){
            Log.d(TAG, "writeToFile: same as last word, returing");
            return;
        }




        Log.d("Log", "this is in write to file, LogManager");
        if (TextUtils.isEmpty(bufferContents)) {
            return;
        }
        final FileWriter outputStream = buffer.getFileWriter();
        if (outputStream == null) {
            // Buffers that are don't successfully create a FileOutputStream should not get
            throw new NullPointerException(
                    "Buffer output stream must not be null! THIS SHOULDN'T HAPPEN so something must have gone wrong.");
        }
        final SimpleDateFormat format = new SimpleDateFormat(FORMAT_LINE_PREFIX, Locale.US);
        final String startTimeString = format.format(startTime);
        final String endTimeString = format.format(new Date());
        final String logLine = String.format("[%s - %s][%f - %f] %s\n", startTimeString, endTimeString, latitude, longitude, bufferContents);
        outputStream.write(logLine);
        Log.i(TAG, String.format("%s logged: %s", buffer.getDebugTag(), logLine));
        prevBuffer = bufferContents;
    }

    private void writeExportLog(final String msg, @Nullable final String label) {

        //latitude = gps.getLatitude();
        //longitude = gps.getLongitude();
        if (fileUploadLog == null) {
            fileUploadLog = new FileUploadLog();
        }
        try {
            fileUploadLog.writeLine(label, msg);
        } catch (final IOException e) {
            final String errMsg = String.format("Error writing log line! (%s | %s)", label, msg);
            Log.e(TAG, errMsg);
        }
    }

    List<File> getExportFiles() {
        final List<File> files = new ArrayList<>(buffers.size());

        Log.d("Log", "the size of buffers = " + buffers.size());
        for (final Buffer buffer : buffers) {
            try {
                final File file = createExportFile(buffer);
                if (file != null) {
                    files.add(file);
                    writeExportLog(buffer.getDebugTag(),
                            String.format("Log file exported : %s", file.getName()));
                } else {
                    final String msg = String.format(
                            "Log file does not need to be exported for %s", buffer.getDebugTag());
                    Log.i(TAG, msg);
                }
            } catch (final IOException e) {
                Log.e(TAG, String.format("Error exporting log for %s!", buffer.getDebugTag()), e);
            }
        }
        return files;
    }

    private File createExportFile(final Buffer buffer) throws IOException {
        final SimpleDateFormat format = new SimpleDateFormat(FORMAT_EXPORT_FILE_PREFIX, Locale.US);
        final String startTimeString = format.format(new Date(preferencesManager.getLogStarted()));
        final String endTimeString = format.format(new Date());
        final String newFilename = String.format("%s-%s_%s", startTimeString, endTimeString, buffer.getFilename());
        return buffer.getFileWriter().exportFile(newFilename);
    }

}
