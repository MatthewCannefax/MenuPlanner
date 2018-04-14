package com.matthewcannefax.menuplanner.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.arrayAdapters.IngredientItemAdapter;
import com.matthewcannefax.menuplanner.model.Enums.GroceryCategory;
import com.matthewcannefax.menuplanner.model.Enums.MeasurementType;
import com.matthewcannefax.menuplanner.model.Ingredient;
import com.matthewcannefax.menuplanner.model.Measurement;
import com.matthewcannefax.menuplanner.model.Recipe;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerHelper {

    public static void setAddIngredientButton(final Context context, final View view, Button button,
                                              final Recipe newRecipe, final IngredientItemAdapter ingredientItemAdapter, final ListView listView){

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(context.getString(R.string.add_ingredient_dialog_title));

                View editIngredientView = LayoutInflater.from(context).inflate(R.layout.add_ingredient_item, (ViewGroup)view.findViewById(android.R.id.content), false);

                //controls inside the view
                final EditText etAmount = editIngredientView.findViewById(R.id.amountText);
                final Spinner spMeasure = editIngredientView.findViewById(R.id.amountSpinner);
                final EditText etName = editIngredientView.findViewById(R.id.ingredientName);
                final Spinner spCat = editIngredientView.findViewById(R.id.categorySpinner);

                //use the clearEditText method to clear the editTexts in the Alert Dialog and the null the listener
                clearEditText(etName);
                clearEditText(etAmount);

                //setup the default array adapters for the category and measurementtype spinners
                ArrayAdapter<MeasurementType> measureAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, MeasurementType.values());
                final ArrayAdapter<GroceryCategory> ingredCatAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, GroceryCategory.getEnumIngredients());

                //set the spinner adpaters
                spMeasure.setAdapter(measureAdapter);
                spCat.setAdapter(ingredCatAdapter);

                //set the new view as the view for the alertdialog
                builder.setView(editIngredientView);

                //setup the buttons for the alertdialog
                builder.setNegativeButton("Cancel", null);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //check if the all the inputs are filled in correctly
                        if (!etName.getText().toString().equals("") && !etAmount.getText().toString().equals("") && NumberHelper.tryParseDouble(etAmount.getText().toString())) {
                            //check if the ingredient list exists for this recipe
                            if(newRecipe.getIngredientList() != null){
                                //add the new Ingredient to the ingredientList
                                newRecipe.getIngredientList().add(new Ingredient(
                                        etName.getText().toString(),
                                        (GroceryCategory)spCat.getSelectedItem(),
                                        new Measurement(
                                                Double.parseDouble(etAmount.getText().toString()),
                                                (MeasurementType)spMeasure.getSelectedItem()
                                        )

                                ));
                                //notify the ingredientItemAdapter that the dataset has been changed
//                                ingredientItemAdapter.notifyDataSetChanged();

                                IngredientItemAdapter ingredientItemAdapter1 = new IngredientItemAdapter(context, newRecipe.getIngredientList());
                                listView.setAdapter(ingredientItemAdapter1);
                            }
                            //if the ingredient list does not exist create the new Ingredient and a new Ingredient list
                            //then add that to the recipe
                            else{
                                //create the new ingredient
                                Ingredient ingredient = new Ingredient(
                                        etName.getText().toString(),
                                        (GroceryCategory)spCat.getSelectedItem(),
                                        new Measurement(
                                                Double.parseDouble(etAmount.getText().toString()),
                                                (MeasurementType)spMeasure.getSelectedItem()
                                        ));
                                //create the new ingredient list
                                List<Ingredient> newIngredredients = new ArrayList<>();

                                //add the new ingredient to the the new list
                                newIngredredients.add(ingredient);

                                //set the new list as the list for the new recipe
                                newRecipe.setIngredientList(newIngredredients);


                                //setup the ingredient item adapter for the recipeIngreds listView
                                IngredientItemAdapter ingredientItemAdapter1 = new IngredientItemAdapter(context, newRecipe.getIngredientList());
                                listView.setAdapter(ingredientItemAdapter1);
                            }
                        }else{
                            //send a Toast prompting the user to make sure and fill in the alert dialog correctly
                            Toast.makeText(context, "Please enter a name and amount", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                builder.show();
            }
        });

    }

    //this method takes an editText, clears the text and then nulls the listener itself, so it won't clear again
    private static void clearEditText(final EditText editText){
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                //clear the text
                editText.setText("");

                //null the listener
                editText.setOnFocusChangeListener(null);
            }
        });
    }
}
