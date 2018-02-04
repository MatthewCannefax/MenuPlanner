package com.matthewcannefax.menuplanner.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.StaticItems.StaticGroceryList;
import com.matthewcannefax.menuplanner.arrayAdapters.GroceryItemAdapter;
import com.matthewcannefax.menuplanner.model.Enums.GroceryCategory;
import com.matthewcannefax.menuplanner.model.Enums.MeasurementType;
import com.matthewcannefax.menuplanner.model.Ingredient;
import com.matthewcannefax.menuplanner.model.Measurement;
import com.matthewcannefax.menuplanner.utils.NumberHelper;

import java.util.List;

//This activity displays a consolidated and sorted Grocery list based on the recipes that are added
//to the menu list
public class GroceryListActivity extends AppCompatActivity {

    //A list of ingrediets made from the menu list
    //and a listview object for the listview in this activity
    private GroceryItemAdapter adapter;
    private List<Ingredient> ingredients;
    private ListView lv;
    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;

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
        else if(item.getItemId() == R.id.addNewGroceryItem){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Add Item");

            View newItemView = getLayoutInflater().inflate(R.layout.add_ingredient_item, null);

            //controls inside the view
            final EditText etAmount = newItemView.findViewById(R.id.amountText);
            final Spinner spMeasure = newItemView.findViewById(R.id.amountSpinner);
            final EditText etName = newItemView.findViewById(R.id.ingredientName);
            final Spinner spCat = newItemView.findViewById(R.id.categorySpinner);

            //set up the clicklisteners for the editTexts inside the alertdialog
            //these clicklisteners will clear the text inside and then null the listener itself
            clearEditText(etName);
            clearEditText(etAmount);

            //setup the default array adapters for the category and measurementtype spinners
            ArrayAdapter<MeasurementType> measureAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, MeasurementType.values());
            ArrayAdapter<GroceryCategory> ingredCatAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, GroceryCategory.values());

            //set the spinner adpaters
            spMeasure.setAdapter(measureAdapter);
            spCat.setAdapter(ingredCatAdapter);

            builder.setView(newItemView);

            builder.setNegativeButton("Cancel", null);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //check that there are values for the name and the amount
                    //also using a custom tryParse method to check that the value for the amount is indeed a double
                    if (!etName.getText().toString().equals("") && !etAmount.getText().toString().equals("") && NumberHelper.tryParseDouble(etAmount.getText().toString())) {
                        //add the new Ingredient to the ingredientList
                        StaticGroceryList.getIngredientList().add(new Ingredient(
                                etName.getText().toString(),
                                (GroceryCategory)spCat.getSelectedItem(),
                                new Measurement(
                                        Double.parseDouble(etAmount.getText().toString()),
                                        (MeasurementType)spMeasure.getSelectedItem()
                                )
                        ));

                        StaticGroceryList.saveGroceries(mContext);

                        //notify the arrayadapter that the dataset has changed
                        adapter = new GroceryItemAdapter(mContext, StaticGroceryList.getIngredientList());
                        lv.setAdapter(adapter);

                    } else {
                        //Send the user a Toast to tell them that they need to enter both a name and amount in the edittexts
                        Toast.makeText(getApplicationContext(), "Please enter a name and amount", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            builder.show();

            return true;

        }
        else {
            return false;
        }
    }

    //this method clears any editText boxes and then nulls the clicklistener so it can't be clear again
    private void clearEditText(final EditText editText){
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                //clear the text in the box
                editText.setText("");

                //null the click listener
                editText.setOnFocusChangeListener(null);
            }
        });
    }
}
