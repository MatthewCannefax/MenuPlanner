package com.matthewcannefax.menuplanner.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.matthewcannefax.menuplanner.model.Enums.MeasurementType;

import java.text.DecimalFormat;


public class Measurement implements Parcelable {

    //fields
    private double amount;
    private final MeasurementType type;

    //constructor
    public Measurement(double amount, MeasurementType type){
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

// --Commented out by Inspection START (4/5/2018 1:44 PM):
//    public void setType(MeasurementType type) {
//        this.type = type;
//    }
// --Commented out by Inspection STOP (4/5/2018 1:44 PM)

    //returning the string format of the Measurement (i.e. "1.0 lbs.")
    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("#.##");
        String formatted = df.format(this.amount);
        return String.format("%s %s", formatted, this.type);
    }

    //region Parcelable Methods
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.amount);
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
    }

    private Measurement(Parcel in) {
        this.amount = in.readDouble();
        int tmpType = in.readInt();
        this.type = tmpType == -1 ? null : MeasurementType.values()[tmpType];
    }

    public static final Creator<Measurement> CREATOR = new Creator<Measurement>() {
        @Override
        public Measurement createFromParcel(Parcel source) {
            return new Measurement(source);
        }

        @Override
        public Measurement[] newArray(int size) {
            return new Measurement[size];
        }
    };
    //endregion
}
