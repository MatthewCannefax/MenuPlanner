package com.matthewcannefax.menuplanner.utils;

public enum NavDrawerEnum {
    MENU_FRAGMENT("My Menu"),
    COOKBOOK_FRAGMENT("My Cookbook"),
    ADD_RECIPE_FRAGMENT("Add New Recipe"),
    VIEW_GROCERY_LIST("View Grocery List"),
    NEW_GROCERY_LIST("New Grocery List"),
    IMPORT_COOKBOOK("Import Recipes"),
    SHARE_COOKBOOK("Share Cookbook");

    //get the name of the activity
    private String getName() {
        return mName;
    }

    //Fields
    private final String mName;

    //constructor
    NavDrawerEnum(final String name){
        mName = name;
    }

    //static method to get the enum
    public static NavDrawerEnum getActivityEnum(int position){
        final NavDrawerEnum navEnum;
        switch (position){
            case 0:
                navEnum = MENU_FRAGMENT;
                break;
            case 1:
                navEnum = COOKBOOK_FRAGMENT;
                break;
            case 2:
                navEnum = ADD_RECIPE_FRAGMENT;
                break;
            case 3:
                navEnum = VIEW_GROCERY_LIST;
                break;
            case 4:
                navEnum = NEW_GROCERY_LIST;
                break;
            case 5:
                navEnum = IMPORT_COOKBOOK;
                break;
            case 6:
                navEnum = SHARE_COOKBOOK;
                break;
            default:
                navEnum = MENU_FRAGMENT;
                break;
        }
        return navEnum;
    }

    //return the name in toString
    @Override
    public String toString() {
        return getName();
    }
}
