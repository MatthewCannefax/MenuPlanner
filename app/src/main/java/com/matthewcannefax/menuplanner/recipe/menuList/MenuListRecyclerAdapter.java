package com.matthewcannefax.menuplanner.recipe.menuList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.addEdit.EditRecipeActivity;
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
    public static final String RECIPE_ID = "item_id";
    Spinner mCategorySpinner;

    public MenuListRecyclerAdapter(Context context, List<Recipe> menuList, Spinner categorySpinner){
        mMenuList = menuList;
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mCategorySpinner = categorySpinner;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View mItemView = mInflater.inflate(R.layout.menu_recipe_list_item, viewGroup, false);

        return new MenuViewHolder(mItemView, this, i);
    }

    @Override
    public void onBindViewHolder(MenuViewHolder holder, int position) {
        Recipe mCurrent = mMenuList.get(position);
        holder.mTextView.setText(mCurrent.getName());
        ImageHelper.setImageViewDrawable(mCurrent.getImagePath(), mContext, holder.mImageView);
    }

    @Override
    public int getItemCount() {
        return mMenuList.size();
    }

    class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        ImageView mImageView;
        TextView mTextView;
        Recipe currentRecipe;
        DataSource mDataSource;
        MenuListRecyclerAdapter recyclerAdapter;


        public MenuViewHolder(View itemView, MenuListRecyclerAdapter adapter, int position) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView);
            mTextView = itemView.findViewById(R.id.itemNameText);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            currentRecipe = mMenuList.get(position);
            mDataSource = new DataSource(itemView.getContext());
            recyclerAdapter = adapter;

        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(view.getContext(), EditRecipeActivity.class);
            intent.putExtra(RECIPE_ID, currentRecipe);
            view.getContext().startActivity(intent);
        }

        @Override
        public boolean onLongClick(View view) {
            return false;
        }
    }
}
