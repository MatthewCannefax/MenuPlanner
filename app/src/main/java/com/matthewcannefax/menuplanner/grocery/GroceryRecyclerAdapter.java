package com.matthewcannefax.menuplanner.grocery;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.recipe.Ingredient;
import com.matthewcannefax.menuplanner.utils.database.DataSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroceryRecyclerAdapter extends RecyclerView.Adapter<GroceryRecyclerAdapter.GroceryViewHolder> {

    private List<Ingredient> mGroceryList;
    private LayoutInflater mInflater;
    private SparseBooleanArray headingArray;
    private SparseBooleanArray dividerArray;
    private boolean divider;

    public GroceryRecyclerAdapter(Context context, List<Ingredient> groceryList){
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
    public GroceryViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View mItemView = mInflater.inflate(R.layout.grocery_item, viewGroup, false);

        return new GroceryViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(GroceryViewHolder holder, int position) {
        Ingredient mCurrent = mGroceryList.get(position);
//        String measurePlusName = String.format("%s %s", mCurrent.getMeasurement().toString(), mCurrent.getName());
        holder.mMeasurement.setText(mCurrent.getMeasurement().toString());
        holder.tvName.setText(mCurrent.getName());
        holder.mCategory.setText(mCurrent.getCategory().toString());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        if(dividerArray.get(position)){
            holder.mGrocerySection.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.divider));
        }else{
            holder.mGrocerySection.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.white));
        }


        if(!headingArray.get(mCurrent.getIngredientID())){
//            holder.mCategory.setVisibility(View.INVISIBLE);
            holder.headingLayout.setVisibility(View.GONE);
            holder.mCategory.setTextSize(0);
            params.setMargins(0, 0, 0, 0);
            holder.mCategory.setLayoutParams(params);

        }else{
//            holder.mCategory.setVisibility(View.VISIBLE);
            holder.headingLayout.setVisibility(View.VISIBLE);
            holder.mCategory.setTextSize(16);
            params.setMargins(0, 10, 0, 0);
            holder.mCategory.setLayoutParams(params);
        }

        if(mCurrent.getItemChecked()){
            holder.mGroceryCheckBox.setChecked(true);
            holder.mMeasurement.setPaintFlags(holder.mMeasurement.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//            holder.mCategory.setPaintFlags(holder.mCategory.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }else{
            holder.mGroceryCheckBox.setChecked(false);
            holder.mMeasurement.setPaintFlags(0);
//            holder.mCategory.setPaintFlags(0);
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
        TextView tvName;
        LinearLayout headingLayout;
        ConstraintLayout mGrocerySection;


        public GroceryViewHolder(View itemView, GroceryRecyclerAdapter adapter) {
            super(itemView);
            mGroceryCheckBox = itemView.findViewById(R.id.groceryCheckBox);
            mMeasurement = itemView.findViewById(R.id.tvMeasurement);
            mCategory = itemView.findViewById(R.id.tvCategory);
            tvName = itemView.findViewById(R.id.tvName);
            headingLayout = itemView.findViewById(R.id.headingLayout);
            mGrocerySection = itemView.findViewById(R.id.groceryItemSection);
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
//                mCategory.setPaintFlags(0);
                mMeasurement.setPaintFlags(0);
                mGroceryCheckBox.setPaintFlags(0);
                mGroceryCheckBox.setChecked(false);
            }else {
                clickedIngredient.setItemChecked(true);
                mDatasource.setGroceryItemChecked(clickedIngredient.getIngredientID(), true);
//                mCategory.setPaintFlags(mCategory.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                mMeasurement.setPaintFlags(mMeasurement.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                mGroceryCheckBox.setPaintFlags(mGroceryCheckBox.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                mGroceryCheckBox.setChecked(true);
            }

        }
    }

}
