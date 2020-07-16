package com.matthewcannefax.menuplanner.grocery;

public class GroceryHeadingRow extends GroceryRow{
    private String heading;

    public GroceryHeadingRow(String heading) {
        setGroceryRowType(GROCERY_HEADER);
        this.heading = heading;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }
}
