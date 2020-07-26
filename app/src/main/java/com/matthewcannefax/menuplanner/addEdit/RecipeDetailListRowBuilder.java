package com.matthewcannefax.menuplanner.addEdit;

import android.content.Context;

import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.recipe.Ingredient;
import com.matthewcannefax.menuplanner.recipe.Recipe;

import java.util.ArrayList;
import java.util.List;

public class RecipeDetailListRowBuilder {

    private Recipe recipe;
    private Context context;

    public RecipeDetailListRowBuilder(Context context, Recipe recipe) {
        this.recipe = recipe;
        this.context = context;
    }

    public List<RecipeDetailListRow> build() {
        List<RecipeDetailListRow> rowList = new ArrayList<>();
        rowList.add(new RecipeDetailHeadingRow(context.getString(R.string.ingredient_header)));
        if (recipe.getIngredientList() != null && recipe.getIngredientList().size() != 0) {
            for (Ingredient ingredient : recipe.getIngredientList()) {
                rowList.add(new RecipeDetailIngredientRow(ingredient));
            }
        }

        rowList.add(new RecipeDetailAddIngredientBTNRow());
        rowList.add(new RecipeDetailHeadingRow(context.getString(R.string.directions_heading)));
        if (recipe.getDirections() != null && !(recipe.getDirections().equals(""))) {
            rowList.add(new RecipeDetailDirectionsRow(recipe.getDirections()));
        } else {
            rowList.add(new RecipeDetailDirectionsRow(""));
        }

        return rowList;
    }

}
