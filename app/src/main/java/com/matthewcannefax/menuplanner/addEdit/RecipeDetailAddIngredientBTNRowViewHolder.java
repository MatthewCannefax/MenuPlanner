package com.matthewcannefax.menuplanner.addEdit;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.matthewcannefax.menuplanner.R;

public class RecipeDetailAddIngredientBTNRowViewHolder extends RecipeDetailViewHolder {

    private AddIngredientClickListener addIngredientClickListener;
    private Button button;

    public RecipeDetailAddIngredientBTNRowViewHolder(@NonNull View itemView, AddIngredientClickListener addIngredientClickListener) {
        super(itemView);
        this.button = itemView.findViewById(R.id.ingredient_layout_button);
        this.addIngredientClickListener = addIngredientClickListener;
    }

    @Override
    public void bind(RecipeDetailListRow item) {
        button.setOnClickListener((view) -> addIngredientClickListener.addIngredient());
    }
}
