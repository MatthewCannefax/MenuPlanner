package com.matthewcannefax.menuplanner.model;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GroceryBuilder {

    public GroceryBuilder(){}

    private List<Recipe> recipes;
    private List<Ingredient> ingredients;
    private List<Ingredient> groceryList;

    public GroceryBuilder(List<Recipe> recipes){
        this.recipes = recipes;
    }

    public List<Ingredient> getGroceryList() {
        return groceryList;
    }

    public void setGroceryList(List<Ingredient> groceryList) {
        this.groceryList = groceryList;
    }

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

                //get the current ingredient and the next ingredient to be compared to one another
                Ingredient ingred1 = newIngredients.get(i);
                Ingredient ingred2 = newIngredients.get(i + 1);

                //while the ingredients have the same name, combine the two into one item
                while (ingred1.getName().toUpperCase().equals(ingred2.getName().toUpperCase())){

                    //make sure the categories and measurement types are the same before combining
                    if (ingred1.getCategory() == ingred2.getCategory() && ingred1.getMeasurement().getType() == ingred2.getMeasurement().getType()) {
                        ingred1.getMeasurement().setAmount(ingred1.getMeasurement().getAmount() + ingred2.getMeasurement().getAmount());

                        //remove ingred2 from the new ingredients list since it has been combined with ingred1
                        newIngredients.remove(i + 1);

                        //if there are more ingredients to be checked make iterate ingred2 to that next ingredient
                        //if not set ingred2 to a new ingredient so we don't get a nullpointer
                        if((i + 1) != newIngredients.size()) {
                            ingred2 = newIngredients.get(i + 1);
                        }else{
                            ingred2 = new Ingredient();
                        }
                    }
                }

        }

        //the new ingredients sorted by category with the sortByCategory method
        return sortByCategory(newIngredients);
    }

    //this method takes a list of ingredients and sorts them by category
    private List<Ingredient> sortByCategory(List<Ingredient> ingredientList){
        ingredients = ingredientList;

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
