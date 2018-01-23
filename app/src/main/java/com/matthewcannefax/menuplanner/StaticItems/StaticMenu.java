package com.matthewcannefax.menuplanner.StaticItems;


import android.content.Context;

import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.model.Recipe;
import com.matthewcannefax.menuplanner.utils.JSONHelper;

import java.util.ArrayList;
import java.util.List;

public class StaticMenu {
    public static List<Recipe> sampleMenuList;

    static{
        sampleMenuList = new ArrayList<>();
    }

    public static void loadMenu(Context context){
        try {
            String fileName = context.getString(R.string.json_menu_list);
            sampleMenuList = JSONHelper.importRecipesFromJSON(context, fileName);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

}
