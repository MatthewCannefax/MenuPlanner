package com.matthewcannefax.menuplanner;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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

    protected void newGroceryList() {
        DataSource mDataSource = new DataSource(this);

        List<Ingredient> groceries = mDataSource.getAllGroceries();
        List<Recipe> menu = mDataSource.getAllMenuRecipes();

        //if there is an existing grocery list
        if ((groceries != null && groceries.size() > 0) && (menu != null && menu.size() > 0)) {

            //ask the user if they truly wish to create a new grocery list
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.new_glist_question);
            builder.setMessage(R.string.are_you_sure_replace_glist);
            builder.setNegativeButton(R.string.cancel, null);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //if the user clicks ok button, create the new grocery list with this method
                    goToGroceryList();
                }
            });

            builder.show();

        }
        //if there is no grocery list and the menu list is not null create a new grocery list
        else if (groceries != null) {
            goToGroceryList();
        }
        //if it gets here there is no grocery list and there is no menu list
        //so prompt the user to add menu items
        else {
//            Toast.makeText(context, "Please add menu items", Toast.LENGTH_SHORT).show();
            Snackbar.make(findViewById(android.R.id.content), R.string.please_add_menu_items, Snackbar.LENGTH_LONG).show();
        }
    }

    private void goToGroceryList() {
        final DataSource mDataSource = new DataSource(this);
        List<Recipe> menuList = mDataSource.getAllMenuRecipes();
        List<Ingredient> groceries = mDataSource.getAllGroceries();
        final Context context = this;
        //check that there are actually items in the menu list
        if (menuList != null && menuList.size() > 0) {
            //new intent to move to the GroceryListActivity
            Intent intent = new Intent(this, GroceryListActivity.class);

            mDataSource.removeAllGroceries();

            mDataSource.menuIngredientsToGroceryDB();
            //start the GroceryListActivity
            startActivity(intent);
        }
        //if there are no items in the menu list, Toast the user saying just that
        else {

            if (groceries == null || groceries.size() == 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.continue_question);
                builder.setMessage(R.string.wish_to_continue);
                builder.setNegativeButton(R.string.no, null);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(context, GroceryListActivity.class);
                        startActivity(intent);
                    }
                });
                builder.show();
            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.replace_grocery_title);
                builder.setMessage(R.string.replace_grocery_message);
                builder.setNegativeButton(R.string.no, null);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        mDataSource.removeAllGroceries();

                        Intent intent = new Intent(context, GroceryListActivity.class);
                        startActivity(intent);
                    }
                });
                builder.show();
            }

        }
    }

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
                        newGroceryList();
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
