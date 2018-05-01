package com.matthewcannefax.menuplanner.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.google.gson.Gson;
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

    }

    public static void importCookbook(){

    }
}
