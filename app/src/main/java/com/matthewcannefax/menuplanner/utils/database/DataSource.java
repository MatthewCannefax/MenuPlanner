package com.matthewcannefax.menuplanner.utils.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.matthewcannefax.menuplanner.model.Enums.GroceryCategory;
import com.matthewcannefax.menuplanner.model.Enums.MeasurementType;
import com.matthewcannefax.menuplanner.model.Enums.RecipeCategory;
import com.matthewcannefax.menuplanner.model.Ingredient;
import com.matthewcannefax.menuplanner.model.Measurement;
import com.matthewcannefax.menuplanner.model.Recipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DataSource {
    private Context mContext;
    private SQLiteDatabase mDatabase;
    SQLiteOpenHelper mDbHelper;

    private static final String USED_RECIPE_CATEGORY_STRING =
            "SELECT category FROM recipe_table " +
            "GROUP BY category ORDER BY category";

    public static String getRecipeCategoryString(){
        return USED_RECIPE_CATEGORY_STRING;
    }

    private static final String USED_MENU_CATEGORY_STRING =
            "SELECT a.category FROM recipe_table AS a " +
            "INNER JOIN menu_table AS b ON b.recipe_id = a.recipe_id " +
            "GROUP BY a.category ORDER BY a.category";

    public static String getMenuCategoryString(){
        return USED_MENU_CATEGORY_STRING;
    }

    //region Constructor/Open/Close
    //constructor
    public DataSource(Context mContext) {
        this.mContext = mContext;
        mDbHelper = new DBHelper(mContext);
        mDatabase = mDbHelper.getWritableDatabase();
    }

    //get the database
    public void open(){
        mDatabase = mDbHelper.getWritableDatabase();
    }

    //close the database
    public void close(){
        mDbHelper.close();
    }
//endregion

    //region Recipe Table Statements
    public Recipe createRecipe(Recipe recipe){//build a similar method for ingredients and call it with recipe.ingredients.tovalues

        if(!mDatabase.isOpen()){
            open();
        }

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

        close();

        return recipe;
    }

    public List<RecipeCategory> getUsedCategoriesFromDB(String sqlString){

        List<RecipeCategory> categories = new ArrayList<>();
        categories.add(RecipeCategory.ALL);

        if(!mDatabase.isOpen()){
            open();
        }

        Cursor cursor = mDatabase.rawQuery(sqlString, null);

        while (cursor.moveToNext()){
            categories.add(RecipeCategory.stringToCategory(cursor.getString(cursor.getColumnIndex(RecipeTable.CATEGORY))));
        }

        close();

        return categories;
    }

    public List<Recipe> getAllRecipes(){

        if(!mDatabase.isOpen()){
            open();
        }

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

        close();

        return recipes;
    }

    //this method is created to retrieve the id for the last recipe entered in the recipe table to use as the foreign key in the ingredient table
    public int getRecipeIDFromDB(){
        if(!mDatabase.isOpen()){
            open();
        }
        //query the last recipe added to the db
        Cursor cursor = mDatabase.query(RecipeTable.TABLE_NAME, new String[]{RecipeTable.RECIPE_ID}, null, null, null, null, RecipeTable.RECIPE_ID + " DESC", "1" );
        int newId = 0;
        while (cursor.moveToNext()){
            //get the id of the last recipe recorded in the db
            newId = cursor.getInt(cursor.getColumnIndex(RecipeTable.RECIPE_ID));
        }
        close();
        return newId;
    }

    public Recipe getSpecificRecipe(int recipeID){

        if(!mDatabase.isOpen()){
            open();
        }

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
            recipe.setCategory(RecipeCategory.stringToCategory(recipeCursor.getString(recipeCursor.getColumnIndex(RecipeTable.CATEGORY))));
            recipe.setImagePath(recipeCursor.getString(recipeCursor.getColumnIndex(RecipeTable.IMG)));
            recipe.setDirections(recipeCursor.getString(recipeCursor.getColumnIndex(RecipeTable.DIRECTIONS)));
            recipe.setIngredientList(getRecipeIngredients(recipe.getRecipeID()));
        }

        close();

        return recipe;
    }

    private Recipe getMenuItems(int id){

        if(!mDatabase.isOpen()){
            open();
        }

        Recipe recipe = new Recipe();

        String[] idArray = {Integer.toString(id)};

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
            recipe.setRecipeID(recipeCursor.getInt(recipeCursor.getColumnIndex(RecipeTable.RECIPE_ID)));
            recipe.setName(recipeCursor.getString(recipeCursor.getColumnIndex(RecipeTable.NAME)));
            recipe.setCategory(RecipeCategory.stringToCategory(recipeCursor.getString(recipeCursor.getColumnIndex(RecipeTable.CATEGORY))));
            recipe.setImagePath(recipeCursor.getString(recipeCursor.getColumnIndex(RecipeTable.IMG)));
            recipe.setDirections(recipeCursor.getString(recipeCursor.getColumnIndex(RecipeTable.DIRECTIONS)));
            recipe.setIngredientList(getRecipeIngredients(recipe.getRecipeID()));
        }

        close();

        return recipe;
    }

    public List<Recipe> getFilteredRecipes(RecipeCategory category){

        if(!mDatabase.isOpen()){
            open();
        }

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

        close();

        return recipes;
    }

    public List<RecipeCategory> getRecipeCategories(){

        if(!mDatabase.isOpen()){
            open();
        }

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

        close();

        return categories;
    }

    public void updateRecipe(Recipe newRecipe){

        if(!mDatabase.isOpen()){
            open();
        }

        Recipe oldRecipe = getSpecificRecipe(newRecipe.getRecipeID());

        String[] ids = {Integer.toString(newRecipe.getRecipeID())};

        if(!mDatabase.isOpen()){
            open();
        }

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
            for(int i = 0; i < newSize; i++){
                if(!newRecipe.getIngredientList().get(i).equals(oldRecipe.getIngredientList().get(i))){
                    String[] ingredientIDS = {Integer.toString(newRecipe.getIngredientList().get(i).getIngredientID())};
                    mDatabase.update(IngredientTable.TABLE_NAME, newRecipe.getIngredientList().get(i).toValues(newRecipe.getRecipeID()),
                            IngredientTable.COLUMN_ID + "=?", ingredientIDS);
                }
            }
            for(int i = newSize; i < oldSize; i++){
                removeSpecificIngredient(oldRecipe.getIngredientList().get(i).getIngredientID());
            }
        }

        close();
    }

    public void removeRecipe(Recipe recipe){

        if(!mDatabase.isOpen()){
            open();
        }

        String[] ids = {Integer.toString(recipe.getRecipeID())};
        removeMenuItem(recipe.getRecipeID());
        removeRecipeIngredients(recipe.getRecipeID());

        if(!mDatabase.isOpen()){
            open();
        }

        mDatabase.delete(RecipeTable.TABLE_NAME, RecipeTable.RECIPE_ID + "=?", ids);

        close();
    }

    public void importRecipesToDB(List<Recipe> importedRecipes){
        for (Recipe r :
                importedRecipes) {
            createRecipe(r);
        }
    }
    //endregion

    //region Menu Table Statements
    public void addToMenu(int recipeID){

        if(!mDatabase.isOpen()){
            open();
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(MenuTable.COLUMN_RECIPE_ID, recipeID);
        mDatabase.insert(MenuTable.TABLE_NAME, null, contentValues);

        close();

    }

    public List<Recipe> getAllMenuRecipes(){

        if(!mDatabase.isOpen()){
            open();
        }

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

        close();

        return recipes;
    }

    public void removeMenuItem(int recipID){

        if(!mDatabase.isOpen()){
            open();
        }

        String[] ids = {Integer.toString(recipID)};

        String recipeIDStatement = String.format("SELECT menu_id FROM menu_table WHERE recipe_id = %s LIMIT 1", recipID);
        mDatabase.execSQL(String.format("DELETE FROM %s WHERE menu_id = (%s)", MenuTable.TABLE_NAME, recipeIDStatement));


        close();

    }

    public void removeAllMenuItems(){
        if(!mDatabase.isOpen()){
            open();
        }

        mDatabase.delete(MenuTable.TABLE_NAME, null, null);

        close();
    }

    //endregion

    //region Ingredient Table Statements
    public Ingredient createIngredient(Ingredient ingredient, int recipeID){

        if(!mDatabase.isOpen()){
            open();
        }

        ContentValues values = ingredient.toValuesCreate(recipeID);
        mDatabase.insert(IngredientTable.TABLE_NAME, null, values);

        close();

        return ingredient;
    }

    private List<Ingredient> getRecipeIngredients(int recipeID){

        if(!mDatabase.isOpen()){
            open();
        }

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

        close();

        return ingredients;
    }

    public void removeSpecificIngredient(int ingredientID){
        if(!mDatabase.isOpen()){
            open();
        }

        String[] ids = {Integer.toString(ingredientID)};
        mDatabase.delete(IngredientTable.TABLE_NAME, IngredientTable.COLUMN_ID + "=?", ids);

        close();
    }

    public void removeRecipeIngredients(int recipeID){

        if(!mDatabase.isOpen()){
            open();
        }

        String[] ids = {Integer.toString(recipeID)};
        mDatabase.delete(IngredientTable.TABLE_NAME, IngredientTable.COLUMN_RECIPE_ID + "=?", ids);

        close();

    }
    //endregion

    //region Grocery List Table Statements

    public void menuIngredientsToGroceryDB(){
        if(!mDatabase.isOpen()){
            open();
        }

        String sqlString = "INSERT INTO grocery_list_table (grocery_name, grocery_category, measurement_amount, measurement_type)\n" +
                "SELECT a.ingredient_name, a.category, SUM(a.measurement_amount), a.measurement_type  \n" +
                "FROM ingredient_table AS a \n" +
                "INNER JOIN menu_table AS b ON b.recipe_id = a.recipe_id\n" +
                "GROUP BY UPPER(a.ingredient_name), a.category, a.measurement_type\n" +
                "ORDER BY a.category, UPPER(a.ingredient_name);";

        String deleteWaterString ="DELETE FROM grocery_list_table WHERE grocery_id = (SELECT grocery_id FROM grocery_list_table WHERE UPPER(grocery_name) = 'WATER');";

        mDatabase.execSQL(sqlString);
        mDatabase.execSQL(deleteWaterString);

        close();
    }

    public void groceryListToDB(List<Ingredient> groceries){
        for (Ingredient i :
                groceries) {
            createGroceryItem(i);
        }
    }

    public void createGroceryItem(Ingredient ingredient){

        if(!mDatabase.isOpen()){
            open();
        }

        ContentValues values = ingredient.toValuesGroceryList();
        mDatabase.insert(GroceryListTable.TABLE_NAME, null, values);

        close();

    }



    public void removeGroceryItem(Ingredient ingredient){

        if(!mDatabase.isOpen()){
            open();
        }

        String[] ids = {Integer.toString(ingredient.getIngredientID())};
        mDatabase.delete(GroceryListTable.TABLE_NAME, GroceryListTable.COLUMN_ID + "=?", ids);

        close();
    }

    public void removeAllGroceries(){
        if(!mDatabase.isOpen()){
            open();
        }

        mDatabase.delete(GroceryListTable.TABLE_NAME, null, null);

        close();
    }


    public void setGroceryItemChecked(int groceryItemID, boolean itemChecked){
        Ingredient selectedGroceryItem = new Ingredient();
        String[] ids = {Integer.toString(groceryItemID)};

        if(!mDatabase.isOpen()){
            open();
        }

        Cursor cursor = mDatabase.query(GroceryListTable.TABLE_NAME,
                GroceryListTable.ALL_COLUMNS,
                GroceryListTable.COLUMN_ID + "=?",
                ids,
                null,
                null,
                null
                );

        while (cursor.moveToNext()){
            selectedGroceryItem.setIngredientID(groceryItemID);
            selectedGroceryItem.setName(cursor.getString(cursor.getColumnIndex(GroceryListTable.COLUMN_NAME)));
            selectedGroceryItem.setCategory(GroceryCategory.stringToCategory(cursor.getString(cursor.getColumnIndex(GroceryListTable.COLUMN_CATEGORY))));
            selectedGroceryItem.setMeasurement(new Measurement(
                    cursor.getDouble(cursor.getColumnIndex(GroceryListTable.COLUMN_MEASUREMENT_AMOUNT)),
                    MeasurementType.stringToCategory(cursor.getString(cursor.getColumnIndex(GroceryListTable.COLUMN_MEASUREMENT_TYPE)).toUpperCase())
            ));
            selectedGroceryItem.setItemChecked(isItemChecked(cursor.getInt(cursor.getColumnIndex(GroceryListTable.COLUMN_IS_CHECKED))));
        }

        selectedGroceryItem.setItemChecked(itemChecked);

        mDatabase.update(GroceryListTable.TABLE_NAME, selectedGroceryItem.toValuesGroceryList(), GroceryListTable.COLUMN_ID + "=?", ids);

        close();
    }


    public List<Ingredient> getAllGroceries(){

        if(!mDatabase.isOpen()){
            open();
        }

        List<Ingredient> groceries = new ArrayList<>();

        Cursor cursor = mDatabase.query(
                GroceryListTable.TABLE_NAME,
                GroceryListTable.ALL_COLUMNS,
                null,
                null,
                null,
                null,
                null
        );

        while(cursor.moveToNext()){
            Ingredient ingredient = new Ingredient();
            ingredient.setIngredientID(cursor.getInt(cursor.getColumnIndex(GroceryListTable.COLUMN_ID)));
            ingredient.setName(cursor.getString(cursor.getColumnIndex(GroceryListTable.COLUMN_NAME)));
            ingredient.setCategory(GroceryCategory.stringToCategory(cursor.getString(cursor.getColumnIndex(GroceryListTable.COLUMN_CATEGORY))));
            ingredient.setMeasurement(new Measurement(
                    cursor.getDouble(cursor.getColumnIndex(GroceryListTable.COLUMN_MEASUREMENT_AMOUNT)),
                    MeasurementType.stringToCategory(cursor.getString(cursor.getColumnIndex(GroceryListTable.COLUMN_MEASUREMENT_TYPE)).toUpperCase())
            ));
            ingredient.setItemChecked(isItemChecked(cursor.getInt(cursor.getColumnIndex(GroceryListTable.COLUMN_IS_CHECKED))));

            groceries.add(ingredient);
        }

        //sort the grocery list so that the categories are grouped together
        Collections.sort(groceries, new Comparator<Ingredient>() {
            @Override
            public int compare(Ingredient ingredient, Ingredient ingredient2) {
                return ingredient.getCategory().toString().compareTo(ingredient2.getCategory().toString());
            }
        });

        close();

        return groceries;
    }

    private boolean isItemChecked(int i){
        if(i == 1){
            return true;
        }
        return false;
    }
    //endregion
}
