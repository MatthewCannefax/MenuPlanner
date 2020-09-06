package com.matthewcannefax.menuplanner.grocery;

import android.os.Build;
import androidx.annotation.RequiresApi;

import java.util.Objects;

abstract class GroceryRow {
    private int groceryRowType;
    private String id;

    public static final int GROCERY_ITEM = 1;
    public static final int GROCERY_HEADER = 2;

    public void setGroceryRowType(int groceryRowType) {
        this.groceryRowType = groceryRowType;
    }

    public int getGroceryRowType() {
        return groceryRowType;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final GroceryRow that = (GroceryRow) o;
        return groceryRowType == that.groceryRowType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(groceryRowType);
    }
}
