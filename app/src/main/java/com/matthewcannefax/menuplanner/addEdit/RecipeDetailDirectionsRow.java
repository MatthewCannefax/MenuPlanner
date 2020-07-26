package com.matthewcannefax.menuplanner.addEdit;

public class RecipeDetailDirectionsRow extends RecipeDetailListRow {

    private String directions;

    public RecipeDetailDirectionsRow(String directions) {
        setRowType(DIRECTIONS_ROW);
        this.directions = directions;
    }

    public String getDirections() {
        return this.directions;
    }
}
