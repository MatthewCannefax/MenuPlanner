package com.matthewcannefax.menuplanner.addEdit;

import java.util.Objects;

abstract class RecipeDetailListRow {

    private int rowType;
    private String id;

    public static final int HEADING_ROW = 1;
    public static final int INGREDIENT_ITEM_ROW = 2;
    public static final int ADD_INGREDIENT_BTN_ROW = 3;
    public static final int DIRECTIONS_ROW = 4;

    public void setRowType(int rowType) {
        this.rowType = rowType;
    }

    public int getRowType() {
        return rowType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecipeDetailListRow that = (RecipeDetailListRow) o;
        return rowType == that.rowType &&
                Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rowType, id);
    }
}
