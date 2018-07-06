package com.matthewcannefax.menuplanner.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

import com.matthewcannefax.menuplanner.StaticItems.StaticGroceryList;
import com.matthewcannefax.menuplanner.StaticItems.StaticMenu;
import com.matthewcannefax.menuplanner.StaticItems.StaticRecipes;


public class SplashActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //load the recipes from JSON file to the Static Class
        StaticRecipes.loadRecipes(this);

        //Load the menu items from JSON to the Static class
        StaticMenu.loadMenu(this);

        //Load the Grocery items from JSON to the static class
        StaticGroceryList.loadGroceries(this);

        //start the main activity
        Intent intent = new Intent(this, MenuListActivity.class);
        startActivity(intent);
        finish();
    }
}
