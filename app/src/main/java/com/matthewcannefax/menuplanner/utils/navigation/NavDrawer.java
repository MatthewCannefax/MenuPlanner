package com.matthewcannefax.menuplanner.utils.navigation;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.ActionBar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.snackbar.Snackbar;
import com.matthewcannefax.menuplanner.MainActivity;
import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.recipe.Ingredient;
import com.matthewcannefax.menuplanner.recipe.Recipe;
import com.matthewcannefax.menuplanner.utils.ActivityNavEnum;
import com.matthewcannefax.menuplanner.utils.ShareHelper;
import com.matthewcannefax.menuplanner.utils.database.DataSource;
import com.matthewcannefax.menuplanner.utils.database.RecipeTable;

import java.util.List;

//this class sets up the navigation drawer for all activities
public class NavDrawer {
    private NavDrawer() {
        throw new AssertionError();
    }

    //fields
//    private static ListView mDrawerListView;

    //setup the nav drawer using the context and activity as arguments
    public static void setupNavDrawer(Activity activity, Context context, ListView mDrawerListView) {

        //use the add drawer items method to add the nav drawer enum items to the nav drawer
        addDrawerItems(activity, context, mDrawerListView);
    }

    //this method adds the activity enum items to the nav drawer and sets up the intents
    private static void addDrawerItems(final Activity currentActivity, final Context context, ListView mDrawerListView) {
        final DataSource mDataSource = new DataSource(context);

        //listview adapter set to the activity enum
        @SuppressWarnings("Convert2Diamond") ArrayAdapter<ActivityNavEnum> mAdapter = new ArrayAdapter<ActivityNavEnum>(context, android.R.layout.simple_list_item_1, ActivityNavEnum.values());
        mDrawerListView.setAdapter(mAdapter);

        //this onClickListener sets starts the activity chosen from the nav drawer list view
        mDrawerListView.setOnItemClickListener((adapterView, view, i, l) -> {
            //set the intent with the chosen activity
            Intent selectedActivity = new Intent(context, ActivityNavEnum.getActivityEnum(i).getActivity());

            //switch statement to ensure that the user doesn't navigate to an empty activity
            switch (ActivityNavEnum.getActivityEnum(i)) {
                case MENU_LIST_ACTIVITY:
                    selectedActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(context, selectedActivity, currentActivity);
                    break;
                case RECIPE_LIST_ACTIVITY:
                    //if the recipe list is null or empty the user will be notified that there is no recipes in the cookbook
//                        if (StaticRecipes.getRecipeList() != null && StaticRecipes.getRecipeList().size() > 0) {
                    if (RecipeTable.isNotEmpty(context)) {
                        selectedActivity.putExtra("TITLE", currentActivity.getString(R.string.my_recipes));
                        startActivity(context, selectedActivity, currentActivity);
                    } else {
//                            Toast.makeText(context, "No Recipes in the Cookbook", Toast.LENGTH_SHORT).show();
                        Snackbar.make(currentActivity.findViewById(android.R.id.content), R.string.no_recipes_found, Snackbar.LENGTH_LONG).show();
                    }
                    break;
                case VIEW_GROCERY_LIST:
                    List<Ingredient> groceries = mDataSource.getAllGroceries();
                    //if there are no items in the grocery list, the user will be notified that there is no grocery list
                    if (groceries != null && groceries.size() > 0) {
                        startActivity(context, selectedActivity, currentActivity);
                    } else {
//                            Toast.makeText(context, "No Grocery List", Toast.LENGTH_SHORT).show();
                        Snackbar.make(currentActivity.findViewById(android.R.id.content), R.string.no_grocery_list_found, Snackbar.LENGTH_LONG).show();
                    }
                    break;
//                case NEW_GROCERY_LIST:
//                    NavHelper.newGroceryList(currentActivity, context);
//                    break;
                case IMPORT_COOKBOOK:
                    ShareHelper.importCookbook(currentActivity);
                    break;
                case SHARE_COOKBOOK:
                    List<Recipe> recipes = mDataSource.getAllRecipes();
                    if (recipes != null && recipes.size() != 0) {
                        ShareHelper.sendAllRecipes(context);
//                            ShareHelper.sendAllRecipesDB(context);
                    } else {
//                            Toast.makeText(context, "There are no recipes in your cookbook", Toast.LENGTH_SHORT).show();
                        Snackbar.make(currentActivity.findViewById(android.R.id.content), R.string.no_recipes_found, Snackbar.LENGTH_LONG).show();
                    }
                    break;
                default:
                    //all other activities just start up
                    startActivity(context, selectedActivity, currentActivity);
            }

        });
    }

    //this method starts the provided activity
    private static void startActivity(Context context, Intent intent, Activity currentActivity) {
        //start the activity with the given intent

        context.startActivity(intent);

        //finish the current activity unless it is the main activity
        if (currentActivity.getClass() != MainActivity.class) {
            currentActivity.finish();
        }
    }

    public static void setupNavDrawerMenuButton(ActionBar actionBar) {
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
    }

    public static void navDrawerOptionsItem(DrawerLayout mDrawerLayout) {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
    }
}
