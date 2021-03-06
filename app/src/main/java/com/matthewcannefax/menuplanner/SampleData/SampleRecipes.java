package com.matthewcannefax.menuplanner.SampleData;


import com.matthewcannefax.menuplanner.grocery.GroceryCategory;
import com.matthewcannefax.menuplanner.recipe.MeasurementType;
import com.matthewcannefax.menuplanner.recipe.RecipeCategory;
import com.matthewcannefax.menuplanner.recipe.Ingredient;
import com.matthewcannefax.menuplanner.recipe.Measurement;
import com.matthewcannefax.menuplanner.recipe.Recipe;

import java.util.ArrayList;
import java.util.List;

public class SampleRecipes {
    public static List<Recipe> recipeList;

    static{
        recipeList = new ArrayList<>();

        recipeList.add(new Recipe("Chicken Noodle Soup", RecipeCategory.SOUP, "Boil chicken broth, add noodles and chicken ", "chickenNoodle.jpg",
                new ArrayList<Ingredient>(){{ //anonymous class or double brace initialization
                    add(new Ingredient("Noodles", GroceryCategory.PASTA_RICE, new Measurement(1, MeasurementType.BAG)));
                    add(new Ingredient("Chicken", GroceryCategory.MEAT, new Measurement(1, MeasurementType.POUND)));
                    add(new Ingredient("Chicken Bullion cubes", GroceryCategory.OTHER, new Measurement(8, MeasurementType.CUP)));
                }}));
        recipeList.add(new Recipe("Orange Picante Chicken", RecipeCategory.MAIN_DISHES, "Cook chicken in large skillet remove, combine picante sauce and marmalade boil until thick",
                "OrangeChicken.jpg",
                new ArrayList<Ingredient>(){{ //anonymous class or double brace initialization
                    add(new Ingredient("Picante Sauce", GroceryCategory.CANNED_GOODS, new Measurement(1, MeasurementType.CAN)));
                    add(new Ingredient("Chicken", GroceryCategory.MEAT, new Measurement(1, MeasurementType.POUND)));
                    add(new Ingredient("Sweet Orange Marmalade", GroceryCategory.BREAD, new Measurement(1, MeasurementType.CAN)));
                    add(new Ingredient("Rice", GroceryCategory.PASTA_RICE, new Measurement(1, MeasurementType.CUP)));
                    add(new Ingredient("Water", GroceryCategory.OTHER, new Measurement(1, MeasurementType.CUP)));
                }}));
        recipeList.add(new Recipe("Beef Beans and Rice", RecipeCategory.MAIN_DISHES, "Cook Ground Beef in large skillet, add beans and rice","beefBeansRice.jpg",
                new ArrayList<Ingredient>(){{ //anonymous class or double brace initialization
                    add(new Ingredient("Ground Beef", GroceryCategory.MEAT, new Measurement(1, MeasurementType.POUND)));
                    add(new Ingredient("Black Beans", GroceryCategory.CANNED_GOODS, new Measurement(1, MeasurementType.CAN)));
                    add(new Ingredient("Red Kidney Beans", GroceryCategory.CANNED_GOODS, new Measurement(1, MeasurementType.CAN)));
                    add(new Ingredient("Rice", GroceryCategory.PASTA_RICE, new Measurement(2, MeasurementType.CUP)));
                }}));
    }
}
