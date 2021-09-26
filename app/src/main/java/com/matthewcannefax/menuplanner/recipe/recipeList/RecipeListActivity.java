package com.matthewcannefax.menuplanner.recipe.recipeList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.matthewcannefax.menuplanner.recipe.RecipeCategory;
import com.matthewcannefax.menuplanner.recipe.Recipe;
import com.matthewcannefax.menuplanner.recipe.menuList.MenuListFragment;
import com.matthewcannefax.menuplanner.utils.navigation.NavDrawer;
import com.matthewcannefax.menuplanner.utils.ShareHelper;
import com.matthewcannefax.menuplanner.utils.database.DataSource;

import java.util.ArrayList;
import java.util.List;

//This activity is to display the total list of recipes from the db
public class RecipeListActivity extends AppCompatActivity {

    //list to hold the list of recipes from the db
    private List<Recipe> recipeList;
    //initialize the listview that will display the recipes
//    private ListView lv;
//    private RecipeListItemAdapter adapter;
    RecyclerView recyclerView;
    RecipeRecyclerAdapter recyclerAdapter;
    private String title;
    private FloatingActionButton fab;

    private Spinner catSpinner;
    private DrawerLayout mDrawerLayout;
    private DataSource mDataSource;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        final Context mContext = this;
        setContentView(R.layout.recycler_recipe_list);

        catSpinner = this.findViewById(R.id.catSpinner);
        Button filterBTN = this.findViewById(R.id.filterBTN);
        fab = this.findViewById(R.id.fab);

        mDataSource = new DataSource(this);
        mDataSource.open();

        ArrayAdapter<RecipeCategory> catSpinnerAdapter = new ArrayAdapter<RecipeCategory>(this, R.layout.category_spinner_item, mDataSource.getRecipeCategories());
        catSpinnerAdapter.setDropDownViewResource(R.layout.category_spinner_item);
        catSpinner.setAdapter(catSpinnerAdapter);


//        recipeList = StaticRecipes.getRecipeList();

        recipeList = mDataSource.getAllRecipes();
        mDataSource.close();

        Bundle extras = getIntent().getExtras();

        getExtraTitle(extras);

        //set the title in the actionbar
        this.setTitle(title);

        //Instantiate the listview
        recyclerView = findViewById(R.id.recipeRecyclerView);


        //this method sets the adapter for the Recipe list view
        setRecipeListAdapter();

        setFilterBTNListener(mContext, filterBTN);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavDrawer.setupNavDrawerMenuButton(getSupportActionBar());

        ListView drawerListView = findViewById(R.id.navList);

        setFabListener();

       //set up the nav drawer for this activity
        NavDrawer.setupNavDrawer(RecipeListActivity.this, this, drawerListView);

    }

    private void setFabListener(){
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean anySelected = anySelected();

                //check if any of the recipe items have been selected
                anySelected = anySelected();

                if (anySelected) {
                    //loop through the recipe list and add the selected recipes to the menu
                    for(int position = 0; position < recipeList.size(); position++){
                        if(recipeList.get(position).isItemChecked()){
                            recipeList.get(position).setItemChecked(false);

                            mDataSource.addToMenu(recipeList.get(position).getRecipeID());

                        }
                    }

                    //return to the menu activity
                    Intent returnToMenu = new Intent(RecipeListActivity.this, MenuListFragment.class);
                    returnToMenu.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    RecipeListActivity.this.startActivity(returnToMenu);
//                    AdHelper.showInterstitial(getApplicationContext());
                    RecipeListActivity.this.finish();

                } else {
//                    Toast.makeText(this, "No Recipes Selected", Toast.LENGTH_SHORT).show();
                    Snackbar.make(findViewById(android.R.id.content), R.string.no_recipes_selected, Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void getExtraTitle(Bundle extras) {
        try {
            title = extras.getString("TITLE");
        } catch (NullPointerException e) {
            title = getString(R.string.my_recipes);
            e.printStackTrace();
        }
    }

    private void setFilterBTNListener(final Context mContext, Button filterBTN) {
        filterBTN.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               RecipeCategory selectedCat = (RecipeCategory)catSpinner.getSelectedItem();
               recipeList = mDataSource.getFilteredRecipes(selectedCat);
//               RecipeListItemAdapter filteredAdapter = new RecipeListItemAdapter(mContext, recipeList);
               RecipeRecyclerAdapter filteredAdapter = new RecipeRecyclerAdapter(mContext, recipeList);
               recyclerView.setAdapter(filteredAdapter);
               recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
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

    private void setRecipeListAdapter(){
        if (recipeList != null){
            //instantiate the RecipeMenuItemAdapter passing the total list of recipes
            recyclerAdapter = new RecipeRecyclerAdapter(this, recipeList);

            //set the RecipeMenuItemAdapter as the adapter for the listview
            recyclerView.setAdapter(recyclerAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }else {
//            Toast.makeText(this, "No Recipes Found", Toast.LENGTH_SHORT).show();
            Snackbar.make(findViewById(android.R.id.content), R.string.no_recipes_found, Snackbar.LENGTH_LONG).show();
        }
    }


    @Override
    public void onResume(){
        super.onResume();
        mDataSource.open();


        recipeList.clear();

        if(catSpinner.getSelectedItemPosition() != 0){
            recipeList = mDataSource.getFilteredRecipes((RecipeCategory)catSpinner.getSelectedItem());
        }else{
            recipeList = mDataSource.getAllRecipes();


        }
        recyclerAdapter = new RecipeRecyclerAdapter(this, recipeList);
        recyclerView.setAdapter(recyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

//        if(recipeList != null) {
//            adapter.notifyDataSetChanged();
            //noinspection Convert2Diamond
//            ArrayAdapter<RecipeCategory> catSpinnerAdapter = new ArrayAdapter<RecipeCategory>(this, R.layout.category_spinner_item, FilterHelper.getRecipeCategoriesUsed(StaticRecipes.getRecipeList()));
//            catSpinnerAdapter.setDropDownViewResource(R.layout.category_spinner_item);
//            catSpinner.setAdapter(catSpinnerAdapter);
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDataSource.close();
    }

    //this overridden method creates the menu item in the actionbar
    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        //add the menu button to add recipes
        MenuInflater menuInflater = getMenuInflater();

        //using the menu layout create specifically for this activity
        menuInflater.inflate(R.menu.recipe_activity_menu, menu);

        //the add recipes to the menu menuitem
//        MenuItem item = menu.findItem(R.id.addRecipesMenuItem);
        MenuItem exportItem = menu.findItem(R.id.exportCookbook);
        MenuItem importItem = menu.findItem(R.id.importCookbook);

        //if this is simply the My Recipes version of the activity, do not show the add recipes menuitem
        if (!title.equals(getString(R.string.add_to_menu))) {
//            item.setVisible(false);
            fab.setVisibility(View.INVISIBLE);
        }else{
            exportItem.setVisible(false);
            importItem.setVisible(false);
        }

        return true;
    }

    //this overridden method is to handle the actionbar item clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        boolean b;
        final Context context = this;

        switch(item.getItemId()){

            case android.R.id.home:

                NavDrawer.navDrawerOptionsItem(mDrawerLayout);
                return true;
            //remove the select items from the recipelist
            case R.id.removeRecipes:
                //this var and the loop checks if there are any recipes selected
                boolean anySelected = anySelected();

                //use the alert dialog to check if the user truly wants to delete the selected recipes
                AlertDialog.Builder builder;
                if (anySelected) {
                    final Context thisContext = this;
                    builder = new AlertDialog.Builder(this);
                    builder.setTitle(R.string.delete_complete);
                    builder.setMessage(R.string.permanently_delete);
                    builder.setNegativeButton(R.string.cancel, null);
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            //loop through the recipes to delete only the recipes that are selected
                            for(int position = 0; position < recipeList.size(); position++){
                                if(recipeList.get(position).isItemChecked()){
                                    //make sure that the recipe is also deleted from the menu if it exists there
//                                    StaticMenu.removeRecipeFromMenu(recipeList.get(position), thisContext);
//                                    recipeList.remove(position);

                                    mDataSource.removeRecipe(recipeList.get(position));

//                                    position = position - 1;
                                }
                            }

                            if (mDataSource.getAllRecipes() != null && mDataSource.getAllRecipes().size() != 0) {
                                //reset the adapter
                                recyclerAdapter = new RecipeRecyclerAdapter(thisContext, mDataSource.getAllRecipes());
                                recyclerView.setAdapter(recyclerAdapter);
                                recyclerView.setLayoutManager(new LinearLayoutManager(thisContext));
                                //save the newly edited recipe list
                                recipeList = mDataSource.getAllRecipes();

                                //notify the user that the recipes have been removed
//                                Toast.makeText(context, "Removed", Toast.LENGTH_SHORT).show();
                                Snackbar.make(findViewById(android.R.id.content), R.string.removed, Snackbar.LENGTH_LONG).show();
                            } else {

                                //this list is empty now, go back to the main activity
                                Intent intent = new Intent(context, MenuListFragment.class);
                                startActivity(intent);

                                //notify the user that the recipes have been removed
//                                Toast.makeText(context, "Removed", Toast.LENGTH_SHORT).show();
                                Snackbar.make(findViewById(android.R.id.content), R.string.removed, Snackbar.LENGTH_LONG).show();

                                //close this activity
                                finish();

                            }
                        }
                    });
                    builder.show();

                } else{
//                    Toast.makeText(this, "No Recipes Selected", Toast.LENGTH_SHORT).show();
                    Snackbar.make(findViewById(android.R.id.content), R.string.no_recipes_selected, Snackbar.LENGTH_LONG).show();
                }

                b = true;
                break;
                //add the selected recipes to the weekly menu
//            case R.id.addRecipesMenuItem:
//
//                //check if any of the recipe items have been selected
//                anySelected = anySelected();
//
//                if (anySelected) {
//                    //loop through the recipe list and add the selected recipes to the menu
//                    for(int position = 0; position < recipeList.size(); position++){
//                        if(recipeList.get(position).isItemChecked()){
//                            recipeList.get(position).setItemChecked(false);
//
//                            mDataSource.addToMenu(recipeList.get(position).getRecipeID());
//
//                        }
//                    }
//
//                    //return to the menu activity
//                    Intent returnToMenu = new Intent(RecipeListActivity.this, MenuListActivity.class);
//                    returnToMenu.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    RecipeListActivity.this.startActivity(returnToMenu);
//                    AdHelper.showInterstitial(this);
//                    RecipeListActivity.this.finish();
//
//                } else {
////                    Toast.makeText(this, "No Recipes Selected", Toast.LENGTH_SHORT).show();
//                    Snackbar.make(findViewById(android.R.id.content), R.string.no_recipes_selected, Snackbar.LENGTH_LONG).show();
//                }
//                b = true;
//                break;
            case R.id.importCookbook:
                ShareHelper.importCookbook(RecipeListActivity.this);
                b = true;
                break;
            case R.id.exportCookbook:

                anySelected = anySelected();
                if(anySelected){
                    //loop through the recipe list and add the selected recipes to send
                    List<Recipe> sendRecipes = new ArrayList<>();

                    for(int position = 0; position < recipeList.size(); position++){
                        if(recipeList.get(position).isItemChecked()){
                            recipeList.get(position).setItemChecked(false);

                            sendRecipes.add(recipeList.get(position));

                        }
                    }
                    ShareHelper.sendRecipeSelection(this, sendRecipes);
                }else{
//                    Toast.makeText(this, "Please select recipes to share", Toast.LENGTH_SHORT).show();
                    Snackbar.make(findViewById(android.R.id.content), R.string.select_recipes_to_share, Snackbar.LENGTH_LONG).show();
                }


                b = true;
                break;
            case R.id.help:
                AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
                helpBuilder.setTitle(R.string.help);
                helpBuilder.setMessage(R.string.recipe_list_help);
                helpBuilder.setNeutralButton(R.string.ok, null);
                helpBuilder.show();
                b = true;
                break;
                default:
                    b = false;
        }

        return b;

    }

    //this method loops through the recipe list to check if any of the recipes have been selected
    private boolean anySelected(){
        boolean anySelected = false;
        for (int n = 0; n < recipeList.size(); n++){
            if (recipeList.get(n).isItemChecked()){
                anySelected = true;
                break;
            }
        }
        return anySelected;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ShareHelper.activityResultImportCookbook(this, RecipeListActivity.this, requestCode, resultCode, data);
    }
}
