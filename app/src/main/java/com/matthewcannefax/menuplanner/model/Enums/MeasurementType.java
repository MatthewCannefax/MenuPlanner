package com.matthewcannefax.menuplanner.model.Enums;


public enum MeasurementType {
    POUND, OUNCE, TABLESPOON, TEASPOON, CUP, GALLON, PIECE, CAN, BAG;

    @Override
    public String toString() {
        String s;
        switch (this){
            case POUND:
                s = "lbs.";
                break;
            case OUNCE:
                s = "oz.";
                break;
            case TABLESPOON:
                s = "tbsp";
                break;
            case TEASPOON:
                s = "tsp";
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
