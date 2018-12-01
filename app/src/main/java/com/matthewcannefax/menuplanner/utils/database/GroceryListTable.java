package com.matthewcannefax.menuplanner.utils.database;

public class GroceryListTable {
    public static final String TABLE_NAME = "grocery_list_table";
    public static final String COLUMN_ID = "grocery_id";
    public static final String COLUMN_NAME = "grocery_name";
    public static final String COLUMN_MEASUREMENT_STRING = "measurement_string";
    public static final String COLUMN_IS_CHECKED = "is_checked";

    public static final String CREATE_SQL =
            "CREATE TABLE " + TABLE_NAME + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_NAME + " TEXT," +
                    COLUMN_MEASUREMENT_STRING + " TEXT," +
                    COLUMN_IS_CHECKED + " INTEGER" + ");";
}
