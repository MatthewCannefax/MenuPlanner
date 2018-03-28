package com.matthewcannefax.menuplanner.arrayAdapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.model.Enums.GroceryCategory;
import com.matthewcannefax.menuplanner.model.Enums.MeasurementType;
import com.matthewcannefax.menuplanner.model.GroceryItem;
import com.matthewcannefax.menuplanner.model.Ingredient;

import java.util.List;

//this adapter is for adding ingredients to the ingredient list view in the add/edit recipe activity

public class IngredientItemAdapter extends ArrayAdapter {

    //initialize a list of ingredients
    private List<Ingredient> mRecipeIngredients;

    //initialize a layoutinflator
    private LayoutInflater mInflator;

    public IngredientItemAdapter(@NonNull Context context, @NonNull List objects) {
        super(context, R.layout.ingredient_list_item, objects);

        //instantiate the list with the list that is passed through the constructor
        mRecipeIngredients = objects;

        //instantiate the layoutinflator with the context that is passed through the constructor
        mInflator = LayoutInflater.from(context);

    }

    //these two methods are overridden to make sure that the views are not recycled
    //when the views are recycled the click event selects all views that are reused
    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }

    //This overridden method sets up the ingredient item view
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull final ViewGroup parent){

        //if convertView is null, set it up with the inflator passing the layout view ingredient item
        if(convertView == null){
            convertView = mInflator.inflate(R.layout.ingredient_list_item, parent, false);
        }

        //initialize the textviews in the layout
        TextView tvAmount = convertView.findViewById(R.id.tvMeasurement);
        TextView tvName = convertView.findViewById(R.id.tvName);

        //get the specific item in the list
        final Ingredient item = mRecipeIngredients.get(position);

        //set the text in the textviews
        tvName.setText(item.getName());
        tvAmount.setText(item.getMeasurement().toString());

        //return the view
        return convertView;
    }


}
