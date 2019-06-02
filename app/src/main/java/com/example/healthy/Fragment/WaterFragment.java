package com.example.healthy.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.healthy.R;
import com.example.healthy.UI.Activity.MainActivity;
import com.example.healthy.Utils.ApplicationConstants;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.text.DecimalFormat;

import me.itangqi.waveloadingview.WaveLoadingView;

import static android.content.Context.MODE_PRIVATE;

public class WaterFragment extends BaseFragment implements View.OnClickListener{
    private float preferenceCurrent,preferenceTarget;
    private TextView textViewWaterSummary;
    private StringBuilder builder;
    private ImageView imageViewComplete;
    private EditText editTextWaterAmount;
    private DecimalFormat twoDecimalPlaces = new DecimalFormat(ApplicationConstants.CONSTANT_TWO_DECIMAL_PLACES_FORMAT);
    private WaveLoadingView waveLoadingView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_water, container, false);
        if(getActivity()!=null) {
            ((MainActivity) getActivity()).updateToolbarTitle(getString(R.string.title_water));
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);
        initializeSharedPreferences();
        checkGlassPercentage();
        textViewWaterSummary = view.findViewById(R.id.textViewWaterSummary);
        builder = new StringBuilder(String.valueOf(twoDecimalPlaces.format(preferenceCurrent))).append(" / ").
                append((twoDecimalPlaces.format(preferenceTarget))).append(" L");
        textViewWaterSummary.setText(builder);
    }

    private void initializeViews(View view) {
        ImageView imageViewAdd1L = view.findViewById(R.id.buttonAdd1L);
        ImageView imageViewAdd500 = view.findViewById(R.id.buttonAdd500mL);
        ImageView imageViewAdd330 = view.findViewById(R.id.buttonAdd330mL);
        ImageView imageViewAdd250 = view.findViewById(R.id.buttonAdd250mL);
        ImageView imageViewAddManually = view.findViewById(R.id.buttonAddManually);
        ImageView imageViewRemove1L = view.findViewById(R.id.buttonRemove1L);
        ImageView imageViewRemove500 = view.findViewById(R.id.buttonRemove500mL);
        ImageView imageViewRemove330 = view.findViewById(R.id.buttonRemove330mL);
        ImageView imageViewRemove250 = view.findViewById(R.id.buttonRemove250mL);
        ImageView imageViewRemoveManually = view.findViewById(R.id.buttonRemoveManually);
        imageViewAdd1L.setOnClickListener(this);
        imageViewAdd250.setOnClickListener(this);
        imageViewAdd330.setOnClickListener(this);
        imageViewAdd500.setOnClickListener(this);
        imageViewAddManually.setOnClickListener(this);
        imageViewRemove1L.setOnClickListener(this);
        imageViewRemove250.setOnClickListener(this);
        imageViewRemove330.setOnClickListener(this);
        imageViewRemove500.setOnClickListener(this);
        imageViewRemoveManually.setOnClickListener(this);
        imageViewComplete = view.findViewById(R.id.imageViewWaterTick);
        editTextWaterAmount = view.findViewById(R.id.editTextWaterAmount);
        waveLoadingView = view.findViewById(R.id.waveLoadingView);
    }

    @Override
    public void onClick(View  v) {
        int id = v.getId();
        switch (id){
            case R.id.buttonAddManually:
                float additionalWaterAmount;
                try {
                    additionalWaterAmount = Float.parseFloat((editTextWaterAmount.getText().toString()));
                }catch (Exception x){
                    x.getMessage();
                    additionalWaterAmount = 0;
                    Toast.makeText(getContext(),getString(R.string.out_of_range),Toast.LENGTH_LONG).show();
                }
                preferenceCurrent += (additionalWaterAmount)/1000;
                checkGlassPercentage();
                putSharedPreferencesData();
                editTextWaterAmount.setText("");
                editTextWaterAmount.clearFocus();
                hideKeyboard();
                dynamicToastMessage();
                break;
            case R.id.buttonAdd1L:
                preferenceCurrent += ApplicationConstants.CONSTANT_AMOUNT;
                checkGlassPercentage();
                putSharedPreferencesData();
                dynamicToastMessage();
                break;
            case R.id.buttonAdd330mL:
                preferenceCurrent += ApplicationConstants.CONSTANT_AMOUNT_0_33;
                checkGlassPercentage();
                putSharedPreferencesData();
                dynamicToastMessage();
                break;
            case R.id.buttonAdd500mL:
                preferenceCurrent += ApplicationConstants.CONSTANT_AMOUNT_0_50;
                checkGlassPercentage();
                putSharedPreferencesData();
                dynamicToastMessage();
                break;
            case R.id.buttonAdd250mL:
                preferenceCurrent += ApplicationConstants.CONSTANT_AMOUNT_0_25;
                checkGlassPercentage();
                putSharedPreferencesData();
                dynamicToastMessage();
                break;
            case R.id.buttonRemove250mL:
                preferenceCurrent -= ApplicationConstants.CONSTANT_AMOUNT_0_25;
                checkGlassPercentage();
                putSharedPreferencesData();
                dynamicToastMessageRemove();
                break;
            case R.id.buttonRemove330mL:
                preferenceCurrent -= ApplicationConstants.CONSTANT_AMOUNT_0_33;
                checkGlassPercentage();
                putSharedPreferencesData();
                dynamicToastMessageRemove();
                break;
            case R.id.buttonRemove500mL:
                preferenceCurrent -= ApplicationConstants.CONSTANT_AMOUNT_0_50;
                checkGlassPercentage();
                putSharedPreferencesData();
                dynamicToastMessageRemove();
                break;
            case R.id.buttonRemove1L:
                preferenceCurrent -= ApplicationConstants.CONSTANT_AMOUNT;
                checkGlassPercentage();
                putSharedPreferencesData();
                dynamicToastMessageRemove();
                break;
            case R.id.buttonRemoveManually:
                float waterAmount;
                try {
                    waterAmount = Float.parseFloat((editTextWaterAmount.getText().toString()));
                }catch (Exception x){
                    x.getMessage();
                    waterAmount = 0;
                    Toast.makeText(getContext(),getString(R.string.out_of_range),Toast.LENGTH_LONG).show();
                }
                preferenceCurrent -= (waterAmount)/1000;
                checkGlassPercentage();
                putSharedPreferencesData();
                editTextWaterAmount.setText("");
                editTextWaterAmount.clearFocus();
                hideKeyboard();
                dynamicToastMessageRemove();
                break;
                default:
                    break;
        }
    }
    private void checkGlassPercentage(){
        int percentage = (int)(preferenceCurrent/preferenceTarget * 100);
        if (percentage >= 100){
            percentage = 100;
            waveLoadingView.setProgressValue(percentage);
            waveLoadingView.setBottomTitle("%"+(percentage));
            imageViewComplete.setVisibility(View.VISIBLE);
        } else if(percentage < 0){
            percentage = 0;
            waveLoadingView.setProgressValue(percentage);
            waveLoadingView.setBottomTitle("%"+(percentage));
        } else {
            waveLoadingView.setProgressValue(percentage);
            waveLoadingView.setBottomTitle("%"+(percentage));
        }
    }
    private void dynamicToastMessage(){
        if (getActivity() != null) {
            if (getContext() != null) {
                DynamicToast.makeSuccess(getContext(),getString(R.string.water_amount_added),Toast.LENGTH_LONG).show();
            }
        }
    }
    private void dynamicToastMessageRemove(){
        if (getActivity() != null){
            if (getContext() != null) {
                DynamicToast.makeWarning(getContext(),getString(R.string.amount_has_been_removed),Toast.LENGTH_LONG).show();
            }
        }
    }
    private void putSharedPreferencesData() {
        if(getActivity() != null){
            SharedPreferences.Editor sharedPreferences =
                    getActivity().getSharedPreferences(ApplicationConstants.CONSTANT_DAILY_GOAL,Context.MODE_PRIVATE).edit();
            sharedPreferences.putFloat(ApplicationConstants.CONSTANT_DAILY_GOAL, preferenceCurrent);
            sharedPreferences.apply();
            String currentValue = String.valueOf(twoDecimalPlaces.format(preferenceCurrent));
            if (preferenceCurrent<0){
                currentValue = String.valueOf(twoDecimalPlaces.format(0));
            }
            builder = new StringBuilder(currentValue).append(" / ").append(twoDecimalPlaces.format(preferenceTarget)).append(" L");
            textViewWaterSummary.setText(builder);
        }
    }
    private void initializeSharedPreferences() {
        if (getActivity() != null) {
            SharedPreferences preferences = getActivity().getSharedPreferences(ApplicationConstants.CONSTANT_REQUIRED_AMOUNT, MODE_PRIVATE);
            SharedPreferences preferences1 = getActivity().getSharedPreferences(ApplicationConstants.CONSTANT_DAILY_GOAL, MODE_PRIVATE);
            preferenceTarget = (preferences.getFloat(ApplicationConstants.CONSTANT_AMOUNT_WATER,0));
            twoDecimalPlaces = new DecimalFormat(ApplicationConstants.CONSTANT_TWO_DECIMAL_PLACES_FORMAT);
            preferenceCurrent = preferences1.getFloat(ApplicationConstants.CONSTANT_DAILY_GOAL,(float)0);
            if (preferenceCurrent<0){
                preferenceCurrent = 0;
            }
        }
    }
    private void hideKeyboard(){
        if (getActivity() != null) {
            if (getActivity().getCurrentFocus() != null) {
                InputMethodManager inputManager = (InputMethodManager)
                        getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
}