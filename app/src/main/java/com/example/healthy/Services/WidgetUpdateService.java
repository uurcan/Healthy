package com.example.healthy.Services;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;

import com.example.healthy.Databases.StepCounterDatabase;
import com.example.healthy.UI.Pedometer.Widget;
import com.example.healthy.Utils.DateUtil;

public class WidgetUpdateService extends Service {
    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        StepCounterDatabase db = StepCounterDatabase.getInstance(this);
        int steps = Math.max(db.getCurrentSteps() + db.getSteps(DateUtil.getToday()), 0);
        db.close();
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(WidgetUpdateService.this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(WidgetUpdateService.this, Widget.class));
        for (int appWidgetId : appWidgetIds) {
            appWidgetManager.updateAppWidget(appWidgetId, Widget.updateWidget(appWidgetId, WidgetUpdateService.this, steps));
        }
        stopSelf();
        return START_NOT_STICKY;
    }
}
