package com.matthewcannefax.menuplanner.utils.navigation;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;

import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.grocery.GroceryListActivity;
import com.matthewcannefax.menuplanner.recipe.Ingredient;
import com.matthewcannefax.menuplanner.recipe.Recipe;
import com.matthewcannefax.menuplanner.utils.database.DataSource;

import java.util.List;

public class NavHelper {
    private NavHelper(){
        throw new AssertionError();
    }
    public static void newGroceryList(final Activity activity, final Context context){


        DataSource mDataSource = new DataSource(context);

        List<Ingredient> groceries = mDataSource.getAllGroceries();
        List<Recipe> menu = mDataSource.getAllMenuRecipes();

        //if there is an existing grocery list
        if ((groceries != null && groceries.size() > 0) && (menu != null && menu.size() > 0)) {

            //ask the user if they truly wish to create a new grocery list
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(R.string.new_glist_question);
            builder.setMessage(R.string.are_you_sure_replace_glist);
            builder.setNegativeButton(R.string.cancel, null);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //if the user clicks ok button, create the new grocery list with this method
                    goToGroceryList(activity, context);
                }
            });

            builder.show();

        }
        //if there is no grocery list and the menu list is not null create a new grocery list
        else if (groceries != null) {
            goToGroceryList(activity, context);
        }
        //if it gets here there is no grocery list and there is no menu list
        //so prompt the user to add menu items
        else {
//            Toast.makeText(context, "Please add menu items", Toast.LENGTH_SHORT).show();
            Snackbar.make(activity.findViewById(android.R.id.content), R.string.please_add_menu_items, Snackbar.LENGTH_LONG).show();
        }
    }

    //create a new grocery list either by creating from menu items, or starting a new blank list
    private static void goToGroceryList(final Activity activity, Context context){
//        List<Recipe> menuList = StaticMenu.getMenuList();
        final DataSource mDataSource = new DataSource(context);

        List<Recipe> menuList = mDataSource.getAllMenuRecipes();
        List<Ingredient> groceries = mDataSource.getAllGroceries();


        //check that there are actually items in the menu list
        if (menuList != null && menuList.size() > 0) {
            //new intent to move to the GroceryListActivity
            Intent intent = new Intent(activity, GroceryListActivity.class);

            mDataSource.removeAllGroceries();

            mDataSource.menuIngredientsToGroceryDB();


            //start the GroceryListActivity
            activity.startActivity(intent);
        }
        //if there are no items in the menu list, Toast the user saying just that
        else {

            if (groceries == null || groceries.size() == 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle(R.string.continue_question);
                        builder.setMessage(R.string.wish_to_continue);
                        builder.setNegativeButton(R.string.no, null);
                        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(activity, GroceryListActivity.class);
                                activity.startActivity(intent);
                            }
                        });
                builder.show();
            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle(R.string.replace_grocery_title);
                        builder.setMessage(R.string.replace_grocery_message);
                        builder.setNegativeButton(R.string.no, null);
                        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                mDataSource.removeAllGroceries();

                                Intent intent = new Intent(activity, GroceryListActivity.class);
                                activity.startActivity(intent);
                            }
                        });
                builder.show();
            }

        }
    }


}

