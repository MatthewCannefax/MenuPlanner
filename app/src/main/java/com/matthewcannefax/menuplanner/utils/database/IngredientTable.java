package com.matthewcannefax.menuplanner.utils.database;

public class IngredientTable {
    public static final String TABLE_NAME = "ingredient_table";
    public static final String COLUMN_ID = "ingredient_id";
    public static final String COLUMN_RECIPE_ID = "recipe_id";
    public static final String COLUMN_NAME = "ingredient_name";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_MEASUREMENT_AMOUNT = "measurement_amount";
    public static final String COLUMN_MEASUREMENT_TYPE = "measurement_type";
    public static final String COLUMN_FOREIGN_KEY = "fk_recipe_id";

    public static final String CREATE_SQL =
            "CREATE TABLE " + TABLE_NAME +"(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_RECIPE_ID + " INTEGER NOT NULL," +
                    COLUMN_NAME + " TEXT," +
                    COLUMN_CATEGORY + " TEXT," +
                    COLUMN_MEASUREMENT_AMOUNT + " REAL," +
                    COLUMN_MEASUREMENT_TYPE + " TEXT" +
                    " CONSTRAINT " + COLUMN_FOREIGN_KEY +
                    " FOREIGN KEY (" + COLUMN_RECIPE_ID + ")" +
                    " REFERENCES " + RecipeTable.TABLE_NAME + "(" + RecipeTable.RECIPE_ID + ")" +
                    " ON DELETE CASCADE" + ");";
}
