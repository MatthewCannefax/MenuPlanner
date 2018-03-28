package com.matthewcannefax.menuplanner.model.Enums;


public enum MeasurementType {
    POUND("lbs"),
    OUNCE("oz"),
    TABLESPOON("tbsp"),
    TEASPOON("tsp"),
    CUP("cup"),
    GALLON("gallon"),
    PIECE("piece"),
    CAN("can"),
    BAG("bag"),
    PACKAGE("package"),
    BOTTLE("bottle"),
    BOX("box");

    //name field
    private String mName;

    //constructor
    MeasurementType(String name){
        mName = name;
    }

    //return the name in to string
    @Override
    public String toString() {
        return mName;
    }
}
