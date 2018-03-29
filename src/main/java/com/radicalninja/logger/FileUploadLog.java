/*
 * Copyright (C)EARSTool
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */





package com.radicalninja.logger;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.sevencupsoftea.ears.BuildConfig;

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
