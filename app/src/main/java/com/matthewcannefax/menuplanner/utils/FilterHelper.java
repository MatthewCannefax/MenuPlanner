package com.matthewcannefax.menuplanner.utils;


import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import com.matthewcannefax.menuplanner.arrayAdapters.RecipeListItemAdapter;
import com.matthewcannefax.menuplanner.model.Enums.GroceryCategory;
import com.matthewcannefax.menuplanner.model.Enums.RecipeCategory;
import com.matthewcannefax.menuplanner.model.Ingredient;
import com.matthewcannefax.menuplanner.model.Recipe;

import java.util.ArrayList;
import java.util.List;

//this class is used to filter through the recipes by category
public class FilterHelper {

    //this method returns a list of categories that are used in the given recipe list
    public static List<RecipeCategory> getRecipeCategoriesUsed(List<Recipe> recipeList){

        //a new list of recipe categories to return from this method
        List<RecipeCategory> recipeCategories = new ArrayList<>();

        //add the ALL category to this list first
        recipeCategories.add(RecipeCategory.ALL);

        //loop through all the given recipes and add a the category if it doesn't already exist in the list
        for(Recipe r: recipeList){
            if(!recipeCategories.contains(r.getCategory())){
                recipeCategories.add(r.getCategory());
            }
        }

        //return the used categories
        return  recipeCategories;
    }
}
