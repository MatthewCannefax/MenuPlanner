package com.matthewcannefax.menuplanner.grocery;

import com.matthewcannefax.menuplanner.recipe.Ingredient;

import java.util.ArrayList;
import java.util.List;

public class GroceryRowBuilder {
    public static List<GroceryRow> buildGroceryRows(final List<Ingredient> groceryList) {
        final List<GroceryRow> groceryRows = new ArrayList<>();
        if (groceryList != null && groceryList.size() != 0) {
            Ingredient previous = groceryList.get(0);
            groceryRows.add(new GroceryHeadingRow(previous.getCategory().toString()));
            groceryRows.add(new GroceryItemRow(previous));
            for (int i = 1; i < groceryList.size(); i++) {
                Ingredient current = groceryList.get(i);

                if (current.getCategory() != previous.getCategory()) {
                    groceryRows.add(new GroceryHeadingRow(current.getCategory().toString()));
                }

                groceryRows.add(new GroceryItemRow(current));
                previous = current;
            }
        }
        return groceryRows;
    }
}
