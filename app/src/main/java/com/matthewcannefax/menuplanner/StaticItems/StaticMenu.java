package com.matthewcannefax.menuplanner.StaticItems;


import android.content.Context;

import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.model.Recipe;
import com.matthewcannefax.menuplanner.utils.JSONHelper;

import java.util.ArrayList;
import java.util.List;

public class StaticMenu {

    private static List<Recipe> mMenuList;

    //provide the menu list with this method
    public static List<Recipe> getMenuList() {
        if(mMenuList != null) {
            return mMenuList;
        }else{
            mMenuList = new ArrayList<>();
            return mMenuList;
        }
    }

    //set the menu list
    public static void setMenuList(List<Recipe> mMenuList) {
        StaticMenu.mMenuList = mMenuList;
    }

    //load the menu list from a JSON file
    public static void loadMenu(Context context){
        try {
            String fileName = context.getString(R.string.json_menu_list);
            mMenuList = JSONHelper.importRecipesFromJSON(context, fileName);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    //save the menu list to a JSON file
    public static void saveMenu(Context context){
        try {
            String fileName = context.getString(R.string.json_menu_list);
            @SuppressWarnings("unused") boolean result = JSONHelper.exportRecipesToJSON(context, mMenuList, fileName);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    //edit a specific recipe within the menu list
    public static void editMenuRecipe(Recipe recipe){
        //check if the menu list has any items
        if(getMenuList() != null && getMenuList().size() > 0){
            //loop through the item of the menu list to find the corresponding recipe
            for (int i = 0; i < getMenuList().size() - 1; i++) {
                //find the recipe by the recipeID
                if(getMenuList().get(i).getRecipeID() == recipe.getRecipeID()){
                    //set the menu item with the edited recipe
                    getMenuList().set(i, recipe);
                }
            }
        }
    }

    //remove a recipe from the menu list
    public static void removeRecipeFromMenu(Recipe recipe, Context context){
        if (mMenuList != null && mMenuList.size() != 0) {
            //loop through the menu list
            for(int i = 0; i < mMenuList.size(); i++){
                //set mRecipe to the the current postion (i)
                Recipe mRecipe = mMenuList.get(i);
                //if the two recipes recipeIDs match
                if(recipe.getRecipeID() == mRecipe.getRecipeID()){
                    //remove the recipe at the current position
                    mMenuList.remove(i);
                    //decrement the position so the next recipe in the list will not be skipped
    //                i--;
                    break;
                }
            }
            //save the edited menu
            saveMenu(context);
        }
    }


}
