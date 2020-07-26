package com.matthewcannefax.menuplanner.addEdit;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.matthewcannefax.menuplanner.R;

public class RecipeDetailIngredientRowViewHolder extends RecipeDetailViewHolder {

    private TextView measurement;
    private TextView name;

    public RecipeDetailIngredientRowViewHolder(@NonNull View itemView) {
        super(itemView);
        this.measurement = itemView.findViewById(R.id.tvMeasurement);
        this.name = itemView.findViewById(R.id.tvName);
    }

    @Override
    public void bind(RecipeDetailListRow item) {
        bind((RecipeDetailIngredientRow) item);
    }

    private void bind(RecipeDetailIngredientRow item) {
        measurement.setText(item.getIngredient().getMeasurement().toString());
        name.setText(item.getIngredient().getName());
    }
}
