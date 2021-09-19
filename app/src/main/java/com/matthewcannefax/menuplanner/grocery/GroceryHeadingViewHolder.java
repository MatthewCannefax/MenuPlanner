package com.matthewcannefax.menuplanner.grocery;

import androidx.annotation.NonNull;

import android.view.View;
import android.widget.TextView;

import com.matthewcannefax.menuplanner.R;

public class GroceryHeadingViewHolder extends GroceryViewHolder {

    private TextView title;

    public GroceryHeadingViewHolder(@NonNull View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.tvCategory);
    }

    @Override
    public void bind(GroceryRow row) {
        bind((GroceryHeadingRow) row);
    }

    private void bind(GroceryHeadingRow row) {
        title.setText(row.getHeading());
    }
}
