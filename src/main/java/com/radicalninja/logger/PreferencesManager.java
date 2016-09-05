package com.radicalninja.logger;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

class PreferencesManager {

    private final static String PREFS_NAME = "com.radicalninja.logger";

    private final static String KEY_LOGSTARTED = "LastUsed";
    private final static long DEF_LOGSTARTED = -1;

    private static PreferencesManager instance;

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public static PreferencesManager getInstance(final Context context) {
        if (instance == null) {
            instance = new PreferencesManager(context);
        }
        return instance;
    }

    @SuppressLint("CommitPrefEdits")
    private PreferencesManager(final Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();

        setupFirstRunPrefs();
    }

    private void setupFirstRunPrefs() {
        if (getLogStarted() == DEF_LOGSTARTED) {
            setLogStarted(System.currentTimeMillis());
        }
    }

    long getLogStarted() {
        return prefs.getLong(KEY_LOGSTARTED, DEF_LOGSTARTED);
    }

    void setLogStarted(final long lastUsedInMillis) {
        editor.putLong(KEY_LOGSTARTED, lastUsedInMillis).apply();
    }

}
