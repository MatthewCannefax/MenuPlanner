package com.matthewcannefax.menuplanner.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.StaticItems.StaticMenu;
import com.matthewcannefax.menuplanner.StaticItems.StaticRecipes;
import com.matthewcannefax.menuplanner.arrayAdapters.IngredientItemAdapter;
import com.matthewcannefax.menuplanner.arrayAdapters.RecipeMenuItemAdapter;
import com.matthewcannefax.menuplanner.model.Enums.GroceryCategory;
import com.matthewcannefax.menuplanner.model.Enums.MeasurementType;
import com.matthewcannefax.menuplanner.model.Enums.RecipeCategory;
import com.matthewcannefax.menuplanner.model.Ingredient;
import com.matthewcannefax.menuplanner.model.Measurement;
import com.matthewcannefax.menuplanner.model.Recipe;
import com.matthewcannefax.menuplanner.utils.ImageHelper;
import com.matthewcannefax.menuplanner.utils.NumberHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

//this class is for editing already existing recipes

public class EditRecipeActivity extends AppCompatActivity {
    //region VARS

    //initialize the objects of the activity
    private EditText recipeName;
    private ImageView recipeIMG;
    private Spinner recipeCat;
    private ListView recipeIngreds;
    private EditText directionsMultiLine;
    private IngredientItemAdapter ingredientItemAdapter;
    private Button addBTN;
    private Context mContext;

    //boolean var to check if the add ingredient button has been added to the list view
    private boolean btnVisible;

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

    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_edit_recipe);

        //set the global context var
        mContext = this;

        //get the recipe item passed from the menulist
        try {
            try {
                oldRecipe = getIntent().getExtras().getParcelable(RecipeMenuItemAdapter.RECIPE_ID);
                newRecipe = oldRecipe;
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
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
        addBTN = setupAddIngredientBTN();

        //set the imgSet var to false as default
        imgSet = false;

        //set text in the textviews
        recipeName.setText(oldRecipe.getName());
        directionsMultiLine.setText(oldRecipe.getDirections());

        //make sure RecipeCategory.ALL is not an option in the categories
        List<RecipeCategory> recipeCats = new ArrayList<>();
        for(RecipeCategory rc : RecipeCategory.values()){
            if(rc != RecipeCategory.ALL){
                recipeCats.add(rc);
            }
        }

        //setup the spinner
        ArrayAdapter<RecipeCategory> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, recipeCats);
        recipeCat.setAdapter(spinnerAdapter);
        recipeCat.setSelection(spinnerAdapter.getPosition(oldRecipe.getCategory()));

        //setup the ingredient listview
        ingredientItemAdapter = new IngredientItemAdapter(this, oldRecipe.getIngredientList());
        recipeIngreds.setAdapter(ingredientItemAdapter);
        recipeIngreds.addFooterView(addBTN);

        //setup the image if it is present
        if(oldRecipe.getImagePath() != null && !oldRecipe.getImagePath().equals("")){
            ImageHelper.setImageViewDrawable(oldRecipe.getImagePath(), this, recipeIMG);
        }

        ImageHelper.setImageViewClickListener(this, recipeIMG, EditRecipeActivity.this);
    }

    //the onResume method fires at initial create as well as on resume
    @Override
    protected void onResume() {
        super.onResume();
        setControlsEnabled(isEditable);
    }

    //this is the longclicklistener for the recipeIngreds listview
    private void listViewClickListener(boolean enabled){
        //a constant instance of the context to be used within the inner methods
        final Context mContext = this;

        //check if editing is enabled
        if(enabled){
        recipeIngreds.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                //set a constant for the location of the ingredient in the ingredientlist
                //this is used in the onclick listener
                final int ingredientPostion = i;

                //the item that is clicked
                final Ingredient item = oldRecipe.getIngredientList().get(ingredientPostion);

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
                ArrayAdapter<GroceryCategory> ingredCatAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, GroceryCategory.getEnumIngredients());

                //set the spinner adpaters
                spMeasure.setAdapter(measureAdapter);
                spCat.setAdapter(ingredCatAdapter);

                //set the type in each spinner
                spMeasure.setSelection(measureAdapter.getPosition(item.getMeasurement().getType()));
                spCat.setSelection(ingredCatAdapter.getPosition(item.getCategory()));

                //fill the editTexts with the appropriate info
                etAmount.setText(String.format(Locale.ENGLISH,"%1.1f", item.getMeasurement().getAmount()));
                etName.setText(item.getName());

                //set the new view as the view for the alertdialog
                builder.setView(editIngredientView);

                //setup the buttons for the alertdialog
                builder.setNegativeButton("Cancel", null);
                builder.setNeutralButton("Remove", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        newRecipe.getIngredientList().remove(ingredientPostion);
                        ingredientItemAdapter.notifyDataSetChanged();
                    }
                });
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //commit the changes to the ingredient in memory only
                        //the change will be made to the database with the submit button
                        newRecipe.getIngredientList().get(ingredientPostion).setMeasurement(
                                new Measurement(
                                        //parse the text to Double
                                        Double.parseDouble(etAmount.getText().toString()),
                                        (MeasurementType) spMeasure.getSelectedItem()
                                )
                        );
                        newRecipe.getIngredientList().get(ingredientPostion).setName(etName.getText().toString());
                        newRecipe.getIngredientList().get(ingredientPostion).setCategory((GroceryCategory)spCat.getSelectedItem());

                        //notify the arrayadapter that the dataset has changed
                        ingredientItemAdapter.notifyDataSetChanged();

                        //notify the user that the changes have been made to the ingredient
                        Toast.makeText(mContext, "Changed", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.show();


                return false;
            }
        });
        }else{
            //nulling the clicklistener to disable it
            recipeIngreds.setOnItemLongClickListener(null);
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

    //this method simply sets up the inflater for the add ingredient button
    private Button setupAddIngredientBTN(){
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.add_ingredient_btn, null);
        return view.findViewById(R.id.addIngredientBTN);
    }

    //this method sets up/nulls the add ingredient button that already exists inside the recipeIngreds listview
    private void addIngredientBTN(boolean enabled) {
        //Constant var for the context to be used inside inner methods
        final Context mContext = this;

        //check if editing is enabled
        if (enabled) {
            //new clicklistener for the existing add ingredient button
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

                    //set up the clicklisteners for the editTexts inside the alertdialog
                    //these clicklisteners will clear the text inside and then null the listener itself
                    clearEditText(etName);
                    clearEditText(etAmount);

                    //setup the default array adapters for the category and measurementtype spinners
                    ArrayAdapter<MeasurementType> measureAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, MeasurementType.values());
                    ArrayAdapter<GroceryCategory> ingredCatAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, GroceryCategory.getEnumIngredients());

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
                            //check that there are values for the name and the amount
                            //also using a custom tryParse method to check that the value for the amount is indeed a double
                            if (!etName.getText().toString().equals("") && !etAmount.getText().toString().equals("") && NumberHelper.tryParseDouble(etAmount.getText().toString())) {
                                //add the new Ingredient to the ingredientList
                                newRecipe.getIngredientList().add(new Ingredient(
                                        etName.getText().toString(),
                                        (GroceryCategory)spCat.getSelectedItem(),
                                        new Measurement(
                                                Double.parseDouble(etAmount.getText().toString()),
                                                (MeasurementType)spMeasure.getSelectedItem()
                                        )
                                ));

                                //notify the arrayadapter that the dataset has changed
                                ingredientItemAdapter.notifyDataSetChanged();

                            } else {
                                //Send the user a Toast to tell them that they need to enter both a name and amount in the edittexts
                                Toast.makeText(mContext, "Please enter a name and amount", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    builder.show();
                }
            });
        } else {
            //null the clicklistener if editing is disabled
            addBTN.setOnClickListener(null);
        }
    }

    //this is the method that disable/enables controls when editing is disabled/enabled
    private void setControlsEnabled(boolean edit){//need to add a way to disable the onlongclicklistener for the listview and the onclicklistener for the addingredient btn

        if (!edit) {
            //setting the focusable allows the edittexts to be active with receiving focus
            //allowing them to be scrollable if needed
            recipeName.setFocusable(false);
            directionsMultiLine.setFocusable(false);

            //disable the category spinner
            recipeCat.setEnabled(false);

            //disable the recipeIngreds listview clicklistener
            listViewClickListener(false);

            //disable the add ingredient button
            addIngredientBTN(false);
        } else {
            //Need to call setFocusable and setFocusableInTouchMode to enable the editTexts to take focus
            recipeName.setFocusable(true);
            directionsMultiLine.setFocusable(true);
            recipeName.setFocusableInTouchMode(true);
            directionsMultiLine.setFocusableInTouchMode(true);

            //enable the spinner
            recipeCat.setEnabled(true);

            //enable the recipeIngreds listview clicklistener
            listViewClickListener(true);

            //enable the add ingredient button
            addIngredientBTN(true);
        }
    }

    //create the menu button in the actionbar (currently only contains the submit option)
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        //add the menu button to add recipes to the recipes list
        MenuInflater menuInflater = getMenuInflater();

        //using the menu layout created specifically for this activity
        menuInflater.inflate(R.menu.add_recipe_menu, menu);

        //currently getting the only item in the menu for this menu item object
        editSubmitBTN = menu.getItem(0);//if other items are added to this menu, this will need to be changed

        //setting the text to "Edit" by default, it will be change on each click
        editSubmitBTN.setTitle("Edit");

        return true;
    }

    //handle the clicks of the menu items
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        //if the submit button is clicked and the recipe is editable
        if(item.getItemId() == R.id.menuSubmitBTN && isEditable){

            //Alert dialog to ask the user if they are sure they want to save the recipe
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Save Recipe?");
            builder.setMessage("Are you sure you want to make changes to " + oldRecipe.getName() + "?");
            builder.setNegativeButton("Cancel", null);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //new recipe object created by the user
                    newRecipe = new Recipe();
                    newRecipe = oldRecipe;
                    newRecipe.setName(recipeName.getText().toString());
                    newRecipe.setDirections(directionsMultiLine.getText().toString());
                    newRecipe.setCategory((RecipeCategory) recipeCat.getSelectedItem());

                    //get the ingredients of the new recipe from the old recipe object
                    newRecipe.setIngredientList(oldRecipe.getIngredientList());

                    //get the image of the new recipe if the image has been set
                    //using the imgSet var to signal whether the image has been set or not!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

                    newRecipe.setImagePath(oldRecipe.getImagePath());

                    //Making the change to the Static list of recipes
                    for(int n = 0; n < StaticRecipes.getRecipeList().size(); n++){
                        if(StaticRecipes.getRecipeList().get(n).getRecipeID() == oldRecipe.getRecipeID()){
                            StaticRecipes.getRecipeList().set(n, newRecipe);
                            break;
                        }
                    }

                    //this method takes the newly edited recipe, finds every instance of that recipe in the menu and makes the necessary changes
                    StaticMenu.editMenuRecipe(newRecipe);

                    //set isEditable to false
                    isEditable = false;

                    //disable the controls
                    setControlsEnabled(false);

                    //change the menu button text back to edit
                    editSubmitBTN.setTitle("Edit");

                    //Save the Static lists to JSON files
                    StaticMenu.saveMenu(mContext);
                    StaticRecipes.saveRecipes(mContext);
                }
            });

            builder.show();

            return true;
        }
        //if the "Edit" button is clicked and the recipe in not Editable
        else if(item.getItemId() == R.id.menuSubmitBTN && !isEditable){

            //change the menu button text to submit
            editSubmitBTN.setTitle("Submit");

            //set isEditable to true
            isEditable = true;

            //enable all the contols
            setControlsEnabled(true);
            return true;
        }else{
            return false;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        newRecipe.setImagePath(ImageHelper.getPhotoTaken(this, requestCode, resultCode, data, recipeIMG));
    }
}
