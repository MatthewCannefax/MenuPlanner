package com.matthewcannefax.menuplanner.recipe;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.text.DecimalFormat;

@Entity(tableName = "measurement")
public class Measurement {

    private final MeasurementType type;
    //fields
    @PrimaryKey(autoGenerate = true)
    private int measurementId;
    private int itemId;
    private double amount;

    //constructor
    public Measurement(double amount, MeasurementType type) {
        this.amount = amount;
        this.type = type;
    }

    public int getMeasurementId() {
        return measurementId;
    }

    public void setMeasurementId(int measurementId) {
        this.measurementId = measurementId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
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
