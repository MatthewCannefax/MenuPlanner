package com.matthewcannefax.menuplanner.grocery;

import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.DiffUtil;

public class GroceryRowDiffUtil extends DiffUtil.ItemCallback<GroceryRow> {
    @Override
    public boolean areItemsTheSame(@NonNull GroceryRow oldItem, @NonNull GroceryRow newItem) {
        return oldItem.getId().equals(newItem.getId());
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean areContentsTheSame(@NonNull GroceryRow oldItem, @NonNull GroceryRow newItem) {
        return oldItem.equals(newItem);
    }
}
