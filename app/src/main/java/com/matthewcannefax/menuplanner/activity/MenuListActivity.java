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
import android.widget.ListView;
import android.widget.Toast;

import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.SampleData.SampleMenu;
import com.matthewcannefax.menuplanner.StaticItems.StaticGroceryList;
import com.matthewcannefax.menuplanner.arrayAdapters.RecipeMenuItemAdapter;
import com.matthewcannefax.menuplanner.model.GroceryBuilder;
import com.matthewcannefax.menuplanner.model.Ingredient;
import com.matthewcannefax.menuplanner.model.Recipe;

import java.util.ArrayList;
import java.util.List;

//this activity is to display the selected MenuList
//it has contains buttons to add a recipe to the menu and generate a grocery list
public class MenuListActivity extends AppCompatActivity {

    //the listview object to display the menu
    private ListView lv;
    //the list of recipes that will be displayed as the menu
    //this list is created in app, and then stored within the database
    private List<Recipe> menuList;

    private RecipeMenuItemAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //using the same layout as the recipelist activity
        setContentView(R.layout.recipe_menu_list);

        //this is currently using a sample data class for testing
        //this is where the activity will call the database adapter
        menuList = SampleMenu.sampleMenuList;

        //initialize the listview in the activity
        lv = findViewById(R.id.recipeMenuListView);

        //set the title in the actionbar
        this.setTitle("Menu");

        //initialize the RecipeMenuItemAdapter passing the list of menu items
        adapter = new RecipeMenuItemAdapter(this, menuList);

        //set the adapter of the listview to the recipeItemAdapter
        //Might try to use a Recycler view instead, since it is typically smoother when scrolling
        lv.setAdapter(adapter);

    }

    @Override
    public void onResume(){
        super.onResume();
        adapter.notifyDataSetChanged();
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
//           Toast.makeText(this, "Add Recipe Selected", Toast.LENGTH_SHORT).show();
           return true;
       }
       //if the Generate Grocery List option is clicked
       else if(item.getItemId() == R.id.generateGroceryListItem){
           if (StaticGroceryList.items.size() != 0) {

               AlertDialog.Builder builder = new AlertDialog.Builder(this);
               builder.setTitle("Generate New Grocery List?");
               builder.setMessage("Are you sure you want to replace your existing grocery list?");
               builder.setNegativeButton("Cancel", null);
               builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {
                       goToGroceryList();
                   }
               });

               builder.show();

               return true;
           } else {
               goToGroceryList();
               return true;
           }
       }else if (item.getItemId() == R.id.viewCurrentGroceryList){
           if (StaticGroceryList.items.size() != 0){

               Intent intent = new Intent(MenuListActivity.this, GroceryListActivity.class);
               MenuListActivity.this.startActivity(intent);

               return true;
           }
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

    private void goToGroceryList(){
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

    //this method sets up the Static Grocery list
    private void setStaticGroceryList(List<Ingredient> g){
        StaticGroceryList.items = g;
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

                groceries.addAll(r.getIngredientList());
                //foreach ingredient in the ingredientslist of the recipe
//                for(Ingredient i : r.getIngredientList()){
//                    //add the ingredient to the new grocery list
//                    groceries.add(i);
//                }
            }
        }

        //return the new grocery list
        return groceries;
    }
}
