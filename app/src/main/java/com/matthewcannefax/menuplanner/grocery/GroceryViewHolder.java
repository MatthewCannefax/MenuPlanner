package com.matthewcannefax.menuplanner.grocery;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

abstract class GroceryViewHolder extends RecyclerView.ViewHolder {
    public GroceryViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void bind(GroceryRow item){}

    public void unbindListeners(){}
}
