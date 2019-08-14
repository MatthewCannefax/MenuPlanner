package com.matthewcannefax.menuplanner.grocery;

//this enum is for the different categories of grocery items
//this is hard coded so we can limit the number of categories to just a few

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public enum GroceryCategory {
    //these are the different grocery item categories
    //some of these categories are food items, some are not signified with a boolean
    FROZEN_FOODS("Frozen Foods", true),
    MEAT("Meat", true),
    PRODUCE("Produce", true),
    BEVERAGES("Beverages", true),
    BREAD("Bread", true),
    CANNED_GOODS("Canned Goods", true),
    DAIRY("Dairy", true),
    PASTA_RICE("Pasta/Rice", true),
    OTHER("Other", true),
//    ALL("All", false),
    SPICES("Spices", true),
    BAKING("Baking", true),
    CONDIMENTS("Condiments", true),
    CHIPS("Chips", true),
    PAPER_PRODUCTS("Paper Goods", false),
    CLEANERS("Cleaners", false),
    ELECTRONICS("Electronics", false),
    CLOTHES("Clothes", false),
    PETS("Pet supplies", false),
    LAWN("Lawn and Garden", false),
    PHARMACY("Pharmacy", false),
    SPORTING("Sporting Goods", false),
    TOOLS("Tools/Hardware", false),
    AUTO("Automotive", false),
    TOYS("Toys", false),
    HOME("Home Goods",false),
    BABY("Baby", false),
    SHOES("Shoes", false),
    CANDY("Candy", true),
    SNACKS("Snacks", true),
    CEREAL("Cereal", true);

    //fields
    final String mName;
    final boolean mIsIngredient;

    //constructor
    GroceryCategory(String name, boolean isIngredient) {
        mName = name;
        mIsIngredient = isIngredient;
    }

    //check if the enum is an ingredient
    private boolean isIngredient() {
        return mIsIngredient;
    }

    //get all the ingredients only
    public static List<GroceryCategory> getEnumIngredients(){
        List<GroceryCategory> groceryCategories = new ArrayList<>();
        for(GroceryCategory gCat: GroceryCategory.values()){
            if(gCat.isIngredient()){
                groceryCategories.add(gCat);
            }
        }

        Collections.sort(groceryCategories, new Comparator<GroceryCategory>() {
            @Override
            public int compare(GroceryCategory o1, GroceryCategory o2) {
                return o1.toString().compareTo(o2.toString());
            }
        });

        return groceryCategories;
    }

    public static int getCatPosition(GroceryCategory groceryCat){
        int i = 0;
        for (GroceryCategory gc: getEnumIngredients()) {
            if(gc == groceryCat){
                return i;
            }
            i++;
        }
        return 0;
    }

    public static List<GroceryCategory> getEnum(){
        List<GroceryCategory> groceryCategories = new ArrayList<>(Arrays.asList(GroceryCategory.values()));

        Collections.sort(groceryCategories, new Comparator<GroceryCategory>() {
            @Override
            public int compare(GroceryCategory groceryCategory, GroceryCategory t1) {
                return groceryCategory.toString().compareTo(t1.toString());
            }
        });

        return  groceryCategories;
    }

    //this overridden toString method will display the categories with the first character capitalized
    @Override
    public String toString() {
        return mName;
    }

    public static GroceryCategory stringToCategory(String strCat){
        for (GroceryCategory cat: GroceryCategory.values()){
            if (cat.mName.equals(strCat)){
                return cat;
            }
        }
        return OTHER;
    }
}
