package com.matthewcannefax.menuplanner.recipe;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "grocery_item")
public class GroceryItem extends BaseItem {
    @PrimaryKey
    private int groceryItemId;

    public int getGroceryItemId() {
        return groceryItemId;
    }

    public void setGroceryItemId(int groceryItemId) {
        this.groceryItemId = groceryItemId;
    }
}
