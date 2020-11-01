package com.matthewcannefax.menuplanner.recipe.menuList;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.matthewcannefax.menuplanner.recipe.Recipe;
import com.matthewcannefax.menuplanner.recipe.RecipeCategory;
import com.matthewcannefax.menuplanner.utils.database.DataSource;

import java.util.List;

public class MenuListViewModel extends ViewModel {
    private MutableLiveData<List<Recipe>> menuList = new MutableLiveData<>();
    private DataSource dataSource;

    public void setDataSource(Context context) {
        dataSource = new DataSource(context);
    }

    public void loadMenu() {
        menuList.postValue(dataSource.getAllMenuRecipes());
    }

    public MutableLiveData<List<Recipe>> getMenuList() {
        return menuList;
    }

    public void filterRecipes(RecipeCategory category) {
        menuList.postValue(dataSource.getFilteredMenuRecipes(category));
    }

    public void createGroceryList() {
        dataSource.menuIngredientsToGroceryDB();
    }

    public void removeAllMenuItems() {
        dataSource.removeAllMenuItems();
    }

    public void removeMenuItem(int id) {
        dataSource.removeMenuItem(id);
        loadMenu();
    }

    public boolean isCookbookEmpty() {
        List<Recipe> allRecipes = dataSource.getAllRecipes();
        return (allRecipes == null || allRecipes.size() == 0);
    }
 }
