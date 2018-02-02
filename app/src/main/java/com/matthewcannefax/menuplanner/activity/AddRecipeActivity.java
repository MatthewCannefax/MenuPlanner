package com.matthewcannefax.menuplanner.activity;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.StaticItems.StaticRecipes;
import com.matthewcannefax.menuplanner.arrayAdapters.ButtonArrayAdapter;
import com.matthewcannefax.menuplanner.arrayAdapters.IngredientItemAdapter;
import com.matthewcannefax.menuplanner.model.Enums.GroceryCategory;
import com.matthewcannefax.menuplanner.model.Enums.MeasurementType;
import com.matthewcannefax.menuplanner.model.Enums.RecipeCategory;
import com.matthewcannefax.menuplanner.model.Ingredient;
import com.matthewcannefax.menuplanner.model.Measurement;
import com.matthewcannefax.menuplanner.model.Recipe;
import com.matthewcannefax.menuplanner.utils.JSONHelper;
import com.matthewcannefax.menuplanner.utils.NumberHelper;

import java.util.ArrayList;
import java.util.List;

public class AddRecipeActivity extends AppCompatActivity{

    //region Class Vars
    //initialize the objects of the activity
    private EditText recipeName;
    private ImageView recipeIMG;
    private Spinner recipeCat;
    private ListView recipeIngreds;
    private EditText directionsMultiLine;

    private IngredientItemAdapter ingredientItemAdapter;
    private ButtonArrayAdapter buttonArrayAdapter;

    private Recipe newRecipe;

    //boolean object to check if an image has been chosen
    //be careful to only change this var to true at the end of the dialog
    private boolean imgSet;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_edit_recipe);

        newRecipe = new Recipe();

        //instantiate all the controls in the activity
        recipeName = findViewById(R.id.recipeName);
        recipeIMG = findViewById(R.id.recipeIMG);
        recipeCat = findViewById(R.id.categorySpinner);
        recipeIngreds = findViewById(R.id.ingredientsListView);
        directionsMultiLine = findViewById(R.id.directionsMultiLine);

        //setup the Category Spinner
        ArrayAdapter<RecipeCategory> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, RecipeCategory.values());
        recipeCat.setAdapter(spinnerAdapter);

        //set the imgSet var to false as default
        imgSet = false;

        //use the clearEditText method to setup a on focus listener to clear the text on focus
        //and then null the listener so it doesn't happen again
        clearEditText(recipeName);

        //this list and adapter acts as a placeholder so the footerView will display at the launch of this activity
        List<Object> objs = new ArrayList<>();
        objs.add(new Object());
        buttonArrayAdapter = new ButtonArrayAdapter(this, objs);
        recipeIngreds.setAdapter(buttonArrayAdapter);

        //calling this method to add the ingredient button to the recipeIngreds listview and setup the on click listener
        addIngredientBTN();
    }

    private void addIngredientBTN() {

        //setup this constant context var to use in the inner methods
        final Context mContext = this;

        //region Setup The Button View
        //add a button at the end of the listview to allow the user to add more ingredients to the recipe
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.add_ingredient_btn, null);
        view.requestFocus();
        Button addBTN = view.findViewById(R.id.addIngredientBTN);
        recipeIngreds.addFooterView(view);
        //endregion

        //The button on click listener
        addBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //the alertdialog will display the ingredient information
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Add Ingredient");

                //create a new view to display the ingredient information
                View editIngredientView = LayoutInflater.from(mContext).inflate(R.layout.add_ingredient_item, (ViewGroup)view.findViewById(android.R.id.content), false);

                //controls inside the view
                final EditText etAmount = editIngredientView.findViewById(R.id.amountText);
                final Spinner spMeasure = editIngredientView.findViewById(R.id.amountSpinner);
                final EditText etName = editIngredientView.findViewById(R.id.ingredientName);
                final Spinner spCat = editIngredientView.findViewById(R.id.categorySpinner);

                //use the clearEditText method to clear the editTexts in the Alert Dialog and the null the listener
                clearEditText(etName);
                clearEditText(etAmount);

                //setup the default array adapters for the category and measurementtype spinners
                ArrayAdapter<MeasurementType> measureAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, MeasurementType.values());
                final ArrayAdapter<GroceryCategory> ingredCatAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, GroceryCategory.values());

                //set the spinner adpaters
                spMeasure.setAdapter(measureAdapter);
                spCat.setAdapter(ingredCatAdapter);

                //set the new view as the view for the alertdialog
                builder.setView(editIngredientView);

                //setup the buttons for the alertdialog
                builder.setNegativeButton("Cancel", null);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //check if the all the inputs are filled in correctly
                        if (!etName.getText().toString().equals("") && !etAmount.getText().toString().equals("") && NumberHelper.tryParseDouble(etAmount.getText().toString())) {
                            //check if the ingredient list exists for this recipe
                            if(newRecipe.getIngredientList() != null){
                                //add the new Ingredient to the ingredientList
                                newRecipe.getIngredientList().add(new Ingredient(
                                        etName.getText().toString(),
                                        (GroceryCategory)spCat.getSelectedItem(),
                                        new Measurement(
                                                Double.parseDouble(etAmount.getText().toString()),
                                                (MeasurementType)spMeasure.getSelectedItem()
                                        )

                                ));
                                //notify the ingredientItemAdapter that the dataset has been changed
                                ingredientItemAdapter.notifyDataSetChanged();
                            }
                            //if the ingredient list does not exist create the new Ingredient and a new Ingredient list
                            //then add that to the recipe
                            else{
                                //create the new ingredient
                                Ingredient ingredient = new Ingredient(
                                        etName.getText().toString(),
                                        (GroceryCategory)spCat.getSelectedItem(),
                                        new Measurement(
                                                Double.parseDouble(etAmount.getText().toString()),
                                                (MeasurementType)spMeasure.getSelectedItem()
                                        ));
                                //create the new ingredient list
                                List<Ingredient> newIngredredients = new ArrayList<>();

                                //add the new ingredient to the the new list
                                newIngredredients.add(ingredient);

                                //set the new list as the list for the new recipe
                                newRecipe.setIngredientList(newIngredredients);

                                //setup the ingredient item adapter for the recipeIngreds listView
                                ingredientItemAdapter = new IngredientItemAdapter(mContext, newRecipe.getIngredientList());
                                recipeIngreds.setAdapter(ingredientItemAdapter);
                            }
                        }else{
                            //send a Toast prompting the user to make sure and fill in the alert dialog correctly
                            Toast.makeText(mContext, "Please enter a name and amount", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                builder.show();


            }
        });
    }

    //this method takes an editText, clears the text and then nulls the listener itself, so it won't clear again
    private void clearEditText(final EditText editText){
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                //clear the text
                editText.setText("");

                //null the listener
                editText.setOnFocusChangeListener(null);
            }
        });
    }

    //create the menu button in the actionbar (currently only contains the submit option)
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        //add the menu button to add recipes to the recipes list
        MenuInflater menuInflater = getMenuInflater();

        //using the menu layout created specifically for this activity
        menuInflater.inflate(R.menu.add_recipe_menu, menu);

        return true;
    }

    //handle the clicks of the menu items
    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        //if the submit button is clicked
        //this is where the new recipe is all put together and then added to Recipe list and JSON files
        if(item.getItemId() == R.id.menuSubmitBTN){

            //get the name, directions and category from the controls
            newRecipe.setName(recipeName.getText().toString());
            newRecipe.setDirections(directionsMultiLine.getText().toString());
            newRecipe.setCategory((RecipeCategory) recipeCat.getSelectedItem());


            //sample image for testing!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            newRecipe.setImagePath("defaultRecipe.jpg");

            //get the image of the new recipe if the image has been set
            //using the imgSet var to signal whether the image has been set or not
            if(imgSet){
                //get the new image from the imageview and store it in the assets package
            }

            //add the new recipe to the static recipe list
            //this call also assigns a recipeID and saves the static list to JSON
            StaticRecipes.addNewRecipe(newRecipe, this);

            //return to the Recipe list activity
            Intent returnToRecipes = new Intent(AddRecipeActivity.this, RecipeListActivity.class);

            //put extra the title of the recipe list activity
            returnToRecipes.putExtra("TITLE", this.getString(R.string.recipe_list_activity_title));
            AddRecipeActivity.this.startActivity(returnToRecipes);

            return true;
        }else{
            return false;
        }

    }
}
