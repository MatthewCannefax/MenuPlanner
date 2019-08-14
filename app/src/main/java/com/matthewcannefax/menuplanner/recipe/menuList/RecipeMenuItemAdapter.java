package com.matthewcannefax.menuplanner.recipe.menuList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.addEdit.EditRecipeActivity;
import com.matthewcannefax.menuplanner.recipe.RecipeCategory;
import com.matthewcannefax.menuplanner.recipe.Recipe;
import com.matthewcannefax.menuplanner.utils.FilterHelper;
import com.matthewcannefax.menuplanner.utils.ImageHelper;
import com.matthewcannefax.menuplanner.utils.database.DataSource;

import java.util.List;

//This adapter is for displaying the recipes in the menu list activity and the total number of recipes'
//in the recipe list activity

public class RecipeMenuItemAdapter extends ArrayAdapter<Recipe> {

    //A list to hold all the recipe objects
    private List<Recipe> mRecipeItems;
    //initialize a new inflater object
    private final LayoutInflater mInflator;

    private final Context mContext;

    public static final String RECIPE_ID = "item_id";
    private DataSource mDataSource;
    private final ListView lv;
    private final Spinner categorySpinner;
    private final Activity currentActivity;

    //constructor
    public RecipeMenuItemAdapter(@NonNull Context context, @NonNull Activity currentActivity, @NonNull List<Recipe> objects, ListView listView, Spinner catSpinner) {
        super(context, R.layout.menu_recipe_list_item, objects);

        //instantiate the list of recipes and the inflater object
        mRecipeItems = objects;
        mInflator = LayoutInflater.from(context);
        mContext = context;
        mDataSource = new DataSource(context);
        lv = listView;
        categorySpinner = catSpinner;
        this.currentActivity = currentActivity;

    }

    //overridden methods to make sure the views are not recycled
    //when the views are recycled, the click event selects all the views that are reused
    @Override
    public int getViewTypeCount() {
        if (getCount() >= 1) {
            return getCount();
        } else {
            return 1;
        }
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }

    //this overridden method is to setup and return a view for displaying recipes with an image
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        //if the convertview is null set it up with the inflater
        if (convertView == null){
            convertView = mInflator.inflate(R.layout.menu_recipe_list_item, parent, false);
        }

        //the layout contains the name and an image of the recipe
        TextView tvName = convertView.findViewById(R.id.itemNameText);
        ImageView imageView = convertView.findViewById(R.id.imageView);


        //get the recipe item in this object based of the position passed in the arguments
        final Recipe recipe = mRecipeItems.get(position);

        //set the text of the textview with the name of the recipe
        tvName.setText(recipe.getName());

        //use ImageHelper Class to set the drawable object in the imageview
        ImageHelper.setImageViewDrawable(recipe.getImagePath(), mContext, imageView);

        //Normal click to open and view the recipe
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, EditRecipeActivity.class);

                intent.putExtra(RECIPE_ID, recipe);

                mContext.startActivity(intent);
            }
        });


        //Long click to remove the recipe
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Remove from menu?");
                builder.setMessage("Are you sure you want to remove " + recipe.toString() + "?");
                builder.setNegativeButton("Cancel", null);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        Toast.makeText(mContext, recipe.toString() + " removed", Toast.LENGTH_LONG).show();
                        Snackbar.make(currentActivity.findViewById(android.R.id.content), recipe.toString() + " removed", Snackbar.LENGTH_LONG).show();
                        mDataSource.removeMenuItem(recipe.getRecipeID());
                        mRecipeItems = mDataSource.getAllMenuRecipes();
                        lv.setAdapter(new RecipeMenuItemAdapter(mContext, currentActivity, mDataSource.getAllMenuRecipes(), lv, categorySpinner));
                        ArrayAdapter<RecipeCategory> rcAdapter = new ArrayAdapter<RecipeCategory>(mContext, R.layout.category_spinner_item, FilterHelper.getRecipeCategoriesUsed(mContext));
                        categorySpinner.setAdapter(rcAdapter);
                    }
                });
                builder.show();
                return true;
            }
        });

        //return the the new a set up view
        return convertView;
    }


}
