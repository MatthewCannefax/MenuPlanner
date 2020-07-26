package com.matthewcannefax.menuplanner.addEdit;

public class RecipeDetailHeadingRow extends RecipeDetailListRow {
    private String heading;

    public RecipeDetailHeadingRow(String heading) {
        setRowType(HEADING_ROW);
        this.heading = heading;
    }

    public String getHeading() {
        return heading;
    }
}
