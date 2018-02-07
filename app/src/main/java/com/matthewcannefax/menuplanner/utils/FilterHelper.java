package com.matthewcannefax.menuplanner.utils;


import com.matthewcannefax.menuplanner.model.Enums.GroceryCategory;
import com.matthewcannefax.menuplanner.model.Enums.RecipeCategory;
import com.matthewcannefax.menuplanner.model.Ingredient;
import com.matthewcannefax.menuplanner.model.Recipe;

import java.util.ArrayList;
import java.util.List;

public class FilterHelper {
    public static List<RecipeCategory> getRecipeCategoriesUsed(List<Recipe> recipeList){
        List<RecipeCategory> recipeCategories = new ArrayList<>();

        recipeCategories.add(RecipeCategory.ALL);

        for(Recipe r: recipeList){
            if(!recipeCategories.contains(r.getCategory())){
                recipeCategories.add(r.getCategory());
            }
        }

        return  recipeCategories;
    }

    public static List<GroceryCategory> getGroceryCategoriesUsed(List<Ingredient> ingredientList){
        List<GroceryCategory> groceryCategories = new ArrayList<>();

        groceryCategories.add(GroceryCategory.ALL);

        for(Ingredient i: ingredientList){
            if(!groceryCategories.contains(i.getCategory())){
                groceryCategories.add(i.getCategory());
            }
        }

        return groceryCategories;
    }
}
