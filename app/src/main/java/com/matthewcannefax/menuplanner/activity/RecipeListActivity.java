package com.matthewcannefax.menuplanner.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
import com.matthewcannefax.menuplanner.StaticItems.StaticRecipes;
import com.matthewcannefax.menuplanner.arrayAdapters.RecipeListItemAdapter;
import com.matthewcannefax.menuplanner.model.Enums.RecipeCategory;
import com.matthewcannefax.menuplanner.model.Recipe;
import com.matthewcannefax.menuplanner.utils.AdHelper;
import com.matthewcannefax.menuplanner.utils.FilterHelper;
import com.matthewcannefax.menuplanner.utils.JSONHelper;
import com.matthewcannefax.menuplanner.utils.NavDrawer;
import com.matthewcannefax.menuplanner.utils.ShareHelper;
import com.matthewcannefax.menuplanner.utils.database.DataSource;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

//This activity is to display the total list of recipes from the db
public class RecipeListActivity extends AppCompatActivity {

    //list to hold the list of recipes from the db
    private List<Recipe> recipeList;
    //initialize the listview that will display the recipes
    private ListView lv;
    private RecipeListItemAdapter adapter;
    private String title;

    private Spinner catSpinner;
    private DrawerLayout mDrawerLayout;
    private DataSource mDataSource;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(StaticRecipes.getRecipeList() == null){
            StaticRecipes.loadRecipes(this);
        }

        final Context mContext = this;
        setContentView(R.layout.recipe_menu_list);

        catSpinner = this.findViewById(R.id.catSpinner);
        Button filterBTN = this.findViewById(R.id.filterBTN);

        mDataSource = new DataSource(this);
        mDataSource.open();

        ArrayAdapter<RecipeCategory> catSpinnerAdapter = new ArrayAdapter<RecipeCategory>(this, R.layout.category_spinner_item, mDataSource.getRecipeCategories());
        catSpinnerAdapter.setDropDownViewResource(R.layout.category_spinner_item);
        catSpinner.setAdapter(catSpinnerAdapter);

        StaticRecipes.resetItemsChecked();
//        recipeList = StaticRecipes.getRecipeList();



        recipeList = mDataSource.getAllRecipes();
        mDataSource.close();

        Bundle extras = getIntent().getExtras();
        try {
            title = extras.getString("TITLE");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        //set the title in the actionbar
        this.setTitle(title);

        //Instantiate the listview
        lv = findViewById(R.id.recipeMenuListView);

        //this method sets the adapter for the Recipe list view
        setRecipeListAdapter();

        filterBTN.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               RecipeCategory selectedCat = (RecipeCategory)catSpinner.getSelectedItem();
               RecipeListItemAdapter filteredAdapter = new RecipeListItemAdapter(mContext, mDataSource.getFilteredRecipes(selectedCat));
               lv.setAdapter(filteredAdapter);
//               if (selectedCat != RecipeCategory.ALL) {
//                   List<Recipe> filteredRecipes = new ArrayList<>();
//
//                   for(Recipe r : StaticRecipes.getRecipeList()){
//                       if(r.getCategory() == selectedCat){
//                           filteredRecipes.add(r);
//                       }
//                   }
//
//                   RecipeListItemAdapter filteredAdapter = new RecipeListItemAdapter(mContext, filteredRecipes);
//                   lv.setAdapter(filteredAdapter);
//               } else {
//                   lv.setAdapter(adapter);
//               }
           }
        });

        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavDrawer.setupNavDrawerMenuButton(getSupportActionBar());

        ListView drawerListView = findViewById(R.id.navList);

       //set up the nav drawer for this activity
        NavDrawer.setupNavDrawer(RecipeListActivity.this, this, drawerListView);

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        AdView mAdView = findViewById(R.id.addEditRecipeBanner);

        AdHelper.SetupBannerAd(this, mAdView);
    }

    private void setRecipeListAdapter(){
        if (recipeList != null){
            //instantiate the RecipeMenuItemAdapter passing the total list of recipes
            adapter = new RecipeListItemAdapter(this, recipeList);

            //set the RecipeMenuItemAdapter as the adapter for the listview
            lv.setAdapter(adapter);
        }else {
            Toast.makeText(this, "No Recipes Found", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onResume(){
        super.onResume();
        mDataSource.open();
        if(recipeList != null) {
            adapter.notifyDataSetChanged();
            //noinspection Convert2Diamond
//            ArrayAdapter<RecipeCategory> catSpinnerAdapter = new ArrayAdapter<RecipeCategory>(this, R.layout.category_spinner_item, FilterHelper.getRecipeCategoriesUsed(StaticRecipes.getRecipeList()));
//            catSpinnerAdapter.setDropDownViewResource(R.layout.category_spinner_item);
//            catSpinner.setAdapter(catSpinnerAdapter);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDataSource.close();
    }

    //this overridden method creates the menu item in the actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        //add the menu button to add recipes
        MenuInflater menuInflater = getMenuInflater();

        //using the menu layout create specifically for this activity
        menuInflater.inflate(R.menu.recipe_activity_menu, menu);

        //the add recipes to the menu menuitem
        MenuItem item = menu.findItem(R.id.addRecipesMenuItem);
        MenuItem exportItem = menu.findItem(R.id.exportCookbook);
        MenuItem importItem = menu.findItem(R.id.importCookbook);

        //if this is simply the My Recipes version of the activity, do not show the add recipes menuitem
        if (title.equals("My Recipes")) {
            item.setVisible(false);
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
                    builder.setTitle("Delete Recipes Completely?");
                    builder.setMessage("The recipes will be deleted permanently!");
                    builder.setNegativeButton("Cancel", null);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            //loop through the recipes to delete only the recipes that are selected
                            for(int position = 0; position < recipeList.size(); position++){
                                if(recipeList.get(position).isItemChecked()){
                                    //make sure that the recipe is also deleted from the menu if it exists there
                                    StaticMenu.removeRecipeFromMenu(recipeList.get(position), thisContext);
                                    recipeList.remove(position);
                                    position = position - 1;
                                }
                            }

                            if (recipeList != null && recipeList.size() != 0) {
                                //reset the adapter
                                adapter = new RecipeListItemAdapter(thisContext, recipeList);
                                lv.setAdapter(adapter);

                                //save the newly edited recipe list
                                StaticRecipes.saveRecipes(thisContext);

                                //notify the user that the recipes have been removed
                                Toast.makeText(context, "Removed", Toast.LENGTH_SHORT).show();
                            } else {
                                //save the recipe list
                                StaticRecipes.saveRecipes(thisContext);

                                //this list is empty now, go back to the main activity
                                Intent intent = new Intent(context, MenuListActivity.class);
                                startActivity(intent);

                                //notify the user that the recipes have been removed
                                Toast.makeText(context, "Removed", Toast.LENGTH_SHORT).show();

                                //close this activity
                                finish();

                            }
                        }
                    });
                    builder.show();

                } else{
                    Toast.makeText(this, "No Recipes Selected", Toast.LENGTH_SHORT).show();
                }

                b = true;
                break;
                //add the selected recipes to the weekly menu
            case R.id.addRecipesMenuItem:

                //check if any of the recipe items have been selected
                anySelected = anySelected();

                if (anySelected) {
                    //loop through the recipe list and add the selected recipes to the menu
                    for(int position = 0; position < recipeList.size(); position++){
                        if(recipeList.get(position).isItemChecked()){
                            recipeList.get(position).setItemChecked(false);
                            if(StaticMenu.getMenuList() != null) {
                                StaticMenu.getMenuList().add(recipeList.get(position));
                            }else{
                                //if the menu list is null set with a new list and then add
                                StaticMenu.setMenuList(new ArrayList<Recipe>());
                                StaticMenu.getMenuList().add(recipeList.get(position));
                            }

                        }
                    }

                    //save the menu list
                    StaticMenu.saveMenu(this);
                    @SuppressWarnings("unused") boolean result = JSONHelper.exportRecipesToJSON(this, StaticMenu.getMenuList(), getString(R.string.json_menu_list));

                    //return to the menu activity
                    Intent returnToMenu = new Intent(RecipeListActivity.this, MenuListActivity.class);
//                    returnToMenu.putExtra("RESULT", result);
                    RecipeListActivity.this.startActivity(returnToMenu);
                } else {
                    Toast.makeText(this, "No Recipes Selected", Toast.LENGTH_SHORT).show();
                }
                b = true;
                break;
            case R.id.importCookbook:
                ShareHelper.importCookbook(RecipeListActivity.this);
                b = true;
                break;
            case R.id.exportCookbook:
                ShareHelper.sendRecipes(this);
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

        final Context context = this;

        if (requestCode == ShareHelper.getPickFileRequestCode() && resultCode == RESULT_OK) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Import a Cookbook?");
            builder.setMessage("Are you sure you want to append your cookbook?");
            builder.setNegativeButton("Cancel", null);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Uri contentUri = data.getData();
                    InputStream inputStream;
                    try {
                        assert contentUri != null;
                        inputStream = getContentResolver().openInputStream(contentUri);
                        assert inputStream != null;
                        BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
                        StringBuilder total = new StringBuilder();
                        String line;
                        while ((line = r.readLine()) != null) {
                            total.append(line);
                        }

                        List<Recipe> importRecipes;
                        try {
                            importRecipes = ShareHelper.jsonToRecipe(context, total.toString());
                            StaticRecipes.addImportedRecipes(context, importRecipes);
                            StaticRecipes.saveRecipes(context);

                            Intent intent = new Intent(RecipeListActivity.this, RecipeListActivity.class);
                            intent.putExtra("TITLE", "My Recipes");
                            startActivity(intent);
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });


            builder.show();

            adapter.notifyDataSetChanged();
        }

    }
}
