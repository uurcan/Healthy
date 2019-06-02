package com.example.healthy.UI.Pedometer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;

import com.example.healthy.Databases.StepCounterDatabase;
import com.example.healthy.Services.ShutdownReceiver;
import com.example.healthy.Services.WidgetUpdateService;
import com.example.healthy.Utils.DateUtil;


public class SensorListener extends Service implements SensorEventListener {

        private final static long MICROSECONDS_IN_ONE_MINUTE = 60000000;
        private final static long SAVE_OFFSET_TIME = AlarmManager.INTERVAL_HOUR;
        private final static int SAVE_OFFSET_STEPS = 500;

        private static int steps;
        private static int lastSaveSteps;
        private static long lastSaveTime;

        private final BroadcastReceiver shutdownReceiver = new ShutdownReceiver();

        @Override
        public void onAccuracyChanged(final Sensor sensor, int accuracy) {
            // when this method is called...
        }

        @Override
        public void onSensorChanged(final SensorEvent event) {
            steps = (int) event.values[0];
            updateIfNecessary();
        }


        private void updateIfNecessary() {
            if (steps > lastSaveSteps + SAVE_OFFSET_STEPS ||
                    (steps > 0 && System.currentTimeMillis() > lastSaveTime + SAVE_OFFSET_TIME)) {

                StepCounterDatabase db = StepCounterDatabase.getInstance(this);
                if (db.getSteps(DateUtil.getToday()) == Integer.MIN_VALUE) {
                    int pauseDifference = steps -
                            getSharedPreferences("pedometer", Context.MODE_PRIVATE)
                                    .getInt("pauseCount", steps);
                    db.insertNewDay(DateUtil.getToday(), steps - pauseDifference);
                    if (pauseDifference > 0) {
                        getSharedPreferences("pedometer", Context.MODE_PRIVATE).edit()
                                .putInt("pauseCount", steps).apply();
                    }
                }
                db.saveCurrentSteps(steps);
                db.close();
                lastSaveSteps = steps;
                lastSaveTime = System.currentTimeMillis();
                startService(new Intent(this, WidgetUpdateService.class));
            }
        }

        @Override
        public IBinder onBind(final Intent intent) {
            return null;
        }

        @Override
        public int onStartCommand(final Intent intent, int flags, int startId) {
            reRegisterSensor();
            registerBroadcastReceiver();

            long nextUpdate = Math.min(DateUtil.getTomorrow(),
                    System.currentTimeMillis() + AlarmManager.INTERVAL_HOUR);
            AlarmManager am =
                    (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            PendingIntent pi = PendingIntent
                    .getService(getApplicationContext(), 2, new Intent(this, SensorListener.class),
                            PendingIntent.FLAG_UPDATE_CURRENT);


            am.set(AlarmManager.RTC, nextUpdate, pi);

            return START_STICKY;
        }

        @Override
        public void onCreate() {
            super.onCreate();
        }

        @Override
        public void onTaskRemoved(final Intent rootIntent) {
            super.onTaskRemoved(rootIntent);

            ((AlarmManager) getSystemService(Context.ALARM_SERVICE))
                    .set(AlarmManager.RTC, System.currentTimeMillis() + 500, PendingIntent
                            .getService(this, 3, new Intent(this, SensorListener.class), 0));
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
            sm.unregisterListener(this);
        }

        private void registerBroadcastReceiver() {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SHUTDOWN);
            registerReceiver(shutdownReceiver, filter);
        }

        private void reRegisterSensor() {
            SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
            try {
                sm.unregisterListener(this);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // enable batching with delay of max 5 min
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER),
                        SensorManager.SENSOR_DELAY_NORMAL, (int) (5 * MICROSECONDS_IN_ONE_MINUTE));
            }
        }
}
