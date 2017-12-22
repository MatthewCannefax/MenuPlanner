package com.matthewcannefax.menuplanner.arrayAdapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.activity.EditRecipeActivity;
import com.matthewcannefax.menuplanner.model.Recipe;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;



public class RecipeListItemAdapter extends ArrayAdapter {
    //A list to hold all the recipe objects
    private List<Recipe> mRecipeItems;
    //initialize a new inflater object
    private LayoutInflater mInflator;

    private Context mContext;

    public static final String RECIPE_ID = "item_id";

    //constructor
    public RecipeListItemAdapter(@NonNull Context context, @NonNull List objects) {
        super(context, R.layout.menu_recipe_list_item, objects);

        //instantiate the list of recipes and the inflater object
        mRecipeItems = objects;
        mInflator = LayoutInflater.from(context);
        mContext = context;

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
        TextView tvName = (TextView) convertView.findViewById(R.id.itemNameText);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);


        //get the recipe item in this object based of the position passed in the arguments
        final Recipe recipe = mRecipeItems.get(position);

        //set the text of the textview with the name of the recipe
        tvName.setText(recipe.getName());

        //open an input stream here.  It is opened here so that it can be closed in the finally clause
        InputStream inputStream = null;

        //in this try/catch I am setting up the image in the view
        try {
            //get the name of the image
            String imageFile = recipe.getImagePath();
            //setup the input stream with the image in the assets folder
            inputStream = getContext().getAssets().open(imageFile);
            //make the image from the assets folder a drawable object
            Drawable d = Drawable.createFromStream(inputStream, null);
            //set the drawable object as the image in the imageview
            imageView.setImageDrawable(d);
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                if (inputStream != null){
                    //close the input stream
                    inputStream.close();
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }

        //created this constant var to carry the color changes to the background
        final View newView = convertView;

        //Normal click to open and view the recipe
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(recipe.isItemChecked()){
                    recipe.setItemChecked(false);
                    newView.setBackgroundColor(Color.TRANSPARENT);
                }else{
                    recipe.setItemChecked(true);
                    newView.setBackgroundColor(Color.LTGRAY);
                }
            }
        });


        //Long click to remove the recipe
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intent = new Intent(mContext, EditRecipeActivity.class);

                intent.putExtra(RECIPE_ID, recipe);

                mContext.startActivity(intent);
                return true;
            }
        });


        //return the the new a set up view
        return newView;
    }
}
