package com.matthewcannefax.menuplanner.utils;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.matthewcannefax.menuplanner.model.Ingredient;
import com.matthewcannefax.menuplanner.model.Recipe;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class JSONHelper {
    private static final String TAG = "JSONHelper";
    private static String mFileName;


    public static boolean exportRecipesToJSON(Context context, List<Recipe> recipeList, String fileName){
        mFileName = fileName;

        Recipes recipeData = new Recipes();
        recipeData.setRecipeList(recipeList);

        Gson gson = new Gson();
        String jsonString = gson.toJson(recipeData);
        Log.i(TAG, "exportRecipesToJSON: " + jsonString);

        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = context.openFileOutput(mFileName, MODE_PRIVATE);
            fileOutputStream.write(jsonString.getBytes());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(fileOutputStream != null){
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public static List<Recipe> importRecipesFromJSON(Context context, String fileName){
        mFileName = fileName;

        FileReader reader = null;


        try {
            File file = new File(context.getFilesDir(), mFileName);
            reader = new FileReader(file);
            Gson gson = new Gson();
            Recipes recipeData = gson.fromJson(reader, Recipes.class);
            return recipeData.getRecipeList();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    public static boolean exportIngredientsToJSON(Context context, List<Ingredient> ingredientList, String fileName){
        mFileName = fileName;

        Ingredients ingredientData = new Ingredients();
        ingredientData.setIngredientList(ingredientList);

        Gson gson = new Gson();
        String jsonString = gson.toJson(ingredientData);
        Log.i(TAG, "exportRecipesToJSON: " + jsonString);

        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = context.openFileOutput(mFileName, MODE_PRIVATE);
            fileOutputStream.write(jsonString.getBytes());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(fileOutputStream != null){
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public static List<Ingredient> importIngredientsFromJSON(Context context, String fileName){
        mFileName = fileName;

        FileReader reader = null;


        try {
            File file = new File(context.getFilesDir(), mFileName);
            reader = new FileReader(file);
            Gson gson = new Gson();
            Ingredients ingredientData = gson.fromJson(reader, Ingredients.class);
            return ingredientData.getIngredientList();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    static class Ingredients{

        List<Ingredient> ingredientList;

        private List<Ingredient> getIngredientList() {
            return ingredientList;
        }

        private void setIngredientList(List<Ingredient> ingredientList) {
            this.ingredientList = ingredientList;
        }
    }

    static class Recipes{

        List<Recipe> recipeList;

        private List<Recipe> getRecipeList() {
            return recipeList;
        }

        private void setRecipeList(List<Recipe> recipeList) {
            this.recipeList = recipeList;
        }
    }

}
