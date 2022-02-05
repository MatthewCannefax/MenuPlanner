package com.matthewcannefax.menuplanner.recipe;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

@androidx.room.Database(entities = {Recipe.class, Ingredient.class, Measurement.class}, exportSchema = false, version = 3)
public abstract class RecipeDatabase extends RoomDatabase {

    private static final String DB_NAME = "recipe_database";
    private static RecipeDatabase instance;

    public static synchronized RecipeDatabase getInstance(final Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), RecipeDatabase.class,
                    DB_NAME)
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }

    public abstract RecipeDao recipeDao();
}
