package com.matthewcannefax.menuplanner.model.Enums;

//this enum is for the different categories of grocery items
//this is hard coded so we can limit the number of categories to just a few

public enum GroceryCategory {
    //these are the different grocery item categories
    FROZEN_FOODS, MEAT, PRODUCE, BEVERAGES, BREAD, CANNED_GOODS, DAIRY, PASTA_RICE, OTHER, ALL, SPICES, BAKING, CONDIMENTS, CHIPS;

    //this overridden toString method will display the categories with the first character capitalized
    @Override
    public String toString() {
        String s;

        switch (this){
            case FROZEN_FOODS:
                s = "Frozen Foods";
                break;
            case MEAT:
                s = "Meat";
                break;
            case PRODUCE:
                s = "Produce";
                break;
            case BEVERAGES:
                s = "Beverages";
                break;
            case BREAD:
                s = "Bread";
                break;
            case CANNED_GOODS:
                s = "Canned Goods";
                break;
            case DAIRY:
                s = "Dairy";
                break;
            case PASTA_RICE:
                s = "Pasta/Rice";
                break;
            case SPICES:
                s = "Spices";
                break;
            case OTHER:
                s = "Other";
                break;
            case ALL:
                s = "All";
                break;
            case CHIPS:
                s = "Chips";
                break;
            case BAKING:
                s= "Baking";
                break;
            case CONDIMENTS:
                s= "Condiments";
                break;
                default:
                    s = "Other";
                    break;
        }

        return s;
    }
}
