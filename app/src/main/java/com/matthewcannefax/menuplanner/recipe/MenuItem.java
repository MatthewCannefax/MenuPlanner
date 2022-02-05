package com.matthewcannefax.menuplanner.recipe;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "menu_item")
public class MenuItem {
    @PrimaryKey(autoGenerate = true)
    private int menuItemId;
    private int recipeId;

    public int getMenuItemId() {
        return menuItemId;
    }

    public void setMenuItemId(int menuItemId) {
        this.menuItemId = menuItemId;
    }

    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }
}
