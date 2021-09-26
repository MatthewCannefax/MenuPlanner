package com.matthewcannefax.menuplanner;

import androidx.lifecycle.ViewModel;

import com.matthewcannefax.menuplanner.recipe.Recipe;

public class MainViewModel extends ViewModel {
    private Recipe selectedRecipe;

    public Recipe getSelectedRecipe() {
        return selectedRecipe;
    }

    public void setSelectedRecipe(Recipe selectedRecipe) {
        this.selectedRecipe = selectedRecipe;
    }
}
