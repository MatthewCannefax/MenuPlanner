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
    List<Ingredient> mGroceryItems;
    //A new LayoutInflater object
    LayoutInflater mInflator;



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
        final TextView tvName = (TextView) convertView.findViewById(R.id.itemNameText);
        TextView tvMeasurement = (TextView) convertView.findViewById(R.id.tvMeasurement);
        TextView tvCategory = (TextView) convertView.findViewById(R.id.tvCategory);

        //the the grocery item by the postion given in the arguments
        final Ingredient item = mGroceryItems.get(position);

        //set the text of the textviews
        tvName.setText(item.getName());
        tvMeasurement.setText(item.getMeasurement().toString());
        tvCategory.setText(item.getCategory().toString());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (item.getItemChecked()){
                    itemChecked[0] = false;
                    item.setItemChecked(false);
                    tvName.setPaintFlags(0);
                }else{
                    itemChecked[0] = true;
                    item.setItemChecked(true);
                    tvName.setPaintFlags(tvName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }
            }
        });


//        convertView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                if (itemChecked){
//                    itemChecked = false;
//                    tvName.setPaintFlags(0);
//                }else{
//                    itemChecked = true;
//                    tvName.setPaintFlags(tvName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//                }
//
//                return false;
//            }
//        });

        //return the now setup view
        return convertView;
    }

}
