package com.matthewcannefax.menuplanner.StaticItems;


import android.content.Context;

import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.model.Enums.GroceryCategory;
import com.matthewcannefax.menuplanner.model.Enums.MeasurementType;
import com.matthewcannefax.menuplanner.model.Enums.RecipeCategory;
import com.matthewcannefax.menuplanner.model.Ingredient;
import com.matthewcannefax.menuplanner.model.Measurement;
import com.matthewcannefax.menuplanner.model.Recipe;
import com.matthewcannefax.menuplanner.utils.JSONHelper;

import java.util.ArrayList;
import java.util.List;

public class StaticRecipes {

    private static List<Recipe> recipeList;

    public static List<Recipe> getRecipeList() {
        if (recipeList != null) {
            return recipeList;
        } else {
            recipeList = new ArrayList<>();
            return recipeList;
        }
    }

    public static void setRecipeList(List<Recipe> recipeList) {
        StaticRecipes.recipeList = recipeList;
    }

    public static void loadRecipes(Context context) {
        try {
            String fileName = context.getString(R.string.recipe_list_to_json);
            setRecipeList(JSONHelper.importRecipesFromJSON(context, fileName));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
    public static void saveRecipes(Context context){
        try {
            String fileName = context.getString(R.string.recipe_list_to_json);
            boolean result = JSONHelper.exportRecipesToJSON(context, recipeList, fileName);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public static void addNewRecipe(Recipe newRecipe, Context context){
        newRecipe.setRecipeID(assignRecipeID());

        //if the static recipe list exists, add the new recipe to the list
        if(recipeList != null) {
            recipeList.add(newRecipe);
        }
        //if the static list doesn't exist, create a new list and then add the new recipe
        else{
            setRecipeList(new ArrayList<Recipe>());
            recipeList.add(newRecipe);
        }

        saveRecipes(context);
        String s = "";
    }

    //assign the an id to a new recipe
    private static int assignRecipeID(){
        int id;

        //check if the new recipe will be the first recipe in the list
        if(recipeList != null && recipeList.size() > 0 ){
            id = recipeList.get(recipeList.size() -1).getRecipeID() + 1;
        }else{
            id = 0;
        }

        //return the new id
        return id;
    }
}

