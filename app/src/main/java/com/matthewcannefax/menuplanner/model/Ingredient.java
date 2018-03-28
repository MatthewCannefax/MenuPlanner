package com.matthewcannefax.menuplanner.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.matthewcannefax.menuplanner.model.Enums.GroceryCategory;

//this ingredient class inherits from the GroceryItem class
//this class is just for ingredients in recipes

public class Ingredient implements Parcelable {

    //this is the only difference from the Grocery item class
    //I might want to change the type to a custom measurement class
    //to help with consolidation when generating the grocery list
    private Measurement measurement;
    private String name;
    private GroceryCategory category;
    private boolean itemChecked = false;

    //this is the only constructor used. there is no need for a default constructor as there will
    //never be an empty ingredient
    public Ingredient(String name, GroceryCategory category, Measurement measurement) {
        this.measurement = measurement;
        this.name = name;
        this.category = category;
    }

    public Ingredient(){}

    public boolean isItemChecked() {
        return itemChecked;
    }

    public void setItemChecked(boolean itemChecked) {
        this.itemChecked = itemChecked;
    }

    public boolean getItemChecked(){return this.itemChecked;}

    public Measurement getMeasurement() {
        return measurement;
    }

    public void setMeasurement(Measurement measurement) {
        this.measurement = measurement;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GroceryCategory getCategory() {
        return category;
    }

    public void setCategory(GroceryCategory category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return getName();
    }

    //region Parcelable Methods
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.measurement, flags);
        dest.writeString(this.name);
        dest.writeInt(this.category == null ? -1 : this.category.ordinal());
        dest.writeByte(this.itemChecked ? (byte) 1 : (byte) 0);
    }

    protected Ingredient(Parcel in) {
        this.measurement = in.readParcelable(Measurement.class.getClassLoader());
        this.name = in.readString();
        int tmpCategory = in.readInt();
        this.category = tmpCategory == -1 ? null : GroceryCategory.values()[tmpCategory];
        this.itemChecked = in.readByte() != 0;
    }

    static final Creator<Ingredient> CREATOR = new Creator<Ingredient>() {
        @Override
        public Ingredient createFromParcel(Parcel source) {
            return new Ingredient(source);
        }

        @Override
        public Ingredient[] newArray(int size) {
            return new Ingredient[size];
        }
    };
    //endregion
}
