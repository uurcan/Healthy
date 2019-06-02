package com.example.healthy.Utils;

import android.content.Context;

import com.example.healthy.R;

public class BMICategory {
     public String getCategory (double result, Context context) {

        String category;

        if (result >= 15 && result <= 16)
            category = context.getString(R.string.bmi_2low_index);
        else if (result > 16 && result <= 18.5)
            category = context.getString(R.string.bmi_low_index);
        else if (result > 18.5 && result <= 25)
            category = context.getString(R.string.bmi_normal_index);
        else if (result > 25 && result <= 30)
            category = context.getString(R.string.bmi_high_index);
        else if (result > 30 && result <= 35)
            category = context.getString(R.string.bmi_2high_index);
        else if (result > 35 && result <= 40)
            category = context.getString(R.string.bmi_obese);
        else
            category = context.getString(R.string.out_of_range);

         return category;
    }
}