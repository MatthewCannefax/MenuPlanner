package com.matthewcannefax.menuplanner.model;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import com.matthewcannefax.menuplanner.model.Enums.RecipeCategory;
import com.matthewcannefax.menuplanner.utils.database.RecipeTable;

import java.util.List;

//This object class is for creating Recipe objects

public class Recipe implements Parcelable {

    //props for the name, category, directions, image and list of ingredients
    private String name;
    private RecipeCategory category; //Categories will be dish related (Chicken, Beef, etc)
    private String directions;
    private String imagePath;
    private List<Ingredient> ingredientList;
    private int recipeID;
    private boolean itemChecked = false;

    //the default constructor
    public Recipe() {
    }

    public Recipe(String name, RecipeCategory category, String directions, String imgPath, List<Ingredient> ingredients){
        this.name = name;
        this.category = category;
        this.directions = directions;
        this.imagePath = imgPath;
        this.ingredientList = ingredients;
    }


    //region Getters and Setters
    public boolean isItemChecked() {
        return itemChecked;
    }

    public void setItemChecked(boolean itemChecked) {
        this.itemChecked = itemChecked;
    }

    public int getRecipeID() {
        return recipeID;
    }

    public void setRecipeID(int recipeID) {
        this.recipeID = recipeID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RecipeCategory getCategory() {
        return category;
    }

    public void setCategory(RecipeCategory category) {
        this.category = category;
    }

    public String getDirections() {
        return directions;
    }

    public void setDirections(String directions) {
        this.directions = directions;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public List<Ingredient> getIngredientList() {
        return ingredientList;
    }

    public void setIngredientList(List<Ingredient> ingredientList) {
        this.ingredientList = ingredientList;
    }

    public ContentValues toValues(){
        ContentValues values = new ContentValues(4);
//        values.put(RecipeTable.RECIPE_ID, recipeID);
        values.put(RecipeTable.NAME, name);
        values.put(RecipeTable.CATEGORY, category.toString());
        values.put(RecipeTable.IMG, imagePath);
        values.put(RecipeTable.DIRECTIONS, directions);
        return values;
    }
    //endregion

    //this currently does the same thing as getName()
    @Override
    public String toString() {
        return name;
    }

    //region Parcelable Methods
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeInt(this.category == null ? -1 : this.category.ordinal());
        dest.writeString(this.directions);
        dest.writeString(this.imagePath);
        dest.writeTypedList(this.ingredientList);
        dest.writeInt(this.recipeID);
        dest.writeByte(this.itemChecked ? (byte) 1 : (byte) 0);
    }

    private Recipe(Parcel in) {
        this.name = in.readString();
        int tmpCategory = in.readInt();
        this.category = tmpCategory == -1 ? null : RecipeCategory.values()[tmpCategory];
        this.directions = in.readString();
        this.imagePath = in.readString();
        this.ingredientList = in.createTypedArrayList(Ingredient.CREATOR);
        this.recipeID = in.readInt();
        this.itemChecked = in.readByte() != 0;
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel source) {
            return new Recipe(source);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };
    //endregion
}
