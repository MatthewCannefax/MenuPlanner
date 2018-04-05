package com.matthewcannefax.menuplanner.model.Enums;

import android.app.Activity;

import com.matthewcannefax.menuplanner.activity.AddRecipeActivity;
import com.matthewcannefax.menuplanner.activity.GroceryListActivity;
import com.matthewcannefax.menuplanner.activity.MainActivity;
import com.matthewcannefax.menuplanner.activity.MenuListActivity;
import com.matthewcannefax.menuplanner.activity.RecipeListActivity;

public enum ActivityNavEnum {
    MAIN_ACTIVITY("Home", MainActivity.class, 0),
    MENU_LIST_ACTIVITY("Weekly Menu", MenuListActivity.class, 1),
    RECIPE_LIST_ACTIVITY("Cookbook", RecipeListActivity.class, 2),
    ADD_RECIPE_ACTIVITY("Add New Recipe", AddRecipeActivity.class, 3),
    GROCERY_LIST_ACTIVITY("Grocery List", GroceryListActivity.class, 4);

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
        ActivityNavEnum navEnum = MAIN_ACTIVITY;

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
