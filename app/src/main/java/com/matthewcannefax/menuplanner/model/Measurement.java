package com.matthewcannefax.menuplanner.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.matthewcannefax.menuplanner.model.Enums.MeasurementType;



public class Measurement implements Parcelable {

    //fields
    private double amount;
    private MeasurementType type;

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

    public void setType(MeasurementType type) {
        this.type = type;
    }

    //returning the string format of the Measurement (i.e. "1.0 lbs.")
    @Override
    public String toString() {
        return String.format("%s %s", this.amount, this.type);
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
