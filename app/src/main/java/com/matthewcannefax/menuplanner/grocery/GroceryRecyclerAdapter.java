package com.matthewcannefax.menuplanner.grocery;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.recipe.Ingredient;

import java.util.List;

import static com.matthewcannefax.menuplanner.grocery.GroceryRow.GROCERY_HEADER;
import static com.matthewcannefax.menuplanner.grocery.GroceryRow.GROCERY_ITEM;

public class GroceryRecyclerAdapter extends RecyclerView.Adapter<GroceryViewHolder> {

    private List<GroceryRow> mGroceryRows;
    GroceryClickListener groceryClickListener;

    public GroceryRecyclerAdapter(List<Ingredient> groceryList, GroceryClickListener groceryClickListener){
        this.groceryClickListener = groceryClickListener;
        mGroceryRows = new GroceryRowBuilder(groceryList).getGroceryRows();
    }

    @NonNull
    @Override
    public GroceryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            default:
            case GROCERY_ITEM: {
                return new GroceryItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.grocery_item, parent, false), groceryClickListener);
            }
            case GROCERY_HEADER: {
                return new GroceryHeadingViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_grocery_header_row, parent, false));
            }
        }
    }

    @Override
    public void onBindViewHolder(GroceryViewHolder holder, int position) {
        holder.bind(mGroceryRows.get(position));
    }

    public GroceryRow getItem(int position){
        return mGroceryRows.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        return mGroceryRows.get(position).getGroceryRowType();
    }

    @Override
    public int getItemCount() {
        return mGroceryRows.size();
    }
}
