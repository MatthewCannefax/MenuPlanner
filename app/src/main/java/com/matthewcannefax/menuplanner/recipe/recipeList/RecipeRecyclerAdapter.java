package com.matthewcannefax.menuplanner.recipe.recipeList;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.matthewcannefax.menuplanner.GenericClickListener;
import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.recipe.Recipe;

public class RecipeRecyclerAdapter extends ListAdapter<Recipe, RecipeViewHolder> {
    private final GenericClickListener<Integer> checkClickListener;
    private final GenericClickListener<Recipe> recipeClickListener;

    public RecipeRecyclerAdapter(final GenericClickListener<Integer> checkClickListener,
                                 final GenericClickListener<Recipe> recipeClickListener) {
        super(new DiffUtil.ItemCallback<Recipe>() {
            @Override
            public boolean areItemsTheSame(@NonNull Recipe oldItem, @NonNull Recipe newItem) {
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areContentsTheSame(@NonNull Recipe oldItem, @NonNull Recipe newItem) {
                return oldItem.equals(newItem);
            }
        });
        this.checkClickListener = checkClickListener;
        this.recipeClickListener = recipeClickListener;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int i) {
        return new RecipeViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recipe_checkbox_item, viewGroup, false))
                .setCheckClickListener(checkClickListener)
                .setRecipeClickListener(recipeClickListener);
    }

    @Override
    public void onBindViewHolder(final RecipeViewHolder holder, final int position) {
        holder.bind(getItem(position));
    }
}
