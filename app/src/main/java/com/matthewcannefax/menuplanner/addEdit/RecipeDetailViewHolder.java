package com.matthewcannefax.menuplanner.addEdit;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

abstract class RecipeDetailViewHolder extends RecyclerView.ViewHolder {

    public RecipeDetailViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void bind(RecipeDetailListRow item) { }
}
