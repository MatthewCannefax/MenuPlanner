package com.matthewcannefax.menuplanner.utils;


public class NumberHelper {
    public static boolean tryParseDouble(String value){
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
