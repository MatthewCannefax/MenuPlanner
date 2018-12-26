package com.matthewcannefax.menuplanner.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
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
import com.matthewcannefax.menuplanner.arrayAdapters.RecipeMenuItemAdapter;
import com.matthewcannefax.menuplanner.model.Enums.RecipeCategory;
import com.matthewcannefax.menuplanner.model.Recipe;
import com.matthewcannefax.menuplanner.utils.AdHelper;
import com.matthewcannefax.menuplanner.utils.FilterHelper;
import com.matthewcannefax.menuplanner.utils.NavDrawer;
import com.matthewcannefax.menuplanner.utils.NavHelper;
import com.matthewcannefax.menuplanner.utils.PermissionsHelper;
import com.matthewcannefax.menuplanner.utils.ShareHelper;
import com.matthewcannefax.menuplanner.utils.database.DataSource;

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

    DataSource mDataSource;

    //endregion

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //using the same layout as the recipelist activity
        setContentView(R.layout.menu_list);

        final Context mContext = this;

        mDataSource = new DataSource(mContext);

        //this is where the activity will call the database adapter
        menuList = mDataSource.getAllMenuRecipes();

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

                    for(Recipe r : mDataSource.getAllMenuRecipes()){
                        if(r.getCategory() == selectedCat){
                            filteredRecipes.add(r);
                        }
                    }

                    RecipeMenuItemAdapter filteredAdapter = new RecipeMenuItemAdapter(mContext, filteredRecipes, lv, catSpinner);
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

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        AdView mAdView = findViewById(R.id.addEditRecipeBanner);

        AdHelper.SetupBannerAd(this, mAdView);
    }

    private void addRecipeToMenu(){
        List<Recipe> allRecipes = mDataSource.getAllRecipes();
        if (allRecipes != null && allRecipes.size() != 0) {
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
        if(mDataSource.getAllMenuRecipes() != null){
            //initialize the RecipeMenuItemAdapter passing the list of menu items
            adapter = new RecipeMenuItemAdapter(this, mDataSource.getAllMenuRecipes(), lv, catSpinner);

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

        menuList = mDataSource.getAllMenuRecipes();
        setMenuListViewAdapter();

        //if the menu list is not null notify the adapter of changes, in case there are any
        if (mDataSource.getAllMenuRecipes() != null) {
            adapter.notifyDataSetChanged();

            //setup the arrayAdapter for catSpinner
            @SuppressWarnings("Convert2Diamond") ArrayAdapter<RecipeCategory> catSpinnerAdapter = new ArrayAdapter<RecipeCategory>(this, R.layout.category_spinner_item, FilterHelper.getRecipeCategoriesUsed(mDataSource.getAllMenuRecipes()));
            catSpinnerAdapter.setDropDownViewResource(R.layout.category_spinner_item);
            catSpinner.setAdapter(catSpinnerAdapter);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ShareHelper.activityResultImportCookbook(this, MenuListActivity.this, requestCode, resultCode, data);

    }
}
