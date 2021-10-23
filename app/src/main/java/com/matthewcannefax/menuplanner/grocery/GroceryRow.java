package com.matthewcannefax.menuplanner.grocery;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroceryRow that = (GroceryRow) o;
        return groceryRowType == that.groceryRowType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(groceryRowType);
    }
}
