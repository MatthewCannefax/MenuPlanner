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
    List<Ingredient> mRecipeIngredients;

    //initialize a layoutinflator
    LayoutInflater mInflator;

    public IngredientItemAdapter(@NonNull Context context, @NonNull List objects) {
        super(context, R.layout.ingredient_list_item, objects);

        //instantiate the list with the list that is passed through the constructor
        mRecipeIngredients = objects;

        //instantiate the layoutinflator with the context that is passed through the constructor
        mInflator = LayoutInflater.from(context);

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
        TextView tvAmount = (TextView)convertView.findViewById(R.id.tvMeasurement);
        TextView tvName = (TextView)convertView.findViewById(R.id.tvName);

        //get the specific item in the list
        final Ingredient item = mRecipeIngredients.get(position);

        //set the text in the textviews
        tvName.setText(item.getName());
        tvAmount.setText(item.getMeasurement().toString());

        //Long click to edit the ingredient
        //Long clicking the editable view will delete the ingredient
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

//                Toast.makeText(view.getContext(), "Edit Ingredient", Toast.LENGTH_SHORT).show();

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Edit Ingredient");
                View editIngredientView = LayoutInflater.from(getContext()).inflate(R.layout.add_ingredient_item, (ViewGroup)view.findViewById(android.R.id.content), false);
                final EditText etAmount = (EditText)editIngredientView.findViewById(R.id.amountText);
                final Spinner spMeasure = (Spinner)editIngredientView.findViewById(R.id.amountSpinner);
                final EditText etName = (EditText)editIngredientView.findViewById(R.id.ingredientName);
                final Spinner spCat = (Spinner)editIngredientView.findViewById(R.id.categorySpinner);

                ArrayAdapter<MeasurementType> measureAdapter = new ArrayAdapter<MeasurementType>(getContext(), android.R.layout.simple_spinner_item, MeasurementType.values());
                ArrayAdapter<GroceryCategory> ingredCatAdapter = new ArrayAdapter<GroceryCategory>(getContext(), android.R.layout.simple_spinner_item, GroceryCategory.values());

                spMeasure.setAdapter(measureAdapter);
                spCat.setAdapter(ingredCatAdapter);

                spMeasure.setSelection(measureAdapter.getPosition(item.getMeasurement().getType()));
                spCat.setSelection(ingredCatAdapter.getPosition(item.getCategory()));

                etAmount.setText(Double.toString(item.getMeasurement().getAmount()));
                etName.setText(item.getName());


                builder.setView(editIngredientView);

                builder.setNegativeButton("Cancel", null);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getContext(), "Changed", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.show();


                return false;
            }
        });

        //return the view
        return convertView;
    }
}
