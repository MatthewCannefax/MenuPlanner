package com.matthewcannefax.menuplanner.grocery;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        GroceryHeadingRow that = (GroceryHeadingRow) o;
        return heading.equals(that.heading);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), heading);
    }
}
