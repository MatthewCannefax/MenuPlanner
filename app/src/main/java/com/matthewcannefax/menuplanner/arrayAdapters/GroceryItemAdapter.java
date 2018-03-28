package com.matthewcannefax.menuplanner.arrayAdapters;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.model.GroceryItem;
import com.matthewcannefax.menuplanner.model.Ingredient;

import org.w3c.dom.Text;

import java.util.List;


public class GroceryItemAdapter extends ArrayAdapter {

    //A list to hold the items of the grocery list
    //I need to try and switch this to Grocery type instead of Ingredient type
    private List<Ingredient> mGroceryItems;
    //A new LayoutInflater object
    private LayoutInflater mInflator;



    //constructor
    public GroceryItemAdapter(@NonNull Context context, @NonNull List objects) {
        super(context, R.layout.grocery_item, objects);

        //set the objects to the groceryitems list
        mGroceryItems = objects;

        //remove water from the grocery list
        for (int i = 0; i < mGroceryItems.size(); i++){
            String name = mGroceryItems.get(i).getName().toUpperCase();
            if (name.equals("WATER")){
                mGroceryItems.remove(i);
            }
        }
        //instantiate the inflater object using the context passed through the constructor
        mInflator = LayoutInflater.from(context);



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

    //this overridden method sets up the grocery item view
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //if the convertview is null, setup the convertview with the inflater passing the layout view grocery item
        if (convertView == null){
            convertView = mInflator.inflate(R.layout.grocery_item, parent, false);//getting a null inflator
        }


        final boolean[] itemChecked = {false};

        //TextView to display the name of the grocery item
        final TextView tvName = convertView.findViewById(R.id.itemNameText);
        final TextView tvMeasurement = convertView.findViewById(R.id.tvMeasurement);
        final TextView tvCategory = convertView.findViewById(R.id.tvCategory);

        //the the grocery item by the postion given in the arguments
        final Ingredient item = mGroceryItems.get(position);

        //set the text of the textviews
        tvName.setText(item.getName());
        tvMeasurement.setText(item.getMeasurement().toString());
        tvCategory.setText(item.getCategory().toString());

        //this click listener strikes through the items of the grocery item view when clicked
        //this signifies that the item has been checked
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (item.getItemChecked()){
                    itemChecked[0] = false;
                    item.setItemChecked(false);
                    tvName.setPaintFlags(0);
                    tvCategory.setPaintFlags(0);
                    tvMeasurement.setPaintFlags(0);
                }else{
                    itemChecked[0] = true;
                    item.setItemChecked(true);
                    tvName.setPaintFlags(tvName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    tvCategory.setPaintFlags(tvCategory.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    tvMeasurement.setPaintFlags(tvMeasurement.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }
            }
        });

        return convertView;
    }
}
