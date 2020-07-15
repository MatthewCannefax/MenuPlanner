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

public class GroceryItemViewHolder extends RecyclerView.ViewHolder {

    Context mContext;

    //Views
    CheckBox mGroceryCheckBox;
    TextView mMeasurement;
    TextView mCategory;
    TextView tvName;
    LinearLayout headingLayout;
    ConstraintLayout mGrocerySection;
    GroceryClickListener groceryClickListener;


    public GroceryItemViewHolder(View itemView, GroceryClickListener groceryClickListener) {
        super(itemView);
        mContext = itemView.getContext();
        mGroceryCheckBox = itemView.findViewById(R.id.groceryCheckBox);
        mMeasurement = itemView.findViewById(R.id.tvMeasurement);
        mCategory = itemView.findViewById(R.id.tvCategory);
        tvName = itemView.findViewById(R.id.tvName);
        headingLayout = itemView.findViewById(R.id.headingLayout);
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

    public void bind(Ingredient ingredient, boolean isDivider, boolean isHeading, final int position){
        final GroceryViewChangeListener groceryViewChangeListener = this::changeView;
        mGrocerySection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                groceryClickListener.checkGroceryItem(position, groceryViewChangeListener);
            }
        });

        mMeasurement.setText(ingredient.getMeasurement().toString());
        tvName.setText(ingredient.getName());
        mCategory.setText(ingredient.getCategory().toString());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        if (isDivider) {
            mGrocerySection.setBackgroundColor(mContext.getResources().getColor(R.color.divider));
        } else {
            mGrocerySection.setBackgroundColor(mContext.getResources().getColor(R.color.white));
        }

        if (isHeading) {
            headingLayout.setVisibility(View.VISIBLE);
            mCategory.setTextSize(16f);
            params.setMargins(0, 10, 0, 0);
            mCategory.setLayoutParams(params);
        } else {
            headingLayout.setVisibility(View.GONE);
            mCategory.setTextSize(0f);
            params.setMargins(0,0,0,0);
            mCategory.setLayoutParams(params);
        }

        if (ingredient.getItemChecked()) {
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
