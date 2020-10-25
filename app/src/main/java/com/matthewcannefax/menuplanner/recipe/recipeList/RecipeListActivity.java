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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.android.gms.ads.InterstitialAd;
import com.matthewcannefax.menuplanner.DrawerActivity;
import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.addEdit.EditRecipeActivity;
import com.matthewcannefax.menuplanner.recipe.RecipeCategory;
import com.matthewcannefax.menuplanner.recipe.Recipe;
import com.matthewcannefax.menuplanner.recipe.menuList.MenuListActivity;
import com.matthewcannefax.menuplanner.utils.navigation.NavDrawer;
import com.matthewcannefax.menuplanner.utils.ShareHelper;
import com.matthewcannefax.menuplanner.utils.database.DataSource;

import java.util.ArrayList;
import java.util.List;

//This activity is to display the total list of recipes from the db
public class RecipeListActivity extends DrawerActivity {

    //list to hold the list of recipes from the db
    private List<Recipe> recipeList;
    //initialize the listview that will display the recipes
    RecyclerView recyclerView;
    RecipeRecyclerAdapter recyclerAdapter;
    private String title;
    private FloatingActionButton fab;

    private Spinner catSpinner;
    private DataSource mDataSource;

    //interstitial ad
    private InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        catSpinner = this.findViewById(R.id.catSpinner);
        fab = this.findViewById(R.id.fab);

        mDataSource = new DataSource(this);
        mDataSource.open();

        ArrayAdapter<RecipeCategory> catSpinnerAdapter = new ArrayAdapter<RecipeCategory>(this, R.layout.category_spinner_item, mDataSource.getRecipeCategories());
        catSpinnerAdapter.setDropDownViewResource(R.layout.category_spinner_item);
        catSpinner.setAdapter(catSpinnerAdapter);

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
        setFilterListener();
        setFabListener();
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_recipe_list;
    }

    private void setFabListener(){
        fab.setOnClickListener(view -> {
            //check if any of the recipe items have been selected
            if (anySelected()) {
                //loop through the recipe list and add the selected recipes to the menu
                for(int position = 0; position < recipeList.size(); position++){
                    if(recipeList.get(position).isItemChecked()){
                        recipeList.get(position).setItemChecked(false);
                        mDataSource.addToMenu(recipeList.get(position).getRecipeID());
                    }
                }
                //return to the menu activity
                Intent returnToMenu = new Intent(RecipeListActivity.this, MenuListActivity.class);
                returnToMenu.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                RecipeListActivity.this.startActivity(returnToMenu);
//                    AdHelper.showInterstitial(getApplicationContext());
                RecipeListActivity.this.finish();
            } else {
                Snackbar.make(findViewById(android.R.id.content), R.string.no_recipes_selected, Snackbar.LENGTH_LONG).show();
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

    private void setFilterListener() {
        catSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                RecipeCategory selectedCat = (RecipeCategory)catSpinner.getSelectedItem();
                recipeList = mDataSource.getFilteredRecipes(selectedCat);
                recyclerAdapter.submitList(recipeList);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

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
            recyclerAdapter = new RecipeRecyclerAdapter(this, this::clickRecipe, this::longClickRecipe);
            //set the RecipeMenuItemAdapter as the adapter for the listview
            recyclerView.setAdapter(recyclerAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerAdapter.submitList(recipeList);
        }else {
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
        recyclerAdapter.submitList(recipeList);
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
                                }
                            }
                            if (mDataSource.getAllRecipes() != null && mDataSource.getAllRecipes().size() != 0) {
                                recipeList = mDataSource.getAllRecipes();
                                recyclerAdapter.submitList(recipeList);
                                //notify the user that the recipes have been removed
                                Snackbar.make(findViewById(android.R.id.content), R.string.removed, Snackbar.LENGTH_LONG).show();
                            } else {
                                //this list is empty now, go back to the main activity
                                Intent intent = new Intent(context, MenuListActivity.class);
                                startActivity(intent);
                                //notify the user that the recipes have been removed
                                Snackbar.make(findViewById(android.R.id.content), R.string.removed, Snackbar.LENGTH_LONG).show();
                                //close this activity
                                finish();
                            }
                        }
                    });
                    builder.show();
                } else{
                    Snackbar.make(findViewById(android.R.id.content), R.string.no_recipes_selected, Snackbar.LENGTH_LONG).show();
                }
                b = true;
                break;
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

    private void clickRecipe(Recipe recipe) {
        recipe.setItemChecked(!recipe.isItemChecked());
    }

    private boolean longClickRecipe(Recipe recipe) {
        Intent intent = new Intent(this, EditRecipeActivity.class);
        intent.putExtra(EditRecipeActivity.RECIPE_ID, recipe);
        startActivity(intent);
        return true;
    }
}
