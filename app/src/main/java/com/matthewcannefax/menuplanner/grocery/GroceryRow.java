package com.matthewcannefax.menuplanner.grocery;

abstract class GroceryRow {
    private int groceryRowType;

    public static final int GROCERY_ITEM = 1;
    public static final int GROCERY_HEADER = 2;

    public void setGroceryRowType(int groceryRowType) {
        this.groceryRowType = groceryRowType;
    }

    public int getGroceryRowType() {
        return groceryRowType;
    }
}
