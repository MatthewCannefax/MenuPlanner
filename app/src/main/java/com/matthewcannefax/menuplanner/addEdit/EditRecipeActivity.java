package com.matthewcannefax.menuplanner.addEdit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.matthewcannefax.menuplanner.DrawerActivity;
import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.grocery.GroceryCategory;
import com.matthewcannefax.menuplanner.recipe.Ingredient;
import com.matthewcannefax.menuplanner.recipe.Measurement;
import com.matthewcannefax.menuplanner.recipe.MeasurementType;
import com.matthewcannefax.menuplanner.recipe.RecipeCategory;
import com.matthewcannefax.menuplanner.recipe.Recipe;
import com.matthewcannefax.menuplanner.utils.ImageHelper;
import com.matthewcannefax.menuplanner.utils.NumberHelper;
import com.matthewcannefax.menuplanner.utils.ShareHelper;
import com.matthewcannefax.menuplanner.utils.database.DataSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

//this class is for editing already existing recipes

public class EditRecipeActivity extends DrawerActivity {
    //region VARS

    //initialize the objects of the activity
    private EditText recipeName;
    private ImageView recipeIMG;
    private Spinner recipeCat;
    private DataSource mDatasource;
    private RecyclerView recyclerView;
    private boolean areDirectionsChanged = false;
    private String newDirections;
    private RecipeDetailListAdapter adapter;

    //an object for the unedited recipe
    private Recipe oldRecipe;

    //an object for any changes made to the oldRecipe
    private Recipe newRecipe;

    public static final String RECIPE_ID = "item_id";

    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mDatasource = new DataSource(this);
        mDatasource.open();
        //get the recipe item passed from the menulist
        try {
            try {
                oldRecipe = getIntent().getExtras().getParcelable(RECIPE_ID);
                newRecipe = oldRecipe;
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        //make sure the recipe received is not a null object
        if(oldRecipe == null){
            throw new AssertionError(getString(R.string.null_recipe_received));
        }

        //instantiate all the controls in the activity
        recipeName = findViewById(R.id.recipeName);
        recipeIMG = findViewById(R.id.recipeIMG);
        recipeCat = findViewById(R.id.categorySpinner);
        recyclerView = findViewById(R.id.ingredient_direction_recyclerview);

        //set text in the textviews
        recipeName.setText(oldRecipe.getName());

        //make sure RecipeCategory.ALL is not an option in the categories
        List<RecipeCategory> recipeCats = new LinkedList<>(Arrays.asList(RecipeCategory.values()));
        recipeCats.remove(0);

        Collections.sort(recipeCats, new Comparator<RecipeCategory>() {
            @Override
            public int compare(RecipeCategory recipeCategory, RecipeCategory t1) {
                return recipeCategory.toString().compareTo(t1.toString());
            }
        });

        //setup the spinner
        ArrayAdapter<RecipeCategory> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, recipeCats);
        recipeCat.setAdapter(spinnerAdapter);
        recipeCat.setSelection(spinnerAdapter.getPosition(oldRecipe.getCategory()));

        //setup the image if it is present
        if(oldRecipe.getImagePath() != null && !oldRecipe.getImagePath().equals("")){
            ImageHelper.setImageViewDrawable(oldRecipe.getImagePath(), this, recipeIMG);
        }else{
            ImageHelper.setImageViewDrawable("", this, recipeIMG);
        }
        ImageHelper.setImageViewClickListener(this, recipeIMG, EditRecipeActivity.this);
        adapter = new RecipeDetailListAdapter(this::clickAddIngredientButton, this::setAreDirectionsChanged, this::longClickEditIngredient);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.submitList(new RecipeDetailListRowBuilder(this, oldRecipe).build());
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_add_edit_recipe;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDatasource.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDatasource.open();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

//        AdView mAdView = findViewById(R.id.addEditRecipeBanner);
//
//        AdHelper.SetupBannerAd(this, mAdView);
    }

    //create the menu button in the actionbar (currently only contains the submit option)
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        //add the menu button to add recipes to the recipes list
        MenuInflater menuInflater = getMenuInflater();

        //using the menu layout created specifically for this activity
        menuInflater.inflate(R.menu.add_recipe_menu, menu);

        //currently getting the only item in the menu for this menu item object
        MenuItem editSubmitBTN = menu.getItem(0);

        //setting the text to "Edit" by default, it will be change on each click
        editSubmitBTN.setTitle(getString(R.string.submit));

        return true;
    }

    //handle the clicks of the menu items
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        //if the submit button is clicked and the recipe is editable
        if(item.getItemId() == R.id.menuSubmitBTN) {

            //check if there are ingredients in the ingredient list
            if (oldRecipe.getIngredientList() != null && oldRecipe.getIngredientList().size() != 0) {
                //Alert dialog to ask the user if they are sure they want to save the recipe
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.save_recipe_question);
                builder.setMessage(getString(R.string.are_you_sure_change) + oldRecipe.getName() + getString(R.string.question_mark));
                builder.setNegativeButton(R.string.cancel, null);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //new recipe object created by the user
                        newRecipe = new Recipe();
                        newRecipe = oldRecipe;
                        newRecipe.setName(recipeName.getText().toString());
//                        newRecipe.setDirections(directionsMultiLine.getText().toString());
                        newRecipe.setCategory((RecipeCategory) recipeCat.getSelectedItem());

                        //get the ingredients of the new recipe from the old recipe object
                        newRecipe.setIngredientList(oldRecipe.getIngredientList());

                        newRecipe.setImagePath(oldRecipe.getImagePath());

                        if (areDirectionsChanged) {
                            newRecipe.setDirections(newDirections);
                        }

                        //update the recipe in the database
                        mDatasource.updateRecipe(newRecipe);
                    }
                });

                builder.show();

                return true;
            } else {
                String message = getString(R.string.at_least_one_ingredient);
//                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
                return true;
            }
        }
        else if(item.getItemId() == android.R.id.home) {
            navDrawerOptionsItem();
            return true;
        }else if(item.getItemId() == R.id.shareRecipe){
            ShareHelper.sendSingleRecipe(this, oldRecipe.getRecipeID());
            return true;
        }
        else if(item.getItemId() == R.id.help){
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
            builder.setTitle("Help");
            builder.setMessage(R.string.edit_recipe_help);
            builder.setNeutralButton(R.string.ok, null);
            builder.show();
            return true;
        }
        else{
            return false;
        }

    }

    //Override the onActivityResult to catch the image chosen or taken to set as the image for the edited recipe
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //get the path of the new image and set to the newRecipe object
        newRecipe.setImagePath(ImageHelper.getPhotoTaken(this, requestCode, resultCode, data, recipeIMG));
        ShareHelper.activityResultImportCookbook(this, EditRecipeActivity.this, requestCode, resultCode, data);
    }

    private void clickAddIngredientButton() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.add_ingredient_dialog_title));

        View editIngredientView = LayoutInflater.from(
                this).inflate(
                        R.layout.add_ingredient_item, findViewById(android.R.id.content), false);

        //controls inside the view
        final EditText etAmount = editIngredientView.findViewById(R.id.amountText);
        final Spinner spMeasure = editIngredientView.findViewById(R.id.amountSpinner);
        final EditText etName = editIngredientView.findViewById(R.id.ingredientName);
        final Spinner spCat = editIngredientView.findViewById(R.id.categorySpinner);

        ArrayAdapter<MeasurementType> measureAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, MeasurementType.getEnum());
        final ArrayAdapter<GroceryCategory> ingredCatAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, GroceryCategory.getEnumIngredients());

        //set the spinner adpaters
        spMeasure.setAdapter(measureAdapter);
        spCat.setAdapter(ingredCatAdapter);

        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String s = charSequence.toString();

                Ingredient ingredient = mDatasource.getSpecificIngredient(charSequence);

                if(ingredient.getCategory() != null && ingredient.getMeasurement().getType() != null){
                    spCat.setSelection(GroceryCategory.getCatPosition(ingredient.getCategory()));
                    spMeasure.setSelection(MeasurementType.getOrdinal(ingredient.getMeasurement().getType()));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        builder.setView(editIngredientView);

        //setup the buttons for the alertdialog
        builder.setNegativeButton(R.string.cancel, null);
        final AddIngredientClickListener addIngredientClickListener = this::clickAddIngredientButton;
        final DirectionsChangedListener directionsChangedListener = this::setAreDirectionsChanged;
        builder.setPositiveButton(R.string.ok, (dialogInterface, i) -> {
            //check if the all the inputs are filled in correctly
            if (!etName.getText().toString().equals("") && !etAmount.getText().toString().equals("") && NumberHelper.tryParseDouble(etAmount.getText().toString())) {
                //check if the ingredient list exists for this recipe
                if(newRecipe.getIngredientList() != null){
                    //add the new Ingredient to the ingredientList
                    Ingredient newIngredient = new Ingredient();
                    newIngredient.setName(etName.getText().toString());
                    newIngredient.setCategory((GroceryCategory)spCat.getSelectedItem());
                    newIngredient.setMeasurement(new Measurement(
                            Double.parseDouble(etAmount.getText().toString()),
                            (MeasurementType)spMeasure.getSelectedItem()
                    ));
                    newRecipe.getIngredientList().add(newIngredient);
                    adapter.submitList(new RecipeDetailListRowBuilder(this, newRecipe).build());
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
                    adapter.submitList(new RecipeDetailListRowBuilder(this, newRecipe).build());
                }
            }else{
                //send a Toast prompting the user to make sure and fill in the alert dialog correctly
                Toast.makeText(this, R.string.enter_name_amount, Toast.LENGTH_SHORT).show();
            }
        });

        builder.show();
    }

    private void setAreDirectionsChanged(String string) {
        if (oldRecipe.getDirections() == null) {
            if (!string.equals("")) {
                areDirectionsChanged = true;
                newDirections = string;
            }
        } else {
            newDirections = string;
            areDirectionsChanged = (!oldRecipe.getDirections().equals(string));
        }
    }

    private boolean longClickEditIngredient(Ingredient clickedIngredient) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(R.string.edit_ingredient);

        View editIngredientView = LayoutInflater.from(this).inflate(R.layout.add_ingredient_item, findViewById(android.R.id.content), false);

        final EditText etAmount = editIngredientView.findViewById(R.id.amountText);
        final Spinner spMeasure = editIngredientView.findViewById(R.id.amountSpinner);
        final EditText etName = editIngredientView.findViewById(R.id.ingredientName);
        final Spinner spCat = editIngredientView.findViewById(R.id.categorySpinner);

         ArrayAdapter<MeasurementType> measureAdapter = new ArrayAdapter<>(
                 this,
                 android.R.layout.simple_spinner_item,
                 MeasurementType.values());
         ArrayAdapter<GroceryCategory> ingredientCategoryAdapter = new ArrayAdapter<>(
                 this,
                 android.R.layout.simple_spinner_item,
                 GroceryCategory.getEnumIngredients());
        etAmount.setText(String.format(Locale.US, "%s", clickedIngredient.getMeasurement().getAmount()));
        etName.setText(clickedIngredient.getName());
        spMeasure.setAdapter(measureAdapter);
        spCat.setAdapter(ingredientCategoryAdapter);
        spCat.setSelection(GroceryCategory.getCatPosition(clickedIngredient.getCategory()));
        spMeasure.setSelection(clickedIngredient.getMeasurement().getType().ordinal());

        builder.setView(editIngredientView)
                .setNegativeButton(getString(R.string.cancel), null)
                .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                    int index = newRecipe.getIngredientList().indexOf(clickedIngredient);
                    newRecipe.getIngredientList().get(index).setName(etName.getText().toString());
                    newRecipe.getIngredientList().get(index).setCategory((GroceryCategory) spCat.getSelectedItem());
                    newRecipe.getIngredientList().get(index).setMeasurement(new Measurement(Double.parseDouble(
                            etAmount.getText().toString()),
                            (MeasurementType) spMeasure.getSelectedItem()));
                    adapter.submitList(new RecipeDetailListRowBuilder(this, newRecipe).build());
                })
                .setNeutralButton(R.string.delete, (dialogInterface, i) -> {
                    newRecipe.getIngredientList().remove(clickedIngredient);
                    adapter.submitList(new RecipeDetailListRowBuilder(this, newRecipe).build());
                });
        builder.show();
        return false;
    }
}
