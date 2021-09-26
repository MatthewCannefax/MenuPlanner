package com.matthewcannefax.menuplanner;

import com.matthewcannefax.menuplanner.addEdit.AddRecipeFragment;
import com.matthewcannefax.menuplanner.addEdit.ViewRecipeFragment;
import com.matthewcannefax.menuplanner.grocery.GroceryListFragment;
import com.matthewcannefax.menuplanner.recipe.menuList.MenuListFragment;
import com.matthewcannefax.menuplanner.recipe.recipeList.CookbookFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component
public interface MenuApplicationComponent {
    void inject(MainActivity activity);

    void inject(SplashFragment fragment);

    void inject(MenuListFragment fragment);

    void inject(CookbookFragment fragment);

    void inject(ViewRecipeFragment fragment);

    void inject(AddRecipeFragment fragment);

    void inject(GroceryListFragment fragment);
}
