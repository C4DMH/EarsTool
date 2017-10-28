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

import java.io.IOException;

abstract class LogFileController {

    protected final String TAG = this.getClass().getSimpleName();

    private final FileWriter fileWriter;

    LogFileController() {
        fileWriter = openLogFile();
    }

    protected FileWriter openLogFile() {
        try {
            return (isLogEnabled()) ?
                    LogManager.getInstance().createFileWriter(getFilename()) : null;
        } catch (final Exception e) {
            onConstructorError(e);
            return null;
        }
    }

    final String getDebugTag() {
        return TAG;
    }

    final FileWriter getFileWriter() throws IOException {
        if (fileWriter == null) {
            throw new IOException(String.format("%s's FileWriter does not exist!", getDebugTag()));
        }
        return fileWriter;
    }

    protected abstract boolean isLogEnabled();

    abstract String getFilename();

    abstract void onConstructorError(final Throwable error);

}
