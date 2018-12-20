package com.matthewcannefax.menuplanner.model;


import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import com.matthewcannefax.menuplanner.model.Enums.GroceryCategory;
import com.matthewcannefax.menuplanner.utils.database.GroceryListTable;
import com.matthewcannefax.menuplanner.utils.database.IngredientTable;

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
    private int ingredientID;

    //this is the only constructor used. there is no need for a default constructor as there will
    //never be an empty ingredient
    public Ingredient(String name, GroceryCategory category, Measurement measurement) {
        this.measurement = measurement;
        this.name = name;
        this.category = category;
    }

    public Ingredient(){};

    // --Commented out by Inspection (4/5/2018 1:42 PM):public Ingredient(){}

// --Commented out by Inspection START (4/5/2018 1:43 PM):
//    public boolean isItemChecked() {
//        return itemChecked;
//    }
// --Commented out by Inspection STOP (4/5/2018 1:43 PM)


    public int getIngredientID() {
        return ingredientID;
    }

    public void setIngredientID(int ingredientID) {
        this.ingredientID = ingredientID;
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

    public String shareIngredientString(){
        return String.format("%s %s", getMeasurement(), getName());
    }

    @Override
    public String toString() {
        return getName();
    }

    public ContentValues toValues(int recipeID) {
        ContentValues values = new ContentValues(6);
        values.put(IngredientTable.COLUMN_ID, ingredientID);
        values.put(IngredientTable.COLUMN_RECIPE_ID, recipeID);
        values.put(IngredientTable.COLUMN_NAME, name);
        values.put(IngredientTable.COLUMN_CATEGORY, category.toString());
        values.put(IngredientTable.COLUMN_MEASUREMENT_AMOUNT, measurement.getAmount());
        values.put(IngredientTable.COLUMN_MEASUREMENT_TYPE, measurement.getType().toString());
        return values;
    }
    //endregion

    public ContentValues toValuesCreate(int recipeID) {
        ContentValues values = new ContentValues(5);
        values.put(IngredientTable.COLUMN_RECIPE_ID, recipeID);
        values.put(IngredientTable.COLUMN_NAME, name);
        values.put(IngredientTable.COLUMN_CATEGORY, category.toString());
        values.put(IngredientTable.COLUMN_MEASUREMENT_AMOUNT, measurement.getAmount());
        values.put(IngredientTable.COLUMN_MEASUREMENT_TYPE, measurement.getType().toString());
        return values;
    }

    public ContentValues toValuesGroceryList() {
        ContentValues values = new ContentValues(5);
        values.put(GroceryListTable.COLUMN_NAME, name);
        values.put(GroceryListTable.COLUMN_CATEGORY, category.toString());
        values.put(GroceryListTable.COLUMN_MEASUREMENT_AMOUNT, measurement.getAmount());
        values.put(GroceryListTable.COLUMN_MEASUREMENT_TYPE, measurement.getType().toString());
        values.put(GroceryListTable.COLUMN_IS_CHECKED, itemChecked);

        return values;
    }

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
        dest.writeInt(this.ingredientID);
    }

    protected Ingredient(Parcel in) {
        this.measurement = in.readParcelable(Measurement.class.getClassLoader());
        this.name = in.readString();
        int tmpCategory = in.readInt();
        this.category = tmpCategory == -1 ? null : GroceryCategory.values()[tmpCategory];
        this.itemChecked = in.readByte() != 0;
        this.ingredientID = in.readInt();
    }

    public static final Creator<Ingredient> CREATOR = new Creator<Ingredient>() {
        @Override
        public Ingredient createFromParcel(Parcel source) {
            return new Ingredient(source);
        }

        @Override
        public Ingredient[] newArray(int size) {
            return new Ingredient[size];
        }
    };
}
