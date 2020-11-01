package com.matthewcannefax.menuplanner.recipe.menuList;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.matthewcannefax.menuplanner.DrawerActivity;
import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.addEdit.EditRecipeActivity;
import com.matthewcannefax.menuplanner.recipe.recipeList.RecipeListActivity;
import com.matthewcannefax.menuplanner.grocery.GroceryListActivity;
import com.matthewcannefax.menuplanner.recipe.RecipeCategory;
import com.matthewcannefax.menuplanner.recipe.Recipe;
import com.matthewcannefax.menuplanner.utils.FilterHelper;
import com.matthewcannefax.menuplanner.utils.PermissionsHelper;
import com.matthewcannefax.menuplanner.utils.ShareHelper;
import com.matthewcannefax.menuplanner.utils.database.DataSource;
import com.matthewcannefax.menuplanner.utils.notifications.NotificationHelper;

import java.util.ArrayList;
import java.util.List;

//this activity is to display the selected MenuList
//it has contains buttons to add a recipe to the menu and generate a grocery list
public class MenuListActivity extends DrawerActivity {

    //region Class VARS
    private RecyclerView recyclerView;
    private Spinner catSpinner;
    private MenuListRecyclerAdapter adapter;
    private MenuListViewModel viewModel;
    //endregion

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(MenuListViewModel.class);
        viewModel.setDataSource(this);
        viewModel.loadMenu();
        final Context mContext = this;
        recyclerView = findViewById(R.id.menuRecyclerView);
        catSpinner = findViewById(R.id.catSpinner);
        FloatingActionButton fab = findViewById(R.id.fab);
        this.setTitle(this.getString(R.string.menu_activity_name));
        setMenuListViewAdapter();
        setFilterListener();
        fab.setOnClickListener(view -> addRecipeToMenu());
        PermissionsHelper.checkPermissions(MenuListActivity.this, this);
        PermissionsHelper.setMenuFirstInstance(false);
        viewModel.getMenuList().observe(this, recipes -> {
            adapter.submitList(recipes);
            setCatAdapter();
        });
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_menu_list;
    }

    private void setCatAdapter() {
        if (viewModel.getMenuList() != null) {
            //setup the arrayAdapter for catSpinner
            @SuppressWarnings("Convert2Diamond") ArrayAdapter<RecipeCategory> catSpinnerAdapter = new ArrayAdapter<RecipeCategory>(this, R.layout.category_spinner_item, FilterHelper.getMenuCategoriesUsed(getApplicationContext()));
            catSpinnerAdapter.setDropDownViewResource(R.layout.category_spinner_item);
            catSpinner.setAdapter(catSpinnerAdapter);
        }
    }

    private void setFilterListener() {
        catSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                viewModel.filterRecipes((RecipeCategory) catSpinner.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //do nothing
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
        if (!viewModel.isCookbookEmpty()) {
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
                navDrawerOptionsItem();
                return true;
                //if the Generate Grocery List option is clicked
            case R.id.generateGroceryListItem:
                newGroceryList();
                return true;
            case R.id.appendGroceryListItem:
                viewModel.createGroceryList();
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
                if (viewModel.getMenuList().getValue() != null && viewModel.getMenuList().getValue().size() > 0) {
                    AlertDialog.Builder removeBuilder = new AlertDialog.Builder(this);
                    removeBuilder.setTitle(getString(R.string.are_you_sure));
                    removeBuilder.setMessage(getString(R.string.remove_all_from_menu));
                    removeBuilder.setNegativeButton(getString(R.string.no), null);
                    removeBuilder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            viewModel.removeAllMenuItems();
                            adapter.submitList(null);
                            setCatAdapter();
                            setFilterListener();
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
                    Snackbar.make(((View) recyclerView.getParent()), String.format(getString(R.string.format_recipe_removed), clickedRecipe.toString()), Snackbar.LENGTH_LONG).show();
                    viewModel.removeMenuItem(clickedRecipe.getRecipeID());
                });
        builder.show();
        return false;
    }
}
