package com.example.healthy.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.healthy.BuildConfig;
import com.example.healthy.Databases.StepCounterDatabase;
import com.example.healthy.R;
import com.example.healthy.UI.Activity.MainActivity;
import com.example.healthy.UI.Pedometer.DialogStatistics;
import com.example.healthy.UI.Pedometer.SensorListener;
import com.example.healthy.Utils.DateUtil;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.BarModel;
import org.eazegraph.lib.models.PieModel;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PedometerFragment extends BaseFragment
                            implements SensorEventListener, View.OnClickListener{

    private TextView textViewStepsCount;
    private TextView textViewTotalSteps;
    private TextView textViewAverageSteps;
    private PieModel sliceGoal, sliceCurrent;
    private PieChart pieChart;
    private static final int MINIMUM_PICKER_VALUE = 1000;
    private static final int MAXIMUM_PICKER_VALUE = 10000;
    private static final int DEFAULT_GOAL = 500;
    private int todayOffset, total_start, goal, since_boot, total_days;
    @SuppressLint("ConstantLocale")
    public final static NumberFormat formatter = NumberFormat.getInstance(Locale.getDefault());
    private boolean showSteps = true;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if(getActivity()!=null) {
            getActivity().startService(new Intent(getActivity(), SensorListener.class));
        }
    }

    @Override
    public View onCreateView(final @NonNull LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        @SuppressLint("InflateParams")
        View v = inflater.inflate(R.layout.fragment_pedometer, null);
        if(getActivity() != null) {
            ((MainActivity) getActivity()).updateToolbarTitle(getString(R.string.title_pedometer));
            TextView textViewtarget = v.findViewById(R.id.textViewTarget);
           textViewStepsCount = v.findViewById(R.id.textViewPedometerStepCount);
           textViewTotalSteps = v.findViewById(R.id.textViewPedometerTotalSteps);
           textViewAverageSteps = v.findViewById(R.id.textViewPedometerAverageSteps);
           pieChart = v.findViewById(R.id.pedometerPieChart);

           sliceCurrent = new PieModel("", 0, Color.parseColor("#99CC00"));
           pieChart.addPieSlice(sliceCurrent);

           sliceGoal = new PieModel("", DEFAULT_GOAL, Color.parseColor("#CC0000"));
           pieChart.addPieSlice(sliceGoal);

           ImageButton imageButtonTarget = v.findViewById(R.id.imageButtonTarget);
           imageButtonTarget.setOnClickListener(this);
           String target = String.valueOf(getActivity().getSharedPreferences("pedometer",Context.MODE_PRIVATE)
                   .getInt("goal",5000));
           textViewtarget.setText(target);
           pieChart.setDrawValueInPie(false);
           pieChart.setUsePieRotation(true);
           pieChart.startAnimation();

        }
        return v;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onResume() {
        super.onResume();
        if(getActivity()!=null) {
            StepCounterDatabase db = StepCounterDatabase.getInstance(getActivity());

            if (BuildConfig.DEBUG) db.logState();
            todayOffset = db.getSteps(DateUtil.getToday());

            SharedPreferences prefs =
                    getActivity().getSharedPreferences("pedometer", Context.MODE_PRIVATE);

            goal = prefs.getInt("goal", DEFAULT_GOAL);
            since_boot = db.getCurrentSteps();
            int pauseDifference = since_boot - prefs.getInt("pauseCount", since_boot);

            SensorManager sm = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
            Sensor sensor;
            sensor = sm.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

            if (sensor == null) {
                    new AlertDialog.Builder(getActivity()).setTitle(R.string.no_sensor)
                            .setMessage(R.string.no_sensor_explain)
                            .setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(final DialogInterface dialogInterface) {
                                        //Objects.requireNonNull(getActivity()).finish();
                                        dialogInterface.dismiss();
                                }
                            }).setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).create().show();

            } else {
                sm.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI, 0);
            }
            since_boot -= pauseDifference;
            total_start = db.getTotalWithoutToday();
            total_days = db.getDays();

            db.close();
            updatePie();
            updateBars();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //empty required method
    }

    @Override
    public void onPause() {
        super.onPause();
        if(getActivity()!=null) {
            SensorManager sm =
                    (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
            sm.unregisterListener(this);

            StepCounterDatabase db = StepCounterDatabase.getInstance(getActivity());
            db.saveCurrentSteps(since_boot);
            db.close();
        }
    }


    @Override
    public void onSensorChanged(final SensorEvent event) {

        if (event.values[0] > Integer.MAX_VALUE || event.values[0] == 0) {
            return;
        }
        if (todayOffset == Integer.MIN_VALUE) {
            todayOffset = -(int) event.values[0];
            StepCounterDatabase db = StepCounterDatabase.getInstance(getActivity());
            db.insertNewDay(DateUtil.getToday(), (int) event.values[0]);
            db.close();
        }
        since_boot = (int) event.values[0];
        updatePie();
    }

    private void updatePie() {
        int steps_today = Math.max(todayOffset + since_boot, 0);
        sliceCurrent.setValue(steps_today);
        if (goal - steps_today > 0) {
            if (pieChart.getData().size() == 1) {

                pieChart.addPieSlice(sliceGoal);
            }
            sliceGoal.setValue(goal - steps_today);
        } else {
            pieChart.clearChart();
            pieChart.addPieSlice(sliceCurrent);
        }
        pieChart.update();
        if (showSteps) {
            textViewStepsCount.setText(formatter.format(steps_today));
            textViewTotalSteps.setText(formatter.format(total_start + steps_today));
            textViewAverageSteps.setText(formatter.format((total_start + steps_today) / total_days));
        }if(getActivity() != null){
            SharedPreferences.Editor sharedPreferences =
                    getActivity().getSharedPreferences("dailySteps",Context.MODE_PRIVATE).edit();
            sharedPreferences.putInt("dailySteps",steps_today);
            sharedPreferences.apply();

        }
    }

    private void updateBars() {
        if (getView() != null) {
            SimpleDateFormat df = new SimpleDateFormat("E", Locale.getDefault());
            BarChart barChart = getView().findViewById(R.id.pedometerBarChart);
            if (barChart.getData().size() > 0) barChart.clearChart();
            int steps;

            barChart.setShowDecimal(!showSteps);
            BarModel bm;
            StepCounterDatabase db = StepCounterDatabase.getInstance(getActivity());
            List<Pair<Long, Integer>> last = db.getLastEntries(8);
            db.close();
            for (int i = last.size() - 1; i > 0; i--) {
                Pair<Long, Integer> current = last.get(i);
                steps = current.second;
                if (steps > 0) {
                    bm = new BarModel(df.format(new Date(current.first)), 0,
                            steps > goal ? Color.parseColor("#99CC00") : Color.parseColor("#0099cc"));
                    if (showSteps) {
                        bm.setValue(steps);
                    }
                    barChart.addBar(bm);
                }
            }
            if (barChart.getData().size() > 0) {
                barChart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        DialogStatistics.getDialog(getActivity(), since_boot).show();
                    }
                });
                barChart.startAnimation();
            } else {
                barChart.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.imageButtonTarget) {
            initializePickerDialog();
        }
    }

    private void initializePickerDialog(){
        if(getActivity() != null){
            final SharedPreferences sharedPreferences = getActivity().getSharedPreferences("pedometer",Context.MODE_PRIVATE);
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            final NumberPicker picker = new NumberPicker(getContext());
            picker.setMinValue(MINIMUM_PICKER_VALUE);
            picker.setMaxValue(MAXIMUM_PICKER_VALUE);
            picker.setValue(sharedPreferences.getInt("goal",5000));
            builder.setTitle(getString(R.string.pick_target));
            builder.setView(picker);
            builder.setNegativeButton(getString(R.string.negative), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setPositiveButton(getString(R.string.positive), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    picker.clearFocus();
                    sharedPreferences.edit().putInt("goal",picker.getValue()).apply();
                    dialog.dismiss();
                }
            });
            builder.show();
        }
    }
}