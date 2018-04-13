package com.garmin;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

/**
 * Created by gwicks on 11/04/2018.
 */

public class BaseActivity extends AppCompatActivity {

    private static final String TAG = BaseActivity.class.getSimpleName();

    protected void showErrorDialog(String message) {
        Log.d(TAG, String.format("showErrorDialog(message = %s)", message));

        ConfirmationDialog dialog = new ConfirmationDialog(this, "ERROR", message, "OK");
        dialog.show();
    }

    protected void showBackInActionBar() {
        Log.d(TAG, "showBackInActionBar()");

        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void finishSuccessfully() {
        setResult(RESULT_OK);
        finish();
    }
}

