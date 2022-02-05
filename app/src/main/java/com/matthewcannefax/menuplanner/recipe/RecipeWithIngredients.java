package com.matthewcannefax.menuplanner.recipe;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class RecipeWithIngredients {
    @Embedded public Recipe recipe;
    @Relation(
            parentColumn = "recipeID",
            entityColumn = "recipeId"
    )
    public List<Ingredient> ingredients;
}
