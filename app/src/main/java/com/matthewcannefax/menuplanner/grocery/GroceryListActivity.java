package com.matthewcannefax.menuplanner.grocery;

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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.recipe.menuList.MenuListFragment;
import com.matthewcannefax.menuplanner.recipe.MeasurementType;
import com.matthewcannefax.menuplanner.recipe.Ingredient;
import com.matthewcannefax.menuplanner.recipe.Measurement;
import com.matthewcannefax.menuplanner.utils.navigation.NavDrawer;
import com.matthewcannefax.menuplanner.utils.NumberHelper;
import com.matthewcannefax.menuplanner.utils.ShareHelper;
import com.matthewcannefax.menuplanner.utils.database.DataSource;

import java.util.List;

//This activity displays a consolidated and sorted Grocery list based on the recipes that are added
//to the menu list
public class GroceryListActivity extends AppCompatActivity {

    //A list of ingrediets made from the menu list
    //and a listview object for the listview in this activity
//    private GroceryItemAdapter adapter;
    private GroceryRecyclerAdapter recyclerAdapter;
    private static List<Ingredient> ingredients;
//    private ListView lv;
    private RecyclerView recyclerView;
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
//        setContentView(R.layout.grocery_list_layout);
        setContentView(R.layout.grocery_recyclerview_layout);

        //set the title in the actionbar
        this.setTitle(R.string.grocery_list);

        //using a static class to pass the grocery list from the MenuListActivity to this activity
        //Need to find a better way to pass that information
        //Maybe make use of Parcelable or use the database once it is available
        ingredients = mDataSource.getAllGroceries();

        //initialize the listview
        recyclerView = findViewById(R.id.groceryRecyclerView);
        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addGroceryItem();
            }
        });

        //this method to setup the grocery list adapter
        setGroceryListAdapter();

        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavDrawer.setupNavDrawerMenuButton(getSupportActionBar());

        ListView drawerListView = findViewById(R.id.navList);

        //set up the nav drawer for this activity
        NavDrawer.setupNavDrawer(GroceryListActivity.this, this, drawerListView);

        GroceryRowBuilder rowBuilder = new GroceryRowBuilder(ingredients);
        List<GroceryRow> rows = rowBuilder.getGroceryRows();
        int i = 0;
    }

    private void checkForNullGroceries() {
        if(mDataSource.getAllGroceries() == null){
            Intent mainIntent = new Intent(this, MenuListFragment.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mainIntent);
            finish();
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

//        AdView mAdView = findViewById(R.id.addEditRecipeBanner);
//
//        AdHelper.SetupBannerAd(this, mAdView);
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

        //setup the default array adapters for the category and measurementtype spinners
        ArrayAdapter<MeasurementType> measureAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, MeasurementType.getEnum());
        ArrayAdapter<GroceryCategory> ingredCatAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, GroceryCategory.getEnum());

        //set the spinner adpaters
        spMeasure.setAdapter(measureAdapter);
        spCat.setAdapter(ingredCatAdapter);

        //set the newItemView as the view for the alertdialog
        builder.setView(newItemView);

        builder.setNegativeButton("Cancel", null);
        final GroceryClickListener clickGroceryItem = this::clickGroceryItem;
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
//                    adapter = new GroceryItemAdapter(mContext, mDataSource.getAllGroceries());
//                    lv.setAdapter(adapter);

                    recyclerAdapter = new GroceryRecyclerAdapter(mDataSource.getAllGroceries(), clickGroceryItem);
                    recyclerView.setAdapter(recyclerAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

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
            recyclerAdapter = new GroceryRecyclerAdapter(ingredients, this::clickGroceryItem);
            recyclerView.setAdapter(recyclerAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
        //if the grocery list does not exist send the user a toast to say that the grocery list was not found
        else {
//            Toast.makeText(this, "No Grocery List Found", Toast.LENGTH_SHORT).show();
            Snackbar.make(findViewById(android.R.id.content), R.string.no_grocery_list_found, Snackbar.LENGTH_LONG).show();
        }
    }

    private void clickGroceryItem(int ingredientID, GroceryViewChangeListener groceryViewChangeListener) {
        for (Ingredient ingredient : ingredients) {
            if (ingredient.getIngredientID() == ingredientID) {
                ingredient.setItemChecked(!ingredient.getItemChecked());
                mDataSource.setGroceryItemChecked(ingredient.getIngredientID(), ingredient.getItemChecked());

                groceryViewChangeListener.changeView(ingredient.getItemChecked());
                break;
            }
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
                int count = recyclerAdapter.getItemCount();

                //loop through the adapter
                for (int i = 0; i < recyclerAdapter.getItemCount(); i++) {
                    if (recyclerAdapter.getItem(i) instanceof GroceryItemRow) {
                        //get the ingredient item from the adapter item
                        Ingredient ingred = ((GroceryItemRow) recyclerAdapter.getItem(i)).getGroceryItem();

                        //if the item is checked and the the ingredient equals the item of the same position in the static grocery list
                        //the item will be removed
                        assert ingred != null;
                        if (ingred.getItemChecked()) {
                            //remove the item from the adapter
                            mDataSource.removeGroceryItem(ingred);
                        }
                    }
                }
                //display a Toast confirming to the user that the items have been removed
                //may want to switch to a dialog so the user can confirm deletion
                if (count != recyclerAdapter.getItemCount()) {
                    Snackbar.make(findViewById(android.R.id.content), R.string.items_removed, Snackbar.LENGTH_LONG).show();
                }

                //reset the adapter
                ingredients = mDataSource.getAllGroceries();
//                adapter = new GroceryItemAdapter(this, ingredients);
//                lv.setAdapter(adapter);

                recyclerAdapter = new GroceryRecyclerAdapter(ingredients, this::clickGroceryItem);
                recyclerView.setAdapter(recyclerAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));

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
//                adapter = new GroceryItemAdapter(this, mDataSource.getAllGroceries());
//                lv.setAdapter(adapter);

                recyclerAdapter = new GroceryRecyclerAdapter(mDataSource.getAllGroceries(), this::clickGroceryItem);
                recyclerView.setAdapter(recyclerAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ShareHelper.activityResultImportCookbook(this, GroceryListActivity.this, requestCode, resultCode, data);
    }
}
