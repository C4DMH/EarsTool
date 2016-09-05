package com.radicalninja.logger;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Parcelable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.text.format.DateFormat;

import com.anysoftkeyboard.ui.SendBugReportUiActivity;
import com.anysoftkeyboard.ui.dev.DeveloperUtils;
import com.anysoftkeyboard.utils.Log;
import com.menny.android.anysoftkeyboard.BuildConfig;
import com.menny.android.anysoftkeyboard.R;

import java.io.File;
import java.util.Date;

class CrashReportUtility {

    // TODO: Move to its own file and build out methods
//    static class EasyLog {
//        static void d(final String message, final Throwable throwable) {
//            Log.d()
//        }
//
//        private static String getTagString(final Class clazz) {
//            return clazz.getSimpleName();
//        }
//    }

    // TODO: Add Log calls to this class, rename to more general name.

    public static final String TAG_LOG_LOCATION = "Log file location";

    public static void displayLoggingAlertNotification(final Context context, final String text,
                                                       final String subText) {
        if (!BuildConfig.DEBUG_NOTIFICATIONS) {
            return;
        }
        sendCrashNotification(context, null, "Logging alert", "Keyboard Logger", text, subText,
                R.id.notification_icon_app_alert);
    }

    // Methods lifted from com.menny.android.anysoftkeyboard.ChewbaccaUncaughtExceptionHandler.
    public static void throwCrashReportNotification(final Context context, final Throwable ex) {

        if (!BuildConfig.DEBUG_NOTIFICATIONS) {
            return;
        }

        String stackTrace = Log.getStackTrace(ex);

        String appName = DeveloperUtils.getAppDetails(context);

        final CharSequence utcTimeDate = DateFormat.format(
                "kk:mm:ss dd.MM.yyyy", new Date());
        final String newline = DeveloperUtils.NEW_LINE;
        String logText = "Hi. It seems that we have crashed.... Here are some details:" + newline
                + "****** UTC Time: "
                + utcTimeDate
                + newline
                + "****** Application name: "
                + appName
                + newline
                + "******************************" + newline
                + "****** Exception type: "
                + ex.getClass().getName()
                + newline
                + "****** Exception message: "
                + ex.getMessage()
                + newline + "****** Trace trace:" + newline + stackTrace + newline;
        logText += "******************************" + newline
                + "****** Device information:" + newline
                + DeveloperUtils.getSysInfo(context);
        if (ex instanceof OutOfMemoryError
                || (ex.getCause() != null && ex.getCause() instanceof OutOfMemoryError)) {
            logText += "******************************\n"
                    + "****** Memory:" + newline + getMemory();
        }
        logText += "******************************" + newline + "****** Log-Cat:" + newline
                + Log.getAllLogLines();

        String crashType = ex.getClass().getSimpleName() + ": " + ex.getMessage();
        Intent notificationIntent = new Intent(context, SendBugReportUiActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        final Parcelable reportDetailsExtra = new SendBugReportUiActivity.BugReportDetails(ex, logText);
        notificationIntent.putExtra(SendBugReportUiActivity.EXTRA_KEY_BugReportDetails, reportDetailsExtra);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        sendCrashNotification(context, contentIntent, context.getText(R.string.ime_crashed_ticker),
                context.getText(R.string.ime_name), context.getText(R.string.ime_crashed_sub_text),
                BuildConfig.DEBUG ? crashType : null/*not showing the type of crash in RELEASE mode*/,
                R.id.notification_icon_app_error);
    }

    private static void sendCrashNotification(final Context context, final PendingIntent contentIntent,
                                              final CharSequence ticker, final CharSequence contentTitle,
                                              final CharSequence contentText, final CharSequence subText,
                                              final int notificationID) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB?
                R.drawable.notification_error_icon : R.drawable.ic_notification_error).
                setColor(ContextCompat.getColor(context, R.color.notification_background_error)).
                setTicker(ticker).
                setContentTitle(contentTitle).
                setContentText(contentText).
                setSubText(subText).
                setWhen(System.currentTimeMillis()).
                setContentIntent(contentIntent).
                setAutoCancel(true).
                setOnlyAlertOnce(true).
                setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);

        builder.setStyle(new NotificationCompat.BigTextStyle().
                setBigContentTitle(contentTitle).bigText(contentText).setSummaryText(subText));

        // notifying
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(notificationID, builder.build());
    }

    private static String getMemory() {
        String mem = "Total: " + Runtime.getRuntime().totalMemory() + "\n"
                + "Free: " + Runtime.getRuntime().freeMemory() + "\n" + "Max: "
                + Runtime.getRuntime().maxMemory() + "\n";

        if (BuildConfig.DEBUG) {
            try {
                File target = DeveloperUtils.createMemoryDump();
                mem += "Created hprof file at " + target.getAbsolutePath()
                        + "\n";
            } catch (Exception e) {
                mem += "Failed to create hprof file cause of " + e.getMessage();
                e.printStackTrace();
            }
        }

        return mem;
    }

}
