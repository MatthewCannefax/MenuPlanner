package com.matthewcannefax.menuplanner.grocery;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.matthewcannefax.menuplanner.R;

import static com.matthewcannefax.menuplanner.grocery.GroceryRow.GROCERY_HEADER;
import static com.matthewcannefax.menuplanner.grocery.GroceryRow.GROCERY_ITEM;

public class GroceryRecyclerAdapter extends ListAdapter<GroceryRow, GroceryViewHolder> {

    private GroceryClickListener groceryClickListener;

    public GroceryRecyclerAdapter(GroceryClickListener groceryClickListener){
        super(new GroceryRowDiffUtil());
        this.groceryClickListener = groceryClickListener;
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
        holder.bind(getItem(position));
    }

    @Override
    public void onViewRecycled(@NonNull GroceryViewHolder holder) {
        super.onViewRecycled(holder);
    }

    //    public GroceryRow getItem(int position){
//        return mGroceryRows.get(position);
//    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getGroceryRowType();
    }

//    @Override
//    public int getItemCount() {
//        return mGroceryRows.size();
//    }
}
