package com.matthewcannefax.menuplanner.recipe.menuList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.matthewcannefax.menuplanner.GenericClickListener;
import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.recipe.Recipe;
import com.matthewcannefax.menuplanner.utils.ImageHelper;

import java.util.List;

public class MenuListRecyclerAdapter extends ListAdapter<Recipe, MenuViewHolder> {
    private final GenericClickListener<Recipe> recipeClickListener;
    private final GenericClickListener<Integer> removeFromMenuListner;

    public MenuListRecyclerAdapter(final GenericClickListener<Recipe> recipeClickListener,
                                   final GenericClickListener<Integer> removeFromMenuListner) {
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
        this.recipeClickListener = recipeClickListener;
        this.removeFromMenuListner = removeFromMenuListner;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new MenuViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.menu_recipe_list_item, viewGroup, false))
                .setRecipeClickListener(recipeClickListener)
                .setRemoveFromMenuListener(removeFromMenuListner);
    }

    @Override
    public void onBindViewHolder(MenuViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position, @NonNull List<Object> payloads) {
        holder.unbindListeners();
        super.onBindViewHolder(holder, position, payloads);
    }
}
