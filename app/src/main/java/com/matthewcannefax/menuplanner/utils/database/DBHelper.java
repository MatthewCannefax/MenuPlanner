package com.matthewcannefax.menuplanner.utils.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DB_FILE_NAME = "cookbook.db";
    public static final int DB_VERSION = 1;

    public DBHelper(@Nullable Context context) {
        super(context, DB_FILE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(RecipeTable.SQL_CREATE);
        db.execSQL(IngredientTable.SQL_CREATE);
        db.execSQL(MenuTable.SQL_CREATE);
        db.execSQL(GroceryListTable.SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(RecipeTable.SQL_DELETE_TABLE);
        db.execSQL(IngredientTable.SQL_DELETE_TABLE);
        db.execSQL(MenuTable.SQL_DELETE_TABLE);
        db.execSQL(GroceryListTable.SQL_DELETE_TABLE);

        db.execSQL(RecipeTable.SQL_CREATE);
        db.execSQL(IngredientTable.SQL_CREATE);
        db.execSQL(MenuTable.SQL_CREATE);
        db.execSQL(GroceryListTable.SQL_CREATE);
    }
}
