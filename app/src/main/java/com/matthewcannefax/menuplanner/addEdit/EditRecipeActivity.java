package com.matthewcannefax.menuplanner.addEdit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.android.gms.ads.AdView;
import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.recipe.menuList.MenuListRecyclerAdapter;
import com.matthewcannefax.menuplanner.recipe.RecipeCategory;
import com.matthewcannefax.menuplanner.recipe.Recipe;
import com.matthewcannefax.menuplanner.utils.AdHelper;
import com.matthewcannefax.menuplanner.utils.ImageHelper;
import com.matthewcannefax.menuplanner.utils.NavDrawer;
import com.matthewcannefax.menuplanner.utils.ShareHelper;
import com.matthewcannefax.menuplanner.utils.database.DataSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

//this class is for editing already existing recipes

public class EditRecipeActivity extends AppCompatActivity {
    //region VARS

    //initialize the objects of the activity
    private EditText recipeName;
    private ImageView recipeIMG;
    private Spinner recipeCat;
    private Context mContext;
    private ViewPager viewPager;
    private DataSource mDatasource;
    private DrawerLayout mDrawerLayout;

    //an object for the unedited recipe
    private Recipe oldRecipe;

    //an object for any changes made to the oldRecipe
    private Recipe newRecipe;

    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_edit_recipe);

        //set the global context var
        mContext = this;
        mDatasource = new DataSource(this);
        mDatasource.open();
        //get the recipe item passed from the menulist
        try {
            try {
                oldRecipe = getIntent().getExtras().getParcelable(MenuListRecyclerAdapter.RECIPE_ID);
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
        mDrawerLayout = findViewById(R.id.drawer_layout);

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

        RecipeViewPagerAdapter recipeViewPagerAdapter = new RecipeViewPagerAdapter(this, newRecipe, 0);
        viewPager = findViewById(R.id.ingredient_direction_viewpager);
        viewPager.setAdapter(recipeViewPagerAdapter);

        setUpTabs();

        ListView drawerListView = findViewById(R.id.navList);

        //set up the nav drawer for this activity
        NavDrawer.setupNavDrawer(EditRecipeActivity.this, this, drawerListView);


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

        NavDrawer.setupNavDrawerMenuButton(getSupportActionBar());
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        AdView mAdView = findViewById(R.id.addEditRecipeBanner);

        AdHelper.SetupBannerAd(this, mAdView);
    }

    private void setUpTabs(){
        final Context context = this;
        TabLayout tabLayout = findViewById(R.id.recipe_tab_layout);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        RecipeViewPagerAdapter recipeViewPagerAdapter = new RecipeViewPagerAdapter(context, oldRecipe, 0);
                        viewPager.setAdapter(recipeViewPagerAdapter);
                        break;
                    case 1:
                        RecipeViewPagerAdapter recipeViewPagerAdapter2 = new RecipeViewPagerAdapter(context, oldRecipe, 1);
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
            NavDrawer.navDrawerOptionsItem(mDrawerLayout);
            return true;
        }else if(item.getItemId() == R.id.shareRecipe){
            ShareHelper.sendSingleRecipe(this, oldRecipe.getRecipeID());
            return true;
        }
        else if(item.getItemId() == R.id.help){
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
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
}
