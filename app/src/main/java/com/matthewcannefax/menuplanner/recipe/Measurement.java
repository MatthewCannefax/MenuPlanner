package com.matthewcannefax.menuplanner.recipe;

import java.text.DecimalFormat;


public class Measurement {

    private final MeasurementType type;
    //fields
    private double amount;

    //constructor
    public Measurement(double amount, MeasurementType type) {
        this.amount = amount;
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    void setAmount(double amount) {
        this.amount = amount;
    }

    public MeasurementType getType() {
        return type;
    }

    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("#.##");
        String formatted = df.format(this.amount);
        return String.format("%s %s", formatted, this.type);
    }
}
