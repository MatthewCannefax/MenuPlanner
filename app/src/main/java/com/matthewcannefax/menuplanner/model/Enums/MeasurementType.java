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

    private String mName;

    MeasurementType(String name){
        mName = name;
    }

    @Override
    public String toString() {
        return mName;
    }
}
