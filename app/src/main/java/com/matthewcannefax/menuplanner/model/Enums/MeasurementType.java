package com.matthewcannefax.menuplanner.model.Enums;

/**
 * Created by mcann on 12/5/2017.
 */

public enum MeasurementType {
    POUND, OUNCE, TABLESPOON, TEASPOON, CUP, GALLON, PIECE, CAN, BAG;

    @Override
    public String toString() {
        String s = "";
        switch (this){
            case POUND:
                s = "lbs.";
                break;
            case OUNCE:
                s = "oz.";
                break;
            case TABLESPOON:
                s = "TBSP";
                break;
            case TEASPOON:
                s = "TSP";
                break;
            case CUP:
                s = "cup";
                break;
            case GALLON:
                s = "gal";
                break;
            case PIECE:
                s = "piece";
                break;
            case CAN:
                s = "can";
                break;
            case BAG:
                s = "bag";
                break;
                default:
                    s = "";
        }

        return s;
    }
}
