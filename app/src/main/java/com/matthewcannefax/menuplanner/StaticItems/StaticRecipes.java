package com.matthewcannefax.menuplanner.StaticItems;


import android.content.Context;

import com.matthewcannefax.menuplanner.model.Recipe;
import com.matthewcannefax.menuplanner.utils.JSONHelper;

import java.util.List;

public class StaticRecipes {

    private static List<Recipe> getRecipeList() {
        return recipeList;
    }

    public static void setRecipeList(List<Recipe> recipeList) {
        StaticRecipes.recipeList = recipeList;
    }

    private static List<Recipe> recipeList;

    public static void loadRecipes(Context context, String filename){
        recipeList = JSONHelper.importFromJSON(context, filename);
    }
}
