package com.radicalninja.logger;

import android.util.Log;

abstract class Buffer extends LogFileController {

    Buffer() {
        super();
        LogManager.getInstance().registerBuffer(this);
        Log.e("Log", "In Buffer constructor");
    }

    @Override
    protected boolean isLogEnabled() {
        return isBufferAllowed() && !LogManager.getInstance().isPrivacyModeEnabled();
    }

    @Override
    void onConstructorError(Throwable error) {
        Log.e(TAG, "Buffer construction error. Buffer was not registered with LogManager.", error);
    }

    abstract void clearBuffer();

    abstract String getBufferContents();

    abstract boolean isBufferAllowed();

    abstract void startNewLine();

}
