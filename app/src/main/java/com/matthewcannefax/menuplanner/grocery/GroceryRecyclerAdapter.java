package com.matthewcannefax.menuplanner.grocery;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.recipe.Ingredient;

import java.util.List;

public class GroceryRecyclerAdapter extends RecyclerView.Adapter<GroceryItemViewHolder> {

    private List<Ingredient> mGroceryList;
    private LayoutInflater mInflater;
    private SparseBooleanArray headingArray;
    private SparseBooleanArray dividerArray;
    private boolean divider;
    GroceryClickListener groceryClickListener;

    public GroceryRecyclerAdapter(Context context, List<Ingredient> groceryList, GroceryClickListener groceryClickListener){
        this.groceryClickListener = groceryClickListener;
        mInflater = LayoutInflater.from(context);
        mGroceryList = groceryList;
        headingArray = new SparseBooleanArray();
        dividerArray = new SparseBooleanArray();
        divider = true;

        if (mGroceryList != null && mGroceryList.size() != 0) {
            headingArray.append(mGroceryList.get(0).getIngredientID(), true);
            dividerArray.append(0, true);

            for (int i = 0; i < mGroceryList.size(); i++){
                if(i != 0){
                    Ingredient current = mGroceryList.get(i);
                    Ingredient previous = mGroceryList.get(i -1);

                    if(current.getCategory() != previous.getCategory()){
                        headingArray.append(current.getIngredientID(), true);
                        divider = true;
                    }else {
                        headingArray.append(current.getIngredientID(), false);
                        if(dividerArray.get(i - 1)){
                            divider = false;
                        }else {
                            divider = true;
                        }
                    }
                    dividerArray.append(i, divider);
                }
            }
        }

    }

    @NonNull
    @Override
    public GroceryItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View mItemView = mInflater.inflate(R.layout.grocery_item, viewGroup, false);

        return new GroceryItemViewHolder(mItemView, groceryClickListener);
    }

    @Override
    public void onBindViewHolder(GroceryItemViewHolder holder, int position) {
        holder.bind(mGroceryList.get(position), dividerArray.get(position), headingArray.get(mGroceryList.get(position).getIngredientID()), position);
    }

    public Ingredient getItem(int position){
        return mGroceryList.get(position);
    }

    @Override
    public int getItemCount() {
        return mGroceryList.size();
    }
}
