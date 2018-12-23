package com.matthewcannefax.menuplanner.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;



public class SplashActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //start the main activity
        Intent intent = new Intent(this, MenuListActivity.class);
        startActivity(intent);
        finish();
    }
}
