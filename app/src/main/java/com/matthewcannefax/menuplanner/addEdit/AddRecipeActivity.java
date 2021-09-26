package com.matthewcannefax.menuplanner.addEdit;


import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.grocery.GroceryCategory;
import com.matthewcannefax.menuplanner.recipe.Ingredient;
import com.matthewcannefax.menuplanner.recipe.Measurement;
import com.matthewcannefax.menuplanner.recipe.MeasurementType;
import com.matthewcannefax.menuplanner.recipe.recipeList.CookbookFragment;
import com.matthewcannefax.menuplanner.recipe.RecipeCategory;
import com.matthewcannefax.menuplanner.recipe.Recipe;
import com.matthewcannefax.menuplanner.utils.ImageHelper;
import com.matthewcannefax.menuplanner.utils.NumberHelper;
import com.matthewcannefax.menuplanner.utils.navigation.NavDrawer;
import com.matthewcannefax.menuplanner.utils.ShareHelper;
import com.matthewcannefax.menuplanner.utils.database.DataSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class AddRecipeActivity extends AppCompatActivity {

    private EditText recipeName;
    private ImageView recipeIMG;
    private Spinner recipeCat;
    private Recipe newRecipe;
    private RecyclerView recyclerView;
    DataSource mDataSource;
    AddIngredientClickListener addIngredientClickListener;
    DirectionsChangedListener directionsChangedListener;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Context mContext = this;
        setContentView(R.layout.layout_add_edit_recipe);

        addIngredientClickListener = this::addIngredientListener;
        directionsChangedListener = this::directionsChangeListener;

        newRecipe = new Recipe();

        this.setTitle(R.string.add_recipe);

        mDataSource = new DataSource(this);

        //instantiate all the controls in the activity
        recipeName = findViewById(R.id.recipeName);
        recipeIMG = findViewById(R.id.recipeIMG);
        recipeCat = findViewById(R.id.categorySpinner);
        recyclerView = findViewById(R.id.ingredient_direction_recyclerview);
        mDrawerLayout = findViewById(R.id.drawer_layout);

        List<RecipeCategory> recipeCats = new LinkedList<>(Arrays.asList(RecipeCategory.values()));
        recipeCats.remove(0);

        Collections.sort(recipeCats, (recipeCategory, t1) -> recipeCategory.toString().compareTo(t1.toString()));

        //setup the Category Spinner
        ArrayAdapter<RecipeCategory> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, recipeCats);
        recipeCat.setAdapter(spinnerAdapter);

        try {
            //set the default image in the recipeIMG imageView
            String imgPath = getString(R.string.no_img_selected);
            ImageHelper.setImageViewDrawable(imgPath, this, recipeIMG);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        RecipeDetailListAdapter adapter = new RecipeDetailListAdapter(
                new RecipeDetailListRowBuilder(this, newRecipe).build(),
                newRecipe,
                addIngredientClickListener,
                directionsChangedListener);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //use the setImageViewClickListener in the ImageHelper class to set the click event for the image view
        ImageHelper.setImageViewClickListener(this, recipeIMG, AddRecipeActivity.this);

        ListView drawerListView = findViewById(R.id.navList);
        //set up the navigation drawer for this activity using the NavDrawer class and passing context and activity
        NavDrawer.setupNavDrawer(AddRecipeActivity.this, this, drawerListView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        NavDrawer.setupNavDrawerMenuButton(getSupportActionBar());
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
    public boolean onCreateOptionsMenu(Menu menu) {
        //add the menu button to add recipes to the recipes list
        MenuInflater menuInflater = getMenuInflater();

        //using the menu layout created specifically for this activity
        menuInflater.inflate(R.menu.add_recipe_menu, menu);
        MenuItem shareItem = menu.findItem(R.id.shareRecipe);
        shareItem.setVisible(false);
        return true;
    }

    //handle the clicks of the menu items
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //if the submit button is clicked
        //this is where the new recipe is all put together and then added to Recipe list and JSON files
        if (item.getItemId() == R.id.menuSubmitBTN) {

            if (newRecipe.getIngredientList() != null && newRecipe.getIngredientList().size() != 0) {
                //get the name, directions and category from the controls
                newRecipe.setName(recipeName.getText().toString());
//                newRecipe.setDirections(directionsMultiLine.getText().toString());
                newRecipe.setCategory((RecipeCategory) recipeCat.getSelectedItem());

                if (newRecipe.getImagePath() == null || newRecipe.getImagePath().equals("")) {
                    newRecipe.setImagePath(getString(R.string.no_img_selected));
                }

                mDataSource.createRecipe(newRecipe);

                //return to the Recipe list activity
                Intent returnToRecipes = new Intent(AddRecipeActivity.this, CookbookFragment.class);

                //put extra the title of the recipe list activity
                returnToRecipes.putExtra("TITLE", this.getString(R.string.recipe_list_activity_title));
                AddRecipeActivity.this.startActivity(returnToRecipes);
                finish();

                return true;
            } else {
                String message = getString(R.string.at_least_one_ingredient);
//                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
                return true;
            }
        } else if (item.getItemId() == android.R.id.home) {
            NavDrawer.navDrawerOptionsItem(mDrawerLayout);
            return true;
        } else if (item.getItemId() == R.id.help) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.help);
            builder.setMessage(R.string.add_recipe_help);
            builder.setNeutralButton(R.string.ok, null);
            builder.show();
            return true;
        } else {
            return false;
        }

    }

    //Override the OnActivityResult to catch the picture chosen or taken to set as the recipe image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //get the path for the new image and set it to the new recipe object
        newRecipe.setImagePath(ImageHelper.getPhotoTaken(this, requestCode, resultCode, data, recipeIMG));
        ShareHelper.activityResultImportCookbook(this, AddRecipeActivity.this, requestCode, resultCode, data);
    }

    private void addIngredientListener() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
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

                Ingredient ingredient = mDataSource.getSpecificIngredient(charSequence);

                if (ingredient.getCategory() != null && ingredient.getMeasurement().getType() != null) {
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
        builder.setPositiveButton(R.string.ok, (dialogInterface, i) -> {
            //check if the all the inputs are filled in correctly
            if (!etName.getText().toString().equals("") && !etAmount.getText().toString().equals("") && NumberHelper.tryParseDouble(etAmount.getText().toString())) {
                //check if the ingredient list exists for this recipe
                if (newRecipe.getIngredientList() != null) {
                    //add the new Ingredient to the ingredientList
                    Ingredient newIngredient = new Ingredient();
                    newIngredient.setName(etName.getText().toString());
                    newIngredient.setCategory((GroceryCategory) spCat.getSelectedItem());
                    newIngredient.setMeasurement(new Measurement(
                            Double.parseDouble(etAmount.getText().toString()),
                            (MeasurementType) spMeasure.getSelectedItem()
                    ));
                    newRecipe.getIngredientList().add(newIngredient);
                    RecipeDetailListAdapter recyclerAdapter1 = new RecipeDetailListAdapter(new RecipeDetailListRowBuilder(this, newRecipe).build(), newRecipe, addIngredientClickListener, directionsChangedListener);
                    recyclerView.setAdapter(recyclerAdapter1);
                    recyclerView.setLayoutManager(new LinearLayoutManager(this));
                }
                //if the ingredient list does not exist create the new Ingredient and a new Ingredient list
                //then add that to the recipe
                else {
                    //create the new ingredient
                    Ingredient ingredient = new Ingredient(
                            etName.getText().toString(),
                            (GroceryCategory) spCat.getSelectedItem(),
                            new Measurement(
                                    Double.parseDouble(etAmount.getText().toString()),
                                    (MeasurementType) spMeasure.getSelectedItem()
                            ));
                    //create the new ingredient list
                    List<Ingredient> newIngredredients = new ArrayList<>();

                    //add the new ingredient to the the new list
                    newIngredredients.add(ingredient);

                    //set the new list as the list for the new recipe
                    newRecipe.setIngredientList(newIngredredients);


                    //setup the ingredient item adapter for the recipeIngreds listView
//                                IngredientItemAdapter ingredientItemAdapter1 = new IngredientItemAdapter(context, newRecipe.getIngredientList());
//                                listView.setAdapter(ingredientItemAdapter1);

                    RecipeDetailListAdapter recyclerAdapter1 = new RecipeDetailListAdapter(new RecipeDetailListRowBuilder(this, newRecipe).build(), newRecipe, addIngredientClickListener, directionsChangedListener);
                    recyclerView.setAdapter(recyclerAdapter1);
                    recyclerView.setLayoutManager(new LinearLayoutManager(this));
                }
            } else {
                //send a Toast prompting the user to make sure and fill in the alert dialog correctly
                Toast.makeText(this, R.string.enter_name_amount, Toast.LENGTH_SHORT).show();
            }
        });

        builder.show();
    }

    private void directionsChangeListener(String string) {
        if (string.equals(null)) {
            newRecipe.setDirections("");
        } else {
            newRecipe.setDirections(string);
        }
    }
}
