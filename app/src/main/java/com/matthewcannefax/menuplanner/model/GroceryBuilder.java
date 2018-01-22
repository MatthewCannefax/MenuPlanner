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
        List<Ingredient> ingredients = sortByCategory();

        //this is the new list of ingredients
        List<Ingredient> newIngredients = new ArrayList<>();

        Collections.sort(ingredients, new Comparator<Ingredient>() {
            @Override
            public int compare(Ingredient o1, Ingredient o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        for(Ingredient i : ingredients){
            Ingredient newIngredient = new Ingredient(i.getName(), i.getCategory(), new Measurement(i.getMeasurement().getAmount(), i.getMeasurement().getType()));
            newIngredients.add(newIngredient);
        }


        for (int i = 0; i<newIngredients.size()-1; i++){


                Ingredient ingred1 = newIngredients.get(i);
                Ingredient ingred2 = newIngredients.get(i + 1);

                while (ingred1.getName().equals(ingred2.getName())){

                    if (ingred1.getCategory() == ingred2.getCategory()) {
                        ingred1.getMeasurement().setAmount(ingred1.getMeasurement().getAmount() + ingred2.getMeasurement().getAmount());
                        newIngredients.remove(i + 1);
                        if((i + 1) != newIngredients.size()) {
                            ingred2 = newIngredients.get(i + 1);
                        }else{
                            ingred2 = new Ingredient();
                        }
                    }
                }

        }

        return sortByCategory(newIngredients);
    }

    private List<Ingredient> sortByCategory(List<Ingredient> ingredientList){
        ingredients = ingredientList;

        Collections.sort(ingredients, new Comparator<Ingredient>() {
            @Override
            public int compare(Ingredient o1, Ingredient o2) {
                return o1.getCategory().compareTo(o2.getCategory());
            }
        });

        return ingredients;
    }

    private List<Ingredient> sortByCategory(){
        List<Ingredient> ingredients = getIngredients();

        Collections.sort(ingredients, new Comparator<Ingredient>() {
            @Override
            public int compare(Ingredient o1, Ingredient o2) {
                return o1.getCategory().compareTo(o2.getCategory());
            }
        });

        return ingredients;
    }

    private List<Ingredient> getIngredients(){

        List<Ingredient> ingredients = new ArrayList<>();

        for(Recipe recipe: this.recipes){
            if (recipe.getIngredientList() != null){
                for(Ingredient i : recipe.getIngredientList()){
                    ingredients.add(i);
                }
            }
        }

        return ingredients;
    }
}
