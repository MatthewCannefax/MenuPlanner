package com.matthewcannefax.menuplanner.utils.database;

public class RecipeTable {
    public static final String TABLE_NAME = "recipe_table";
    public static final String RECIPE_ID = "recipe_id";
    public static final String NAME = "recipe_name";
    public static final String CATEGORY = "category";
    public static final String IMG = "img";
    public static final String DIRECTIONS = "directions";

    public static final String SQL_CREATE =
            "CREATE TABLE " + TABLE_NAME + "(" +
                    RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    NAME + " TEXT," +
                    CATEGORY + " TEXT," +
                    IMG + " TEXT," +
                    DIRECTIONS + " TEXT" + ");";

    public static final String SQL_DELETE_TABLE =
            "DROP TABLE " + TABLE_NAME;
}
