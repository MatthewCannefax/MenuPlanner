package com.matthewcannefax.menuplanner.StaticItems;

//this static list is currently used to pass a grocery list from the menuListActivity to the
//GroceryListActivity.
//Probably should find a better way to move List data between activities, such as with Parcelable or
//with a DB adapter


import android.content.Context;

import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.model.GroceryItem;
import com.matthewcannefax.menuplanner.model.Ingredient;
import com.matthewcannefax.menuplanner.utils.JSONHelper;

import java.util.ArrayList;
import java.util.List;

public class StaticGroceryList {
    public static List<Ingredient> items;

    static {
        items = new ArrayList<>();
    }

    public static void loadGroceries(Context context){
        try {
            String fileName = context.getString(R.string.json_grocery_list);
            items = JSONHelper.importIngredientsFromJSON(context, fileName);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public static void saveGroceries(Context context){
        if(items != null){
            try {
                String fileName = context.getString(R.string.json_grocery_list);
                boolean result = JSONHelper.exportIngredientsToJSON(context,items, fileName);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }
}
