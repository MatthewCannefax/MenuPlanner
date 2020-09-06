package com.matthewcannefax.menuplanner.recipe.menuList;

import androidx.annotation.NonNull;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.recipe.Recipe;
import com.matthewcannefax.menuplanner.recipe.RecipeClickListener;
import com.matthewcannefax.menuplanner.recipe.RecipeDiffUtil;
import com.matthewcannefax.menuplanner.recipe.recipeList.RecipeLongClickListener;
import com.matthewcannefax.menuplanner.utils.ImageHelper;
import com.matthewcannefax.menuplanner.utils.database.DataSource;

public class MenuListRecyclerAdapter extends ListAdapter<Recipe, MenuListRecyclerAdapter.MenuViewHolder> {

    private RecipeClickListener clickListener;
    private RecipeLongClickListener longClickListener;

    public MenuListRecyclerAdapter(RecipeClickListener clickListener, RecipeLongClickListener longClickListener) {
        super(new RecipeDiffUtil());
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View mItemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.menu_recipe_list_item, viewGroup, false);
        return new MenuViewHolder(mItemView, this)
                .setClickListener(clickListener)
                .setLongClickListener(longClickListener);
    }

    @Override
    public void onBindViewHolder(MenuViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    @Override
    public void onViewRecycled(@NonNull MenuViewHolder holder) {
        holder.unbindListeners();
    }

    class MenuViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;
        TextView mTextView;
        TextView tvCategory;
        CardView cardView;
        private RecipeClickListener clickListener;
        private RecipeLongClickListener longClickListener;
        DataSource mDataSource;
        MenuListRecyclerAdapter recyclerAdapter;

        public MenuViewHolder(View itemView, MenuListRecyclerAdapter adapter) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView);
            mTextView = itemView.findViewById(R.id.itemNameText);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            cardView = itemView.findViewById(R.id.itemCard);
            mDataSource = new DataSource(itemView.getContext());
            recyclerAdapter = adapter;
        }

        public void bind(Recipe recipe) {
            mTextView.setText(recipe.getName());
            tvCategory.setText(recipe.getCategory().toString());
            ImageHelper.setImageViewDrawable(recipe.getImagePath(), itemView.getContext(), mImageView);
            cardView.setOnClickListener(view -> clickListener.click(recipe));
            cardView.setOnLongClickListener(view -> longClickListener.longClick(recipe));
        }

        public void unbindListeners() {
            itemView.setOnClickListener(null);
            itemView.setOnLongClickListener(null);
        }

        public MenuViewHolder setClickListener(RecipeClickListener clickListener) {
            this.clickListener = clickListener;
            return this;
        }

        public MenuViewHolder setLongClickListener(RecipeLongClickListener longClickListener) {
            this.longClickListener = longClickListener;
            return this;
        }
    }
}
