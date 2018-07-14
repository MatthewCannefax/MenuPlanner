package com.matthewcannefax.menuplanner.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.StaticItems.StaticMenu;
import com.matthewcannefax.menuplanner.StaticItems.StaticGroceryList;
import com.matthewcannefax.menuplanner.StaticItems.StaticRecipes;
import com.matthewcannefax.menuplanner.arrayAdapters.RecipeMenuItemAdapter;
import com.matthewcannefax.menuplanner.model.Enums.RecipeCategory;
import com.matthewcannefax.menuplanner.model.GroceryBuilder;
import com.matthewcannefax.menuplanner.model.Ingredient;
import com.matthewcannefax.menuplanner.model.Recipe;
import com.matthewcannefax.menuplanner.utils.AdHelper;
import com.matthewcannefax.menuplanner.utils.FilterHelper;
import com.matthewcannefax.menuplanner.utils.NavDrawer;
import com.matthewcannefax.menuplanner.utils.NavHelper;
import com.matthewcannefax.menuplanner.utils.PermissionsHelper;

import java.util.ArrayList;
import java.util.List;

//this activity is to display the selected MenuList
//it has contains buttons to add a recipe to the menu and generate a grocery list
public class MenuListActivity extends AppCompatActivity {

    //region Class VARS
    //the listview object to display the menu
    private ListView lv;

    private Spinner catSpinner;

    //the list of recipes that will be displayed as the menu
    //this list is created in app, and then stored within the JSON
    private List<Recipe> menuList;

    //the adapter will be used across the app
    private RecipeMenuItemAdapter adapter;

    private DrawerLayout mDrawerLayout;

    //endregion

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //using the same layout as the recipelist activity
        setContentView(R.layout.menu_list);

        if(StaticMenu.getMenuList() == null){
            StaticMenu.loadMenu(this);
        }

        final Context mContext = this;

        //this is currently using a sample data class for testing
        //this is where the activity will call the database adapter
        menuList = StaticMenu.getMenuList();

        //initialize the listview in the activity
        lv = findViewById(R.id.recipeMenuListView);
        catSpinner = findViewById(R.id.catSpinner);
        Button filterBTN = findViewById(R.id.filterBTN);
        Button addIngredientButton = findViewById(R.id.add_recipe_button);
        mDrawerLayout = findViewById(R.id.drawer_layout);

        //set the title in the actionbar
        this.setTitle(this.getString(R.string.menu_activity_name));

        //this method to set the menu list adapter
        setMenuListViewAdapter();

        filterBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecipeCategory selectedCat = (RecipeCategory)catSpinner.getSelectedItem();
                if (selectedCat != RecipeCategory.ALL) {
                    List<Recipe> filteredRecipes = new ArrayList<>();

                    for(Recipe r : StaticMenu.getMenuList()){
                        if(r.getCategory() == selectedCat){
                            filteredRecipes.add(r);
                        }
                    }

                    RecipeMenuItemAdapter filteredAdapter = new RecipeMenuItemAdapter(mContext, filteredRecipes);
                    lv.setAdapter(filteredAdapter);
                } else {
                    lv.setAdapter(adapter);
                }
            }
        });

        addIngredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addRecipeToMenu();
            }
        });

        NavDrawer.setupNavDrawerMenuButton(getSupportActionBar());

        ListView drawerListView = findViewById(R.id.navList);

        NavDrawer.setupNavDrawer(MenuListActivity.this, this, drawerListView);

        //check that the required permissions are allowed
        PermissionsHelper.checkPermissions(MenuListActivity.this, this);

        AdView mAdView = findViewById(R.id.addEditRecipeBanner);

        AdHelper.SetupBannerAd(this, mAdView);
    }

    private void addRecipeToMenu(){
        if (StaticRecipes.getRecipeList() != null && StaticRecipes.getRecipeList().size() != 0) {
            //new intent to move to the RecipeListActivity
            Intent intent = new Intent(MenuListActivity.this, RecipeListActivity.class);
            intent.putExtra("TITLE", "Add To Menu");
            MenuListActivity.this.startActivity(intent);
        } else {
            Toast.makeText(this, "No Recipes in the Cookbook", Toast.LENGTH_SHORT).show();
        }
    }

    //this method sets up the menu list adapter
    private void setMenuListViewAdapter(){
        //set up the menu list adapter only if the menu list exists
        if(menuList != null){
            //initialize the RecipeMenuItemAdapter passing the list of menu items
            adapter = new RecipeMenuItemAdapter(this, menuList);

            //set the adapter of the listview to the recipeItemAdapter
            //Might try to use a Recycler view instead, since it is typically smoother when scrolling
            lv.setAdapter(adapter);
        }
        //if the list does not exist, send the user a toast saying that there are no menu items
        else{
            Toast.makeText(this, "No Menu Items", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        //if the menu list is not null notify the adapter of changes, in case there are any
        if (menuList != null) {
            adapter.notifyDataSetChanged();

            //setup the arrayAdapter for catSpinner
            ArrayAdapter catSpinnerAdapter = new ArrayAdapter(this, R.layout.category_spinner_item, FilterHelper.getRecipeCategoriesUsed(StaticMenu.getMenuList()));
            catSpinnerAdapter.setDropDownViewResource(R.layout.category_spinner_item);
            catSpinner.setAdapter(catSpinnerAdapter);
        }
    }

    //This is the overridden method to create the options menu in the actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        //add the menu button to add recipes
        MenuInflater menuInflater = getMenuInflater();

        //using the menu layout that is made specifically for this activity
        menuInflater.inflate(R.menu.menu_activity_menu, menu);

        return true;
    }


    //this overridden method is to handle the actionbar clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        //if the Add Recipe option is clicked
        switch (item.getItemId()) {
            case android.R.id.home:

                NavDrawer.navDrawerOptionsItem(mDrawerLayout);
                return true;

                //if the Generate Grocery List option is clicked
            case R.id.generateGroceryListItem:

                NavHelper.newGroceryList(this, this);
                return true;
                //default; this will allow the back button to work correctly
            default:
                return false;
        }
    }

    //this method creates the intent for the grocery list activity, and calls the grocery builder methods
    private void goToGroceryList(){

        //check that there are actually items in the menu list
        if (menuList != null && menuList.size() > 0) {
            //new intent to move to the GroceryListActivity
            Intent intent = new Intent(MenuListActivity.this, GroceryListActivity.class);

            //adding to the staticList. this needs to change to a different way of moving data around
            //as it stands now, If I generate a list then move back and generate the list again
            //it will double the list
            GroceryBuilder groceryBuilder = new GroceryBuilder(menuList);
            setStaticGroceryList(groceryBuilder.consolidateGroceries());

            //start the GroceryListActivity
            MenuListActivity.this.startActivity(intent);
        }
        //if there are items in the menu list, Toast the user saying just that
        else {
            Toast.makeText(this, "Please add menu items", Toast.LENGTH_SHORT).show();
        }
    }

    //this method sets up the Static Grocery list
    private void setStaticGroceryList(List<Ingredient> groceryItems){
        StaticGroceryList.setIngredientList(groceryItems);
        StaticGroceryList.saveGroceries(this);
    }

// --Commented out by Inspection START (4/5/2018 1:47 PM):
//    //this method creates the grocery list from the ingredient lists in the recipes on the recipe list
//    private List<Ingredient> getGroceryList()
//    {
//        //create a list to hold all the separate ingredients
//        List<Ingredient> groceries = new ArrayList<>();
//
//        //foreach recipe in the menulist
//        for(Recipe r : menuList){
//            //check to make sure the ingredient list has items in it
//            if(r.getIngredientList().size() != 0){
//                //add all the ingredients from each recipe to the grocery list
//                groceries.addAll(r.getIngredientList());
//            }
//        }
//
//        //return the new grocery list
//        return groceries;
//    }
// --Commented out by Inspection STOP (4/5/2018 1:47 PM)
}
