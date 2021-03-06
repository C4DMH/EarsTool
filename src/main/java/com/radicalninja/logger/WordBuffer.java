package com.radicalninja.logger;

import android.text.TextUtils;
import android.util.Log;

import com.anysoftkeyboard.base.dictionaries.WordComposer;
import com.sevencupsoftea.ears.BuildConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * WordBuffer keeps a buffer of the user's current typing line.
 * The buffer is updated in real-time with auto-completed word, cancelled
 * auto-completions, manual character input (punctuation, emoji, etc).
 */
public class WordBuffer extends Buffer {

    private final StringBuilder lineBuffer = new StringBuilder();

    // TODO: Verify we won't need these unused variables, and then scrap 'em.
    private int keyboardCursorStart, keyboardCursorEnd;
    private int oldKeyboardCursorStart, oldKeyboardCursorEnd;
    private int cursorPosition, composingTextCursorPosition;

    private String composingText = "";
    private String prevInput = "";
    private String prevWordCorrected = "", prevWordUntouched = "";

    @Override
    void onConstructorError(final Throwable error) {
        //
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    String getBufferContents() {
        return BuildConfig.LOG_WORD_SAMPLE_INTERVAL > 1 ? getWordSample() : getLineBuffer();
    }

    @Override
    String getFilename() {
        return BuildConfig.WORD_LOG_FILENAME;
    }

    @Override
    void startNewLine() {
        cursorPosition = 0;
        oldKeyboardCursorStart = oldKeyboardCursorEnd = 0;
        keyboardCursorStart = keyboardCursorEnd = 0;
        composingText = prevInput = "";
        Log.d(TAG, "startNewLine: ");
    }

    @Override
    boolean isBufferAllowed() {
        return BuildConfig.LOG_WORDS;
    }

    private boolean diffOutOfRange(final int a, final int b, final int range) {
        final int diff = a - b;
        return diff > range || diff < -range;
    }

    public void clearBuffer() {
        //Log.d(TAG, "clearBuffer: in wordbuffer clear buffer");
        //Log.d(TAG, "clearBuffer: the length of linebuffer is: " + lineBuffer.length());
        //Log.d(TAG, "clearBuffer: linebuffer: " + lineBuffer);
        if (isLogEnabled() && (lineBuffer.length() > 0 || composingText.length() > 0)) {
            lineBuffer.delete(0, lineBuffer.length());
            //Log.d(TAG, "clearBuffer: after end of clear buffer");
            //Log.d(TAG, "clearBuffer: the length of linebuffer is: " + lineBuffer.length());
            //Log.d(TAG, "clearBuffer: linebuffer: " + lineBuffer);


        }
    }

    private String getLineBuffer() {
        //Log.d(TAG, "getLineBuffer: ");
        return String.format("%s%s", lineBuffer.toString(), composingText);
    }

    private String getWordSample() {
        Log.d(TAG, "getWordSample: ");
        final String[] bufferWords = getLineBuffer().split("\\s+");
        final List<String> sample = new ArrayList<>();
        for (int i = 0; i < bufferWords.length; i++) {
            if (i % BuildConfig.LOG_WORD_SAMPLE_INTERVAL == 0) {
                sample.add(bufferWords[i]);
            }
        }
        return TextUtils.join(" ", sample);
    }

    public void setCursorPositions(final int cursorStart, final int cursorEnd) {
        Log.d(TAG, "setCursorPositions: 1");
        if (!isLogEnabled() ||
                (cursorStart == cursorPosition && diffOutOfRange(keyboardCursorStart, cursorStart, 1))) {
            return;
        }
        Log.d(TAG, "setCursorPositions: 2");
        oldKeyboardCursorStart = keyboardCursorStart;
        //oldKeyboardCursorEnd = keyboardCursorEnd;
        keyboardCursorStart = cursorStart;
        //keyboardCursorEnd = cursorEnd;
        updateCursorPosition(keyboardCursorStart);
    }

    public void setCursorPosition(final int cursorPosition) {
        if (!isLogEnabled()) {
            return;
        }
        this.cursorPosition = cursorPosition;
    }

    private void updateCursorPosition(final int newCursorPosition) {
        Log.d(TAG, "updateCursorPosition: ");
        if (diffOutOfRange(oldKeyboardCursorStart, newCursorPosition, 1)) {
            cursorPosition = newCursorPosition;
        }
    }

    private void moveCursorToLeft(final int toLeft) {
        Log.d(TAG, "moveCursorToLeft: ");
        cursorPosition -= toLeft;
        updateCursorPosition(cursorPosition);
    }

    public void moveCursorToLeft() {
        moveCursorToLeft(1);
    }

    private void moveCursorToRight(final int toRight) {
        Log.d(TAG, "moveCursorToRight: ");
        cursorPosition += toRight;
        updateCursorPosition(cursorPosition);
    }

    public void moveCursorToRight() {
        Log.d(TAG, "moveCursorToRight: ");
        moveCursorToRight(1);
    }

    public void moveCursorToStart() {
        Log.d(TAG, "moveCursorToStart: ");
        cursorPosition = oldKeyboardCursorStart = 0;
    }

    public void moveCursorToEnd() {
        Log.d(TAG, "moveCursorToEnd: ");
        cursorPosition = oldKeyboardCursorStart = lineBuffer.length();
    }

    public void setComposingText(final String composingText) {
        this.composingText = composingText;
        composingTextCursorPosition = cursorPosition;
    }

    public void setComposingText(final CharSequence composingText) {
        this.composingText = composingText.toString();
    }

    public void insertText(final WordComposer word) {
        Log.d(TAG, "insertText: ");
        if (!isLogEnabled()) {
            return;
        }
        final String input = word.getPreferredWord().toString();
        final int start = (cursorPosition > lineBuffer.length()) ? lineBuffer.length() : Math.max(cursorPosition, 0);
        prevInput = input;
        prevWordCorrected = input;
        prevWordUntouched = word.getTypedWord().toString();
        composingText = "";
        lineBuffer.insert(start, input);

        moveCursorToRight(input.length());

        oldKeyboardCursorStart = keyboardCursorStart = cursorPosition;
    }

    public void insertText(String input) {
        Log.d(TAG, "insertText: ");
        if (!isLogEnabled()) {
            return;
        }
        prevInput = input;
        final int start = (cursorPosition > lineBuffer.length()) ? lineBuffer.length() : Math.max(cursorPosition, 0);
        if (composingText.length() > 0) {
            if (!composingText.equals(input)) {
                input = String.format("%s%s", composingText, input);
            }
            composingText = "";
        }
        lineBuffer.insert(start, input);

        moveCursorToRight(input.length());

        oldKeyboardCursorStart = keyboardCursorStart = cursorPosition;
    }

    public void insertText(final CharSequence input) {
        insertText(input.toString());
    }

    public void deleteSurroundingText(final int lengthBefore, final int lengthAfter) {
        Log.d(TAG, "deleteSurroundingText: ");
        if (!isLogEnabled() || lengthBefore == lengthAfter) {
            return;
        }
        int deleteStart = Math.max(cursorPosition - lengthBefore, 0);
        int deleteEnd = Math.min(cursorPosition + lengthAfter, lineBuffer.length());
        if (deleteStart > deleteEnd) {
            final int swapSpace = deleteStart;
            deleteStart = Math.max(deleteEnd, 0);
            deleteEnd = Math.min(swapSpace, lineBuffer.length());
        }
        lineBuffer.delete(deleteStart, deleteEnd);
        moveCursorToLeft(lengthBefore);
    }

    public void revertLastCorrection() {
        Log.d(TAG, "revertLastCorrection: ");
        if (!isLogEnabled()) {
            return;
        }
        deleteSurroundingText(prevWordCorrected.length() + prevInput.length(), 0);
//        setCursorPosition(cursorPosition - prevWordCorrected.length() + prevWordUntouched.length());

        oldKeyboardCursorStart = keyboardCursorStart = cursorPosition;

        setComposingText(prevWordUntouched);
    }
}
