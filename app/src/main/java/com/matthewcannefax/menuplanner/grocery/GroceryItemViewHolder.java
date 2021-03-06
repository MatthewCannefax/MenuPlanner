package com.matthewcannefax.menuplanner.grocery;

import android.content.Context;
import android.graphics.Paint;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.recipe.Ingredient;

public class GroceryItemViewHolder extends GroceryViewHolder {

    Context mContext;

    //Views
    CheckBox mGroceryCheckBox;
    TextView mMeasurement;
    TextView tvName;
    ConstraintLayout mGrocerySection;
    GroceryClickListener groceryClickListener;


    public GroceryItemViewHolder(View itemView, GroceryClickListener groceryClickListener) {
        super(itemView);
        mContext = itemView.getContext();
        mGroceryCheckBox = itemView.findViewById(R.id.groceryCheckBox);
        mMeasurement = itemView.findViewById(R.id.tvMeasurement);
        tvName = itemView.findViewById(R.id.tvName);
        mGrocerySection = itemView.findViewById(R.id.groceryItemSection);
        this.groceryClickListener = groceryClickListener;
    }

    private void changeView(boolean isChecked) {
        mGroceryCheckBox.setChecked(isChecked);
        if (isChecked) {
            mMeasurement.setPaintFlags(mMeasurement.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            tvName.setPaintFlags(tvName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            mMeasurement.setPaintFlags(0);
            tvName.setPaintFlags(0);
        }
    }

    @Override
    public void bind(GroceryRow item) {
        bind((GroceryItemRow) item);
    }

    private void bind(GroceryItemRow item) {
        final GroceryViewChangeListener groceryViewChangeListener = this::changeView;
        mGrocerySection.setOnClickListener(view -> groceryClickListener.checkGroceryItem(item.getGroceryItem().getIngredientID(), groceryViewChangeListener));

        mMeasurement.setText(item.getGroceryItem().getMeasurement().toString());
        tvName.setText(item.getGroceryItem().getName());

        if (item.getGroceryItem().getItemChecked()) {
            mGroceryCheckBox.setChecked(true);
            mMeasurement.setPaintFlags(mMeasurement.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            tvName.setPaintFlags(tvName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            mGroceryCheckBox.setChecked(false);
            tvName.setPaintFlags(0);
            mMeasurement.setPaintFlags(0);
        }
    }
}
