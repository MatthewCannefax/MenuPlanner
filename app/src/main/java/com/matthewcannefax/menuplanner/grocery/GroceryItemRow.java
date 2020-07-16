package com.matthewcannefax.menuplanner.grocery;

import com.matthewcannefax.menuplanner.recipe.Ingredient;

public class GroceryItemRow extends GroceryRow {

    private Ingredient groceryItem;

    public GroceryItemRow(Ingredient groceryItem) {
        setGroceryRowType(GROCERY_ITEM);
        this.groceryItem = groceryItem;
    }

    public Ingredient getGroceryItem() {
        return groceryItem;
    }

    public void setGroceryItem(Ingredient groceryItem) {
        this.groceryItem = groceryItem;
    }
}
