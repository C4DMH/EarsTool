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
