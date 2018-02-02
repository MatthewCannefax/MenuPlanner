package com.matthewcannefax.menuplanner.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.StaticItems.StaticGroceryList;
import com.matthewcannefax.menuplanner.arrayAdapters.GroceryItemAdapter;
import com.matthewcannefax.menuplanner.model.Ingredient;

import java.util.List;

//This activity displays a consolidated and sorted Grocery list based on the recipes that are added
//to the menu list
public class GroceryListActivity extends AppCompatActivity {

    //A list of ingrediets made from the menu list
    //and a listview object for the listview in this activity
    private GroceryItemAdapter adapter;
    private List<Ingredient> ingredients;
    private ListView lv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //using the same list as the RecipeList and MenuList activities
        setContentView(R.layout.recipe_menu_list);

        //set the title in the actionbar
        this.setTitle("Grocery List");

        //using a static class to pass the grocery list from the MenuListActivity to this activity
        //Need to find a better way to pass that information
        //Maybe make use of Parcelable or use the database once it is available
        ingredients = StaticGroceryList.getIngredientList();

        //initialize the listview
        //might change to recyclerview since it tends to be a little smoother while scrolling
        lv = findViewById(R.id.recipeMenuListView);

        //this method to setup the grocery list adapter
        setGroceryListAdapter();

    }

    //this method is to setup the grocery list adapter, and will only fire if the grocery list exists
    private void setGroceryListAdapter(){
        //if the ingredients list exists
        if(ingredients != null){
            //initialize the GroceryItemAdapter passing the ingredients list
            adapter = new GroceryItemAdapter(this, ingredients);

            //set the GroceryItemAdapter as the adapter for the listview
            lv.setAdapter(adapter);
        }
        //if the grocery list does not exist send the user a toast to say that the grocery list was not found
        else {
            Toast.makeText(this, "No Grocery List Found", Toast.LENGTH_SHORT).show();
        }
    }

    //create the menu in the actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        //get the new menuinfater object
        MenuInflater menuInflater = getMenuInflater();

        //inflate the grocery list menu view
        menuInflater.inflate(R.menu.grocery_list_menu, menu);

        return true;
    }

    //handle clicks on the actionbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item){


        //if the remove selected items option is clicked
        if(item.getItemId() == R.id.removeSelectItems){
            int count = adapter.getCount();

            //loop through the adapter
            for (int i = 0; i < adapter.getCount(); i++) {

                //get the ingredient item from the adapter item
                Ingredient ingred = (Ingredient) adapter.getItem(i);

                //if the item is checked and the the ingredient equals the item of the same position in the static grocery list
                //the item will be removed
                if (ingred.getItemChecked() && ingred == StaticGroceryList.getIngredientList().get(i))
                {
                    //remove the item from the adapter
                    adapter.remove(ingred);

                    //since the item has been removed, the position needs to be stepped back by one
                    //otherwise it will skip an item
                    i = i - 1;
                }
            }

            //display a Toast confirming to the user that the items have been removed
            //may want to switch to a dialog so the user can confirm deletion
            if (count != adapter.getCount()) {
                Toast.makeText(this, "Items Removed", Toast.LENGTH_SHORT).show();
            }

            //reset the adapter
            adapter = new GroceryItemAdapter(this, ingredients);
            lv.setAdapter(adapter);

            //when the item was removed from the adpater it was also removed from the static grocery list
            //save the static grocery list to JSON
            StaticGroceryList.saveGroceries(this);
            return true;
        }
        //default to make sure the back button functions properly
        else {
            return false;
        }
    }
}
