package com.matthewcannefax.menuplanner.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.StaticItems.StaticRecipes;
import com.matthewcannefax.menuplanner.model.Recipe;
import com.matthewcannefax.menuplanner.utils.NavDrawer;
import com.matthewcannefax.menuplanner.utils.ShareHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;

public class OptionsActivity extends AppCompatActivity {

    Button importBTN;
    Button exportBTN;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.options_layout);

        importBTN = findViewById(R.id.importBTN);
        exportBTN = findViewById(R.id.exportBTN);


        setupButtonListeners();

        NavDrawer.setupNavDrawer(OptionsActivity.this, this);
    }

    private void setupButtonListeners(){
        final Context context = this;
        importBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareHelper.importCookbook(OptionsActivity.this, context);
            }
        });

        exportBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareHelper.sendRecipes(context);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri contentUri = data.getData();
        InputStream inputStream;
        try {
            inputStream = getContentResolver().openInputStream(contentUri);
            BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
            }
            List<Recipe> importRecipes = ShareHelper.jsonToRecipe(total.toString());

            StaticRecipes.getRecipeList().addAll(importRecipes);//need to do more to protect this!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!and save
            String s = "";


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
