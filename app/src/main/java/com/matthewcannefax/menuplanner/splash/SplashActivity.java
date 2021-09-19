package com.matthewcannefax.menuplanner.splash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.recipe.menuList.MenuListActivity;
import com.matthewcannefax.menuplanner.utils.JSONHelper;
import com.matthewcannefax.menuplanner.utils.database.DataSource;


public class SplashActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final SharedPreferences sharedPref = getSharedPreferences(getString(R.string.is_preloaded), 0);
        boolean isPreloaded = sharedPref.getBoolean(getString(R.string.is_preloaded), false);

        if (!isPreloaded) {
            new Thread(() -> {
                DataSource mDataSource = new DataSource(getApplicationContext());
                mDataSource.open();

                mDataSource.importRecipesToDB(JSONHelper.preloadCookbookFromJSON(getApplicationContext()));

                mDataSource.close();

                SharedPreferences.Editor edit = sharedPref.edit();
                edit.putBoolean(getString(R.string.is_preloaded), true);
                edit.apply();
            }).start();
        }

        //start the main activity
        Intent intent = new Intent(this, MenuListActivity.class);
        startActivity(intent);
        finish();
    }
}
