package com.matthewcannefax.menuplanner.addEdit;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import android.view.View;
import android.widget.TextView;

import com.matthewcannefax.menuplanner.R;

public class RecipeDetailIngredientRowViewHolder extends RecipeDetailViewHolder {

    private TextView measurement;
    private TextView name;
    private CardView cardView;
    private IngredientLongClickListener longClickListener;

    public RecipeDetailIngredientRowViewHolder(@NonNull View itemView, IngredientLongClickListener longClickListener) {
        super(itemView);
        this.measurement = itemView.findViewById(R.id.tvMeasurement);
        this.name = itemView.findViewById(R.id.tvName);
        this.cardView = itemView.findViewById(R.id.ingredient_card_view);
        this.longClickListener = longClickListener;
    }

    @Override
    public void bind(RecipeDetailListRow item) {
        bind((RecipeDetailIngredientRow) item);
    }

    private void bind(RecipeDetailIngredientRow item) {
        measurement.setText(item.getIngredient().getMeasurement().toString());
        name.setText(item.getIngredient().getName());
        cardView.setOnLongClickListener(view -> longClickListener.longClick(item.getIngredient()));
    }
}
