package com.matthewcannefax.menuplanner.activity;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.SampleData.SampleRecipes;
import com.matthewcannefax.menuplanner.arrayAdapters.IngredientItemAdapter;
import com.matthewcannefax.menuplanner.model.Enums.GroceryCategory;
import com.matthewcannefax.menuplanner.model.Enums.MeasurementType;
import com.matthewcannefax.menuplanner.model.Enums.RecipeCategory;
import com.matthewcannefax.menuplanner.model.Ingredient;
import com.matthewcannefax.menuplanner.model.Measurement;
import com.matthewcannefax.menuplanner.model.Recipe;

import java.util.ArrayList;
import java.util.List;

public class AddRecipeActivity extends AppCompatActivity{

    //initialize the objects of the activity
    private EditText recipeName;
    private ImageView recipeIMG;
    private Spinner recipeCat;
    private ListView recipeIngreds;
    private EditText directionsMultiLine;

    //boolean object to check if an image has been chosen
    //be careful to only change this var to true at the end of the dialog
    private boolean imgSet;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_edit_recipe);



        //instantiate all the controls in the activity
        recipeName = (EditText)findViewById(R.id.recipeName);
        recipeIMG = (ImageView)findViewById(R.id.recipeIMG);
        recipeCat = (Spinner)findViewById(R.id.categorySpinner);
        recipeIngreds = (ListView)findViewById(R.id.ingredientsListView);
        directionsMultiLine = (EditText)findViewById(R.id.directionsMultiLine);

        //setup the Category Spinner
        ArrayAdapter<RecipeCategory> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, RecipeCategory.values());
        recipeCat.setAdapter(spinnerAdapter);

        //set the imgSet var to false as default
        imgSet = false;
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

            //new recipe object created by the user
            Recipe newRecipe = new Recipe();
            newRecipe.setName(recipeName.getText().toString());
            newRecipe.setDirections(directionsMultiLine.getText().toString());
            newRecipe.setCategory((RecipeCategory) recipeCat.getSelectedItem());

            //get the ingredients of the new recipe
            Ingredient ingredient = new Ingredient("Poop", GroceryCategory.OTHER, new Measurement(1.0, MeasurementType.BAG));//This will all need to go away!!!!!!!!!!!!!!!!
            List<Ingredient> ingredientList = new ArrayList<>();
            ingredientList.add(ingredient);
            newRecipe.setIngredientList(ingredientList);

            //sample image for testing!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            newRecipe.setImagePath("chickenNoodle.jpg");

            //get the image of the new recipe if the image has been set
            //using the imgSet var to signal whether the image has been set or not
            if(imgSet){
                //get the new image from the imageview and store it in the assets package
            }

            SampleRecipes.recipeList.add(newRecipe);

            return true;
        }else{
            return false;
        }

    }
}
