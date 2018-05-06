package com.matthewcannefax.menuplanner.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import com.google.gson.Gson;
import com.matthewcannefax.menuplanner.BuildConfig;
import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.StaticItems.StaticRecipes;
import com.matthewcannefax.menuplanner.model.Ingredient;
import com.matthewcannefax.menuplanner.model.Recipe;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

public class ShareHelper {

    private final static int PICK_FILE_REQUEST_CODE = 4;

    public static void sendGroceryList(Context context, List<Ingredient> ingredients){
        String sendText;
        StringBuilder sb = new StringBuilder();
        for(Ingredient i: ingredients){
            sb.append(i.getName()).append("\n");
        }
        sendText = sb.toString();
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, sendText);
        sendIntent.setType("text/plain");
        context.startActivity(sendIntent);

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

    public static void importCookbook(Activity activity, Context context){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        activity.startActivityForResult(Intent.createChooser(intent, "Select a cookbook file..."), PICK_FILE_REQUEST_CODE);
    }
}
