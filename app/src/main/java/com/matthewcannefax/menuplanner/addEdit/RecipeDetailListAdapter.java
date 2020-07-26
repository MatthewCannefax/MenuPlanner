package com.matthewcannefax.menuplanner.addEdit;

import android.app.AlertDialog;
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

import static com.matthewcannefax.menuplanner.addEdit.RecipeDetailListRow.ADD_INGREDIENT_BTN_ROW;
import static com.matthewcannefax.menuplanner.addEdit.RecipeDetailListRow.DIRECTIONS_ROW;
import static com.matthewcannefax.menuplanner.addEdit.RecipeDetailListRow.HEADING_ROW;
import static com.matthewcannefax.menuplanner.addEdit.RecipeDetailListRow.INGREDIENT_ITEM_ROW;

public class RecipeDetailListAdapter extends RecyclerView.Adapter<RecipeDetailViewHolder> {

    private List<RecipeDetailListRow> rowList;
    private Recipe mRecipe;
    private AddIngredientClickListener addIngredientClickListener;
    private DirectionsChangedListener directionsChangedListener;

    public RecipeDetailListAdapter(
            List<RecipeDetailListRow> ingredientList,
            Recipe recipe,
            AddIngredientClickListener addIngredientClickListener,
            DirectionsChangedListener directionsChangedListener) {
        rowList = ingredientList;
        mRecipe = recipe;
        this.addIngredientClickListener = addIngredientClickListener;
        this.directionsChangedListener = directionsChangedListener;
    }

    @Override
    public RecipeDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            default:
            case HEADING_ROW:
                return new RecipeDetailHeaderRowViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(
                                R.layout.list_item_grocery_header_row,
                                parent,
                                false));

            case INGREDIENT_ITEM_ROW:
                return new RecipeDetailIngredientRowViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(
                                R.layout.ingredient_list_item,
                                parent,
                                false));

            case ADD_INGREDIENT_BTN_ROW:
                return new RecipeDetailAddIngredientBTNRowViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(
                                R.layout.list_item_recipe_detail_add_ingredient_btn,
                                parent,
                                false),
                        addIngredientClickListener);

            case DIRECTIONS_ROW:
                return new RecipeDetailDirectionsRowViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(
                                R.layout.directions_multiline_layout,
                                parent,
                                false),
                        directionsChangedListener);
        }
    }

    @Override
    public void onBindViewHolder(RecipeDetailViewHolder holder, int position) {
        holder.bind(rowList.get(position));
    }

    @Override
    public int getItemCount() {
        return rowList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return rowList.get(position).getRowType();
    }

    public class IngredientViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        TextView mMeasurement;
        TextView mName;
        RecipeDetailListAdapter mAdapter;

        public IngredientViewHolder(View itemView, RecipeDetailListAdapter adapter) {
            super(itemView);
            mMeasurement = itemView.findViewById(R.id.tvMeasurement);
            mName = itemView.findViewById(R.id.tvName);
            itemView.setOnLongClickListener(this);
            mAdapter = adapter;
        }


        @Override
        public boolean onLongClick(View view) {
            final int mPosition = getLayoutPosition();
            Ingredient currentIngredient = new Ingredient();

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
