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

    @Insert
    void insertMenuItem(final MenuItem menuItem);

    @Delete
    void deleteMenuItem(final MenuItem menuItem);

    @Insert
    void insertIngredient(final Ingredient ingredient);

    @Update
    void updateIngredient(final Ingredient ingredient);

    @Delete
    void deleteIngredient(final Ingredient ingredient);

    @Insert
    void insertMeasurement(final Measurement measurement);

    @Update
    void updateMeasurement(final Measurement measurement);

    @Delete
    void deleteMeasurement(final Measurement measurement);

    @Insert
    void insertGroceryItem(final GroceryItem groceryItem);

    @Update
    void updateGroceryItem(final GroceryItem groceryItem);

    @Update
    void deleteGroceryItem(final GroceryItem groceryItem);

    @Transaction
    @Query("SELECT * FROM recipe")
    public List<RecipeWithIngredients> getRecipesWithIngredients();

    //TODO this gets all ingredients
    @Transaction
    @Query("SELECT * FROM ingredient")
    public List<IngredientAndMeasurement> getIngredientsAndMeasurements();

    @Transaction
    @Query("SELECT * FROM grocery_item")
    public List<GroceryItemAndMeasurement> getGroceryItemsAndMeasurements();

    @Transaction
    @Query("SELECT * FROM menu_item")
    public List<MenuItemAndRecipe> getMenuRecipes();

    //TODO add these transactions: getRecipeIngredients, getGroceryList,
}
