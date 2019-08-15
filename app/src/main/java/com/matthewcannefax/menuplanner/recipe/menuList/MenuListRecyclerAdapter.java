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

        return new MenuViewHolder(mItemView, this);
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
        DataSource mDataSource;
        MenuListRecyclerAdapter recyclerAdapter;


        public MenuViewHolder(View itemView, MenuListRecyclerAdapter adapter) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.imageView);
            mTextView = itemView.findViewById(R.id.itemNameText);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            mDataSource = new DataSource(itemView.getContext());
            recyclerAdapter = adapter;

        }

        @Override
        public void onClick(View view) {
            int mPosition = getLayoutPosition();

            Recipe currentRecipe = mMenuList.get(mPosition);

            Intent intent = new Intent(view.getContext(), EditRecipeActivity.class);
            intent.putExtra(RECIPE_ID, currentRecipe);
            view.getContext().startActivity(intent);
        }

        @Override
        public boolean onLongClick(final View view) {
            int mPosition = getLayoutPosition();

            final Recipe currentRecipe = mMenuList.get(mPosition);

            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext())
                    .setTitle(view.getContext().getString(R.string.remove_from_menu))
                    .setMessage(String.format(view.getContext().getString(R.string.are_you_sure_remove_format), currentRecipe.toString()))
                    .setNegativeButton(view.getContext().getString(R.string.cancel), null)
                    .setPositiveButton(view.getContext().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Snackbar.make(view, String.format(view.getContext().getString(R.string.format_recipe_removed), currentRecipe.toString()), Snackbar.LENGTH_LONG).show();
                            mDataSource.removeMenuItem(currentRecipe.getRecipeID());
                            mMenuList = mDataSource.getAllMenuRecipes();
                            recyclerAdapter.notifyDataSetChanged();
                            ArrayAdapter<RecipeCategory> rcAdapter = new ArrayAdapter<>(view.getContext(), R.layout.category_spinner_item, FilterHelper.getMenuCategoriesUsed(view.getContext()));
                            mCategorySpinner.setAdapter(rcAdapter);
                            int test = 0;
                        }
                    });
            builder.show();
            return false;
        }
    }
}
