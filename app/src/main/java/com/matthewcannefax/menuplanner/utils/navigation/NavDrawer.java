package com.matthewcannefax.menuplanner.utils.navigation;


import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

//this class sets up the navigation drawer for all activities
public class NavDrawer {
    private NavDrawer() {
        throw new AssertionError();
    }


    public static void navDrawerOptionsItem(DrawerLayout mDrawerLayout) {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
    }
}
