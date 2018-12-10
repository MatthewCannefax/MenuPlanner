package com.matthewcannefax.menuplanner.model.Enums;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
    private final String mName;

    public static List<MeasurementType> getEnum(){
        List<MeasurementType> measurementTypes = new ArrayList<>(Arrays.asList(MeasurementType.values()));

        Collections.sort(measurementTypes, new Comparator<MeasurementType>() {
            @Override
            public int compare(MeasurementType measurementType, MeasurementType t1) {
                return measurementType.toString().compareTo(t1.toString());
            }
        });

        return measurementTypes;
    }

    //constructor
    MeasurementType(String name){
        mName = name;
    }

    //return the name in to string
    @Override
    public String toString() {
        return mName;
    }

    public static MeasurementType stringToCategory(String measurement){
        for(MeasurementType type: MeasurementType.values()){
            if(type.mName.toUpperCase().equals(measurement)){
                return type;
            }
        }

        return PIECE;

    }
}
