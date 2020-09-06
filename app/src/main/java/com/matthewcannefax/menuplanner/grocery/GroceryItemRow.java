package com.matthewcannefax.menuplanner.grocery;

import com.matthewcannefax.menuplanner.recipe.Ingredient;

import java.util.Objects;

public class GroceryItemRow extends GroceryRow {

    private Ingredient groceryItem;

    public GroceryItemRow(Ingredient groceryItem) {
        setGroceryRowType(GROCERY_ITEM);
        this.groceryItem = groceryItem;
        setId(Integer.toString(this.groceryItem.getIngredientID()));
    }

    public Ingredient getGroceryItem() {
        return groceryItem;
    }

    public void setGroceryItem(Ingredient groceryItem) {
        this.groceryItem = groceryItem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        GroceryItemRow that = (GroceryItemRow) o;
        return Objects.equals(groceryItem, that.groceryItem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), groceryItem);
    }
}
