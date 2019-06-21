package com.example.healthy.UI.Reminder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;

import com.example.healthy.R;

public class Alert extends Activity {

    private MediaPlayer mp;
    int resource = R.raw.alarm;
    private Vibrator vibrator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);
        mp = MediaPlayer.create(getApplicationContext(),resource);
        mp.start();
        vibrator = (Vibrator)getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(10000);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if(getIntent().getExtras() != null) {
            String alert = "Alert : " + getIntent().getExtras().getString("title");
            builder.setMessage(alert).setCancelable(true).setPositiveButton(getString(R.string.text_button_proceed),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Alert.this.finish();
                        }
                    });
            builder.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mp.release();
        vibrator.cancel();
    }
}
