package com.matthewcannefax.menuplanner.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.SampleData.SampleRecipes;
import com.matthewcannefax.menuplanner.arrayAdapters.IngredientItemAdapter;
import com.matthewcannefax.menuplanner.arrayAdapters.RecipeMenuItemAdapter;
import com.matthewcannefax.menuplanner.model.Enums.RecipeCategory;
import com.matthewcannefax.menuplanner.model.Recipe;

import java.io.IOException;
import java.io.InputStream;

//this class is for editing already existing recipes

public class EditRecipeActivity extends AppCompatActivity {

    //initialize the objects of the activity
    private EditText recipeName;
    private ImageView recipeIMG;
    private Spinner recipeCat;
    private ListView recipeIngreds;
    private EditText directionsMultiLine;

    //boolean object to check if an image has been chosen
    //be careful to only change this var to true at the end of the dialog
    private boolean imgSet;

    //an object for the unedited recipe
    private Recipe oldRecipe;

    //an object for any changes made to the oldRecipe
    private Recipe newRecipe;

    //an object for the menu item
    private MenuItem editSubmitBTN;

    //a boolean var to tell us if the recipe is to be edited
    private boolean isEditable = false;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_edit_recipe);

        //get the recipe item passed from the menulist
        try {
            oldRecipe = getIntent().getExtras().getParcelable(RecipeMenuItemAdapter.RECIPE_ID);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        //make sure the recipe received is not a null object
        if(oldRecipe == null){
            throw new AssertionError("Null recipe received");
        }

        //instantiate all the controls in the activity
        recipeName = findViewById(R.id.recipeName);
        recipeIMG = findViewById(R.id.recipeIMG);
        recipeCat = findViewById(R.id.categorySpinner);
        recipeIngreds = findViewById(R.id.ingredientsListView);
        directionsMultiLine = findViewById(R.id.directionsMultiLine);

        //set the imgSet var to false as default
        imgSet = false;

        recipeName.setText(oldRecipe.getName());
        directionsMultiLine.setText(oldRecipe.getDirections());

        //setup the spinner
        ArrayAdapter<RecipeCategory> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, RecipeCategory.values());
        recipeCat.setAdapter(spinnerAdapter);
        recipeCat.setSelection(spinnerAdapter.getPosition(oldRecipe.getCategory()));

        //setup the ingredient listview
        IngredientItemAdapter ingredientItemAdapter = new IngredientItemAdapter(this, oldRecipe.getIngredientList());
        recipeIngreds.setAdapter(ingredientItemAdapter);

        //setup the image if it is present
        if(oldRecipe.getImagePath() != null && !oldRecipe.getImagePath().equals("")){
            InputStream inputStream = null;

            try {
                String imageFile = oldRecipe.getImagePath();
                inputStream = this.getAssets().open(imageFile);
                Drawable d = Drawable.createFromStream(inputStream, null);
                recipeIMG.setImageDrawable(d);
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    if(inputStream != null){
                        inputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //setControlsEnabled is called inside the OnCreateOptionsMenu method because it always threw a NullPointer
        //inside this method
    }

    private void setControlsEnabled(boolean edit){

        if (!edit) {
            recipeName.setEnabled(false);
            recipeCat.setEnabled(false);
            recipeIngreds.setEnabled(false);
            directionsMultiLine.setEnabled(false);
            for (int i = 0; i < recipeIngreds.getCount(); i++){
                recipeIngreds.getChildAt(i).setEnabled(false);
            }
        } else {
            recipeName.setEnabled(true);
            recipeCat.setEnabled(true);
            recipeIngreds.setEnabled(true);
            directionsMultiLine.setEnabled(true);
            for (int i = 0; i < recipeIngreds.getCount(); i++){
                recipeIngreds.getChildAt(i).setEnabled(true);
            }
        }
    }

    //create the menu button in the actionbar (currently only contains the submit option)
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        //add the menu button to add recipes to the recipes list
        MenuInflater menuInflater = getMenuInflater();

        //using the menu layout created specifically for this activity
        menuInflater.inflate(R.menu.add_recipe_menu, menu);

        editSubmitBTN = menu.getItem(0);//if other items are added to this menu, this will need to be changed

        editSubmitBTN.setTitle("Edit");

        //Had to call this method here because it wouldn't work inside the Oncreate method
        setControlsEnabled(isEditable);

        return true;
    }

    //handle the clicks of the menu items
    @Override
    public boolean onOptionsItemSelected(MenuItem item){


        //if the submit button is clicked
        if(item.getItemId() == R.id.menuSubmitBTN && isEditable){

            //new recipe object created by the user
            newRecipe = new Recipe();
            newRecipe = oldRecipe;
            newRecipe.setName(recipeName.getText().toString());
            newRecipe.setDirections(directionsMultiLine.getText().toString());
            newRecipe.setCategory((RecipeCategory) recipeCat.getSelectedItem());

            //get the ingredients of the new recipe
            newRecipe.setIngredientList(oldRecipe.getIngredientList());//This will need to change!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

            //get the image of the new recipe if the image has been set
            //using the imgSet var to signal whether the image has been set or not

                newRecipe.setImagePath(oldRecipe.getImagePath());


            //make the change in the database
            //This is currently changing the sample data, not the database!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            for(int i = 0; i < SampleRecipes.recipeList.size(); i++){
                if(SampleRecipes.recipeList.get(i).getRecipeID() == oldRecipe.getRecipeID()){
                    SampleRecipes.recipeList.set(i, newRecipe);
                    break;
                }
            }

            isEditable = false;
            setControlsEnabled(false);
            editSubmitBTN.setTitle("Edit");

            return true;
        }else if(item.getItemId() == R.id.menuSubmitBTN && !isEditable){

            editSubmitBTN.setTitle("Submit");
            isEditable = true;
            setControlsEnabled(true);
            return true;
        }else{
            return false;
        }

    }


}
