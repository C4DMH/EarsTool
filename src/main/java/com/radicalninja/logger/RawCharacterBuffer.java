package com.radicalninja.logger;

import android.util.Log;

import com.menny.android.anysoftkeyboard.BuildConfig;

/**
 * RawCharacterBuffer keeps a buffer of the user's current typing line.
 * The buffer strictly keeps track of each character typed and doesn't
 * take in to account cursor positioning or auto-correction.
 */
public class RawCharacterBuffer extends Buffer {

    private final StringBuilder lineBuffer = new StringBuilder();

    @Override
    String getBufferContents() {
        return lineBuffer.toString();
    }

    @Override
    void clearBuffer() {
        Log.d(TAG, "clearBuffer: in rawcharbuffer clear buffer");
        lineBuffer.delete(0, lineBuffer.length());
    }

    @Override
    String getFilename() {
        return BuildConfig.RAW_LOG_FILENAME;
    }

    @Override
    boolean isBufferAllowed() {
        return BuildConfig.LOG_RAW;
    }

    @Override
    void onConstructorError(final Throwable error) {
        //
    }

    @Override
    void startNewLine() {
        //
    }

    public void log(final char character) {
        lineBuffer.append(character);
    }

    public void log(final String string) {
        lineBuffer.append(string);
    }
}
