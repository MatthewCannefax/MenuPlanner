package com.matthewcannefax.menuplanner.utils.database;

public class MenuTable {
    public static final String TABLE_NAME = "menu_table";
    public static final String COLUMN_ID = "menu_id";
    public static final String COLUMN_RECIPE_ID = "recipe_id";
    public static final String COLUMN_FOREIGN_KEY = "fk_recipe_id";

    public static final String CREATE_SQL =
            "CREATE TABLE " + TABLE_NAME + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_RECIPE_ID + " INTEGER," +
                    " CONSTRAINT " + COLUMN_FOREIGN_KEY +
                    " FOREIGN KEY (" + COLUMN_RECIPE_ID + ")" +
                    " REFERENCES " + RecipeTable.TABLE_NAME + "(" + RecipeTable.RECIPE_ID + ")" +
                    " ON CASCADE DELETE" + ");";
}
