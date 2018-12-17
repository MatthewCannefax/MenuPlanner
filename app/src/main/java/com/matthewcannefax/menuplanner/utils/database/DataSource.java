package com.matthewcannefax.menuplanner.utils.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.matthewcannefax.menuplanner.model.Enums.GroceryCategory;
import com.matthewcannefax.menuplanner.model.Enums.MeasurementType;
import com.matthewcannefax.menuplanner.model.Enums.RecipeCategory;
import com.matthewcannefax.menuplanner.model.Ingredient;
import com.matthewcannefax.menuplanner.model.Measurement;
import com.matthewcannefax.menuplanner.model.Recipe;

import java.util.ArrayList;
import java.util.List;

public class DataSource {
    private Context mContext;
    private SQLiteDatabase mDatabase;
    SQLiteOpenHelper mDbHelper;


    public DataSource(Context mContext) {
        this.mContext = mContext;
        mDbHelper = new DBHelper(mContext);
        mDatabase = mDbHelper.getWritableDatabase();
    }

    public void open(){
        mDatabase = mDbHelper.getWritableDatabase();
    }

    public void close(){
        mDbHelper.close();
    }

    public void addToMenu(int recipeID){
        ContentValues contentValues = new ContentValues();
        contentValues.put(MenuTable.COLUMN_RECIPE_ID, recipeID);
        mDatabase.insert(MenuTable.TABLE_NAME, null, contentValues);
    }

    public Recipe createRecipe(Recipe recipe){//build a similar method for ingredients and call it with recipe.ingredients.tovalues
        ContentValues values = recipe.toValuesCreate();
        mDatabase.insert(RecipeTable.TABLE_NAME, null, values);

        //get the id of the last recipe added to the db
        //which is the one just entered above
        int recipeID = getRecipeIDFromDB();

        if (recipe.getIngredientList().size() != 0 && recipe.getIngredientList() != null){
            for(Ingredient i: recipe.getIngredientList()){
                createIngredient(i, recipeID);
            }
        }


        return recipe;
    }

    public Ingredient createIngredient(Ingredient ingredient, int recipeID){
        ContentValues values = ingredient.toValuesCreate(recipeID);
        mDatabase.insert(IngredientTable.TABLE_NAME, null, values);
        return ingredient;
    }

    //this method is created to retrieve the id for the last recipe entered in the recipe table to use as the foreign key in the ingredient table
    public int getRecipeIDFromDB(){
        //query the last recipe added to the db
        Cursor cursor = mDatabase.query(RecipeTable.TABLE_NAME, new String[]{RecipeTable.RECIPE_ID}, null, null, null, null, RecipeTable.RECIPE_ID + " DESC", "1" );
        int newId = 0;
        while (cursor.moveToNext()){
            //get the id of the last recipe recorded in the db
            newId = cursor.getInt(cursor.getColumnIndex(RecipeTable.RECIPE_ID));
        }
        return newId;
    }

    public Recipe getSpecificRecipe(int recipeID){
        Recipe recipe = new Recipe();
        String[] recipeIDS = {Integer.toString(recipeID)};

        Cursor recipeCursor = mDatabase.query(
                RecipeTable.TABLE_NAME,
                RecipeTable.ALL_COLUMNS,
                RecipeTable.RECIPE_ID + "=?",
                recipeIDS,
                null,
                null,
                null);

        while (recipeCursor.moveToNext()){
            recipe.setRecipeID(recipeCursor.getInt(recipeCursor.getColumnIndex(RecipeTable.RECIPE_ID)));
            recipe.setName(recipeCursor.getString(recipeCursor.getColumnIndex(RecipeTable.NAME)));
            recipe.setCategory(RecipeCategory.stringToCategory(recipeCursor.getString(recipeCursor.getColumnIndex(RecipeTable.CATEGORY)).toUpperCase()));
            recipe.setImagePath(recipeCursor.getString(recipeCursor.getColumnIndex(RecipeTable.IMG)));
            recipe.setDirections(recipeCursor.getString(recipeCursor.getColumnIndex(RecipeTable.DIRECTIONS)));
            recipe.setIngredientList(getRecipeIngredients(recipe.getRecipeID()));
        }

        return recipe;
    }

    private Recipe getMenuItems(int id){
        Recipe recipe = new Recipe();

        String[] idArray = {Integer.toString(id)};

        String[] testArray = {"1"};

        Cursor recipeCursor = mDatabase.query(
                RecipeTable.TABLE_NAME,
                RecipeTable.ALL_COLUMNS,
                RecipeTable.RECIPE_ID + "=?",
                idArray,
                null,
                null,
                RecipeTable.NAME
        );

        while (recipeCursor.moveToNext()){
//            Recipe recipe = new Recipe();
            recipe.setRecipeID(recipeCursor.getInt(recipeCursor.getColumnIndex(RecipeTable.RECIPE_ID)));
            recipe.setName(recipeCursor.getString(recipeCursor.getColumnIndex(RecipeTable.NAME)));
            recipe.setCategory(RecipeCategory.stringToCategory(recipeCursor.getString(recipeCursor.getColumnIndex(RecipeTable.CATEGORY))));
            recipe.setImagePath(recipeCursor.getString(recipeCursor.getColumnIndex(RecipeTable.IMG)));
            recipe.setDirections(recipeCursor.getString(recipeCursor.getColumnIndex(RecipeTable.DIRECTIONS)));
            recipe.setIngredientList(getRecipeIngredients(recipe.getRecipeID()));

//            recipes.add(recipe);
        }

        return recipe;
    }

    public List<Recipe> getFilteredRecipes(RecipeCategory category){
        List<Recipe> recipes = new ArrayList<>();

        if(category == RecipeCategory.ALL){
            return getAllRecipes();
        }

        String[] categories = {category.toString()};

        Cursor recipeCursor = mDatabase.query(
                RecipeTable.TABLE_NAME,
                RecipeTable.ALL_COLUMNS,
                RecipeTable.CATEGORY + "=?",
                categories,
                null,
                null,
                RecipeTable.NAME);

        while(recipeCursor.moveToNext()){
            Recipe recipe = new Recipe();
            recipe.setRecipeID(recipeCursor.getInt(recipeCursor.getColumnIndex(RecipeTable.RECIPE_ID)));
            recipe.setName(recipeCursor.getString(recipeCursor.getColumnIndex(RecipeTable.NAME)));
            recipe.setCategory(RecipeCategory.stringToCategory(recipeCursor.getString(recipeCursor.getColumnIndex(RecipeTable.CATEGORY))));
            recipe.setImagePath(recipeCursor.getString(recipeCursor.getColumnIndex(RecipeTable.IMG)));
            recipe.setDirections(recipeCursor.getString(recipeCursor.getColumnIndex(RecipeTable.DIRECTIONS)));
            recipe.setIngredientList(getRecipeIngredients(recipe.getRecipeID()));

            recipes.add(recipe);
        }

        return recipes;
    }

    public List<Recipe> getAllMenuRecipes(){
         List<Integer> menuIDS = new ArrayList<>();

        String[] recipeIDColumn = {MenuTable.COLUMN_RECIPE_ID};

        Cursor menuTableCursor = mDatabase.query(
                MenuTable.TABLE_NAME,
                recipeIDColumn,
                null,
                null,
                null,
                null,
                null
        );

        while(menuTableCursor.moveToNext()){
            int id;
            id = menuTableCursor.getInt(menuTableCursor.getColumnIndex(MenuTable.COLUMN_RECIPE_ID));
            menuIDS.add(id);
        }

        List<Recipe> recipes = new ArrayList<>();

        for (int i:
             menuIDS) {
            recipes.add(getMenuItems(i));
        }

        return recipes;
    }

    public List<Recipe> getAllRecipes(){
        List<Recipe> recipes = new ArrayList<>();

        Cursor recipeCursor = mDatabase.query(
                RecipeTable.TABLE_NAME,
                RecipeTable.ALL_COLUMNS,
                null,
                null,
                null,
                null,
                null);


        while(recipeCursor.moveToNext()){
            Recipe recipe = new Recipe();
            recipe.setRecipeID(recipeCursor.getInt(recipeCursor.getColumnIndex(RecipeTable.RECIPE_ID)));
            recipe.setName(recipeCursor.getString(recipeCursor.getColumnIndex(RecipeTable.NAME)));
            recipe.setCategory(RecipeCategory.stringToCategory(recipeCursor.getString(recipeCursor.getColumnIndex(RecipeTable.CATEGORY))));
            recipe.setImagePath(recipeCursor.getString(recipeCursor.getColumnIndex(RecipeTable.IMG)));
            recipe.setDirections(recipeCursor.getString(recipeCursor.getColumnIndex(RecipeTable.DIRECTIONS)));
            recipe.setIngredientList(getRecipeIngredients(recipe.getRecipeID()));

            recipes.add(recipe);
        }

        return recipes;
    }

    private List<Ingredient> getRecipeIngredients(int recipeID){
        List<Ingredient> ingredients = new ArrayList<>();

        String[] recipeIDS = {Integer.toString(recipeID)};

        Cursor ingredientCursor = mDatabase.query(
                IngredientTable.TABLE_NAME,
                IngredientTable.ALL_COLUMNS,
                IngredientTable.COLUMN_RECIPE_ID +"=?",
                recipeIDS,
                null,
                null,
                null);

        while(ingredientCursor.moveToNext()){
            Ingredient ingredient = new Ingredient();
            ingredient.setIngredientID(ingredientCursor.getInt(ingredientCursor.getColumnIndex(IngredientTable.COLUMN_ID)));
            ingredient.setName(ingredientCursor.getString(ingredientCursor.getColumnIndex(IngredientTable.COLUMN_NAME)));
            ingredient.setCategory(GroceryCategory.stringToCategory(ingredientCursor.getString(ingredientCursor.getColumnIndex(IngredientTable.COLUMN_CATEGORY))));
            ingredient.setMeasurement(new Measurement(
                    ingredientCursor.getDouble(ingredientCursor.getColumnIndex(IngredientTable.COLUMN_MEASUREMENT_AMOUNT)),
                    MeasurementType.stringToCategory(ingredientCursor.getString(ingredientCursor.getColumnIndex(IngredientTable.COLUMN_MEASUREMENT_TYPE)).toUpperCase())
            ));
            ingredients.add(ingredient);

        }

        return ingredients;
    }

    public List<RecipeCategory> getRecipeCategories(){
        List<RecipeCategory> categories = new ArrayList<>();
        String[] columns = {RecipeTable.CATEGORY};

        Cursor cursor = mDatabase.query(RecipeTable.TABLE_NAME, columns, null, null, RecipeTable.CATEGORY, null, RecipeTable.CATEGORY);
        categories.add(RecipeCategory.ALL);
        while (cursor.moveToNext()){
            categories.add(
                    RecipeCategory.stringToCategory(
                            cursor.getString(
                                    cursor.getColumnIndex(
                                            RecipeTable.CATEGORY))));
        }

        return categories;
    }

    public void updateRecipe(Recipe newRecipe){
        Recipe oldRecipe = getSpecificRecipe(newRecipe.getRecipeID());

            String[] ids = {Integer.toString(newRecipe.getRecipeID())};
            mDatabase.update(RecipeTable.TABLE_NAME, newRecipe.toValues(), RecipeTable.RECIPE_ID + "=?", ids);

            int oldSize = oldRecipe.getIngredientList().size();
            int newSize = newRecipe.getIngredientList().size();

            if (oldSize == newSize){
                for(int i = 0; i < newRecipe.getIngredientList().size(); i++){
                    if(!newRecipe.getIngredientList().get(i).equals(oldRecipe.getIngredientList().get(i))){
                        String[] ingredientIDS = {Integer.toString(newRecipe.getIngredientList().get(i).getIngredientID())};
                        mDatabase.update(IngredientTable.TABLE_NAME, newRecipe.getIngredientList().get(i).toValues(newRecipe.getRecipeID()),
                                IngredientTable.COLUMN_ID + "=?", ingredientIDS);
                    }
                }
            }else if(oldSize < newSize){
                for(int i = 0; i < oldSize; i++){
                    if(!newRecipe.getIngredientList().get(i).equals(oldRecipe.getIngredientList().get(i))){
                        String[] ingredientIDS = {Integer.toString(newRecipe.getIngredientList().get(i).getIngredientID())};
                        mDatabase.update(IngredientTable.TABLE_NAME, newRecipe.getIngredientList().get(i).toValues(newRecipe.getRecipeID()),
                                IngredientTable.COLUMN_ID + "=?", ingredientIDS);
                    }
                }

                //loop through new ingredients and insert them into the db
                for (int i = oldSize; i < newSize; i++){
                    createIngredient(newRecipe.getIngredientList().get(i), newRecipe.getRecipeID());
                }

            }else{//oldSize > newSize
                //loop through all ingredients in the new recipe and delete the difference in the old recipe
            }



    }


}
