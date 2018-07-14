package com.matthewcannefax.menuplanner.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

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

    private Button importBTN;
    private Button exportBTN;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.options_layout);

        importBTN = findViewById(R.id.importBTN);
        exportBTN = findViewById(R.id.exportBTN);

        setupButtonListeners();

        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavDrawer.setupNavDrawerMenuButton(getSupportActionBar());

        ListView drawerListView = findViewById(R.id.navList);

        NavDrawer.setupNavDrawer(OptionsActivity.this, this, drawerListView);
    }

    private void setupButtonListeners(){
        final Context context = this;
        importBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareHelper.importCookbook(OptionsActivity.this);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:

                NavDrawer.navDrawerOptionsItem(mDrawerLayout);
                return true;

                default:
                    return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        final Context context = this;

        if (requestCode == ShareHelper.getPickFileRequestCode() && resultCode == RESULT_OK) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Import a Cookbook?");
            builder.setMessage("Are you sure you want to append your cookbook?");
            builder.setNegativeButton("Cancel", null);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
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

                        List<Recipe> importRecipes;
                        try {
                            importRecipes = ShareHelper.jsonToRecipe(context, total.toString());
                            StaticRecipes.addImportedRecipes(context, importRecipes);
                            StaticRecipes.saveRecipes(context);

                            Intent intent = new Intent(OptionsActivity.this, RecipeListActivity.class);
                            intent.putExtra("TITLE", "My Recipes");
                            startActivity(intent);
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();

                        }



                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });


            builder.show();




        }

    }
}
