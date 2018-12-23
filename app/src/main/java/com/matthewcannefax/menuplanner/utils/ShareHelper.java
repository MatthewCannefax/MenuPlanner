package com.matthewcannefax.menuplanner.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.matthewcannefax.menuplanner.BuildConfig;
import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.model.Ingredient;
import com.matthewcannefax.menuplanner.model.Recipe;
import com.matthewcannefax.menuplanner.utils.database.DataSource;

import java.io.File;
import java.util.List;

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

    public static void sendRecipes(Context context){
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

    //this static class is needed to work with the GSON library
    static class Recipes{

        @SuppressWarnings("unused")
        List<Recipe> recipeList;

        private List<Recipe> getRecipeList() {
            return recipeList;
        }

    }
}
