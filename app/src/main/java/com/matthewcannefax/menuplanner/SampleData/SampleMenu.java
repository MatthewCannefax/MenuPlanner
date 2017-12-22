package com.matthewcannefax.menuplanner.SampleData;


import com.matthewcannefax.menuplanner.model.Recipe;

import java.util.ArrayList;
import java.util.List;

public class SampleMenu {
    public static List<Recipe> sampleMenuList;

    static{
        sampleMenuList = new ArrayList<>();

        sampleMenuList.add(SampleRecipes.recipeList.get(0));
        sampleMenuList.add(SampleRecipes.recipeList.get(2));
        sampleMenuList.add(SampleRecipes.recipeList.get(2));
        sampleMenuList.add(SampleRecipes.recipeList.get(0));
        sampleMenuList.add(SampleRecipes.recipeList.get(1));
        sampleMenuList.add(SampleRecipes.recipeList.get(0));
        sampleMenuList.add(SampleRecipes.recipeList.get(2));
        sampleMenuList.add(SampleRecipes.recipeList.get(2));
        sampleMenuList.add(SampleRecipes.recipeList.get(0));
        sampleMenuList.add(SampleRecipes.recipeList.get(1));
    }

}
