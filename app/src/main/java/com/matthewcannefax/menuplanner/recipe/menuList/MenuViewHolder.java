package com.matthewcannefax.menuplanner.recipe.menuList;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.matthewcannefax.menuplanner.GenericClickListener;
import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.recipe.Recipe;
import com.matthewcannefax.menuplanner.utils.ImageHelper;

public class MenuViewHolder extends RecyclerView.ViewHolder {
    private final ImageView mImageView;
    private final TextView mTextView;
    private final TextView tvCategory;
    private GenericClickListener<Recipe> recipeClickListener;
    private GenericClickListener<Integer> removeFromMenuListener;

    public MenuViewHolder(final View itemView) {
        super(itemView);
        mImageView = itemView.findViewById(R.id.imageView);
        mTextView = itemView.findViewById(R.id.itemNameText);
        tvCategory = itemView.findViewById(R.id.tvCategory);
    }

    public void bind(final Recipe recipe) {
        mTextView.setText(recipe.getName());
        tvCategory.setText(recipe.getCategory().toString());
        ImageHelper.setImageViewDrawable(recipe.getImagePath(), itemView.getContext(), mImageView);
        itemView.setOnLongClickListener(view -> {
            removeFromMenuListener.onClick(getLayoutPosition());
            return true;
        });
        itemView.setOnClickListener(view -> recipeClickListener.onClick(recipe));
    }

    public void unbindListeners() {
        itemView.setOnClickListener(null);
    }

    public MenuViewHolder setRecipeClickListener(GenericClickListener<Recipe> recipeClickListener) {
        this.recipeClickListener = recipeClickListener;
        return this;
    }

    public MenuViewHolder setRemoveFromMenuListener(final GenericClickListener<Integer> removeFromMenuListener) {
        this.removeFromMenuListener = removeFromMenuListener;
        return this;
    }
}
