package com.matthewcannefax.menuplanner.addEdit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.grocery.GroceryCategory;
import com.matthewcannefax.menuplanner.recipe.Ingredient;
import com.matthewcannefax.menuplanner.recipe.Measurement;
import com.matthewcannefax.menuplanner.recipe.MeasurementType;
import com.matthewcannefax.menuplanner.recipe.Recipe;

import java.util.List;
import java.util.Locale;

public class IngredientRecyclerAdapter extends RecyclerView.Adapter<IngredientRecyclerAdapter.IngredientViewHolder> {

    private List<Ingredient> mIngredientList;
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private Recipe mRecipe;


    public IngredientRecyclerAdapter(Context context, List<Ingredient> ingredientList, Recipe recipe){
        mContext = context;
        mIngredientList = ingredientList;
        mLayoutInflater = LayoutInflater.from(context);
        mRecipe = recipe;

    }

    @Override
    public IngredientViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View mItemView = mLayoutInflater.inflate(R.layout.ingredient_list_item, viewGroup, false);

        return new IngredientViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(IngredientViewHolder holder, int position) {
        Ingredient mCurrent = mIngredientList.get(position);
        holder.mName.setText(mCurrent.getName());
        holder.mMeasurement.setText(mCurrent.getMeasurement().toString());
    }

    @Override
    public int getItemCount() {
        return mIngredientList.size();
    }

    public class IngredientViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        TextView mMeasurement;
        TextView mName;
        IngredientRecyclerAdapter mAdapter;

        public IngredientViewHolder(View itemView, IngredientRecyclerAdapter adapter) {
            super(itemView);
            mMeasurement = itemView.findViewById(R.id.tvMeasurement);
            mName = itemView.findViewById(R.id.tvName);
            itemView.setOnLongClickListener(this);
            mAdapter = adapter;
        }


        @Override
        public boolean onLongClick(View view) {
            final int mPosition = getLayoutPosition();
            Ingredient currentIngredient = mIngredientList.get(mPosition);

            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext())
                    .setTitle(view.getContext().getString(R.string.edit_ingredient));

            View editIngredientView = LayoutInflater.from(
                    view.getContext()).inflate(R.layout.add_ingredient_item,
                    (ViewGroup)view.findViewById(android.R.id.content),
                    false);

            final EditText etAmount = editIngredientView.findViewById(R.id.amountText);
            final Spinner spMeasure = editIngredientView.findViewById(R.id.amountSpinner);
            final EditText etName = editIngredientView.findViewById(R.id.ingredientName);
            final Spinner spCat = editIngredientView.findViewById(R.id.categorySpinner);

            ArrayAdapter<MeasurementType> measureAdapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_item, MeasurementType.values());
            ArrayAdapter<GroceryCategory> ingredCatAdapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_item, GroceryCategory.getEnumIngredients());

            etAmount.setText(String.format(Locale.US, "%s", currentIngredient.getMeasurement().getAmount()));
            etName.setText(currentIngredient.getName());

            spMeasure.setAdapter(measureAdapter);
            spCat.setAdapter(ingredCatAdapter);

            spCat.setSelection(GroceryCategory.getCatPosition(currentIngredient.getCategory()));
            spMeasure.setSelection(currentIngredient.getMeasurement().getType().ordinal());

            builder.setView(editIngredientView)
                    .setNegativeButton(view.getContext().getString(R.string.cancel), null)
                    .setPositiveButton(view.getContext().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mRecipe.getIngredientList().get(mPosition).setName(etName.getText().toString());
                            mRecipe.getIngredientList().get(mPosition).setCategory((GroceryCategory) spCat.getSelectedItem());
                            mRecipe.getIngredientList().get(mPosition).setMeasurement(new Measurement(Double.parseDouble(etAmount.getText().toString()),
                                                                                                        (MeasurementType)spMeasure.getSelectedItem()));
                            mAdapter.notifyDataSetChanged();
                        }
                    })
                    .setNeutralButton(view.getContext().getString(R.string.delete), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mRecipe.getIngredientList().remove(mPosition);
                            mAdapter.notifyDataSetChanged();
                        }
                    });
            builder.show();
            return false;

        }
    }
}
