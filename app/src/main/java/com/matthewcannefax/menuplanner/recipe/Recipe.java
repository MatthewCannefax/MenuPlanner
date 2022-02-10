package com.matthewcannefax.menuplanner.recipe;

import android.content.ContentValues;

import com.matthewcannefax.menuplanner.utils.database.RecipeTable;

import java.util.List;
import java.util.Objects;

//This object class is for creating Recipe objects

public class Recipe {

    //props for the name, category, directions, image and list of ingredients
    private String name;
    private RecipeCategory category; //Categories will be dish related (Chicken, Beef, etc)
    private String directions = "";
    private String imagePath;
    private List<Ingredient> ingredientList;
    private int recipeID;
    private boolean itemChecked = false;

    //the default constructor
    public Recipe() {
    }

    public Recipe(String name, RecipeCategory category, String directions, String imgPath, List<Ingredient> ingredients) {
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

    public ContentValues toValues() {
        ContentValues values = new ContentValues(5);
        values.put(RecipeTable.RECIPE_ID, recipeID);
        values.put(RecipeTable.NAME, name);
        values.put(RecipeTable.CATEGORY, category.toString());
        values.put(RecipeTable.IMG, imagePath);
        values.put(RecipeTable.DIRECTIONS, directions);
        return values;
    }

    public ContentValues toValuesCreate() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Recipe recipe = (Recipe) o;
        return recipeID == recipe.recipeID && itemChecked == recipe.itemChecked && Objects.equals(name, recipe.name) && category == recipe.category && Objects.equals(directions, recipe.directions) && Objects.equals(imagePath, recipe.imagePath) && Objects.equals(ingredientList, recipe.ingredientList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, category, directions, imagePath, ingredientList, recipeID, itemChecked);
    }
}
