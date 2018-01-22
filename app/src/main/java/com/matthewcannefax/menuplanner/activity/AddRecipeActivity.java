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
import android.widget.TextView;
import android.widget.Toast;

import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.SampleData.SampleRecipes;
import com.matthewcannefax.menuplanner.arrayAdapters.ButtonArrayAdapter;
import com.matthewcannefax.menuplanner.arrayAdapters.IngredientItemAdapter;
import com.matthewcannefax.menuplanner.model.Enums.GroceryCategory;
import com.matthewcannefax.menuplanner.model.Enums.MeasurementType;
import com.matthewcannefax.menuplanner.model.Enums.RecipeCategory;
import com.matthewcannefax.menuplanner.model.Ingredient;
import com.matthewcannefax.menuplanner.model.Measurement;
import com.matthewcannefax.menuplanner.model.Recipe;
import com.matthewcannefax.menuplanner.utils.JSONHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AddRecipeActivity extends AppCompatActivity{

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

        recipeName.setText(R.string.new_recipe_name_box);

        //setup the Category Spinner
        ArrayAdapter<RecipeCategory> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, RecipeCategory.values());
        recipeCat.setAdapter(spinnerAdapter);

        //set the imgSet var to false as default
        imgSet = false;

        recipeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recipeName.setText("");
                recipeName.setOnClickListener(null);
            }
        });

        //this list and adapter acts as a placeholder so the footerView will display at the launch of this activity
        List<Object> objs = new ArrayList<>();
        objs.add(new Object());
        buttonArrayAdapter = new ButtonArrayAdapter(this, objs);
        recipeIngreds.setAdapter(buttonArrayAdapter);

        addIngredientBTN();



    }

    private void addIngredientBTN() {
        final Context mContext = this;
        //add a button at the end of the listview to allow the user to add more ingredients to the recipe
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.add_ingredient_btn, null);
        Button addBTN = view.findViewById(R.id.addIngredientBTN);
        recipeIngreds.addFooterView(view);

        addBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //the alertdialog will display the ingredient information
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Edit Ingredient");

                //create a new view to display the ingredient information
                View editIngredientView = LayoutInflater.from(mContext).inflate(R.layout.add_ingredient_item, (ViewGroup)view.findViewById(android.R.id.content), false);

                //controls inside the view
                final EditText etAmount = editIngredientView.findViewById(R.id.amountText);
                final Spinner spMeasure = editIngredientView.findViewById(R.id.amountSpinner);
                final EditText etName = editIngredientView.findViewById(R.id.ingredientName);
                final Spinner spCat = editIngredientView.findViewById(R.id.categorySpinner);

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
                            ingredientItemAdapter.notifyDataSetChanged();
                        }else{
                            Ingredient ingredient = new Ingredient(
                                    etName.getText().toString(),
                                    (GroceryCategory)spCat.getSelectedItem(),
                                    new Measurement(
                                            Double.parseDouble(etAmount.getText().toString()),
                                            (MeasurementType)spMeasure.getSelectedItem()
                                    ));
                            List<Ingredient> newIngredredients = new ArrayList<>();
                            newIngredredients.add(ingredient);
                            newRecipe.setIngredientList(newIngredredients);
                            ingredientItemAdapter = new IngredientItemAdapter(mContext, newRecipe.getIngredientList());
                            //recipeIngreds.removeAllViews();
                            recipeIngreds.setAdapter(ingredientItemAdapter);
                        }

                        //notify the user that the changes have been made to the ingredient
                        Toast.makeText(mContext, "Added", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.show();


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
        if(item.getItemId() == R.id.menuSubmitBTN){

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

            //this might need to be removed once the DB has been implemented!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            int newId = SampleRecipes.recipeList.get(SampleRecipes.recipeList.size() - 1).getRecipeID() + 1;
            newRecipe.setRecipeID(newId);

            SampleRecipes.recipeList.add(newRecipe);

            JSONHelper.exportRecipesToJSON(
                    this,
                    SampleRecipes.recipeList,
                    getString(R.string.recipe_list_to_json)
            );

            Intent returnToRecipes = new Intent(AddRecipeActivity.this, RecipeListActivity.class);
            returnToRecipes.putExtra("TITLE", "My Recipes");
            AddRecipeActivity.this.startActivity(returnToRecipes);

            return true;
        }else{
            return false;
        }

    }
}
