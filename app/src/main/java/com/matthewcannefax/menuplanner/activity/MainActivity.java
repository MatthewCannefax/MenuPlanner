package com.matthewcannefax.menuplanner.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.model.MenuList;

public class MainActivity extends AppCompatActivity {

    //there is currently nothing in this activity that will be in the final state of the application
    //This is basically just a way to get to the different activities of the app



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.setTitle("Menu Planner");


    }

    //setup the menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        MenuInflater menuInflater = getMenuInflater();

        menuInflater.inflate(R.menu.main_activity_menu, menu);

        return true;
    }

    //handle the clicks in the menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        //the var to return a true or false
        boolean b = false;

        //switch statement to find which menu item was clicked
        switch (item.getItemId()){

            case R.id.recipesMenuItem:
                goToRecipeActivity();
                b = true;
                break;
            case R.id.menuListMenuItem:
                goToMenuActivity();
                b=true;
                break;
            case R.id.groceriesMenuItem:
                goToGroceryActivity();
                b=true;
                break;
//            case R.id.addRecipeMenuItem:
//                goToAddRecipeActivity();
//                b=true;
//                break;
                default:
                    b= false;
        }
        //return the boolean value
        return b;
    }

    //method that goes to the recipe activity
    private void goToRecipeActivity(){
        Intent intent = new Intent(MainActivity.this, RecipeListActivity.class);
        intent.putExtra("TITLE", "My Recipes");
        MainActivity.this.startActivity(intent);
    }

    //method goes to the menu activity
    private void goToMenuActivity(){
        Intent intent = new Intent(MainActivity.this, MenuListActivity.class);
        MainActivity.this.startActivity(intent);
    }

    //method that goes to the grocery list activity
    private void goToGroceryActivity(){
        Intent intent = new Intent(MainActivity.this, GroceryListActivity.class);
        MainActivity.this.startActivity(intent);
    }

    private void goToAddRecipeActivity(){
        Intent intent = new Intent(MainActivity.this, AddRecipeActivity.class);
        MainActivity.this.startActivity(intent);
    }


}
