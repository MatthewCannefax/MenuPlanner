package com.matthewcannefax.menuplanner.model;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GroceryBuilder {

    // --Commented out by Inspection (4/5/2018 1:43 PM):public GroceryBuilder(){}

    private List<Recipe> recipes;
    private List<Ingredient> groceryList;

    public GroceryBuilder(List<Recipe> recipes){
        this.recipes = recipes;
    }

// --Commented out by Inspection START (4/5/2018 1:43 PM):
//    public List<Ingredient> getGroceryList() {
//        return groceryList;
//    }
// --Commented out by Inspection STOP (4/5/2018 1:43 PM)

// --Commented out by Inspection START (4/5/2018 1:43 PM):
//    public void setGroceryList(List<Ingredient> groceryList) {
//        this.groceryList = groceryList;
//    }
// --Commented out by Inspection STOP (4/5/2018 1:43 PM)

    public List<Ingredient> consolidateGroceries(){
        //this is the current list
        List<Ingredient> ingredients = getIngredients();

        //this is the new list of ingredients
        List<Ingredient> newIngredients = new ArrayList<>();

        //sort the ingredients by name
        Collections.sort(ingredients, new Comparator<Ingredient>() {
            @Override
            public int compare(Ingredient o1, Ingredient o2) {
                return o1.getName().toUpperCase().compareTo(o2.getName().toUpperCase());
            }
        });

        //add all the items from the ingredients list to the new ingredients list to be consolidated
        newIngredients.addAll(ingredients);

        //loop through all the ingredients and combine each one with the same name and measurement type
        for (int i = 0; i<newIngredients.size()-1; i++){

            //the current item to compare to
            Ingredient ingred1 = newIngredients.get(i);

            //loop through all the ingredients that come after ingred1 in the list
            for(int n = i + 1; n < newIngredients.size() - 1; n++){

                //now comparing n and i
                Ingredient ingred2 = newIngredients.get(n);

                //if the two ingredients match in name, category and measurement type, they will be combined
                if(ingred1.getName().toUpperCase().equals(ingred2.getName().toUpperCase()) &&
                        ingred1.getCategory().equals(ingred2.getCategory()) &&
                        ingred1.getMeasurement().getType().equals(ingred2.getMeasurement().getType())){

                                //set the amount of the first ingred to the two ingredients combined
                                ingred1.getMeasurement().setAmount(
                                        ingred1.getMeasurement().getAmount() + ingred2.getMeasurement().getAmount()
                                );

                                //remove the second ingredient from the list
                                newIngredients.remove(n);

                                //all the ingredients have moved up one position, decrement here so the next item won't be skipped
                                n--;
                }
            }

        }

        //the new ingredients sorted by category with the sortByCategory method
        return sortByCategory(newIngredients);
    }

    //this method takes a list of ingredients and sorts them by category
    private List<Ingredient> sortByCategory(List<Ingredient> ingredientList){
        List<Ingredient> ingredients = ingredientList;

        //sort the ingredients list by category
        Collections.sort(ingredients, new Comparator<Ingredient>() {
            @Override
            public int compare(Ingredient o1, Ingredient o2) {
                return o1.getCategory().compareTo(o2.getCategory());
            }
        });

        return ingredients;
    }

    //pull all the ingredients out of the provided recipe list
    private List<Ingredient> getIngredients(){

        //this list will hold all the ingredients
        List<Ingredient> ingredients = new ArrayList<>();

        //loop through the provided recipes and pull out all the ingredients
        for(Recipe recipe: this.recipes){
            if (recipe.getIngredientList() != null){
                //add all the ingredients in the recipe to the ingredients list
                ingredients.addAll(recipe.getIngredientList());
            }
        }

        //return the ingredients list
        return ingredients;
    }
}
