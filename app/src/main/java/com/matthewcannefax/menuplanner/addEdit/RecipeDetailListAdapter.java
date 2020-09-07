package com.matthewcannefax.menuplanner.addEdit;

import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.recipe.Recipe;

import java.util.List;

import static com.matthewcannefax.menuplanner.addEdit.RecipeDetailListRow.ADD_INGREDIENT_BTN_ROW;
import static com.matthewcannefax.menuplanner.addEdit.RecipeDetailListRow.DIRECTIONS_ROW;
import static com.matthewcannefax.menuplanner.addEdit.RecipeDetailListRow.HEADING_ROW;
import static com.matthewcannefax.menuplanner.addEdit.RecipeDetailListRow.INGREDIENT_ITEM_ROW;

public class RecipeDetailListAdapter extends ListAdapter<RecipeDetailListRow, RecipeDetailViewHolder> {

    private AddIngredientClickListener addIngredientClickListener;
    private DirectionsChangedListener directionsChangedListener;
    private IngredientLongClickListener longClickListener;

    public RecipeDetailListAdapter(
            AddIngredientClickListener addIngredientClickListener,
            DirectionsChangedListener directionsChangedListener,
            IngredientLongClickListener longClickListener) {
        super(new RecipeDetailDiffUtil());
        this.addIngredientClickListener = addIngredientClickListener;
        this.directionsChangedListener = directionsChangedListener;
        this.longClickListener = longClickListener;
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
                                false),
                        longClickListener);

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
        holder.bind(getItem(position));
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getRowType();
    }

}
