package com.example.healthy.UI.Pedometer;

import android.app.Dialog;
import android.content.Context;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.example.healthy.Databases.StepCounterDatabase;
import com.example.healthy.Fragment.PedometerFragment;
import com.example.healthy.R;
import com.example.healthy.Utils.DateUtil;
import java.util.Calendar;
import java.util.Date;

public abstract class DialogStatistics {

    public static Dialog getDialog(final Context c, int since_boot) {
        final Dialog dialog = new Dialog(c);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.statistics);
        dialog.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        StepCounterDatabase db = StepCounterDatabase.getInstance(c);

        Pair<Date, Integer> record = db.getRecordData();

        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(DateUtil.getToday());

        date.add(Calendar.DATE, -6);

        int thisWeek = db.getSteps(date.getTimeInMillis(), System.currentTimeMillis()) + since_boot;
        date.setTimeInMillis(DateUtil.getToday());
        date.set(Calendar.DAY_OF_MONTH, 1);
        ((TextView) dialog.findViewById(R.id.record)).setText(
                String.format("%s @ %s", PedometerFragment.formatter.format(record.second), java.text.DateFormat.getDateInstance().format(record.first)));
        ((TextView) dialog.findViewById(R.id.totalthisweek)).setText(PedometerFragment.formatter.format(thisWeek));
        ((TextView) dialog.findViewById(R.id.averagethisweek)).setText(PedometerFragment.formatter.format(thisWeek / 7));

        db.close();

        return dialog;
    }

}
