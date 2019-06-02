package com.example.healthy.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.healthy.Databases.StepCounterDatabase;
import com.example.healthy.UI.Pedometer.SensorListener;
import com.example.healthy.Utils.DateUtil;
public class ShutdownReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {

        context.startService(new Intent(context, SensorListener.class));
        context.getSharedPreferences("pedometer", Context.MODE_PRIVATE).edit()
                .putBoolean("correctShutdown", true).apply();

        StepCounterDatabase db = StepCounterDatabase.getInstance(context);
        if (db.getSteps(DateUtil.getToday()) == Integer.MIN_VALUE) {
            int steps = db.getCurrentSteps();
            db.insertNewDay(DateUtil.getToday(), steps);
        } else {
            db.addToLastEntry(db.getCurrentSteps());
        }
        db.close();
    }
}
