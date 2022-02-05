package com.matthewcannefax.menuplanner.recipe;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

@Dao
public interface RecipeDao {
    @Query("SELECT * FROM recipe")
    List<Recipe> getAllRecipes();

    @Insert
    void insertRecipe(final Recipe recipe);

    @Update
    void updateRecipe(final Recipe recipe);

    @Delete
    void deleteRecipe(final Recipe recipe);

    @Transaction
    @Query("SELECT * FROM recipe")
    public List<RecipeWithIngredients> getRecipesWithIngredients();

    //TODO this gets all ingredients
    @Transaction
    @Query("SELECT * FROM ingredient")
    public List<IngredientAndMeasurement> getIngredientsAndMeasurements();

    //TODO add these transactions: getRecipeIngredients, getGroceryList,
}
