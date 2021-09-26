package com.matthewcannefax.menuplanner.utils;

import android.app.Activity;

import com.matthewcannefax.menuplanner.MainActivity;
import com.matthewcannefax.menuplanner.addEdit.AddRecipeActivity;
import com.matthewcannefax.menuplanner.grocery.GroceryListActivity;
import com.matthewcannefax.menuplanner.recipe.recipeList.CookbookFragment;

public enum ActivityNavEnum {
    MENU_LIST_ACTIVITY("My Menu", MainActivity.class, 0),
    RECIPE_LIST_ACTIVITY("My Cookbook", MainActivity.class, 1),
    ADD_RECIPE_ACTIVITY("Add New Recipe", AddRecipeActivity.class, 2),
    VIEW_GROCERY_LIST("View Grocery List", GroceryListActivity.class, 3),
    NEW_GROCERY_LIST("New Grocery List", GroceryListActivity.class, 4),
    IMPORT_COOKBOOK("Import Recipes", MainActivity.class, 5),
    SHARE_COOKBOOK("Share Cookbook", MainActivity.class, 6);

    //get the name of the activity
    private String getName() {
        return mName;
    }

    //get the Activity
    public Class<? extends Activity> getActivity() {
        return mActivity;
    }

    //get the position in the order of Activities
    private int getPosition() {
        return mPosition;
    }

    //Fields
    private final String mName;
    private final Class<? extends Activity> mActivity;
    private final int mPosition;

    //constructor
    ActivityNavEnum(String name, Class<? extends Activity> activity, int position){
        mName = name;
        mActivity = activity;
        mPosition = position;
    }

    //static method to get the enum
    public static ActivityNavEnum getActivityEnum(int position){
        ActivityNavEnum navEnum = MENU_LIST_ACTIVITY;

        switch (position){
            case 0:
                navEnum = MENU_LIST_ACTIVITY;
                break;
            case 1:
                navEnum = RECIPE_LIST_ACTIVITY;
                break;
            case 2:
                navEnum = ADD_RECIPE_ACTIVITY;
                break;
            case 3:
                navEnum = VIEW_GROCERY_LIST;
                break;
            case 4:
                navEnum = NEW_GROCERY_LIST;
                break;
            case 5:
                navEnum = IMPORT_COOKBOOK;
                break;
            case 6:
                navEnum = SHARE_COOKBOOK;
                break;
            default:
                navEnum = MENU_LIST_ACTIVITY;
                break;

        }

//        for (ActivityNavEnum e:
//             ActivityNavEnum.values()) {
//            if (e.getPosition() == position){
//                navEnum = e;
//                break;
//            }
//        }

        return navEnum;
    }

    //return the name in toString
    @Override
    public String toString() {
        return getName();
    }
}
