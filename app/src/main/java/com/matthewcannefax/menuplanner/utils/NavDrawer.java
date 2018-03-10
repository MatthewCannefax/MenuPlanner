package com.matthewcannefax.menuplanner.utils;


import android.app.Activity;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.matthewcannefax.menuplanner.R;

public class NavDrawer {
    private static ListView mDrawerListView;
    private static ArrayAdapter mAdapter;

    public static void setupNavDrawer(Activity activity, Context context){
        mDrawerListView = activity.findViewById(R.id.navList);
        addDrawerItems(context);
    }

    private static void addDrawerItems(Context context){
        String[] activityArray = {"Weekly Menu", "Cook Book", "Grocery List", "Add New Recipe"};
        mAdapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, activityArray);
        mDrawerListView.setAdapter(mAdapter);
    }
}
