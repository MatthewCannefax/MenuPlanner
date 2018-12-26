package com.matthewcannefax.menuplanner.model.Enums;

import android.app.Activity;

import com.matthewcannefax.menuplanner.activity.AddRecipeActivity;
import com.matthewcannefax.menuplanner.activity.GroceryListActivity;
import com.matthewcannefax.menuplanner.activity.MenuListActivity;
import com.matthewcannefax.menuplanner.activity.RecipeListActivity;

public enum ActivityNavEnum {
    MENU_LIST_ACTIVITY("My Menu", MenuListActivity.class, 0),
    RECIPE_LIST_ACTIVITY("My Cookbook", RecipeListActivity.class, 1),
    ADD_RECIPE_ACTIVITY("Add New Recipe", AddRecipeActivity.class, 2),
    VIEW_GROCERY_LIST("View Grocery List", GroceryListActivity.class, 3),
    NEW_GROCERY_LIST("New Grocery List", GroceryListActivity.class, 4),
    IMPORT_COOKBOOK("Import Cookbook", RecipeListActivity.class, 5);

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

        for (ActivityNavEnum e:
             ActivityNavEnum.values()) {
            if (e.getPosition() == position){
                navEnum = e;
                break;
            }
        }

        return navEnum;
    }

    //return the name in toString
    @Override
    public String toString() {
        return getName();
    }
}
