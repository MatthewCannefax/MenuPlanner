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
}

