package com.matthewcannefax.menuplanner.utils;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.StaticItems.StaticGroceryList;
import com.matthewcannefax.menuplanner.StaticItems.StaticRecipes;
import com.matthewcannefax.menuplanner.activity.MainActivity;
import com.matthewcannefax.menuplanner.model.Enums.ActivityNavEnum;

import java.util.ArrayList;
import java.util.List;

//this class sets up the navigation drawer for all activities
public class NavDrawer {
    //fields
    private static ListView mDrawerListView;

    //setup the nav drawer using the context and activity as arguments
    public static void setupNavDrawer(Activity activity, Context context){
        //find the navlist listview in the given activity
        mDrawerListView = activity.findViewById(R.id.navList);

        //use the add drawer items method to add the nav drawer enum items to the nav drawer
        addDrawerItems(activity, context);
    }

    //this method adds the activity enum items to the nav drawer and sets up the intents
    private static void addDrawerItems(final Activity currentActivity, final Context context){
        //listview adapter set to the activity enum
        ArrayAdapter mAdapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, ActivityNavEnum.values());
        mDrawerListView.setAdapter(mAdapter);

        //this onClickListener sets starts the activity chosen from the nav drawer list view
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //set the intent with the chosen activity
                Intent selectedActivity = new Intent(context, ActivityNavEnum.getActivityEnum(i).getActivity());

                //switch statement to ensure that the user doesn't navigate to an empty activity
                switch (ActivityNavEnum.getActivityEnum(i)){
                    case RECIPE_LIST_ACTIVITY:
                        //if the recipe list is null or empty the user will be notified that there is no recipes in the cookbook
                        if (StaticRecipes.getRecipeList() != null && StaticRecipes.getRecipeList().size() > 0) {
                            selectedActivity.putExtra("TITLE", "My Recipes");
                            startActivity(context, selectedActivity, currentActivity);
                        } else {
                            Toast.makeText(context, "No Recipes in the Cookbook", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case GROCERY_LIST_ACTIVITY:
                        //if there are no items in the grocery list, the user will be notified that there is no grocery list
                        if(StaticGroceryList.getIngredientList() != null && StaticGroceryList.getIngredientList().size() > 0){
                            startActivity(context, selectedActivity, currentActivity);
                        }else{
                            Toast.makeText(context, "No Grocery List", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    default:
                        //all other activities just start up
                        startActivity(context, selectedActivity, currentActivity);
                }

            }
        });
    }

    //this method starts the provided activity
    private static void startActivity(Context context, Intent intent, Activity currentActivity){
        //start the activity with the given intent
        context.startActivity(intent);

        //finish the current activity unless it is the main activity
        if (currentActivity.getClass() != MainActivity.class) {
            currentActivity.finish();
        }
    }
}
