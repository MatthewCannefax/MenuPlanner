package com.matthewcannefax.menuplanner.addEdit;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

abstract class RecipeDetailViewHolder extends RecyclerView.ViewHolder {

    public RecipeDetailViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void bind(RecipeDetailListRow item) { }
}
