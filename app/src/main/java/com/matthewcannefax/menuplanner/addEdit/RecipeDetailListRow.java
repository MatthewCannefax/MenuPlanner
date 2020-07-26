package com.matthewcannefax.menuplanner.addEdit;

abstract class RecipeDetailListRow {

    private int rowType;

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
}
