package com.matthewcannefax.menuplanner.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.matthewcannefax.menuplanner.BuildConfig;
import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.activity.MenuListActivity;
import com.matthewcannefax.menuplanner.activity.RecipeListActivity;
import com.matthewcannefax.menuplanner.model.Ingredient;
import com.matthewcannefax.menuplanner.model.Recipe;
import com.matthewcannefax.menuplanner.utils.database.DataSource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class ShareHelper {

    public static int getPickFileRequestCode() {
        return PICK_FILE_REQUEST_CODE;
    }

    private final static int PICK_FILE_REQUEST_CODE = 4;

    public static void sendGroceryList(Context context){
        DataSource mDataSource = new DataSource(context);

        List<Ingredient> groceries =  mDataSource.getAllGroceries();

        if (groceries != null && groceries.size() != 0) {
            String sendText;
            StringBuilder sb = new StringBuilder();
            for(Ingredient i: groceries){
                sb.append(i.shareIngredientString()).append("\n");
            }
            sendText = sb.toString();
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, sendText);
            sendIntent.setType("text/plain");
            context.startActivity(sendIntent);
        } else {
            Toast.makeText(context, R.string.no_grocery_to_share, Toast.LENGTH_SHORT).show();
        }

    }

    public static void sendSingleRecipe(Context context, int recipeID){

        DataSource mDataSource = new DataSource(context);

        List<Recipe> recipes = new ArrayList<>();
        recipes.add(mDataSource.getSpecificRecipe(recipeID));

        String filename = recipes.get(0).getName() + " Recipe";
        JSONHelper.exportRecipesToJSON(context, recipes, filename);
        File fileLocation = new File(context.getFilesDir().getAbsolutePath(), filename );
        String authority = BuildConfig.APPLICATION_ID + ".provider";
        Uri path = FileProvider.getUriForFile(context, authority, fileLocation);

        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setType("text/*");
        emailIntent.putExtra(Intent.EXTRA_STREAM, path);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "MY COOKBOOK");
        context.startActivity(Intent.createChooser(emailIntent, "Send email...3"));
    }

    public static void sendRecipeSelection(Context context, List<Recipe> recipes){

        JSONHelper.exportRecipesToJSON(context, recipes, context.getString(R.string.recipe_list_to_json));
        String filename = context.getString(R.string.recipe_list_to_json);
        File fileLocation = new File(context.getFilesDir().getAbsolutePath(), filename );
        String authority = BuildConfig.APPLICATION_ID + ".provider";
        Uri path = FileProvider.getUriForFile(context, authority, fileLocation);

        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setType("text/*");
        emailIntent.putExtra(Intent.EXTRA_STREAM, path);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "MY COOKBOOK");
        context.startActivity(Intent.createChooser(emailIntent, "Send email...3"));
    }

    public static void sendAllRecipes(Context context){

        DataSource mDataSource = new DataSource(context);

        JSONHelper.exportRecipesToJSON(context, mDataSource.getAllRecipes(), context.getString(R.string.recipe_list_to_json));
        String filename = context.getString(R.string.recipe_list_to_json);
        File fileLocation = new File(context.getFilesDir().getAbsolutePath(), filename );
        String authority = BuildConfig.APPLICATION_ID + ".provider";
        Uri path = FileProvider.getUriForFile(context, authority, fileLocation);

        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setType("text/*");
        emailIntent.putExtra(Intent.EXTRA_STREAM, path);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "MY COOKBOOK");
        context.startActivity(Intent.createChooser(emailIntent, "Send email...3"));

    }

    public static void importCookbook(Activity activity){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        activity.startActivityForResult(Intent.createChooser(intent, "Select a cookbook file..."), PICK_FILE_REQUEST_CODE);
    }

    public static List<Recipe> jsonToRecipe(Context context, String jsonString){
        

        Gson gson = new Gson();

        Recipes recipedata = null;
        try {
            recipedata = gson.fromJson(jsonString, Recipes.class);
        } catch (JsonSyntaxException e) {
            Toast.makeText(context, "Invalid File", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        assert recipedata != null;
        return recipedata.getRecipeList();
    }

    public static void activityResultImportCookbook(final Context context, final Activity currentActivity, int requestCode, int resultCode, final Intent data){

        if (requestCode == ShareHelper.getPickFileRequestCode() && resultCode == RESULT_OK) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Import a Cookbook?");
            builder.setMessage("Are you sure you want to append your cookbook?");
            builder.setNegativeButton("Cancel", null);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Uri contentUri = data.getData();
                    InputStream inputStream;
                    try{
                        assert contentUri != null;
                        inputStream = context.getContentResolver().openInputStream(contentUri);
                        assert  inputStream != null;
                        BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
                        StringBuilder total = new StringBuilder();
                        String line;
                        while ((line = r.readLine()) != null) {
                            total.append(line);
                        }

                        List<Recipe> importRecipes;
                        try {
                            DataSource mDataSource = new DataSource(context);
                            importRecipes = ShareHelper.jsonToRecipe(context, total.toString());
                            mDataSource.importRecipesToDB(importRecipes);

                            Intent intent = new Intent(currentActivity, RecipeListActivity.class);
                            intent.putExtra("TITLE", "My Recipes");
                            context.startActivity(intent);
                            if(currentActivity.getClass() != MenuListActivity.class){
                                currentActivity.finish();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                    }catch (FileNotFoundException e){
                        e.printStackTrace();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            });

            builder.show();
        }
    }

    //this static class is needed to work with the GSON library
    static class Recipes{

        @SuppressWarnings("unused")
        List<Recipe> recipeList;

        private List<Recipe> getRecipeList() {
            return recipeList;
        }

    }
}
