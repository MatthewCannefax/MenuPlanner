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

import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.addEdit.EditRecipeActivity;
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
    private RecyclerView recyclerView;
    private Spinner catSpinner;
    private List<Recipe> menuList;
    private MenuListRecyclerAdapter adapter;
    private DrawerLayout mDrawerLayout;
    private Button filterBTN;
    DataSource mDataSource;
    //endregion

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //using the same layout as the recipelist activity
        setContentView(R.layout.recycler_menu_list);
        final Context mContext = this;
        mDataSource = new DataSource(mContext);

        //this is where the activity will call the database adapter
        menuList = mDataSource.getAllMenuRecipes();

        //initialize the listview in the activity
        recyclerView = findViewById(R.id.menuRecyclerView);
        catSpinner = findViewById(R.id.catSpinner);
        filterBTN = findViewById(R.id.filterBTN);
        FloatingActionButton fab = findViewById(R.id.fab);
        mDrawerLayout = findViewById(R.id.drawer_layout);

        //set the title in the actionbar
        this.setTitle(this.getString(R.string.menu_activity_name));

        //this method to set the menu list adapter
        setMenuListViewAdapter();
//        final MenuListRecyclerAdapter allMenuAdapter = new MenuListRecyclerAdapter(this, mDataSource.getAllMenuRecipes(), catSpinner);
        setFilterBTNListener(mContext, filterBTN, adapter);
        fab.setOnClickListener(view -> addRecipeToMenu());
        NavDrawer.setupNavDrawerMenuButton(getSupportActionBar());
        ListView drawerListView = findViewById(R.id.navList);
        NavDrawer.setupNavDrawer(MenuListActivity.this, this, drawerListView);
        //check that the required permissions are allowed
        PermissionsHelper.checkPermissions(MenuListActivity.this, this);
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
        filterBTN.setOnClickListener(view -> {
            RecipeCategory selectedCat = (RecipeCategory) catSpinner.getSelectedItem();
            if (selectedCat != RecipeCategory.ALL) {
                List<Recipe> filteredRecipes = new ArrayList<>();
                for (Recipe r : mDataSource.getAllMenuRecipes()) {
                    if (r.getCategory() == selectedCat) {
                        filteredRecipes.add(r);
                    }
                }
//                MenuListRecyclerAdapter filteredAdapter = new MenuListRecyclerAdapter(mContext, filteredRecipes, catSpinner);
//                recyclerView.setAdapter(filteredAdapter);
//                recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
//                adapter.submitList(new ArrayList<>());
                adapter.submitList(filteredRecipes);
            } else {
//                MenuListRecyclerAdapter allAdapter = new MenuListRecyclerAdapter(getApplicationContext(), mDataSource.getAllMenuRecipes(), catSpinner);
//                recyclerView.setAdapter(allAdapter);
//                recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
//                adapter.submitList(new ArrayList<>());
                adapter.submitList(mDataSource.getAllMenuRecipes());
            }
        });
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
//        AdView mAdView = findViewById(R.id.addEditRecipeBanner);
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
            Snackbar.make(findViewById(android.R.id.content), R.string.no_recipes_in_cookbook, Snackbar.LENGTH_LONG).show();
        }
    }

    //this method sets up the menu list adapter
    private void setMenuListViewAdapter(){
        adapter = new MenuListRecyclerAdapter(this::clickRecipe, this::longClickRecipe);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<Recipe> menuRecipes = mDataSource.getAllMenuRecipes();
        if(menuRecipes != null){
            adapter.submitList(menuRecipes);
        } else {
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
//                adapter = new MenuListRecyclerAdapter( this, filteredRecipes, catSpinner);
                adapter.submitList(null);
                adapter.submitList(filteredRecipes);
            }else{
//                adapter = new MenuListRecyclerAdapter(this, mDataSource.getAllMenuRecipes(), catSpinner);
                adapter.submitList(null);
                adapter.submitList(mDataSource.getAllMenuRecipes());
            }
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        //add the menu button to add recipes
        MenuInflater menuInflater = getMenuInflater();

        //using the menu layout that is made specifically for this activity
        menuInflater.inflate(R.menu.menu_activity_menu, menu);

        return true;
    }

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
            default:
                return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ShareHelper.activityResultImportCookbook(this, MenuListActivity.this, requestCode, resultCode, data);
    }

    private void clickRecipe(Recipe clickedRecipe) {
        Intent intent = new Intent(this, EditRecipeActivity.class);
        intent.putExtra(EditRecipeActivity.RECIPE_ID, clickedRecipe);
        startActivity(intent);
    }

    private boolean longClickRecipe(Recipe clickedRecipe) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(R.string.remove_from_menu)
                .setMessage(String.format(getString(R.string.are_you_sure_remove_format), clickedRecipe.toString()))
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                    Snackbar.make(
                            ((View) recyclerView.getParent()),
                            String.format(getString(R.string.format_recipe_removed), clickedRecipe.toString()),
                            Snackbar.LENGTH_LONG).show();
                    mDataSource.removeMenuItem(clickedRecipe.getRecipeID());
                    new Handler().postDelayed(() -> adapter.submitList(mDataSource.getAllMenuRecipes()), 200);
                    catSpinner.setAdapter(new ArrayAdapter<>(
                            this,
                            R.layout.category_spinner_item,
                            FilterHelper.getMenuCategoriesUsed(this)));

                });
        builder.show();
        return false;
    }
}
