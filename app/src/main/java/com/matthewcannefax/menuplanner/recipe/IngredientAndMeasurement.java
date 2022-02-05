package com.matthewcannefax.menuplanner.recipe;

import androidx.room.Embedded;
import androidx.room.Relation;

public class IngredientAndMeasurement {
    @Embedded public Ingredient ingredient;
    @Relation(
            parentColumn = "ingredientID",
            entityColumn = "ingredientId"
    )
    public Measurement measurement;
}
