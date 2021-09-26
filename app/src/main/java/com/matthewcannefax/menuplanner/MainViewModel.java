package com.matthewcannefax.menuplanner;
import androidx.lifecycle.ViewModel;

import com.matthewcannefax.menuplanner.recipe.Ingredient;
import com.matthewcannefax.menuplanner.recipe.Recipe;
import com.matthewcannefax.menuplanner.recipe.RecipeCategory;
import com.matthewcannefax.menuplanner.utils.database.DataSource;

import java.util.List;

public class MainViewModel extends ViewModel {

    private final DataSource dataSource;
    private Recipe selectedRecipe;

    public MainViewModel(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Recipe getSelectedRecipe() {
        return selectedRecipe;
    }

    public void setSelectedRecipe(Recipe selectedRecipe) {
        this.selectedRecipe = selectedRecipe;
    }

    public void addRecipe(final Recipe recipe) {
        dataSource.createRecipe(recipe);
    }

    public List<RecipeCategory> getMenuCategories() {
        return dataSource.getMenuCategoriesFromDB();
    }

    public List<RecipeCategory> getCookbookCategories() {
        return dataSource.getCookbookCategoriesFromDB();
    }

    public List<Recipe> getCookbook() {
        return dataSource.getAllRecipes();
    }

    public int getRecipeId() {
        return dataSource.getRecipeIDFromDB();
    }

    public Recipe getRecipeById(final int id) {
        return dataSource.getSpecificRecipe(id);
    }

    public List<Recipe> getRecipesByCategory(final RecipeCategory recipeCategory) {
        return dataSource.getFilteredRecipes(recipeCategory);
    }

    public void updateRecipe(final Recipe recipe) {
        dataSource.updateRecipe(recipe);
    }

    public void removeRecipe(final Recipe recipe) {
        dataSource.removeRecipe(recipe);
    }

    public void addRecipes(List<Recipe> recipes) {
        dataSource.importRecipesToDB(recipes);
    }

    public void addRecipeToMenu(final int recipeId) {
        dataSource.addToMenu(recipeId);
    }

    public List<Recipe> getMenu() {
        return dataSource.getAllMenuRecipes();
    }

    public void removeRecipeFromMenu(final int recipeId) {
        dataSource.removeMenuItem(recipeId);
    }

    public void removeAllFromMenu() {
        dataSource.removeAllMenuItems();
    }

    public void addIngredientToRecipe(final Ingredient ingredient, final int recipeId) {
        dataSource.createIngredient(ingredient, recipeId);
    }

    public void removeIngredient(final int ingredientId) {
        dataSource.removeSpecificIngredient(ingredientId);
    }

    public void removeAllIngredients(final int recipeId) {
        dataSource.removeRecipeIngredients(recipeId);
    }

    public Ingredient getIngredientByText(final CharSequence charSequence) {
        return dataSource.getSpecificIngredient(charSequence);
    }

    public void createGroceryListFromMenu() {
        dataSource.menuIngredientsToGroceryDB();
    }

    public void addGroceryItem(final Ingredient ingredient) {
        dataSource.createGroceryItem(ingredient);
    }

    public void removeGroceryItem(final Ingredient ingredient) {
        dataSource.removeGroceryItem(ingredient);
    }

    public void removeAllGroceries() {
        dataSource.removeAllGroceries();
    }

    public void setGroceryItemChecked(final int groceryId, final boolean checked) {
        dataSource.setGroceryItemChecked(groceryId, checked);
    }

    public List<Ingredient> getAllGroceries() {
        return dataSource.getAllGroceries();
    }
 }
