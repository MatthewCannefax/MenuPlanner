package com.matthewcannefax.menuplanner.grocery;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.recipe.Ingredient;
import com.matthewcannefax.menuplanner.utils.database.DataSource;

import java.util.List;

public class GroceryRecyclerAdapter extends RecyclerView.Adapter<GroceryRecyclerAdapter.GroceryViewHolder> {

    private List<Ingredient> mGroceryList;
    private LayoutInflater mInflater;

    public GroceryRecyclerAdapter(Context context, List<Ingredient> groceryList){
        mInflater = LayoutInflater.from(context);
        mGroceryList = groceryList;
    }

    @NonNull
    @Override
    public GroceryViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View mItemView = mInflater.inflate(R.layout.grocery_item, viewGroup, false);

        return new GroceryViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(GroceryViewHolder holder, int position) {
        Ingredient mCurrent = mGroceryList.get(position);
        String measurePlusName = String.format("%s %s", mCurrent.getMeasurement().toString(), mCurrent.getName());
        holder.mMeasurement.setText(measurePlusName);
        holder.mCategory.setText(mCurrent.getCategory().toString());

        if(mCurrent.getItemChecked()){
            holder.mGroceryCheckBox.setChecked(true);
            holder.mMeasurement.setPaintFlags(holder.mMeasurement.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.mCategory.setPaintFlags(holder.mCategory.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }else{
            holder.mGroceryCheckBox.setChecked(false);
            holder.mMeasurement.setPaintFlags(0);
            holder.mCategory.setPaintFlags(0);
        }
    }

    public Ingredient getItem(int position){
        return mGroceryList.get(position);
    }

    @Override
    public int getItemCount() {
        return mGroceryList.size();
    }

    class GroceryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        //Views
        CheckBox mGroceryCheckBox;
        TextView mMeasurement;
        TextView mCategory;


        public GroceryViewHolder(View itemView, GroceryRecyclerAdapter adapter) {
            super(itemView);
            mGroceryCheckBox = itemView.findViewById(R.id.groceryCheckBox);
            mMeasurement = itemView.findViewById(R.id.tvMeasurement);
            mCategory = itemView.findViewById(R.id.tvCategory);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            DataSource mDatasource = new DataSource(view.getContext());
            int mPosition = getLayoutPosition();

            Ingredient clickedIngredient = mGroceryList.get(mPosition);

            if (clickedIngredient.getItemChecked()){
                clickedIngredient.setItemChecked(false);
                mDatasource.setGroceryItemChecked(clickedIngredient.getIngredientID(), false);
                mCategory.setPaintFlags(0);
                mMeasurement.setPaintFlags(0);

                mGroceryCheckBox.setChecked(false);
            }else {
                clickedIngredient.setItemChecked(true);
                mDatasource.setGroceryItemChecked(clickedIngredient.getIngredientID(), true);
                mCategory.setPaintFlags(mCategory.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                mMeasurement.setPaintFlags(mMeasurement.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                mGroceryCheckBox.setChecked(true);
            }

        }
    }

}
