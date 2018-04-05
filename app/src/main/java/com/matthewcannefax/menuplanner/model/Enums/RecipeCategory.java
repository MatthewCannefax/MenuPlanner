package com.matthewcannefax.menuplanner.model.Enums;

//this enum is here for the different categories of recipes so the recipelist can be more searchable
//The categories are hard coded to limit the number of categories to just a few instead of an unlimited amount

public enum RecipeCategory {
    //the different categories of recipes based on a standard cookbook
    ALL, DESSERTS, APPETIZERS, BEVERAGES, VEGETABLES, PASTA, BREAD, MAIN_DISHES, MISCELLANEOUS, SOUP, MEXICAN,
    BBQ, ITALIAN, ASIAN, BEEF, CHICKEN, PORK;

    //this overridden toString method will display the categories with the first character capitalized
    @Override
    public String toString() {
        switch (this) {
            case MAIN_DISHES:
                return "Main Dishes";
            case BBQ:
                return "BBQ";
            default:
                return this.name().substring(0, 1) + this.name().substring(1).toLowerCase();
        }

    }
}
