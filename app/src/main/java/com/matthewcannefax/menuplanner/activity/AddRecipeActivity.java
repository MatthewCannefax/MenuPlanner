package com.matthewcannefax.menuplanner.activity;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.StaticItems.StaticRecipes;
import com.matthewcannefax.menuplanner.arrayAdapters.ButtonArrayAdapter;
import com.matthewcannefax.menuplanner.arrayAdapters.IngredientItemAdapter;
import com.matthewcannefax.menuplanner.arrayAdapters.RecipeViewPagerAdapter;
import com.matthewcannefax.menuplanner.model.Enums.GroceryCategory;
import com.matthewcannefax.menuplanner.model.Enums.MeasurementType;
import com.matthewcannefax.menuplanner.model.Enums.RecipeCategory;
import com.matthewcannefax.menuplanner.model.Ingredient;
import com.matthewcannefax.menuplanner.model.Measurement;
import com.matthewcannefax.menuplanner.model.Recipe;
import com.matthewcannefax.menuplanner.utils.AdHelper;
import com.matthewcannefax.menuplanner.utils.ImageHelper;
import com.matthewcannefax.menuplanner.utils.NavDrawer;
import com.matthewcannefax.menuplanner.utils.NumberHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AddRecipeActivity extends AppCompatActivity{

    //region Class Vars
    //initialize the objects of the activity
    private EditText recipeName;
    private ImageView recipeIMG;
    private Spinner recipeCat;

    private IngredientItemAdapter ingredientItemAdapter;

    private Recipe newRecipe;

    private ViewPager viewPager;

    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

//        Context mContext = this;
        setContentView(R.layout.add_edit_recipe);

        newRecipe = new Recipe();

        //instantiate all the controls in the activity
        recipeName = findViewById(R.id.recipeName);
        recipeIMG = findViewById(R.id.recipeIMG);
        recipeCat = findViewById(R.id.categorySpinner);

        //make sure RecipeCategory.ALL is not an option in the spinner
        List<RecipeCategory> recipeCats = new ArrayList<>();
        for (RecipeCategory rc : RecipeCategory.values()){
            if(rc != RecipeCategory.ALL){
                recipeCats.add(rc);
            }
        }

        Collections.sort(recipeCats, new Comparator<RecipeCategory>() {
            @Override
            public int compare(RecipeCategory recipeCategory, RecipeCategory t1) {
                 return recipeCategory.toString().compareTo(t1.toString());
            }
        });

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

        //use the clearEditText method to setup a on focus listener to clear the text on focus
        //and then null the listener so it doesn't happen again
        clearEditText(recipeName);

        //use the setImageViewClickListener in the ImageHelper class to set the click event for the image view
        ImageHelper.setImageViewClickListener(this, recipeIMG, AddRecipeActivity.this);

        RecipeViewPagerAdapter recipeViewPagerAdapter = new RecipeViewPagerAdapter(this, newRecipe, 0);
        viewPager = findViewById(R.id.ingredient_direction_viewpager);
        viewPager.setAdapter(recipeViewPagerAdapter);

        setupTabs();

        ListView drawerListView = findViewById(R.id.navList);

        //set up the navigation drawer for this activity using the NavDrawer class and passing context and activity
        NavDrawer.setupNavDrawer(AddRecipeActivity.this, this, drawerListView);

        AdView mAdView = findViewById(R.id.addEditRecipeBanner);

        AdHelper.SetupBannerAd(this, mAdView);

    }

    private void setupTabs(){

        final Context context = this;
        TabLayout tabLayout = findViewById(R.id.recipe_tab_layout);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        RecipeViewPagerAdapter recipeViewPagerAdapter = new RecipeViewPagerAdapter(context, newRecipe, 0);
                        viewPager.setAdapter(recipeViewPagerAdapter);
                        break;
                    case 1:
                        RecipeViewPagerAdapter recipeViewPagerAdapter2 = new RecipeViewPagerAdapter(context, newRecipe, 1);
                        viewPager.setAdapter(recipeViewPagerAdapter2);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

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

            if (newRecipe.getIngredientList() != null && newRecipe.getIngredientList().size() != 0) {
                //get the name, directions and category from the controls
                newRecipe.setName(recipeName.getText().toString());
//                newRecipe.setDirections(directionsMultiLine.getText().toString());
                newRecipe.setCategory((RecipeCategory) recipeCat.getSelectedItem());


                if(newRecipe.getImagePath() == null || newRecipe.getImagePath().equals("")){
                    newRecipe.setImagePath(getString(R.string.no_img_selected));
                }

                //add the new recipe to the static recipe list
                //this call also assigns a recipeID and saves the static list to JSON
                StaticRecipes.addNewRecipe(newRecipe, this);

                //return to the Recipe list activity
                Intent returnToRecipes = new Intent(AddRecipeActivity.this, RecipeListActivity.class);

                //put extra the title of the recipe list activity
                returnToRecipes.putExtra("TITLE", this.getString(R.string.recipe_list_activity_title));
                AddRecipeActivity.this.startActivity(returnToRecipes);
                finish();

                return true;
            } else {
                String message = getString(R.string.at_least_one_ingredient);
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                return true;
            }
        }else{
            return false;
        }

    }

    //Override the OnActivityResult to catch the picture chosen or taken to set as the recipe image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //get the path for the new image and set it to the new recipe object
        newRecipe.setImagePath(ImageHelper.getPhotoTaken(this, requestCode, resultCode, data, recipeIMG));

    }
}
