package com.matthewcannefax.menuplanner.StaticItems;


import android.content.Context;

import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.model.Enums.RecipeCategory;
import com.matthewcannefax.menuplanner.model.Recipe;
import com.matthewcannefax.menuplanner.utils.JSONHelper;

import java.util.ArrayList;
import java.util.List;

public class StaticMenu {

    private static List<Recipe> mMenuList;

    public static List<Recipe> getMenuList() {
        if(mMenuList != null) {
            return mMenuList;
        }else{
            mMenuList = new ArrayList<>();
            return mMenuList;
        }
    }

    public static void setMenuList(List<Recipe> mMenuList) {
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

    public static void editMenuRecipe(Recipe recipe){
        if(getMenuList() != null && getMenuList().size() > 0){
            for (int i = 0; i < getMenuList().size() - 1; i++) {
                if(getMenuList().get(i).getRecipeID() == recipe.getRecipeID()){
                    getMenuList().set(i, recipe);
                }
            }
        }
    }

    public static void removeRecipeFromMenu(Recipe recipe, Context context){
        for(int i = 0; i < mMenuList.size(); i++){
            Recipe mRecipe = mMenuList.get(i);
            if(recipe.getRecipeID() == mRecipe.getRecipeID()){
                mMenuList.remove(i);
                i--;
                break;
            }
        }
        saveMenu(context);
    }


}
