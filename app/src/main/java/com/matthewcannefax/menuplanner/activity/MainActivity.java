package com.matthewcannefax.menuplanner.activity;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.StaticItems.StaticMenu;
import com.matthewcannefax.menuplanner.StaticItems.StaticRecipes;
import com.matthewcannefax.menuplanner.StaticItems.StaticGroceryList;
import com.matthewcannefax.menuplanner.arrayAdapters.MainActivityViewPagerAdapter;
import com.matthewcannefax.menuplanner.utils.FadeTransformer;
import com.matthewcannefax.menuplanner.utils.NavDrawer;
import com.matthewcannefax.menuplanner.utils.PermissionsHelper;

public class MainActivity extends AppCompatActivity {

    //there is currently nothing in this activity that will be in the final state of the application
    //This is basically just a way to get to the different activities of the app


    private ViewPager viewPager;
    private MainActivityViewPagerAdapter adapter;
    private LinearLayout sliderDotsPanel;
    private int dotsCount;
    private ImageView[] dots;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.setTitle(getString(R.string.app_name));

        //load the recipes from JSON file to the Static Class
        StaticRecipes.loadRecipes(this);

        //Load the menu items from JSON to the Static class
        StaticMenu.loadMenu(this);

        //Load the Grocery items from JSON to the static class
        StaticGroceryList.loadGroceries(this);

        viewPager = findViewById(R.id.viewPager);
        sliderDotsPanel = findViewById(R.id.SliderDots);
        adapter = new MainActivityViewPagerAdapter(this);
        viewPager.setAdapter(adapter);
        FadeTransformer transformer = new FadeTransformer();
        viewPager.setPageTransformer(true, transformer);
        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavDrawer.setupNavDrawerMenuButton(getSupportActionBar());

        dotsCount = adapter.getCount();
        dots = new ImageView[dotsCount];

        for(int i = 0; i < dotsCount; i++){

            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.nonactive_dots));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            params.setMargins(20,0,20, 0);

            sliderDotsPanel.addView(dots[i], params);

        }

        dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dots));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for(int i = 0; i<dotsCount; i++){
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.nonactive_dots));
                }
                dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dots));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        ListView drawerListView = findViewById(R.id.navList);

        //setup the nav drawer
        NavDrawer.setupNavDrawer(MainActivity.this, this, drawerListView);

        //check that the required permissions are allowed
        PermissionsHelper.checkPermissions(MainActivity.this, this);
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
        switch (item.getItemId()) {
            case android.R.id.home:

                NavDrawer.navDrawerOptionsItem(mDrawerLayout);
                break;
            case R.id.options:
                Intent intent = new Intent(MainActivity.this, OptionsActivity.class);
                startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }


}
