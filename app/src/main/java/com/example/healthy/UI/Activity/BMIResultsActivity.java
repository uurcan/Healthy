package com.example.healthy.UI.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.healthy.Model.MetricFormula;
import com.example.healthy.R;
import com.example.healthy.Utils.ApplicationConstants;
import com.example.healthy.Utils.BMICategory;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class BMIResultsActivity extends BaseActivity implements View.OnClickListener {

    private BMICategory bmiCategory = new BMICategory();
    private TextView textViewBMIIndex,textViewBMIResult,textViewRequiredAmount,textViewCurrentTime;
    private NumberPicker numberPickerWeight,numberPickerHeight;
    private DecimalFormat TWO_DECIMAL_PLACES = new DecimalFormat(".##");
    private Button buttonProceed;
    private ViewGroup transition;
    private int hour,minute,second;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi);
        initializeViews();
        initializeLayoutAnimations();
    }

    private void initializeViews() {
            initializeNumberPickers();
            initializeCurrentTime();
            textViewBMIIndex = findViewById(R.id.txtBMIValue);
            textViewBMIResult = findViewById(R.id.txtResultBMI);
            textViewRequiredAmount = findViewById(R.id.txtRequiredAmount);
            Button buttonCalculate = findViewById(R.id.btnBMISubmit);
            buttonCalculate.setOnClickListener(this);
            buttonProceed = findViewById(R.id.btnBMIProceed);
            buttonProceed.setVisibility(View.GONE);
            buttonProceed.setOnClickListener(this);
            transition = findViewById(R.id.transition);
    }

    private void initializeNumberPickers() {
        numberPickerWeight = findViewById(R.id.numberPickerWeight);
        numberPickerHeight = findViewById(R.id.numberPickerHeight);
        numberPickerWeight.setMinValue(ApplicationConstants.CONSTANT_MINIMUM_WEIGHT);
        numberPickerWeight.setMaxValue(ApplicationConstants.CONSTANT_MAXIMUM_WEIGHT);
        numberPickerWeight.setValue(ApplicationConstants.CONSTANT_SELECTED_WEIGHT);
        numberPickerHeight.setMinValue(ApplicationConstants.CONSTANT_MINIMUM_HEIGHT);
        numberPickerHeight.setMaxValue(ApplicationConstants.CONSTANT_MAXIMUM_HEIGHT);
        numberPickerHeight.setValue(ApplicationConstants.CONSTANT_SELECTED_HEIGHT);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.btnBMISubmit:
            initializeBMICalculator();
            initializeAnimation();
                break;
            case R.id.btnBMIProceed:
                switchActivity();
                this.finish();
                break;
        }
    }

    private void initializeAnimation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            TransitionManager.beginDelayedTransition(transition);
        }
        buttonProceed.setVisibility(View.VISIBLE);
        //textViewCurrentTime.setVisibility(View.GONE);
    }

    private void switchActivity() {
        startActivity(new Intent(this,MainActivity.class));
    }

    private void initializeLayoutAnimations() {
        LinearLayout linearLayout = findViewById(R.id.layout_bmi_calculator);
        AnimationDrawable drawable = (AnimationDrawable)linearLayout.getBackground();
        drawable.setEnterFadeDuration(2000);
        drawable.setExitFadeDuration(4000);
        drawable.start();
    }

    private void initializeCurrentTime() {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        textViewCurrentTime = findViewById(R.id.textCurrentTime);
                        StringBuilder currentTimeBuilder = new StringBuilder();
                        Calendar calendar = Calendar.getInstance();
                        hour = calendar.get(Calendar.HOUR_OF_DAY);
                        minute = calendar.get(Calendar.MINUTE);
                        second = calendar.get(Calendar.SECOND);
                        currentTimeBuilder.append(String.format(getString(R.string.value_time_format),hour,minute,second));
                        textViewCurrentTime.setText(currentTimeBuilder);
                    }
                });
            }
        }; timer.schedule(task,1000,1000);
    }

    @SuppressLint("SetTextI18n")
    private void initializeBMICalculator() {
       int weight;
       int height;
       try{
            weight =(numberPickerWeight.getValue());
            height =(numberPickerHeight.getValue());
        }   catch (NumberFormatException n){
            weight = 0;
            height = 0;
        }
        MetricFormula metricFormula = new MetricFormula(weight, height);
        StringBuilder bmiIndex = new StringBuilder("BMI : " + (TWO_DECIMAL_PLACES.format
                (metricFormula.computeBMI(metricFormula.getInputKG(), metricFormula.getInputMeter()))));

        StringBuilder bmiResult = new StringBuilder(bmiCategory.getCategory(metricFormula.computeBMI
                (metricFormula.getInputKG(), metricFormula.getInputMeter()),getApplicationContext()));

        StringBuilder requiredAmountText = new StringBuilder().append(getString(R.string.required_amount_of_water))
                .append((TWO_DECIMAL_PLACES.format(metricFormula.requiredAmount
                        (metricFormula.getInputKG(),metricFormula.getInputMeter())))).append(getString(R.string.liter));

        float amountWater = (float) metricFormula.requiredAmount(metricFormula.getInputKG(),metricFormula.getInputMeter());
        SharedPreferences.Editor editor = getSharedPreferences("requiredAmount", MODE_PRIVATE).edit();
        editor.putFloat("amountWater",amountWater);
        editor.apply();

       textViewBMIResult.setText(bmiResult);
       textViewBMIIndex.setText(bmiIndex);
       textViewRequiredAmount.setText(requiredAmountText);
    }
}
