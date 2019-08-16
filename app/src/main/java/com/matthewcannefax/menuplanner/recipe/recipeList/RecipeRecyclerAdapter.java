package com.matthewcannefax.menuplanner.recipe.recipeList;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;

import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.addEdit.EditRecipeActivity;
import com.matthewcannefax.menuplanner.recipe.Recipe;
import com.matthewcannefax.menuplanner.utils.ImageHelper;
import com.matthewcannefax.menuplanner.utils.database.DataSource;

import java.util.List;

public class RecipeRecyclerAdapter extends RecyclerView.Adapter<RecipeRecyclerAdapter.RecipeViewHolder> {

    private List<Recipe> mRecipeList;
    private LayoutInflater mInflater;
    private Context mContext;


    public RecipeRecyclerAdapter(Context context, List<Recipe> recipeList){
        mRecipeList =recipeList;
        mInflater = LayoutInflater.from(context);
        mContext = context;
    }

    @NonNull
    @Override
    public RecipeRecyclerAdapter.RecipeViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View mItemView = mInflater.inflate(R.layout.recipe_checkbox_item, viewGroup, false);

        return new RecipeViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(RecipeRecyclerAdapter.RecipeViewHolder holder, int position) {

        Recipe mCurrent = mRecipeList.get(position);
        holder.mCheckBox.setText(mCurrent.toString());
        holder.mCheckBox.setChecked(mCurrent.isItemChecked());
        ImageHelper.setImageViewDrawable(mCurrent.getImagePath(), mContext, holder.mImageView);

    }

    @Override
    public int getItemCount() {
        return mRecipeList.size();
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        CheckBox mCheckBox;
        ImageView mImageView;
        RecipeRecyclerAdapter recyclerAdapter;
        DataSource mDataSource;



        public RecipeViewHolder(View itemView, RecipeRecyclerAdapter adapter) {
            super(itemView);
            mCheckBox = itemView.findViewById(R.id.cbName);
            mImageView = itemView.findViewById(R.id.imageView);
            recyclerAdapter = adapter;
            mDataSource = new DataSource(itemView.getContext());
            itemView.setOnLongClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int mPosition = getLayoutPosition();
            Recipe currentRecipe = mRecipeList.get(mPosition);

            currentRecipe.setItemChecked(!currentRecipe.isItemChecked());
            mCheckBox.setChecked(currentRecipe.isItemChecked());
        }

        @Override
        public boolean onLongClick(View view) {
            int mPosition = getLayoutPosition();
            Recipe currentRecipe = mRecipeList.get(mPosition);

            Intent intent = new Intent(view.getContext(), EditRecipeActivity.class);
            intent.putExtra(EditRecipeActivity.RECIPE_ID, currentRecipe);
            view.getContext().startActivity(intent);

            return true;
        }
    }
}
