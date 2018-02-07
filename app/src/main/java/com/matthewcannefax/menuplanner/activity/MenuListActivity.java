package com.matthewcannefax.menuplanner.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.StaticItems.StaticMenu;
import com.matthewcannefax.menuplanner.StaticItems.StaticGroceryList;
import com.matthewcannefax.menuplanner.StaticItems.StaticRecipes;
import com.matthewcannefax.menuplanner.arrayAdapters.RecipeMenuItemAdapter;
import com.matthewcannefax.menuplanner.model.GroceryBuilder;
import com.matthewcannefax.menuplanner.model.Ingredient;
import com.matthewcannefax.menuplanner.model.Recipe;
import com.matthewcannefax.menuplanner.utils.FilterHelper;

import java.util.ArrayList;
import java.util.List;

//this activity is to display the selected MenuList
//it has contains buttons to add a recipe to the menu and generate a grocery list
public class MenuListActivity extends AppCompatActivity {

    //region Class VARS
    //the listview object to display the menu
    private ListView lv;

    private Button filterBTN;
    private Spinner catSpinner;

    //the list of recipes that will be displayed as the menu
    //this list is created in app, and then stored within the JSON
    private List<Recipe> menuList;

    //the adapter will be used across the app
    private RecipeMenuItemAdapter adapter;
    //endregion

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //using the same layout as the recipelist activity
        setContentView(R.layout.recipe_menu_list);

        //this is currently using a sample data class for testing
        //this is where the activity will call the database adapter
        menuList = StaticMenu.getMenuList();

        //initialize the listview in the activity
        lv = findViewById(R.id.recipeMenuListView);
        catSpinner = findViewById(R.id.catSpinner);
        filterBTN = findViewById(R.id.filterBTN);

        //setup the arrayAdapter for catSpinner
        ArrayAdapter catSpinnerAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, FilterHelper.getRecipeCategoriesUsed(StaticMenu.getMenuList()));
        catSpinner.setAdapter(catSpinnerAdapter);

        //set the title in the actionbar
        this.setTitle("Menu");

        //this method to set the menu list adapter
        setMenuListViewAdapter();
    }

    //this method sets up the menu list adapter
    private void setMenuListViewAdapter(){
        //set up the menu list adapter only if the menu list exists
        if(menuList != null){
            //initialize the RecipeMenuItemAdapter passing the list of menu items
            adapter = new RecipeMenuItemAdapter(this, menuList);

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

        //if the menu list is not null notify the adapter of changes, in case there are any
        if (menuList != null) {
            adapter.notifyDataSetChanged();
        }
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
       if(item.getItemId() == R.id.addRecipeToMenuItem){
           //new intent to move to the RecipeListActivity
           Intent intent = new Intent(MenuListActivity.this, RecipeListActivity.class);
           intent.putExtra("TITLE", "Add Recipes");
           MenuListActivity.this.startActivity(intent);
           return true;
       }
       //if the Generate Grocery List option is clicked
       else if(item.getItemId() == R.id.generateGroceryListItem){
           //if the grocery list is not null and actually has items in it
           if (StaticGroceryList.getIngredientList() != null && StaticGroceryList.getIngredientList().size() > 0) {

               //ask the user if they truly wish to create a new grocery list
               AlertDialog.Builder builder = new AlertDialog.Builder(this);
               builder.setTitle("Generate New Grocery List?");
               builder.setMessage("Are you sure you want to replace your existing grocery list?");
               builder.setNegativeButton("Cancel", null);
               builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {
                        //if the user clicks ok button, create the new grocery list with this method
                       goToGroceryList();
                   }
               });

               builder.show();

               return true;
           }
           //if there is no grocery list and the menu list is not null create a new grocery list
           else if (StaticMenu.getMenuList() != null){
               goToGroceryList();
               return true;
           }
           //if it gets here there is no grocery list and there is no menu list
           //so prompt the user to add menu items
           else {
               Toast.makeText(this, "Please add menu items", Toast.LENGTH_SHORT).show();
               return true;
           }
       }else if (item.getItemId() == R.id.viewCurrentGroceryList){
           //if the grocery list exists and actually has items in it, go to the grocery list activity
           if (StaticGroceryList.getIngredientList() != null && StaticGroceryList.getIngredientList().size() > 0){

               Intent intent = new Intent(MenuListActivity.this, GroceryListActivity.class);
               MenuListActivity.this.startActivity(intent);

               return true;
           }
           //if there is no grocery list, send a toast telling the user just that
           else{
               Toast.makeText(this, "No List", Toast.LENGTH_SHORT).show();
               return false;
           }
        }
       //default; this will allow the back button to work correctly
       else {
           return false;
       }
    }

    //this method creates the intent for the grocery list activity, and calls the grocery builder methods
    private void goToGroceryList(){

        //check that there are actually items in the menu list
        if (menuList != null && menuList.size() > 0) {
            //new intent to move to the GroceryListActivity
            Intent intent = new Intent(MenuListActivity.this, GroceryListActivity.class);

            //adding to the staticList. this needs to change to a different way of moving data around
            //as it stands now, If I generate a list then move back and generate the list again
            //it will double the list
            GroceryBuilder groceryBuilder = new GroceryBuilder(menuList);
            setStaticGroceryList(groceryBuilder.consolidateGroceries());

            //start the GroceryListActivity
            MenuListActivity.this.startActivity(intent);
        }
        //if there are items in the menu list, Toast the user saying just that
        else {
            Toast.makeText(this, "Please add menu items", Toast.LENGTH_SHORT).show();
        }
    }

    //this method sets up the Static Grocery list
    private void setStaticGroceryList(List<Ingredient> groceryItems){
        StaticGroceryList.setIngredientList(groceryItems);
        StaticGroceryList.saveGroceries(this);
    }

    //this method creates the grocery list from the ingredient lists in the recipes on the recipe list
    private List<Ingredient> getGroceryList()
    {
        //create a list to hold all the separate ingredients
        List<Ingredient> groceries = new ArrayList<>();

        //foreach recipe in the menulist
        for(Recipe r : menuList){
            //check to make sure the ingredient list has items in it
            if(r.getIngredientList().size() != 0){
                //add all the ingredients from each recipe to the grocery list
                groceries.addAll(r.getIngredientList());
            }
        }

        //return the new grocery list
        return groceries;
    }
}
