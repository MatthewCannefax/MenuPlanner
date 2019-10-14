package com.matthewcannefax.menuplanner.recipe;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public enum MeasurementType {
    PIECE(""),//This will show as an empty, so instead of 1 piece onion it will show 1 onion
    POUND("lbs"),
    OUNCE("oz"),
    TABLESPOON("tbsp"),
    TEASPOON("tsp"),
    CUP("cup"),
    PINT("pt"),
    QUART("qt"),
    GALLON("gallon"),
    CAN("can"),
    JAR("jar"),
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

    public static int getOrdinal(MeasurementType measurement){
        List<MeasurementType> mTypes = getEnum();
        int count = 0;
        for (MeasurementType mType :
                mTypes) {
            if (mType == measurement) {
                return count;
            }
            count++;
        }
        return 0;
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
