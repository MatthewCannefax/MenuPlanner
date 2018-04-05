package com.matthewcannefax.menuplanner.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.StaticItems.StaticGroceryList;
import com.matthewcannefax.menuplanner.StaticItems.StaticMenu;
import com.matthewcannefax.menuplanner.StaticItems.StaticRecipes;
import com.matthewcannefax.menuplanner.arrayAdapters.RecipeListItemAdapter;
import com.matthewcannefax.menuplanner.model.Enums.RecipeCategory;
import com.matthewcannefax.menuplanner.model.Recipe;
import com.matthewcannefax.menuplanner.utils.FilterHelper;
import com.matthewcannefax.menuplanner.utils.JSONHelper;
import com.matthewcannefax.menuplanner.utils.NavDrawer;

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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Context mContext = this;
        setContentView(R.layout.recipe_menu_list);

        catSpinner = this.findViewById(R.id.catSpinner);
        Button filterBTN = this.findViewById(R.id.filterBTN);
        //setup the list of Recipes currently using a sample class for testing
        recipeList = StaticRecipes.getRecipeList();

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

        //setup the arrayAdapter for catSpinner
        ArrayAdapter catSpinnerAdapter = new ArrayAdapter(this, R.layout.category_spinner_item, FilterHelper.getRecipeCategoriesUsed(StaticRecipes.getRecipeList()));
        catSpinnerAdapter.setDropDownViewResource(R.layout.category_spinner_item);
        catSpinner.setAdapter(catSpinnerAdapter);

        //this method sets the adapter for the Recipe list view
        setRecipeListAdapter();

        filterBTN.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               RecipeCategory selectedCat = (RecipeCategory)catSpinner.getSelectedItem();
               if (selectedCat != RecipeCategory.ALL) {
                   List<Recipe> filteredRecipes = new ArrayList<>();

                   for(Recipe r : StaticRecipes.getRecipeList()){
                       if(r.getCategory() == selectedCat){
                           filteredRecipes.add(r);
                       }
                   }

                   RecipeListItemAdapter filteredAdapter = new RecipeListItemAdapter(mContext, filteredRecipes);
                   lv.setAdapter(filteredAdapter);
               } else {
                   lv.setAdapter(adapter);
               }
           }
        });

       //set up the nav drawer for this activity
        NavDrawer.setupNavDrawer(RecipeListActivity.this, this);
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
        if(recipeList != null) {
            adapter.notifyDataSetChanged();
        }
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

        //if this is simply the My Recipes version of the activity, do not show the add recipes menuitem
        if (title.equals("My Recipes")) {
            item.setVisible(false);
        }

        return true;
    }

    //this overridden method is to handle the actionbar item clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        boolean b;
        final Context context = this;

        switch(item.getItemId()){
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

                            //reset the adapter
                            adapter = new RecipeListItemAdapter(thisContext, recipeList);
                            lv.setAdapter(adapter);

                            //save the newly edited recipe list
                            StaticRecipes.saveRecipes(thisContext);

                            //notify the user that the recipes have been removed
                            Toast.makeText(context, "Removed", Toast.LENGTH_SHORT).show();
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
                b= true;
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
}
