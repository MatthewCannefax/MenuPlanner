package com.matthewcannefax.menuplanner.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.matthewcannefax.menuplanner.model.Enums.MeasurementType;

/**
 * Created by mcann on 12/5/2017.
 */

public class Measurement implements Parcelable {

    private double amount;
    private MeasurementType type;

    public Measurement(double amount, MeasurementType type){
        this.amount = amount;
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public MeasurementType getType() {
        return type;
    }

    public void setType(MeasurementType type) {
        this.type = type;
    }



    @Override
    public String toString() {
        return String.format("%s %s", this.amount, this.type);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.amount);
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
    }

    protected Measurement(Parcel in) {
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
}
