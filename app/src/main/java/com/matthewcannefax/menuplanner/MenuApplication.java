package com.matthewcannefax.menuplanner;

import android.app.Application;
import android.content.Context;

public class MenuApplication extends Application {
    private MenuApplicationComponent menuApplicationComponent;
    private static MenuApplication appContext;
    private static MenuApplication tInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        tInstance = this;
        appContext = this;
        menuApplicationComponent = DaggerMenuApplicationComponent.builder().build();
    }

    public MenuApplicationComponent getMenuApplicationComponent() {
        return menuApplicationComponent;
    }

    public static MenuApplication getInstance() {
        return tInstance;
    }

    public static Context getMenuApplicationContext() {
        return appContext.getApplicationContext();
    }
}
