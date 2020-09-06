package com.matthewcannefax.menuplanner.recipe;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

public class RecipeDiffUtil extends DiffUtil.ItemCallback<Recipe> {
    @Override
    public boolean areItemsTheSame(@NonNull Recipe oldItem, @NonNull Recipe newItem) {
        return oldItem.getRecipeID() == newItem.getRecipeID();
    }

    @Override
    public boolean areContentsTheSame(@NonNull Recipe oldItem, @NonNull Recipe newItem) {
        return oldItem.equals(newItem);
    }
}
