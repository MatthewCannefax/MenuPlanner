package com.matthewcannefax.menuplanner;
import androidx.lifecycle.ViewModel;

import com.matthewcannefax.menuplanner.recipe.Recipe;
import com.matthewcannefax.menuplanner.utils.database.DataSource;

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
}
