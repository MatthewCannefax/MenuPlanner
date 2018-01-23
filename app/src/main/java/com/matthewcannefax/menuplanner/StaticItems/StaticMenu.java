package com.matthewcannefax.menuplanner.StaticItems;


import android.content.Context;

import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.model.Recipe;
import com.matthewcannefax.menuplanner.utils.JSONHelper;

import java.util.ArrayList;
import java.util.List;

public class StaticMenu {

    private static List<Recipe> mMenuList;

    public static List<Recipe> getmMenuList() {
        return mMenuList;
    }

    public static void setmMenuList(List<Recipe> mMenuList) {
        StaticMenu.mMenuList = mMenuList;
    }

    public static void loadMenu(Context context){
        try {
            String fileName = context.getString(R.string.json_menu_list);
            mMenuList = JSONHelper.importRecipesFromJSON(context, fileName);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public static void saveMenu(Context context){
        try {
            String fileName = context.getString(R.string.json_menu_list);
            boolean result = JSONHelper.exportRecipesToJSON(context, mMenuList, fileName);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

}
