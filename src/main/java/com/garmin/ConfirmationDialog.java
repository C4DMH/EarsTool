package com.garmin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

/**
 * Created by gwicks on 11/04/2018.
 */

public class ConfirmationDialog
{
    private final Context mContext;
    private final String mTitle;
    private final String mMessage;
    private final String mPositiveText;
    private final String mNegativeText;
    private final DialogInterface.OnClickListener mOnClickListener;

    public ConfirmationDialog(@NonNull Context context, String title, String message, String positiveText)
    {
        this(context, title, message, positiveText, null, null);
    }

    public ConfirmationDialog(@NonNull Context context, String title, String message, String positiveText,
                              @Nullable DialogInterface.OnClickListener listener)
    {
        this(context, title, message, positiveText, null, listener);
    }

    public ConfirmationDialog(@NonNull Context context, String title, String message, String positiveText,
                              String negativeText, @Nullable DialogInterface.OnClickListener listener)
    {
        this.mContext = context;
        this.mTitle = title;
        this.mMessage = message;
        this.mPositiveText = positiveText;
        this.mNegativeText = negativeText;
        this.mOnClickListener = listener;
    }

    public void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        if (!TextUtils.isEmpty(mTitle)) {
            builder.setTitle(mTitle);
        }
        if (!TextUtils.isEmpty(mMessage)) {
            builder.setMessage(mMessage);
        }
        if (!TextUtils.isEmpty(mPositiveText)) {
            builder.setPositiveButton(mPositiveText, mOnClickListener);
        }
        if (!TextUtils.isEmpty(mNegativeText)) {
            builder.setNegativeButton(mNegativeText, mOnClickListener);
        }

        builder.create().show();
    }
}