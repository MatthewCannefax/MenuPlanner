package com.matthewcannefax.menuplanner.addEdit;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

public class RecipeDetailDiffUtil extends DiffUtil.ItemCallback<RecipeDetailListRow> {
    @Override
    public boolean areItemsTheSame(@NonNull RecipeDetailListRow oldItem, @NonNull RecipeDetailListRow newItem) {
        if (oldItem instanceof RecipeDetailIngredientRow && newItem instanceof RecipeDetailIngredientRow) {
            return ((RecipeDetailIngredientRow) oldItem).getIngredient().equals(((RecipeDetailIngredientRow) newItem).getIngredient());
        }
        return oldItem.getId().equals(newItem.getId());
    }

    @Override
    public boolean areContentsTheSame(@NonNull RecipeDetailListRow oldItem, @NonNull RecipeDetailListRow newItem) {
        return oldItem.equals(newItem);
    }
}
