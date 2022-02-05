package com.matthewcannefax.menuplanner.recipe;

import androidx.room.Embedded;
import androidx.room.Relation;

public class MenuItemAndRecipe {
    @Embedded
    public MenuItem menuItem;
    @Relation(
            parentColumn = "recipeId",
            entityColumn = "recipeID"
    )
    public Recipe recipe;
}
