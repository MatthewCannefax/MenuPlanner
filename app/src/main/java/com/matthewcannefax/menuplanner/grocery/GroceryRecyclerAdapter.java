package com.matthewcannefax.menuplanner.grocery;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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
    private Map<Integer, Boolean> headingMap;

    public GroceryRecyclerAdapter(Context context, List<Ingredient> groceryList){
        mInflater = LayoutInflater.from(context);
        mGroceryList = groceryList;
        headingMap = new HashMap<>();

        if (mGroceryList != null && mGroceryList.size() != 0) {
            headingMap.put(mGroceryList.get(0).getIngredientID(), true);

            for (int i = 0; i < mGroceryList.size(); i++){
                if(i != 0){
                    Ingredient current = mGroceryList.get(i);
                    Ingredient previous = mGroceryList.get(i -1);

                    if(current.getCategory() != previous.getCategory()){
                        headingMap.put(current.getIngredientID(), true);
                    }else {
                        headingMap.put(current.getIngredientID(), false);
                    }
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
        holder.mGroceryCheckBox.setText(mCurrent.getName());
        holder.mCategory.setText(mCurrent.getCategory().toString());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        if(position == 0 || (position %2) == 0){
            holder.mGrocerySection.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.divider));
        }else{
            holder.mGrocerySection.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.white));
        }


        if(!headingMap.get(mCurrent.getIngredientID())){
            holder.mCategory.setVisibility(View.INVISIBLE);
            holder.mCategory.setTextSize(0);
            params.setMargins(0, 0, 0, 0);
            holder.mCategory.setLayoutParams(params);

        }else{
            holder.mCategory.setVisibility(View.VISIBLE);
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
        LinearLayout mGrocerySection;


        public GroceryViewHolder(View itemView, GroceryRecyclerAdapter adapter) {
            super(itemView);
            mGroceryCheckBox = itemView.findViewById(R.id.groceryCheckBox);
            mMeasurement = itemView.findViewById(R.id.tvMeasurement);
            mCategory = itemView.findViewById(R.id.tvCategory);
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

                mGroceryCheckBox.setChecked(false);
            }else {
                clickedIngredient.setItemChecked(true);
                mDatasource.setGroceryItemChecked(clickedIngredient.getIngredientID(), true);
//                mCategory.setPaintFlags(mCategory.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                mMeasurement.setPaintFlags(mMeasurement.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                mGroceryCheckBox.setChecked(true);
            }

        }
    }

}
