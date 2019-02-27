package com.matthewcannefax.menuplanner.utils;


import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.matthewcannefax.menuplanner.model.Ingredient;
import com.matthewcannefax.menuplanner.model.Recipe;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

//for creating and loading JSON files
public class JSONHelper {
    private static final String TAG = "JSONHelper";
    private static String mFileName;

    //create a JSON file with a list of Recipe objects
    public static boolean exportRecipesToJSON(Context context, List<Recipe> recipeList, String fileName){
        //the name of the file that is being created
        mFileName = fileName;

        //recipe list
        Recipes recipeData = new Recipes();

        //set the recipe Data list to the given recipe list
        recipeData.setRecipeList(recipeList);

        //create a GSON object. Google's JSON Helper library
        Gson gson = new Gson();

        //convert the recipeData list to a JSON string
        String jsonString = gson.toJson(recipeData);
        Log.i(TAG, "exportRecipesToJSON: " + jsonString);

        //output stream to write the JSON string to a file
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

    public static List<Recipe> preloadCookbookFromJSON(Context context){
        List<Recipe> recipes = new ArrayList<>();

        try {
            InputStream is = context.getAssets().open("preloaded");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String jsonString = new String(buffer, "UTF-8");

            Gson gson = new Gson();
            Recipes recipeData = gson.fromJson(jsonString, Recipes.class);
            recipes = recipeData.getRecipeList();
        }catch (IOException e){
            e.printStackTrace();
        }


        return recipes;
    }

    //import a list of recipes from a JSON file
    public static List<Recipe> importRecipesFromJSON(Context context, String fileName){
        //the file name we're looking for
        mFileName = fileName;

        //file reader to read the JSON file
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

    //use a list of ingredients to create a JSON file
    public static boolean exportIngredientsToJSON(Context context, List<Ingredient> ingredientList, String fileName){
        mFileName = fileName;

        Ingredients ingredientData = new Ingredients();
        ingredientData.setIngredientList(ingredientList);

        //Google's JSON Helper library
        Gson gson = new Gson();

        //convert the ingredientData list to a JSON string
        String jsonString = gson.toJson(ingredientData);
        Log.i(TAG, "exportRecipesToJSON: " + jsonString);

        //file output stream to write the JSON string to a file
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

    //import list of ingredient from a JSON file
    public static List<Ingredient> importIngredientsFromJSON(Context context, String fileName){
        mFileName = fileName;

        //file reader to read the JSON file into an ingredient list
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

    //this static class is needed to work with the GSON library
    static class Ingredients{

        List<Ingredient> ingredientList;

        private List<Ingredient> getIngredientList() {
            return ingredientList;
        }

        private void setIngredientList(List<Ingredient> ingredientList) {
            this.ingredientList = ingredientList;
        }
    }

    //this static class is needed to work with the GSON library
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
