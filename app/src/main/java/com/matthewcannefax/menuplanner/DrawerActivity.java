package com.matthewcannefax.menuplanner;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.snackbar.Snackbar;
import com.matthewcannefax.menuplanner.grocery.GroceryListActivity;
import com.matthewcannefax.menuplanner.recipe.Ingredient;
import com.matthewcannefax.menuplanner.recipe.Recipe;
import com.matthewcannefax.menuplanner.recipe.menuList.MenuListActivity;
import com.matthewcannefax.menuplanner.recipe.recipeList.RecipeListActivity;
import com.matthewcannefax.menuplanner.utils.ActivityNavEnum;
import com.matthewcannefax.menuplanner.utils.ShareHelper;
import com.matthewcannefax.menuplanner.utils.database.DataSource;
import com.matthewcannefax.menuplanner.utils.database.RecipeTable;
import com.matthewcannefax.menuplanner.utils.navigation.NavHelper;

import java.util.List;

public abstract class DrawerActivity extends AppCompatActivity {
    protected DrawerLayout mDrawerLayout;
    private ListView drawerListView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentLayoutId());
        mDrawerLayout = findViewById(R.id.drawer_layout);
        setupHamburgerButton();
        drawerListView = findViewById(R.id.navList);
        setupNavDrawer();
    }

    /**
     * @return activity layout id
     */
    protected abstract int getContentLayoutId();

    protected void navDrawerOptionsItem() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
    }

    protected void setupHamburgerButton() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
    }

    private void setupNavDrawer() {
        final DataSource mDataSource = new DataSource(this);

        ArrayAdapter<ActivityNavEnum> mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ActivityNavEnum.values());
        drawerListView.setAdapter(mAdapter);

        drawerListView.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent selectedActivityIntent = new Intent(this, ActivityNavEnum.getActivityEnum(i).getActivity());

            switch (ActivityNavEnum.getActivityEnum(i)) {
                case MENU_LIST_ACTIVITY:
                    if (!(this instanceof MenuListActivity)) {
                        selectedActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivityFromDrawer(selectedActivityIntent);
                    }
                    break;
                case RECIPE_LIST_ACTIVITY:
                    //if the recipe list is null or empty the user will be notified that there is no recipes in the cookbook
                    if (!(this instanceof RecipeListActivity)) {
                        if(RecipeTable.isNotEmpty(this)){
                            selectedActivityIntent.putExtra("TITLE", getString(R.string.my_recipes));
                            startActivityFromDrawer(selectedActivityIntent);
                        } else {
                            Snackbar.make(findViewById(android.R.id.content), R.string.no_recipes_found, Snackbar.LENGTH_LONG).show();
                        }
                    }
                    break;
                case VIEW_GROCERY_LIST:
                    if (!(this instanceof GroceryListActivity)) {
                        List<Ingredient> groceries = mDataSource.getAllGroceries();
                        //if there are no items in the grocery list, the user will be notified that there is no grocery list
                        if(groceries != null && groceries.size() > 0){
                            startActivityFromDrawer(selectedActivityIntent);
                        }else{
                            //                            Toast.makeText(context, "No Grocery List", Toast.LENGTH_SHORT).show();
                            Snackbar.make(findViewById(android.R.id.content), R.string.no_grocery_list_found, Snackbar.LENGTH_LONG).show();
                        }
                    }
                    break;
                case NEW_GROCERY_LIST:
                    if (!(this instanceof GroceryListActivity)) {
                        NavHelper.newGroceryList(this, this);
                    }
                    break;
                case IMPORT_COOKBOOK:
                    ShareHelper.importCookbook(this);
                    break;
                case SHARE_COOKBOOK:
                    List<Recipe> recipes = mDataSource.getAllRecipes();
                    if(recipes != null && recipes.size() != 0){
                        ShareHelper.sendAllRecipes(this);
                    }else{
                        Snackbar.make(findViewById(android.R.id.content), R.string.no_recipes_found, Snackbar.LENGTH_LONG).show();
                    }
                    break;
                default:
                    startActivityFromDrawer(selectedActivityIntent);
            }
        });
    }

    private void startActivityFromDrawer(Intent intent) {
        startActivity(intent);
        if (!(this instanceof MenuListActivity)) {
            finish();
        }
    }
}
