package com.example.healthy.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.healthy.Utils.ApplicationConstants;

public class PreferencesCleanReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        context.getSharedPreferences(ApplicationConstants.CONSTANT_FOOD_CALORY,Context.MODE_PRIVATE)
                .edit().clear().apply();
        context.getSharedPreferences(ApplicationConstants.CONSTANT_DAILY_GOAL,Context.MODE_PRIVATE)
                .edit().clear().apply();
    }
}
