package com.matthewcannefax.menuplanner.utils.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class RecipeTable {
    public static final String TABLE_NAME = "recipe_table";
    public static final String RECIPE_ID = "recipe_id";
    public static final String NAME = "recipe_name";
    public static final String CATEGORY = "category";
    public static final String IMG = "img";
    public static final String DIRECTIONS = "directions";

    public static final String[] ALL_COLUMNS =
            {RECIPE_ID, NAME, CATEGORY, IMG, DIRECTIONS};

    public static final String SQL_CREATE =
            "CREATE TABLE " + TABLE_NAME + "(" +
                    RECIPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    NAME + " TEXT," +
                    CATEGORY + " TEXT," +
                    IMG + " TEXT," +
                    DIRECTIONS + " TEXT" + ");";

    public static final String SQL_DELETE_TABLE =
            "DROP TABLE " + TABLE_NAME;

    public static final String SQL_COUNT_ALL_ROWS = "SELECT count(*) FROM " + TABLE_NAME;

    public static boolean isNotEmpty(Context context){
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery(SQL_COUNT_ALL_ROWS, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        db.close();
        if (count > 0){
            return true;
        }else {
            return false;
        }
    }
}
