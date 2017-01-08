package com.radicalninja.logger;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.menny.android.anysoftkeyboard.BuildConfig;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

class FileUploadLog extends LogFileController {

    private static final String FORMAT_LINE_PREFIX = "[yyyy-MM-dd HH:mm:ss]";
    private static final String TAG = "FileUploadLog";


    @Override
    String getFilename() {
        return BuildConfig.UPLOAD_LOG_FILENAME;
    }

    @Override
    protected boolean isLogEnabled() {
        return true;
    }

    @Override
    void onConstructorError(final Throwable error) {
        //
    }

    void writeLine(final String msg, @Nullable final String label) throws IOException {


        if (TextUtils.isEmpty(label)) {
            if (!TextUtils.isEmpty(msg)) {
                writeLine(msg);
            }
        } else if (!TextUtils.isEmpty(msg)) {
            final String line = String.format("%s | %s\n", label, msg);
            writeLine(line);
        }
    }

    void writeLine(final String line) throws IOException {



        if (TextUtils.isEmpty(line)) {
            return;
        }
        final SimpleDateFormat format = new SimpleDateFormat(FORMAT_LINE_PREFIX, Locale.US);
        final String timestamp = format.format(new Date());
        android.util.Log.d(TAG, "writeLine: were are about to write in FileUploadLog " + line);
        final String output = String.format("%s %s", timestamp, line);
        getFileWriter().write(output);
    }

}
