package com.matthewcannefax.menuplanner.addEdit;

import com.matthewcannefax.menuplanner.recipe.Ingredient;

import java.util.Objects;

public class RecipeDetailIngredientRow extends RecipeDetailListRow {

    private Ingredient ingredient;

    public RecipeDetailIngredientRow(Ingredient ingredient) {
        setRowType(INGREDIENT_ITEM_ROW);
        setId(Integer.toString(ingredient.getIngredientID()));
        this.ingredient = ingredient;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        RecipeDetailIngredientRow that = (RecipeDetailIngredientRow) o;
        return Objects.equals(ingredient, that.ingredient);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), ingredient);
    }
}
