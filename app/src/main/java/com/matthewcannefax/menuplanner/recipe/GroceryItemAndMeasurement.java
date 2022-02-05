package com.matthewcannefax.menuplanner.recipe;

import androidx.room.Embedded;
import androidx.room.Relation;

public class GroceryItemAndMeasurement {
    @Embedded
    public GroceryItem groceryItem;
    @Relation(
            parentColumn = "groceryItemId",
            entityColumn = "itemId"
    )
    public Measurement measurement;
}
