package com.matthewcannefax.menuplanner.recipe;


import android.content.ContentValues;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.matthewcannefax.menuplanner.grocery.GroceryCategory;
import com.matthewcannefax.menuplanner.utils.database.GroceryListTable;
import com.matthewcannefax.menuplanner.utils.database.IngredientTable;

//this ingredient class inherits from the GroceryItem class
//this class is just for ingredients in recipes

@Entity(tableName = "ingredient")
public class Ingredient extends BaseItem {

    @PrimaryKey
    private int ingredientID;
    private int recipeId;

    //this is the only constructor used. there is no need for a default constructor as there will
    //never be an empty ingredient
    public Ingredient(String name, GroceryCategory category, Measurement measurement) {
        this.measurement = measurement;
        this.name = name;
        this.category = category;
    }

    public Ingredient() {
    }

    public int getIngredientID() {
        return ingredientID;
    }

    public void setIngredientID(int ingredientID) {
        this.ingredientID = ingredientID;
    }

    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    public String shareIngredientString() {
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
}
