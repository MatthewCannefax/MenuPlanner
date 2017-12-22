package com.matthewcannefax.menuplanner.StaticItems;

//this static list is currently used to pass a grocery list from the menuListActivity to the
//GroceryListActivity.
//Probably should find a better way to move List data between activities, such as with Parcelable or
//with a DB adapter


import com.matthewcannefax.menuplanner.model.GroceryItem;
import com.matthewcannefax.menuplanner.model.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class StaticGroceryList {
    public static List<Ingredient> items;

    static {
        items = new ArrayList<>();
    }
}
