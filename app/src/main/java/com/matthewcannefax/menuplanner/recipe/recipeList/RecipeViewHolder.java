package com.matthewcannefax.menuplanner.recipe.recipeList;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.matthewcannefax.menuplanner.GenericClickListener;
import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.recipe.Recipe;
import com.matthewcannefax.menuplanner.utils.ImageHelper;

public class RecipeViewHolder extends RecyclerView.ViewHolder {
    private final CheckBox mCheckBox;
    private final TextView tvName;
    private final TextView tvCategory;
    private final ImageView mImageView;
    private final ConstraintLayout itemWrapper;
    private GenericClickListener<Integer> checkClickListener;
    private GenericClickListener<Recipe> recipeClickListener;

    public RecipeViewHolder(final View itemView) {
        super(itemView);
        this.mCheckBox = itemView.findViewById(R.id.cbName);
        this.tvName = itemView.findViewById(R.id.tvName);
        this.tvCategory = itemView.findViewById(R.id.tvCategory);
        this.mImageView = itemView.findViewById(R.id.imageView);
        this.itemWrapper = itemView.findViewById(R.id.item_wrapper);
    }

    public void bind(final Recipe recipe) {
        tvName.setText(recipe.toString());
        tvCategory.setText(recipe.getCategory().toString());
        mCheckBox.setChecked(recipe.isItemChecked());
        ImageHelper.setImageViewDrawable(recipe.getImagePath(), itemView.getContext(), mImageView);
        itemWrapper.setOnClickListener(view -> recipeClickListener.onClick(recipe));
        mCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> checkClickListener.onClick(getLayoutPosition()));
    }

    public RecipeViewHolder setCheckClickListener(GenericClickListener<Integer> checkClickListener) {
        this.checkClickListener = checkClickListener;
        return this;
    }

    public RecipeViewHolder setRecipeClickListener(GenericClickListener<Recipe> recipeClickListener) {
        this.recipeClickListener = recipeClickListener;
        return this;
    }
}
