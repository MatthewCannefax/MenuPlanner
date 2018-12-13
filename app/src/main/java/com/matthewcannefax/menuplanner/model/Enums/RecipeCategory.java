package com.matthewcannefax.menuplanner.model.Enums;

//this enum is here for the different categories of recipes so the recipelist can be more searchable
//The categories are hard coded to limit the number of categories to just a few instead of an unlimited amount

public enum RecipeCategory {
    //the different categories of recipes based on a standard cookbook
    ALL("All"),
    BREAKFAST("Breakfast"),
    DESSERTS("Desserts"),
    APPETIZERS("Appetizers"),
    BEVERAGES("Beverages"),
    VEGETABLES("Vegetables"),
    PASTA("Pasta"),
    BREAD("Bread"),
    MAIN_DISHES("Main Dishes"),
    MISCELLANEOUS("Misc"),
    SOUP("Soup"),
    MEXICAN("Mexican"),
    BBQ("BBQ"),
    ITALIAN("Italian"),
    ASIAN("Asian"),
    BEEF("Beef"),
    CHICKEN("Chicken"),
    PORK("Pork"),
    FISH("Fish"),
    SUSHI("Sushi");

    private final String mName;

    //constructor
    RecipeCategory(String name){mName = name;}

    //this overridden toString method will display the categories with the first character capitalized
    @Override
    public String toString() {
        return mName;

    }

    public static RecipeCategory stringToCategory(String strCategory){
        for(RecipeCategory cat:RecipeCategory.values()){
            if(cat.toString().equals(strCategory)){
                return cat;
            }
        }
        return MISCELLANEOUS;
    }
}
