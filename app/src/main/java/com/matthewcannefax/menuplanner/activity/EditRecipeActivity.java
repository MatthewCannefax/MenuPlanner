package com.matthewcannefax.menuplanner.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.matthewcannefax.menuplanner.utils.NumberHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

//this class is for editing already existing recipes

public class EditRecipeActivity extends AppCompatActivity {

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


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_edit_recipe);

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

        recipeName.setText(R.string.new_recipe_name_box);

        //set the imgSet var to false as default
        imgSet = false;

        recipeName.setText(oldRecipe.getName());
        directionsMultiLine.setText(oldRecipe.getDirections());

        //setup the spinner
        ArrayAdapter<RecipeCategory> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, RecipeCategory.values());
        recipeCat.setAdapter(spinnerAdapter);
        recipeCat.setSelection(spinnerAdapter.getPosition(oldRecipe.getCategory()));

        //setup the ingredient listview
        ingredientItemAdapter = new IngredientItemAdapter(this, oldRecipe.getIngredientList());
        recipeIngreds.setAdapter(ingredientItemAdapter);
        recipeIngreds.addFooterView(addBTN);

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

        final Context mContext = this;

        //setControlsEnabled is called inside the OnCreateOptionsMenu method because it always threw a NullPointer
        //inside this method
    }

    private void listViewClickListener(boolean enabled){
        final Context mContext = this;
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
                ArrayAdapter<GroceryCategory> ingredCatAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, GroceryCategory.values());

                //set the spinner adpaters
                spMeasure.setAdapter(measureAdapter);
                spCat.setAdapter(ingredCatAdapter);

                //set the type in each spinner
                spMeasure.setSelection(measureAdapter.getPosition(item.getMeasurement().getType()));
                spCat.setSelection(ingredCatAdapter.getPosition(item.getCategory()));

                //fill the editTexts with the appropriate info
//                etAmount.setText(Double.toString(item.getMeasurement().getAmount()));
                etAmount.setText(String.format(Locale.ENGLISH,"%1.1f", item.getMeasurement().getAmount()));
                etName.setText(item.getName());

                //set the new view as the view for the alertdialog
                builder.setView(editIngredientView);

                //setup the buttons for the alertdialog
                builder.setNegativeButton("Cancel", null);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //commit the changes to the ingredient in memory only
                        //the change will be made to the database with the submit button
                        newRecipe.getIngredientList().get(ingredientPostion).setMeasurement(
                                new Measurement(
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
            recipeIngreds.setOnItemLongClickListener(null);
        }
    }

    private void clearEditText(final EditText editText){
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                editText.setText("");
                editText.setOnFocusChangeListener(null);
            }
        });
    }

    private Button setupAddIngredientBTN(){
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.add_ingredient_btn, null);
        return view.findViewById(R.id.addIngredientBTN);
    }

    private void addIngredientBTN(boolean enabled) {
        final Context mContext = this;
        //add a button at the end of the listview to allow the user to add more ingredients to the recipe
//        LayoutInflater inflater = LayoutInflater.from(this);
//        View view = inflater.inflate(R.layout.add_ingredient_btn, null);
//        addBTN = view.findViewById(R.id.addIngredientBTN);
//        recipeIngreds.addFooterView(view);


        if (enabled) {
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

                    clearEditText(etName);
                    clearEditText(etAmount);

                    //setup the default array adapters for the category and measurementtype spinners
                    ArrayAdapter<MeasurementType> measureAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, MeasurementType.values());
                    ArrayAdapter<GroceryCategory> ingredCatAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, GroceryCategory.values());

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
                                Toast.makeText(mContext, "Please enter a name and amount", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    builder.show();


                }
            });
        } else {
            addBTN.setOnClickListener(null);
        }
    }

    private void setControlsEnabled(boolean edit){//need to add a way to disable the onlongclicklistener for the listview and the onclicklistener for the addingredient btn

        if (!edit) {
            recipeName.setFocusable(false);
            directionsMultiLine.setFocusable(false);
            recipeCat.setEnabled(false);
            listViewClickListener(false);
            addIngredientBTN(false);
        } else {
            recipeName.setFocusable(true);
            directionsMultiLine.setFocusable(true);
            recipeName.setFocusableInTouchMode(true);
            directionsMultiLine.setFocusableInTouchMode(true);
            recipeCat.setEnabled(true);

            listViewClickListener(true);
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

                    //get the ingredients of the new recipe
                    newRecipe.setIngredientList(oldRecipe.getIngredientList());//This will need to change!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

                    //get the image of the new recipe if the image has been set
                    //using the imgSet var to signal whether the image has been set or not

                    newRecipe.setImagePath(oldRecipe.getImagePath());


                    //make the change in the database
                    //This is currently changing the sample data, not the database!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    for(int n = 0; n < StaticRecipes.getRecipeList().size(); n++){
                        if(StaticRecipes.getRecipeList().get(n).getRecipeID() == oldRecipe.getRecipeID()){
                            StaticRecipes.getRecipeList().set(n, newRecipe);
                            break;
                        }
                    }
                    StaticMenu.editMenuRecipe(newRecipe);

                    isEditable = false;
                    setControlsEnabled(false);
                    editSubmitBTN.setTitle("Edit");

                    StaticMenu.saveMenu(mContext);
                    StaticRecipes.saveRecipes(mContext);
                }
            });

            builder.show();

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
