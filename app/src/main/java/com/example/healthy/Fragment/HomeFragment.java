package com.example.healthy.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.healthy.BuildConfig;
import com.example.healthy.R;
import com.example.healthy.UI.Activity.MainActivity;
import com.example.healthy.Utils.ApplicationConstants;
import com.ncapdevi.fragnav.FragNavController;

import java.text.DecimalFormat;
import java.util.Locale;
import java.util.Objects;

public class HomeFragment extends BaseFragment implements View.OnClickListener,View.OnLongClickListener {

    private TextView textViewRequiredAmount,textViewPedometerSummary,textViewFoodSummary,textViewReminderSummary;
    private ProgressBar progressBar;
    private RadioButton radioButtonTurkish,radioButtonEnglish;
    private SharedPreferences dailySteps,preferences,sharedPreferences,currentAmountWater;
    private ImageView imageViewWaterSummary;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeLocale();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
        initializeSharedPreferences();
        initializePedometerProgressCalculator();
        initializeWaterFragmentProgress();
        initializeFoodCalorySummary();
        initializeReminderSummary();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.imageViewLanguagePreference:
                initializeLanguagePicker();
                break;
            case R.id.imageViewInfo:
                initializeInfoDialog();
                break;
            case R.id.imageViewCaloriesSummary:
                switchToCaloriesFragment();
                break;
            case R.id.imageViewWaterSummary:
                switchToWaterFragment();
                break;
            case R.id.imageViewReminderSummary:
                switchToReminderFragment();
                break;
            case R.id.imageViewPedometerSummary:
                switchToPedometerFragment();
                break;
                default:
                    break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()){
            case R.id.imageViewWaterSummary:
                waterCleanerDialog();
                break;
            case R.id.imageViewCaloriesSummary:
                caloriesCleanerDialog();
                break;
        }
        return false;
    }

    private void initializeSharedPreferences(){
        if(getActivity() != null){
             preferences = getActivity().getSharedPreferences(ApplicationConstants.CONSTANT_REQUIRED_AMOUNT, Context.MODE_PRIVATE);
             sharedPreferences = getActivity().getSharedPreferences(ApplicationConstants.CONSTANT_PEDOMETER,Context.MODE_PRIVATE);
             dailySteps = getActivity().getSharedPreferences(ApplicationConstants.CONSTANT_DAILY_STEPS,Context.MODE_PRIVATE);
             currentAmountWater = getActivity().getSharedPreferences(ApplicationConstants.CONSTANT_DAILY_GOAL,Context.MODE_PRIVATE);
        }
    }

    private void initializeViews(View view){
        if(getActivity() != null) {
            ((MainActivity) getActivity()).updateToolbarTitle(getString(R.string.title_home));
            ImageView imageViewLanguagePreference = view.findViewById(R.id.imageViewLanguagePreference);
            ImageView imageViewInfo = view.findViewById(R.id.imageViewInfo);
            ImageView imageViewCaloriesSummary = view.findViewById(R.id.imageViewCaloriesSummary);
            imageViewWaterSummary = view.findViewById(R.id.imageViewWaterSummary);
            ImageView imageViewReminderSummary = view.findViewById(R.id.imageViewReminderSummary);
            ImageView imageViewPedometerSummary = view.findViewById(R.id.imageViewPedometerSummary);
            textViewRequiredAmount = view.findViewById(R.id.textViewRequiredAmount);
            textViewFoodSummary = view.findViewById(R.id.textViewFoodSummary);
            textViewPedometerSummary = view.findViewById(R.id.textViewPedometerSummary);
            textViewReminderSummary = view.findViewById(R.id.textViewReminderSummary);
            progressBar = view.findViewById(R.id.determinateBar);
            imageViewCaloriesSummary.setOnLongClickListener(this);
            imageViewWaterSummary.setOnLongClickListener(this);
            imageViewLanguagePreference.setOnClickListener(this);
            imageViewInfo.setOnClickListener(this);
            imageViewCaloriesSummary.setOnClickListener(this);
            imageViewWaterSummary.setOnClickListener(this);
            imageViewReminderSummary.setOnClickListener(this);
            imageViewPedometerSummary.setOnClickListener(this);
        }
    }

    private void initializePedometerProgressCalculator(){
        int totalSteps = dailySteps.getInt(ApplicationConstants.CONSTANT_DAILY_STEPS, 0);
        int value = sharedPreferences.getInt(ApplicationConstants.CONSTANT_GOAL, 10000);
        int percentage = (int) (((double) totalSteps / (double) value) * 100);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            progressBar.setProgress(percentage,true);
        } else {
            progressBar.setProgress(percentage);
        }
        StringBuilder pedometerSummary = new StringBuilder().append(totalSteps).append(" / ").append(value);

        textViewPedometerSummary.setText(pedometerSummary);
    }

    private void initializeLanguagePicker(){
        if(getActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            LayoutInflater layoutInflater = getActivity().getLayoutInflater();
            @SuppressLint("InflateParams")
            View languagePickerView = layoutInflater.inflate(R.layout.language_picker,null,false);
            builder.setView(languagePickerView);
            builder.setTitle(getString(R.string.pick_language));
            radioButtonEnglish = languagePickerView.findViewById(R.id.imageViewEnglish);
            radioButtonTurkish = languagePickerView.findViewById(R.id.imageViewTurkish);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (Objects.equals(getActivity().getSharedPreferences(ApplicationConstants.CONSTANT_LANGUAGE_PREFERENCES, Context.MODE_PRIVATE)
                        .getString(ApplicationConstants.CONSTANT_SELECTED_LANGUAGE, ApplicationConstants.CONSTANT_TR), ApplicationConstants.CONSTANT_TR))
                            radioButtonTurkish.setChecked(true);
                else
                    radioButtonEnglish.setChecked(true);
            }
            builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(getActivity() != null) {
                            if (radioButtonTurkish.isChecked()) {
                                setLocale(ApplicationConstants.CONSTANT_TR);
                                Toast.makeText(getContext(), getString(R.string.restart_device), Toast.LENGTH_LONG).show();
                                } else if (radioButtonEnglish.isChecked()) {
                                setLocale(ApplicationConstants.CONSTANT_EN);
                                Toast.makeText(getContext(), getString(R.string.restart_device), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
                builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        }

    private void initializeInfoDialog(){
        if (getActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            @SuppressLint("InflateParams") final View preferencesView = inflater.inflate(R.layout.preferences_view,null,false);
            builder.setView(preferencesView);
            TextView textViewAbout = preferencesView.findViewById(R.id.textViewAbout);
            StringBuilder builder1 = new StringBuilder("Version : ").append(BuildConfig.VERSION_NAME).append(" (")
                    .append(BuildConfig.VERSION_CODE).append(") \n");
            StringBuilder builder2 = new StringBuilder(getString(R.string.created_by)).append(" ").append(getString(R.string.Ugurcan_Celebi));

            textViewAbout.setText(builder1.append(builder2));
            builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }
    }
    private void initializeWaterFragmentProgress(){

        DecimalFormat twoDecimalPlaces = new DecimalFormat(".##");
        float amount = preferences.getFloat(ApplicationConstants.CONSTANT_AMOUNT_WATER, 0);
        float current = currentAmountWater.getFloat(ApplicationConstants.CONSTANT_DAILY_GOAL,0);
        if (current < 0 )
            current = 0;
        StringBuilder waterSummary = new StringBuilder().append(twoDecimalPlaces.format(current)).
                append(" / ").append(twoDecimalPlaces.format(amount)).append(" L");
        textViewRequiredAmount.setText(waterSummary);
            if ((double)current / (double)amount >= 0 && (double)current / (double)amount <= 0.25){
                imageViewWaterSummary.setImageResource(R.drawable.ico_empty_glass_256);
            }else if ((double)current / (double)amount >= 0.26 && (double)current / (double)amount <= 0.50){
                imageViewWaterSummary.setImageResource(R.drawable.ico_glass_quarter_256);
            }else if ((double)current / (double)amount >= 0.51 && (double)current / (double)amount <= 0.75){
                imageViewWaterSummary.setImageResource(R.drawable.ico_glass_half_256);
            }else if ((double)current / (double)amount >= 0.76 && (double)current / (double)amount <= 1){
                imageViewWaterSummary.setImageResource(R.drawable.ico_glass_full_256);
            }else {
                imageViewWaterSummary.setImageResource(R.drawable.ico_glass_full_256);
            }

    }

    private void initializeFoodCalorySummary() {
        if (getActivity() != null){
            SharedPreferences preferences = getActivity().getSharedPreferences(ApplicationConstants.CONSTANT_FOOD_CALORY,Context.MODE_PRIVATE);
            int preferencesTotal = preferences.getInt(ApplicationConstants.CONSTANT_TOTAL_CALORY,0);
            StringBuilder builder = new StringBuilder(getString(R.string.calories)).append(" : ").append(preferencesTotal);
            textViewFoodSummary.setText(builder);
        }
    }
    private void initializeReminderSummary(){
        if (getActivity() != null){
            SharedPreferences preferences = getActivity().getSharedPreferences(ApplicationConstants.CONSTANT_REMINDER_COUNT,Context.MODE_PRIVATE);
            int reminderCount = preferences.getInt(ApplicationConstants.CONSTANT_REMINDER_COUNT,0);
            StringBuilder builder = new StringBuilder(getString(R.string.reminder)).append(" : ").append(reminderCount);
            textViewReminderSummary.setText(builder);
        }
    }

    private void switchToCaloriesFragment(){
        if (getActivity() != null){
            ((MainActivity) getActivity()).switchTab(FragNavController.TAB2);
        }
    }
    private void switchToWaterFragment(){
        if (getActivity() != null){
            ((MainActivity) getActivity()).switchTab(FragNavController.TAB3);
        }
    }
    private void switchToPedometerFragment(){
        if (getActivity() != null){
            ((MainActivity) getActivity()).switchTab(FragNavController.TAB4);
        }
    }
    private void switchToReminderFragment(){
        if (getActivity() != null){
            ((MainActivity) getActivity()).switchTab(FragNavController.TAB5);
        }
    }
    private void initializeLocale(){
            if (getActivity() != null) {
                SharedPreferences preferences = getActivity().getSharedPreferences(ApplicationConstants.CONSTANT_LANGUAGE_PREFERENCES, Context.MODE_PRIVATE);
                String language = preferences.getString(ApplicationConstants.CONSTANT_SELECTED_LANGUAGE, ApplicationConstants.CONSTANT_TR);
                setLocale(language);
        }
    }
    private void setLocale(String lang){
        if (getContext() != null) {
            Locale locale = new Locale(lang);
            Configuration configuration = getResources().getConfiguration();
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            configuration.locale = locale;
            getResources().updateConfiguration(configuration, displayMetrics);
            SharedPreferences.Editor editor = getContext().getSharedPreferences(ApplicationConstants.CONSTANT_LANGUAGE_PREFERENCES, Context.MODE_PRIVATE).edit();
            editor.putString(ApplicationConstants.CONSTANT_SELECTED_LANGUAGE, lang);
            editor.apply();
        }
    }
    private void waterCleanerDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.clean));
        builder.setMessage(getString(R.string.sure_to_clean));
        builder.setNegativeButton(getString(R.string.negative), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(getString(R.string.positive), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (getContext() != null) {
                    if (getActivity() != null)
                            getContext().getSharedPreferences(ApplicationConstants.CONSTANT_DAILY_GOAL, Context.MODE_PRIVATE)
                                .edit().clear().apply();
                    }
                }
        });
        builder.show();
    }
    private void caloriesCleanerDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.clean));
        builder.setMessage(getString(R.string.sure_to_clean));
        builder.setNegativeButton(getString(R.string.negative), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(getString(R.string.positive), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (getContext() != null) {
                    if (getActivity() != null)
                            getContext().getSharedPreferences(ApplicationConstants.CONSTANT_FOOD_CALORY, Context.MODE_PRIVATE)
                                    .edit().clear().apply();
                }
            }
        });
        builder.show();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}