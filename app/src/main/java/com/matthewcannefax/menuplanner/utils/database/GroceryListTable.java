package com.matthewcannefax.menuplanner.utils.database;

public class GroceryListTable {
    public static final String TABLE_NAME = "grocery_list_table";
    public static final String COLUMN_ID = "grocery_id";
    public static final String COLUMN_NAME = "grocery_name";
    public static final String COLUMN_CATEGORY = "grocery_category";
    public static final String COLUMN_MEASUREMENT_AMOUNT = "measurement_amount";
    public static final String COLUMN_MEASUREMENT_TYPE = "measurement_type";
    public static final String COLUMN_IS_CHECKED = "is_checked";

    public static final String[] ALL_COLUMNS = {COLUMN_ID, COLUMN_NAME, COLUMN_CATEGORY, COLUMN_MEASUREMENT_AMOUNT, COLUMN_MEASUREMENT_TYPE, COLUMN_IS_CHECKED};

    public static final String SQL_CREATE =
            "CREATE TABLE " + TABLE_NAME + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_NAME + " TEXT," +
                    COLUMN_CATEGORY + " TEXT," +
                    COLUMN_MEASUREMENT_AMOUNT + " REAL," +
                    COLUMN_MEASUREMENT_TYPE + " TEXT," +
                    COLUMN_IS_CHECKED + " INTEGER" + ");";

    public static final String SQL_DELETE_TABLE =
            "DROP TABLE " + TABLE_NAME;
}
