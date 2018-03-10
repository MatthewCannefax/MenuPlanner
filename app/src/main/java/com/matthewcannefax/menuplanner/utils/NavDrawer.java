package com.matthewcannefax.menuplanner.utils;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.activity.MainActivity;
import com.matthewcannefax.menuplanner.model.Enums.ActivityNavEnum;

import java.util.ArrayList;
import java.util.List;

public class NavDrawer {
    private static ListView mDrawerListView;
    private static ArrayAdapter mAdapter;

    public static void setupNavDrawer(Activity activity, Context context){
        mDrawerListView = activity.findViewById(R.id.navList);
        addDrawerItems(activity, context);
    }

    private static void addDrawerItems(final Activity currentActivity, final Context context){
        mAdapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, ActivityNavEnum.values());
        mDrawerListView.setAdapter(mAdapter);
        List<ActivityNavEnum> enums = new ArrayList<>();


        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent selectedActivity = new Intent(context, ActivityNavEnum.getActivityEnum(i).getActivity());
                if (ActivityNavEnum.getActivityEnum(i) == ActivityNavEnum.RECIPE_LIST_ACTIVITY){
                    selectedActivity.putExtra("TITLE", "My Recipes");
                }
                context.startActivity(selectedActivity);


                if (currentActivity.getClass() != MainActivity.class) {
                    currentActivity.finish();
                }
            }
        });
    }

    private static void openActivity(Context context, ActivityNavEnum activityNavEnum){
        context.startActivity(new Intent(context, activityNavEnum.getActivity()));
    }
}
