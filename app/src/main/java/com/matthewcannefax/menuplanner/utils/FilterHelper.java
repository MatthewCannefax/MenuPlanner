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

        return mDataSource.getMenuCategoriesFromDB();

    }
}
