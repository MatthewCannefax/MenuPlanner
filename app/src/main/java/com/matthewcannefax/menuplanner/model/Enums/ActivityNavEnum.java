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




    public String getName() {
        return mName;
    }

    public Class<? extends Activity> getActivity() {
        return mActivity;
    }

    public int getPosition() {
        return mPosition;
    }

    private String mName;
    private Class<? extends Activity> mActivity;
    private int mPosition;

    ActivityNavEnum(String name, Class<? extends Activity> activity, int position){
        mName = name;
        mActivity = activity;
        mPosition = position;
    }

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

    @Override
    public String toString() {
        return getName();
    }
}
