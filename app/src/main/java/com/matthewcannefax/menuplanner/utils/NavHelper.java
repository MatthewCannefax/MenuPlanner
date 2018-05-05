package com.matthewcannefax.menuplanner.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.matthewcannefax.menuplanner.StaticItems.StaticGroceryList;
import com.matthewcannefax.menuplanner.StaticItems.StaticMenu;
import com.matthewcannefax.menuplanner.activity.GroceryListActivity;
import com.matthewcannefax.menuplanner.activity.MenuListActivity;
import com.matthewcannefax.menuplanner.model.GroceryBuilder;
import com.matthewcannefax.menuplanner.model.Ingredient;
import com.matthewcannefax.menuplanner.model.Recipe;

import java.util.List;

public class NavHelper {


    public static void newGroceryList(final Activity activity, final Context context){
        if (StaticGroceryList.getIngredientList() != null && StaticGroceryList.getIngredientList().size() > 0) {

            //ask the user if they truly wish to create a new grocery list
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Generate New Grocery List?");
            builder.setMessage("Are you sure you want to replace your existing grocery list?");
            builder.setNegativeButton("Cancel", null);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //if the user clicks ok button, create the new grocery list with this method
                    goToGroceryList(activity, context);
                }
            });

            builder.show();

        }
        //if there is no grocery list and the menu list is not null create a new grocery list
        else if (StaticMenu.getMenuList() != null) {
            goToGroceryList(activity, context);
        }
        //if it gets here there is no grocery list and there is no menu list
        //so prompt the user to add menu items
        else {
            Toast.makeText(context, "Please add menu items", Toast.LENGTH_SHORT).show();
        }
    }

    private static void goToGroceryList(Activity activity, Context context){
        List<Recipe> menuList = StaticMenu.getMenuList();
        //check that there are actually items in the menu list
        if (menuList != null && menuList.size() > 0) {
            //new intent to move to the GroceryListActivity
            Intent intent = new Intent(activity, GroceryListActivity.class);

            //adding to the staticList. this needs to change to a different way of moving data around
            //as it stands now, If I generate a list then move back and generate the list again
            //it will double the list
            GroceryBuilder groceryBuilder = new GroceryBuilder(menuList);
            setStaticGroceryList(context, groceryBuilder.consolidateGroceries());

            //start the GroceryListActivity
            activity.startActivity(intent);
        }
        //if there are items in the menu list, Toast the user saying just that
        else {
            Toast.makeText(context, "Please add menu items", Toast.LENGTH_SHORT).show();
        }
    }

    private static void setStaticGroceryList(Context context, List<Ingredient> groceryItems){
        StaticGroceryList.setIngredientList(groceryItems);
        StaticGroceryList.saveGroceries(context);
    }
}

