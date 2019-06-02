package com.example.healthy.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.healthy.R;
import com.example.healthy.UI.Activity.MainActivity;
import com.example.healthy.Utils.ApplicationConstants;
import com.squareup.picasso.Picasso;

import java.util.Objects;


public class FoodDetailFragment extends BaseFragment implements View.OnClickListener{
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.food_detail_page,container,false);
        if (getActivity() != null){
            ((MainActivity)getActivity()).updateToolbarTitle(getString(R.string.food_fragment));
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeViews(view);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.buttonAddCalory:
                addCalories();
                break;
            case R.id.buttonFoodPreviousPage:
                switchPreviousPage();
                break;
        }
    }

    private void initializeViews(View view){
        TextView textViewFoodName = view.findViewById(R.id.textViewDetailedFoodName);
        TextView textViewFoodDetail = view.findViewById(R.id.textViewDetailedFoodDescription);
        TextView textViewFoodCalory = view.findViewById(R.id.textViewDetailedFoodCalory);
        ImageView imageViewFoodURL = view.findViewById(R.id.imageViewFoodURLDetail);
        if (getActivity() != null) {
            String foodName,foodDetail,foodURL;
            int foodCalory;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                foodName = Objects.requireNonNull(getActivity().getIntent().getExtras()).getString("foodName");
                foodDetail = Objects.requireNonNull(getActivity().getIntent().getExtras().getString("foodDetail"));
                foodCalory = getActivity().getIntent().getExtras().getInt("foodCalory");
                foodURL = Objects.requireNonNull(getActivity().getIntent().getExtras().getString("foodURL"));
                textViewFoodName.setText(foodName);
                StringBuilder builder = new StringBuilder(getString(R.string.calories)).append(" ").append((foodCalory));
                textViewFoodCalory.setText(builder);
                textViewFoodDetail.setText(foodDetail);
                Picasso.with(view.getContext())
                        .load(foodURL)
                        .into(imageViewFoodURL);
            }
        }
        Button buttonPrevious = view.findViewById(R.id.buttonFoodPreviousPage);
        buttonPrevious.setOnClickListener(this);
        Button buttonAdd = view.findViewById(R.id.buttonAddCalory);
        buttonAdd.setOnClickListener(this);
    }
    private void switchPreviousPage(){
        if(getActivity() != null) {
            Fragment fragment = new FoodFragment();
            FragmentManager fm = getActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.replace(R.id.content_frame, fragment);
            transaction.commit();
        }
    }
    private void addCalories(){
        if (getActivity() != null) {
            int foodCalory = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                foodCalory = Objects.requireNonNull(getActivity().getIntent().getExtras()).
                        getInt(ApplicationConstants.CONSTANT_FOOD_CALORY, 0);
            }
            SharedPreferences.Editor editor = getActivity().getSharedPreferences(
                        ApplicationConstants.CONSTANT_FOOD_CALORY, Context.MODE_PRIVATE).edit();
                editor.putInt(ApplicationConstants.CONSTANT_FOOD_CALORY,foodCalory);
                editor.apply();
                SharedPreferences a =  getActivity().getSharedPreferences(ApplicationConstants.CONSTANT_FOOD_CALORY,Context.MODE_PRIVATE);
                int aq = a.getInt(ApplicationConstants.CONSTANT_FOOD_CALORY,0);
                int bq = a.getInt(ApplicationConstants.CONSTANT_TOTAL_CALORY,0);
                bq += aq;editor.putInt(ApplicationConstants.CONSTANT_TOTAL_CALORY,bq);
                editor.apply();
                switchPreviousPage();
                Toast.makeText(getContext(),getString(R.string.food_added_calory),Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
