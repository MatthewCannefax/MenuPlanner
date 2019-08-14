package com.matthewcannefax.menuplanner.grocery;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.recipe.menuList.MenuListActivity;
import com.matthewcannefax.menuplanner.recipe.MeasurementType;
import com.matthewcannefax.menuplanner.recipe.Ingredient;
import com.matthewcannefax.menuplanner.recipe.Measurement;
import com.matthewcannefax.menuplanner.utils.AdHelper;
import com.matthewcannefax.menuplanner.utils.NavDrawer;
import com.matthewcannefax.menuplanner.utils.NumberHelper;
import com.matthewcannefax.menuplanner.utils.ShareHelper;
import com.matthewcannefax.menuplanner.utils.database.DataSource;

import java.util.List;

//This activity displays a consolidated and sorted Grocery list based on the recipes that are added
//to the menu list
public class GroceryListActivity extends AppCompatActivity {

    //A list of ingrediets made from the menu list
    //and a listview object for the listview in this activity
    private GroceryItemAdapter adapter;
    private static List<Ingredient> ingredients;
    private ListView lv;
    private Context mContext;
    private DrawerLayout mDrawerLayout;

    private DataSource mDataSource;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDataSource = new DataSource(this);
        mDataSource.open();

        checkForNullGroceries();

        mContext = this;

        //using the same list as the RecipeList and MenuList activities
        setContentView(R.layout.grocery_list_layout);

        //set the title in the actionbar
        this.setTitle(R.string.grocery_list);

        //using a static class to pass the grocery list from the MenuListActivity to this activity
        //Need to find a better way to pass that information
        //Maybe make use of Parcelable or use the database once it is available
        ingredients = mDataSource.getAllGroceries();

        //initialize the listview
        //might change to recyclerview since it tends to be a little smoother while scrolling
        lv = findViewById(R.id.recipeMenuListView);
//        Button mButton = findViewById(R.id.addItemButton);
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addGroceryItem();
            }
        });
//        mButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                addGroceryItem();
//            }
//        });

        //this method to setup the grocery list adapter
        setGroceryListAdapter();

        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavDrawer.setupNavDrawerMenuButton(getSupportActionBar());

        ListView drawerListView = findViewById(R.id.navList);

        //set up the nav drawer for this activity
        NavDrawer.setupNavDrawer(GroceryListActivity.this, this, drawerListView);


    }

    private void checkForNullGroceries() {
        if(mDataSource.getAllGroceries() == null){
            Intent mainIntent = new Intent(this, MenuListActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mainIntent);
            finish();
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        AdView mAdView = findViewById(R.id.addEditRecipeBanner);

        AdHelper.SetupBannerAd(this, mAdView);
    }

    private void addGroceryItem(){
        //create an alertdialog to input this information
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.add_item);

        //inflate the add_ingredient_item layout
        @SuppressLint("InflateParams") View newItemView = getLayoutInflater().inflate(R.layout.add_ingredient_item, null);

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
        ArrayAdapter<MeasurementType> measureAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, MeasurementType.getEnum());
        ArrayAdapter<GroceryCategory> ingredCatAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, GroceryCategory.getEnum());

        //set the spinner adpaters
        spMeasure.setAdapter(measureAdapter);
        spCat.setAdapter(ingredCatAdapter);

        //set the newItemView as the view for the alertdialog
        builder.setView(newItemView);

        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //check that there are values for the name and the amount
                //also using a custom tryParse method to check that the value for the amount is indeed a double
                if (!etName.getText().toString().equals("") && !etAmount.getText().toString().equals("") && NumberHelper.tryParseDouble(etAmount.getText().toString())) {
                    //add the new Ingredient to the ingredientList
                    Ingredient newGroceryItem = new Ingredient(
                            etName.getText().toString(),
                            (GroceryCategory) spCat.getSelectedItem(),
                            new Measurement(
                                    Double.parseDouble(etAmount.getText().toString()),
                                    (MeasurementType) spMeasure.getSelectedItem()
                            )
                    );

                    //add the new grocery item to the database
                    mDataSource.createGroceryItem(newGroceryItem);

                    //notify the arrayadapter that the dataset has changed
                    adapter = new GroceryItemAdapter(mContext, mDataSource.getAllGroceries());
                    lv.setAdapter(adapter);

                } else {
                    //Send the user a Toast to tell them that they need to enter both a name and amount in the edittexts
                    Toast.makeText(getApplicationContext(), R.string.enter_name_amount, Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDataSource.open();
        ingredients = mDataSource.getAllGroceries();
        setGroceryListAdapter();

    }

    @Override
    protected void onPause() {
        super.onPause();
        mDataSource.close();
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
//            Toast.makeText(this, "No Grocery List Found", Toast.LENGTH_SHORT).show();
            Snackbar.make(findViewById(android.R.id.content), R.string.no_grocery_list_found, Snackbar.LENGTH_LONG).show();
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
        switch (item.getItemId()) {
            case android.R.id.home:

                NavDrawer.navDrawerOptionsItem(mDrawerLayout);
                return true;
            case R.id.removeSelectItems:
                int count = adapter.getCount();

                //loop through the adapter
                for (int i = 0; i < adapter.getCount(); i++) {

                    //get the ingredient item from the adapter item
                    Ingredient ingred = adapter.getItem(i);

                    //if the item is checked and the the ingredient equals the item of the same position in the static grocery list
                    //the item will be removed
                    assert ingred != null;
                    if (ingred.getItemChecked()) {
                        //remove the item from the adapter
//                    adapter.remove(ingred);
//                        StaticGroceryList.getIngredientList().remove(i);
                        mDataSource.removeGroceryItem(ingred);

                        //since the item has been removed, the position needs to be stepped back by one
                        //otherwise it will skip an item
//                        i = i - 1;
                    }
                }

                //display a Toast confirming to the user that the items have been removed
                //may want to switch to a dialog so the user can confirm deletion
                if (count != adapter.getCount()) {
//                    Toast.makeText(this, "Items Removed", Toast.LENGTH_SHORT).show();
                    Snackbar.make(findViewById(android.R.id.content), R.string.items_removed, Snackbar.LENGTH_LONG).show();
                }

                //reset the adapter
                ingredients = mDataSource.getAllGroceries();
                adapter = new GroceryItemAdapter(this, ingredients);
                lv.setAdapter(adapter);

//                AdHelper.showGroceryInterstitial(this);

                return true;
                
            case R.id.shareGroceryList:
                ShareHelper.sendGroceryList(this);
                return true;
            case R.id.selectAllGroceries:
                for (Ingredient i :
                        mDataSource.getAllGroceries()) {
                    mDataSource.setGroceryItemChecked(i.getIngredientID(), true);
                }
                adapter = new GroceryItemAdapter(this, mDataSource.getAllGroceries());
                lv.setAdapter(adapter);
                return true;
            case R.id.help:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Help");
                builder.setMessage(R.string.grocery_list_help);
                builder.setNeutralButton("OK", null);
                builder.show();
                return true;
            default:
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ShareHelper.activityResultImportCookbook(this, GroceryListActivity.this, requestCode, resultCode, data);
    }
}
