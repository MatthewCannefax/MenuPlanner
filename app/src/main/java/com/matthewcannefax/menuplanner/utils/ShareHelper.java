package com.matthewcannefax.menuplanner.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.matthewcannefax.menuplanner.BuildConfig;
import com.matthewcannefax.menuplanner.MainActivity;
import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.recipe.menuList.MenuListFragment;
import com.matthewcannefax.menuplanner.recipe.recipeList.RecipeListActivity;
import com.matthewcannefax.menuplanner.recipe.Ingredient;
import com.matthewcannefax.menuplanner.recipe.Recipe;
import com.matthewcannefax.menuplanner.utils.database.DataSource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class ShareHelper {
    private ShareHelper(){
        throw new AssertionError();
    }

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

        String filename = context.getString(R.string.selected_recipes);
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

    public static void sendAllRecipesDB(Context context){
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();

        File copyDBFile = new File("");

        if(sd.canWrite()){
            String currentDBPath = "//data//com.matthewcannefax.menuplanner//databases//cookbook.db";
            String copyDBPath = "cookbookCOPY.db";
            File currentDBFile = new File(data, currentDBPath);
            copyDBFile = new File(sd, copyDBPath);
            boolean completed = false;
            if(currentDBFile.exists()){

                try {
                    FileChannel src = new FileInputStream(currentDBFile).getChannel();
                    FileChannel dst = new FileOutputStream(copyDBFile).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                    completed = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(completed){
                    Toast.makeText(context, "File Copy completed", Toast.LENGTH_LONG).show();
                }
            }
        }

        if (copyDBFile.exists()) {
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("application/octet-stream");
            String authority = BuildConfig.APPLICATION_ID + ".provider";
            Uri path = FileProvider.getUriForFile(context, authority, copyDBFile);
            emailIntent.putExtra(Intent.EXTRA_STREAM, path);
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "MY COOKBOOK");
            context.startActivity(emailIntent);
        }
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
        activity.startActivityForResult(Intent.createChooser(intent, activity.getString(R.string.select_a_cookbook_file)), PICK_FILE_REQUEST_CODE);
    }

    public static List<Recipe> jsonToRecipe(Context context, String jsonString){
        

        Gson gson = new Gson();

        Recipes recipedata = null;
        try {
            recipedata = gson.fromJson(jsonString, Recipes.class);
        } catch (JsonSyntaxException e) {
            Toast.makeText(context, R.string.invalid_file, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        assert recipedata != null;
        return recipedata.getRecipeList();
    }

    public static void activityResultImportCookbook(final Context context, final Activity currentActivity, int requestCode, int resultCode, final Intent data){

        if (requestCode == ShareHelper.getPickFileRequestCode() && resultCode == RESULT_OK) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(R.string.import_cookbook_question);
            builder.setMessage(R.string.append_cookbook_question);
            builder.setNegativeButton(R.string.cancel, null);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
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
                            intent.putExtra("TITLE", R.string.my_recipes);
                            context.startActivity(intent);
                            if(currentActivity.getClass() != MainActivity.class){
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
