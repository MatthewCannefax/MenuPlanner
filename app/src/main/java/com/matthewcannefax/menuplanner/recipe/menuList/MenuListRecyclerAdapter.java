package com.matthewcannefax.menuplanner.recipe.menuList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.android.material.snackbar.Snackbar;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.matthewcannefax.menuplanner.GenericClickListener;
import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.addEdit.ViewRecipeFragment;
import com.matthewcannefax.menuplanner.recipe.Recipe;
import com.matthewcannefax.menuplanner.recipe.RecipeCategory;
import com.matthewcannefax.menuplanner.utils.FilterHelper;
import com.matthewcannefax.menuplanner.utils.ImageHelper;
import com.matthewcannefax.menuplanner.utils.database.DataSource;

import java.util.List;

public class MenuListRecyclerAdapter extends RecyclerView.Adapter<MenuListRecyclerAdapter.MenuViewHolder> {

    private List<Recipe> mMenuList;
    private LayoutInflater mInflater;
    private Context mContext;
    private FragmentManager mFragmentManager;
    private Spinner mCategorySpinner;
    private GenericClickListener<Recipe> recipeClickListener;

    public MenuListRecyclerAdapter(FragmentManager fragmentManager, Context context, List<Recipe> menuList, Spinner categorySpinner, GenericClickListener<Recipe> recipeClickListener) {
        mMenuList = menuList;
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mFragmentManager = fragmentManager;
        mCategorySpinner = categorySpinner;
        this.recipeClickListener = recipeClickListener;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View mItemView = mInflater.inflate(R.layout.menu_recipe_list_item, viewGroup, false);

        return new MenuViewHolder(mItemView, this).setRecipeClickListener(recipeClickListener);
    }

    @Override
    public void onBindViewHolder(MenuViewHolder holder, int position) {
        Recipe mCurrent = mMenuList.get(position);
        holder.mTextView.setText(mCurrent.getName());
        holder.tvCategory.setText(mCurrent.getCategory().toString());
        holder.bind(mCurrent);
        ImageHelper.setImageViewDrawable(mCurrent.getImagePath(), mContext, holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return mMenuList.size();
    }

    class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        ImageView mImageView;
        TextView mTextView;
        TextView tvCategory;
        DataSource mDataSource;
        MenuListRecyclerAdapter recyclerAdapter;
        private GenericClickListener<Recipe> recipeClickListener;

        public MenuViewHolder(View itemView, MenuListRecyclerAdapter adapter) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView);
            mTextView = itemView.findViewById(R.id.itemNameText);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            itemView.setOnLongClickListener(this);
            mDataSource = new DataSource(itemView.getContext());
            recyclerAdapter = adapter;
        }

        public void bind(final Recipe recipe) {
            itemView.setOnClickListener(view -> recipeClickListener.onClick(recipe));
        }

        public void unbindListeners() {
            itemView.setOnClickListener(null);
        }

        @Override
        public boolean onLongClick(final View view) {
            int mPosition = getLayoutPosition();

            final Recipe currentRecipe = mMenuList.get(mPosition);

            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext())
                    .setTitle(view.getContext().getString(R.string.remove_from_menu))
                    .setMessage(String.format(view.getContext().getString(R.string.are_you_sure_remove_format), currentRecipe.toString()))
                    .setNegativeButton(view.getContext().getString(R.string.cancel), null)
                    .setPositiveButton(view.getContext().getString(R.string.ok), (dialogInterface, i) -> {
                        Snackbar.make(view, String.format(view.getContext().getString(R.string.format_recipe_removed), currentRecipe.toString()), Snackbar.LENGTH_LONG).show();
                        mDataSource.removeMenuItem(currentRecipe.getRecipeID());
                        mMenuList = mDataSource.getAllMenuRecipes();
                        recyclerAdapter.notifyDataSetChanged();
                        ArrayAdapter<RecipeCategory> rcAdapter = new ArrayAdapter<>(view.getContext(), R.layout.category_spinner_item, FilterHelper.getMenuCategoriesUsed(view.getContext()));
                        mCategorySpinner.setAdapter(rcAdapter);
                    });
            builder.show();
            return false;
        }

        public MenuViewHolder setRecipeClickListener(GenericClickListener<Recipe> recipeClickListener) {
            this.recipeClickListener = recipeClickListener;
            return this;
        }
    }
}
