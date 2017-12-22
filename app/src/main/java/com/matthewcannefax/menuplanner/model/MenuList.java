package com.matthewcannefax.menuplanner.model;


import java.util.ArrayList;
import java.util.List;

//A static list to hold the menulist. This might need to be removed for better functionality
//Try to find a better way to handle this that using a static class

 public class MenuList {

     public static List<Recipe> recipeList;

     static{
         recipeList = new ArrayList<>();
     }


 }
