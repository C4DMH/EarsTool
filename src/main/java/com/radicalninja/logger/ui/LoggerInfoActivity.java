package com.radicalninja.logger.ui;

import android.app.Activity;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.menny.android.anysoftkeyboard.AnyApplication;
import com.menny.android.anysoftkeyboard.R;

public class LoggerInfoActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logger_info_activity);

        final TextView deviceValueView = (TextView) findViewById(R.id.device_id_value);
        final String userId = Settings.Secure.getString(
                AnyApplication.getInstance().getContentResolver(), Settings.Secure.ANDROID_ID);
        deviceValueView.setText(userId);

        final Button button = (Button) findViewById(R.id.device_id_button);
        button.setOnClickListener(closeListener);
    }

    private View.OnClickListener closeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };

}
