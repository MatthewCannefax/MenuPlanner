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
//    public static List<Recipe> recipeList;
//
//    static{
//        recipeList = new ArrayList<>();
//
//        recipeList.add(new Recipe(0 , "Chicken Noodle Soup", RecipeCategory.SOUP, "Boil chicken broth, add noodles and chicken ", "chickenNoodle.jpg",
//                new ArrayList<Ingredient>(){{ //anonymous class or double brace initialization
//                        add(new Ingredient("Noodles", GroceryCategory.PASTA_RICE, new Measurement(1, MeasurementType.BAG)));
//                        add(new Ingredient("Chicken", GroceryCategory.MEAT, new Measurement(1, MeasurementType.POUND)));
//                        add(new Ingredient("Chicken Bullion cubes", GroceryCategory.OTHER, new Measurement(8, MeasurementType.CUP)));
//                }}));
//        recipeList.add(new Recipe(1, "Orange Picante Chicken", RecipeCategory.MAIN_DISHES, "Cook chicken in large skillet remove, combine picante sauce and marmalade boil until thick",
//                "OrangeChicken.jpg",
//                new ArrayList<Ingredient>(){{ //anonymous class or double brace initialization
//                    add(new Ingredient("Picante Sauce", GroceryCategory.CANNED_GOODS, new Measurement(1, MeasurementType.CAN)));
//                    add(new Ingredient("Chicken", GroceryCategory.MEAT, new Measurement(1, MeasurementType.POUND)));
//                    add(new Ingredient("Sweet Orange Marmalade", GroceryCategory.BREAD, new Measurement(1, MeasurementType.CAN)));
//                    add(new Ingredient("Rice", GroceryCategory.PASTA_RICE, new Measurement(2, MeasurementType.CUP)));
//                    add(new Ingredient("Water", GroceryCategory.OTHER, new Measurement(1, MeasurementType.CUP)));
//                }}));
//        recipeList.add(new Recipe(2, "Beef Beans and Rice", RecipeCategory.MAIN_DISHES, "Cook Ground Beef in large skillet, add beans and rice", "beefBeansRice.jpg",
//                new ArrayList<Ingredient>(){{ //anonymous class or double brace initialization
//                    add(new Ingredient("Ground Beef", GroceryCategory.MEAT, new Measurement(1, MeasurementType.POUND)));
//                    add(new Ingredient("Black Beans", GroceryCategory.CANNED_GOODS, new Measurement(1, MeasurementType.CAN)));
//                    add(new Ingredient("Red Kidney Beans", GroceryCategory.CANNED_GOODS, new Measurement(1, MeasurementType.CAN)));
//                    add(new Ingredient("Rice", GroceryCategory.PASTA_RICE, new Measurement(2, MeasurementType.CUP)));
//                }}));
//    }

    private static List<Recipe> recipeList;

    public static List<Recipe> getRecipeList() {
        return recipeList;
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

