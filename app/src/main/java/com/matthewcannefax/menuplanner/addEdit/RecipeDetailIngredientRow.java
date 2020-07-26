package com.matthewcannefax.menuplanner.addEdit;

import com.matthewcannefax.menuplanner.recipe.Ingredient;

public class RecipeDetailIngredientRow extends RecipeDetailListRow {

    private Ingredient ingredient;

    public RecipeDetailIngredientRow(Ingredient ingredient) {
        setRowType(INGREDIENT_ITEM_ROW);
        this.ingredient = ingredient;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }
}
