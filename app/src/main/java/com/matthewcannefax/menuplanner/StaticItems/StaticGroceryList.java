package com.matthewcannefax.menuplanner.StaticItems;

//this static list is currently used to pass a grocery list from the menuListActivity to the
//GroceryListActivity.
//Probably should find a better way to move List data between activities, such as with Parcelable or
//with a DB adapter


import android.content.Context;

import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.model.Enums.GroceryCategory;
import com.matthewcannefax.menuplanner.model.GroceryItem;
import com.matthewcannefax.menuplanner.model.Ingredient;
import com.matthewcannefax.menuplanner.utils.JSONHelper;

import java.util.ArrayList;
import java.util.List;

public class StaticGroceryList {

    private static List<Ingredient> mIngredientList;

    //method to provide the grocery list
    public static List<Ingredient> getIngredientList() {

        if (mIngredientList != null) {
            return mIngredientList;
        } else {
            mIngredientList = new ArrayList<>();
            return mIngredientList;
        }
    }

    //method to set the grocery list
    public static void setIngredientList(List<Ingredient> ingredientList) {
        mIngredientList = ingredientList;
    }

    //load the groceries from JSON file
    public static void loadGroceries(Context context){
        try {
            String fileName = context.getString(R.string.json_grocery_list);
            mIngredientList = JSONHelper.importIngredientsFromJSON(context, fileName);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    //save the grocery list to a JSON file
    public static void saveGroceries(Context context){
        if(mIngredientList != null){
            try {
                String fileName = context.getString(R.string.json_grocery_list);
                @SuppressWarnings("unused") boolean result = JSONHelper.exportIngredientsToJSON(context, mIngredientList, fileName);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

}
