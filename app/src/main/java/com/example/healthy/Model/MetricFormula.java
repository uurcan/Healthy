package com.example.healthy.Model;


import com.example.healthy.Utils.ApplicationConstants;

public class MetricFormula {

    private double inputKG;
    private double inputMeter;

    public MetricFormula(double inputKG, double inputMeter){
        this.inputKG = inputKG;
        this.inputMeter = inputMeter;
    }

    public double getInputKG() {
        return inputKG;
    }

    public double getInputMeter() {
        return inputMeter;
    }

    public double computeBMI(double kg,double meter){
        return (kg/(Math.pow(meter,2)/ ApplicationConstants.CONSTANT_VALUE_GET_SQUARED_METER));
    }
    public double requiredAmount(double kg,double meter){
        return (kg /(Math.pow(meter,2)/ApplicationConstants.CONSTANT_VALUE_GET_SQUARED_AMOUNT));
    }
}