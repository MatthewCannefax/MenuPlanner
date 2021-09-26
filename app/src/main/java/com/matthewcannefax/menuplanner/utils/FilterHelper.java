package com.matthewcannefax.menuplanner.utils;


import android.content.Context;

import com.matthewcannefax.menuplanner.recipe.RecipeCategory;
import com.matthewcannefax.menuplanner.utils.database.DataSource;

import java.util.List;

//this class is used to filter through the recipes by category
public class FilterHelper {

    private FilterHelper(){
        throw new AssertionError();
    }

    public static List<RecipeCategory> getMenuCategoriesUsed(Context context){
        DataSource mDataSource = new DataSource();
        mDataSource.init(context);

        return mDataSource.getUsedCategoriesFromDB(DataSource.getMenuCategoryString());

    }

    //this method returns a list of categories that are used in the given recipe list
    public static List<RecipeCategory> getRecipeCategoriesUsed(Context context){

        DataSource mDataSource = new DataSource();
        mDataSource.init(context);

        return mDataSource.getUsedCategoriesFromDB(DataSource.getRecipeCategoryString());


//        //a new list of recipe categories to return from this method
//        List<RecipeCategory> recipeCategories = new ArrayList<>();
//
//        //add the ALL category to this list first
//        recipeCategories.add(RecipeCategory.ALL);
//
//        //loop through all the given recipes and add a the category if it doesn't already exist in the list
//        for(Recipe r: recipeList){
//            if(!recipeCategories.contains(r.getCategory())){
//                recipeCategories.add(r.getCategory());
//            }
//        }
//
//        Collections.sort(recipeCategories, new Comparator<RecipeCategory>() {
//            @Override
//            public int compare(RecipeCategory recipeCategory, RecipeCategory t1) {
//                return recipeCategory.toString().compareTo(t1.toString());
//            }
//        });
//
//        //return the used categories
//        return  recipeCategories;
    }
}
