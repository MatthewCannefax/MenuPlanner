package com.matthewcannefax.menuplanner.arrayAdapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.StaticItems.StaticMenu;
import com.matthewcannefax.menuplanner.activity.EditRecipeActivity;
import com.matthewcannefax.menuplanner.model.Recipe;
import com.matthewcannefax.menuplanner.utils.ImageHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

//This adapter is for displaying the recipes in the menu list activity and the total number of recipes'
//in the recipe list activity

public class RecipeMenuItemAdapter extends ArrayAdapter {

    //A list to hold all the recipe objects
    private List<Recipe> mRecipeItems;
    //initialize a new inflater object
    private LayoutInflater mInflator;

    private Context mContext;

    public static final String RECIPE_ID = "item_id";

    //constructor
    public RecipeMenuItemAdapter(@NonNull Context context, @NonNull List objects) {
        super(context, R.layout.menu_recipe_list_item, objects);

        //instantiate the list of recipes and the inflater object
        mRecipeItems = objects;
        mInflator = LayoutInflater.from(context);
        mContext = context;

    }

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
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {



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
                        Toast.makeText(mContext, recipe.toString() + " removed", Toast.LENGTH_LONG).show();
                        remove(recipe);

                        StaticMenu.saveMenu(mContext);
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
