package com.example.healthy.Model;

public class Food {
    private String foodName;
    private String foodURL;
    private String foodDetails;
    private int foodCalories;

    Food(String foodName,String foodURL,String foodDetails,int foodCalories){
        this.foodCalories = foodCalories;
        this.foodDetails = foodDetails;
        this.foodURL = foodURL;
        this.foodName = foodName;
    }

    public String getFoodName() {
        return foodName;
    }

    public String getFoodURL() {
        return foodURL;
    }

    public String getFoodDetails() {
        return foodDetails;
    }

    public int getFoodCalories() {
        return foodCalories;
    }

}
