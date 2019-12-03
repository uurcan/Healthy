package com.example.healthy;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import com.example.healthy.Services.PreferencesCleanReceiver;

import java.util.Calendar;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        scheduleJob();
    }

    public void scheduleJob(){
        Calendar setCalendar = Calendar.getInstance();
        setCalendar.set(Calendar.HOUR_OF_DAY, 0);
        setCalendar.set(Calendar.MINUTE,0);
        setCalendar.set(Calendar.SECOND,0);
        setCalendar.add(Calendar.DATE, 1);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0,
                new Intent(this, PreferencesCleanReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC, setCalendar.getTimeInMillis(), 1000 * 60 *60 *24 , pi);
    } 
}
