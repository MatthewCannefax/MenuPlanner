package com.matthewcannefax.menuplanner.recipe.menuList;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.recipe.recipeList.RecipeListActivity;
import com.matthewcannefax.menuplanner.grocery.GroceryListActivity;
import com.matthewcannefax.menuplanner.recipe.RecipeCategory;
import com.matthewcannefax.menuplanner.recipe.Recipe;
import com.matthewcannefax.menuplanner.utils.FilterHelper;
import com.matthewcannefax.menuplanner.utils.navigation.NavDrawer;
import com.matthewcannefax.menuplanner.utils.navigation.NavHelper;
import com.matthewcannefax.menuplanner.utils.PermissionsHelper;
import com.matthewcannefax.menuplanner.utils.ShareHelper;
import com.matthewcannefax.menuplanner.utils.database.DataSource;
import com.matthewcannefax.menuplanner.utils.notifications.NotificationHelper;

import java.util.ArrayList;
import java.util.List;

//this activity is to display the selected MenuList
//it has contains buttons to add a recipe to the menu and generate a grocery list
public class MenuListActivity extends AppCompatActivity {

    //region Class VARS
    //the listview object to display the menu
//    private ListView lv;
    private RecyclerView recyclerView;

    private Spinner catSpinner;

    //the list of recipes that will be displayed as the menu
    //this list is created in app, and then stored within the JSON
    private List<Recipe> menuList;

    //the adapter will be used across the app
//    private RecipeMenuItemAdapter adapter;
    private MenuListRecyclerAdapter adapter;

    private DrawerLayout mDrawerLayout;

    private Button filterBTN;

    DataSource mDataSource;

    private boolean mTwoPane;
    private Activity mActivity;

    public static final String RECIPE_ID_STRING = "selected_recipe";

    //endregion

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //using the same layout as the recipelist activity
        setContentView(R.layout.recycler_menu_list);

        if(findViewById(R.id.recipe_detail_container) != null){
            mTwoPane = true;
        }
        mActivity = this;
        final Context mContext = this;

        mDataSource = new DataSource(mContext);

        final SharedPreferences sharedPref = getSharedPreferences(getString(R.string.is_preloaded), 0);
        boolean isPreloaded = sharedPref.getBoolean(getString(R.string.is_preloaded), false);

        //this is where the activity will call the database adapter
        menuList = mDataSource.getAllMenuRecipes();

        //initialize the listview in the activity
//        lv = findViewById(R.id.recipeMenuListView);
        recyclerView = findViewById(R.id.menuRecyclerView);
        catSpinner = findViewById(R.id.catSpinner);
        filterBTN = findViewById(R.id.filterBTN);
        FloatingActionButton fab = findViewById(R.id.fab);
//        Button addIngredientButton = findViewById(R.id.add_recipe_button);
        mDrawerLayout = findViewById(R.id.drawer_layout);

        //set the title in the actionbar
        this.setTitle(this.getString(R.string.menu_activity_name));

        //this method to set the menu list adapter
        setMenuListViewAdapter();

//        final RecipeMenuItemAdapter allMenuAdapter = new RecipeMenuItemAdapter(this, MenuListActivity.this, mDataSource.getAllMenuRecipes(), lv, catSpinner);
        final MenuListRecyclerAdapter allMenuAdapter = new MenuListRecyclerAdapter(getSupportFragmentManager(),this, mDataSource.getAllMenuRecipes(), catSpinner, mTwoPane);

        setFilterBTNListener(mContext, filterBTN, allMenuAdapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addRecipeToMenu();
            }
        });

//        addIngredientButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                addRecipeToMenu();
//            }
//        });

        NavDrawer.setupNavDrawerMenuButton(getSupportActionBar());

        ListView drawerListView = findViewById(R.id.navList);

        NavDrawer.setupNavDrawer(MenuListActivity.this, this, drawerListView);

        //check that the required permissions are allowed
        PermissionsHelper.checkPermissions(MenuListActivity.this, this);

        //checkPermissions(mContext, sharedPref, isPreloaded);

        //if the menu list is not null notify the adapter of changes, in case there are any
        setCatAdapter();

        PermissionsHelper.setMenuFirstInstance(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationHelper notificationHelper = new NotificationHelper(mContext);
            notificationHelper.scheduleJob();
        }


    }

    private void setCatAdapter() {
        if (mDataSource.getAllMenuRecipes() != null) {
            adapter.notifyDataSetChanged();

            //setup the arrayAdapter for catSpinner
            @SuppressWarnings("Convert2Diamond") ArrayAdapter<RecipeCategory> catSpinnerAdapter = new ArrayAdapter<RecipeCategory>(this, R.layout.category_spinner_item, FilterHelper.getMenuCategoriesUsed(getApplicationContext()));
            catSpinnerAdapter.setDropDownViewResource(R.layout.category_spinner_item);
            catSpinner.setAdapter(catSpinnerAdapter);
        }
    }

    private void setFilterBTNListener(final Context mContext, Button filterBTN, final MenuListRecyclerAdapter allMenuAdapter) {
        filterBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecipeCategory selectedCat = (RecipeCategory) catSpinner.getSelectedItem();
                if (selectedCat != RecipeCategory.ALL) {
                    List<Recipe> filteredRecipes = new ArrayList<>();

                    for (Recipe r : mDataSource.getAllMenuRecipes()) {
                        if (r.getCategory() == selectedCat) {
                            filteredRecipes.add(r);
                        }
                    }

//                    RecipeMenuItemAdapter filteredAdapter = new RecipeMenuItemAdapter(mContext, MenuListActivity.this, filteredRecipes, lv, catSpinner);
                    MenuListRecyclerAdapter filteredAdapter = new MenuListRecyclerAdapter(getSupportFragmentManager(), mContext, filteredRecipes, catSpinner, mTwoPane);
                    recyclerView.setAdapter(filteredAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                } else {
                    MenuListRecyclerAdapter allAdapter = new MenuListRecyclerAdapter(getSupportFragmentManager(), getApplicationContext(), mDataSource.getAllMenuRecipes(), catSpinner, mTwoPane);
                    recyclerView.setAdapter(allAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                }
            }
        });
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

//        AdView mAdView = findViewById(R.id.addEditRecipeBanner);
//
//        AdHelper.SetupBannerAd(this, mAdView);
    }

    private void addRecipeToMenu(){
        List<Recipe> allRecipes = mDataSource.getAllRecipes();
        if (allRecipes != null && allRecipes.size() != 0) {
            //new intent to move to the RecipeListActivity
            Intent intent = new Intent(MenuListActivity.this, RecipeListActivity.class);
            intent.putExtra("TITLE", getString(R.string.add_to_menu));
            MenuListActivity.this.startActivity(intent);
        } else {
//            Toast.makeText(this, "No Recipes in the Cookbook", Toast.LENGTH_SHORT).show();
            Snackbar.make(findViewById(android.R.id.content), R.string.no_recipes_in_cookbook, Snackbar.LENGTH_LONG).show();
        }
    }

    //this method sets up the menu list adapter
    private void setMenuListViewAdapter(){
        //set up the menu list adapter only if the menu list exists
        if(mDataSource.getAllMenuRecipes() != null){
            //initialize the RecipeMenuItemAdapter passing the list of menu items
//            adapter = new RecipeMenuItemAdapter(this, MenuListActivity.this, mDataSource.getAllMenuRecipes(), lv, catSpinner);
            adapter = new MenuListRecyclerAdapter(getSupportFragmentManager(), this, mDataSource.getAllMenuRecipes(), catSpinner, mTwoPane);
            //set the adapter of the listview to the recipeItemAdapter
            //Might try to use a Recycler view instead, since it is typically smoother when scrolling
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
        //if the list does not exist, send the user a toast saying that there are no menu items
        else{
//            Toast.makeText(this, "No Menu Items", Toast.LENGTH_SHORT).show();
            Snackbar.make(findViewById(android.R.id.content), R.string.no_menu_items, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        menuList = mDataSource.getAllMenuRecipes();
        setMenuListViewAdapter();

        if(mDataSource.getAllMenuRecipes() != null && mDataSource.getAllMenuRecipes().size() != 0){
            if(catSpinner.getSelectedItemPosition() != 0){

                RecipeCategory selectedCat = (RecipeCategory)catSpinner.getSelectedItem();
                List<Recipe> filteredRecipes = new ArrayList<>();

                for (Recipe r :
                        mDataSource.getAllMenuRecipes()) {
                    if(r.getCategory() == selectedCat){
                        filteredRecipes.add(r);
                    }
                }


//                adapter = new RecipeMenuItemAdapter(this, MenuListActivity.this, filteredRecipes, lv, catSpinner);
                adapter = new MenuListRecyclerAdapter(getSupportFragmentManager(), this, filteredRecipes, catSpinner, mTwoPane);
            }else{
//                adapter = new RecipeMenuItemAdapter(this, MenuListActivity.this, mDataSource.getAllMenuRecipes(), lv, catSpinner);
                adapter = new MenuListRecyclerAdapter(getSupportFragmentManager(), this, mDataSource.getAllMenuRecipes(), catSpinner, mTwoPane);
            }

            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
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
            case R.id.appendGroceryListItem:
                mDataSource.menuIngredientsToGroceryDB();
                Intent appendIntent = new Intent(this, GroceryListActivity.class);
                startActivity(appendIntent);
                return true;
            case R.id.help:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.help);
                builder.setMessage(R.string.menu_list_help);
                builder.setNeutralButton(R.string.ok, null);
                builder.show();
                return true;
            case R.id.removeAll:
                menuList = mDataSource.getAllMenuRecipes();
                if (menuList != null && menuList.size() != 0) {
                    AlertDialog.Builder removeBuilder = new AlertDialog.Builder(this);
                    removeBuilder.setTitle(getString(R.string.are_you_sure));
                    removeBuilder.setMessage(getString(R.string.remove_all_from_menu));
                    removeBuilder.setNegativeButton(getString(R.string.no), null);
                    removeBuilder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mDataSource.removeAllMenuItems();
                            menuList = null;
                            recyclerView.setAdapter(null);
                            setCatAdapter();
                            setFilterBTNListener(getApplicationContext(),filterBTN,null);
                            Snackbar.make(findViewById(android.R.id.content), getString(R.string.recipes_removed), Snackbar.LENGTH_LONG).show();
                        }
                    });
                    removeBuilder.show();
                } else {
                    Snackbar.make(findViewById(android.R.id.content), getString(R.string.no_menu_items), Snackbar.LENGTH_LONG).show();
                }
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
