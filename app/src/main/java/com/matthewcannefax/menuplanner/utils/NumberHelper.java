package com.matthewcannefax.menuplanner.utils;

//class to help out with parsing numbers
public class NumberHelper {
    //this is a custom tryParse method for double type numbers
    @SuppressWarnings({"ignored", "ResultOfMethodCallIgnored"})
    public static boolean tryParseDouble(String value){
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
