package com.matthewcannefax.menuplanner;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.matthewcannefax.menuplanner.utils.navigation.NavDrawer;

public abstract class DrawerActivity extends AppCompatActivity {
    protected DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentLayoutId());
        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavDrawer.setupNavDrawerMenuButton(getSupportActionBar());
        ListView drawerListView = findViewById(R.id.navList);
        NavDrawer.setupNavDrawer(this, this, drawerListView);
    }


    /**
     * @return activity layout id
     */
    protected abstract int getContentLayoutId();
}
