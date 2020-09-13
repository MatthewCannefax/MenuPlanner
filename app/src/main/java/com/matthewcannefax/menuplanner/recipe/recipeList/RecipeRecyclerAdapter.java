package com.matthewcannefax.menuplanner.recipe.recipeList;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.addEdit.EditRecipeActivity;
import com.matthewcannefax.menuplanner.recipe.Recipe;
import com.matthewcannefax.menuplanner.recipe.RecipeClickListener;
import com.matthewcannefax.menuplanner.recipe.RecipeDiffUtil;
import com.matthewcannefax.menuplanner.recipe.RecipeLongClickListener;
import com.matthewcannefax.menuplanner.utils.ImageHelper;
import com.matthewcannefax.menuplanner.utils.database.DataSource;

import java.util.List;


public class RecipeRecyclerAdapter extends ListAdapter<Recipe, RecipeRecyclerAdapter.RecipeViewHolder> {
    private Context mContext;
    private RecipeClickListener clickListener;
    private RecipeLongClickListener longClickListener;


    public RecipeRecyclerAdapter(Context context, RecipeClickListener clickListener, RecipeLongClickListener longClickListener){
        super(new RecipeDiffUtil());
        mContext = context;
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public RecipeRecyclerAdapter.RecipeViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        return new RecipeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_checkbox_item, parent, false))
                .setClickListener(clickListener)
                .setLongClickListener(longClickListener);
    }

    @Override
    public void onBindViewHolder(RecipeRecyclerAdapter.RecipeViewHolder holder, int position) {
        holder.bind(getItem(position), mContext);
    }

    @Override
    public void onViewRecycled(@NonNull RecipeViewHolder holder) {
        holder.unbindListeners();
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder{
        private CheckBox mCheckBox;
        private TextView tvName;
        private TextView tvCategory;
        private ImageView mImageView;
        private RecipeClickListener clickListener;
        private RecipeLongClickListener longClickListener;

        public RecipeViewHolder(View itemView) {
            super(itemView);
            mCheckBox = itemView.findViewById(R.id.cbName);
            tvName = itemView.findViewById(R.id.tvName);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            mImageView = itemView.findViewById(R.id.imageView);
        }

        public void bind(Recipe recipe, Context context) {
            tvName.setText(recipe.toString());
            tvCategory.setText(recipe.getCategory().toString());
            mCheckBox.setChecked(recipe.isItemChecked());
            itemView.setOnClickListener(view -> {
                clickListener.click(recipe);
                mCheckBox.setChecked(recipe.isItemChecked());
            });
            itemView.setOnLongClickListener(view -> longClickListener.longClick(recipe));
            ImageHelper.setImageViewDrawable(recipe.getImagePath(), context, mImageView);
        }

        public void unbindListeners() {
            itemView.setOnClickListener(null);
            itemView.setOnLongClickListener(null);
        }

        public RecipeViewHolder setClickListener(RecipeClickListener clickListener) {
            this.clickListener = clickListener;
            return this;
        }

        public RecipeViewHolder setLongClickListener(RecipeLongClickListener longClickListener) {
            this.longClickListener = longClickListener;
            return this;
        }
    }
}
