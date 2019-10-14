package com.matthewcannefax.menuplanner.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.matthewcannefax.menuplanner.addEdit.IngredientRecyclerAdapter;
import com.matthewcannefax.menuplanner.grocery.GroceryCategory;
import com.matthewcannefax.menuplanner.recipe.MeasurementType;
import com.matthewcannefax.menuplanner.recipe.Ingredient;
import com.matthewcannefax.menuplanner.recipe.Measurement;
import com.matthewcannefax.menuplanner.recipe.Recipe;
import com.matthewcannefax.menuplanner.utils.database.DataSource;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerHelper {
    private ViewPagerHelper(){
        throw new AssertionError();
    }

    public static void setAddIngredientButton(final Context context, Button button,
                                              final Recipe newRecipe, final RecyclerView recyclerView){

        final DataSource mDataSource = new DataSource(context);

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


                //setup the default array adapters for the category and measurementtype spinners
                ArrayAdapter<MeasurementType> measureAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, MeasurementType.getEnum());
                final ArrayAdapter<GroceryCategory> ingredCatAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, GroceryCategory.getEnumIngredients());

                //set the spinner adpaters
                spMeasure.setAdapter(measureAdapter);
                spCat.setAdapter(ingredCatAdapter);

                etName.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        String s = charSequence.toString();

                        Ingredient ingredient = mDataSource.getSpecificIngredient(charSequence);

                        if(ingredient.getCategory() != null && ingredient.getMeasurement().getType() != null){
                            spCat.setSelection(GroceryCategory.getCatPosition(ingredient.getCategory()));
                            spMeasure.setSelection(MeasurementType.getOrdinal(ingredient.getMeasurement().getType()));
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                    }
                });

                //set the new view as the view for the alertdialog
                builder.setView(editIngredientView);

                //setup the buttons for the alertdialog
                builder.setNegativeButton(R.string.cancel, null);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //check if the all the inputs are filled in correctly
                        if (!etName.getText().toString().equals("") && !etAmount.getText().toString().equals("") && NumberHelper.tryParseDouble(etAmount.getText().toString())) {
                            //check if the ingredient list exists for this recipe
                            if(newRecipe.getIngredientList() != null){
                                //add the new Ingredient to the ingredientList
                                Ingredient newIngredient = new Ingredient();
                                newIngredient.setName(etName.getText().toString());
                                newIngredient.setCategory((GroceryCategory)spCat.getSelectedItem());
                                newIngredient.setMeasurement(new Measurement(
                                        Double.parseDouble(etAmount.getText().toString()),
                                        (MeasurementType)spMeasure.getSelectedItem()
                                ));
                                newRecipe.getIngredientList().add(newIngredient);
                                //notify the ingredientItemAdapter that the dataset has been changed
//                                ingredientItemAdapter.notifyDataSetChanged();

//                                IngredientItemAdapter ingredientItemAdapter1 = new IngredientItemAdapter(context, newRecipe.getIngredientList());
                                IngredientRecyclerAdapter recyclerAdapter1 = new IngredientRecyclerAdapter(context, newRecipe.getIngredientList(), newRecipe);
                                recyclerView.setAdapter(recyclerAdapter1);
                                recyclerView.setLayoutManager(new LinearLayoutManager(context));
//                                listView.setAdapter(ingredientItemAdapter1);
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
//                                IngredientItemAdapter ingredientItemAdapter1 = new IngredientItemAdapter(context, newRecipe.getIngredientList());
//                                listView.setAdapter(ingredientItemAdapter1);

                                IngredientRecyclerAdapter recyclerAdapter1 = new IngredientRecyclerAdapter(context, newRecipe.getIngredientList(), newRecipe);
                                recyclerView.setAdapter(recyclerAdapter1);
                                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                            }
                        }else{
                            //send a Toast prompting the user to make sure and fill in the alert dialog correctly
                            Toast.makeText(context, R.string.enter_name_amount, Toast.LENGTH_SHORT).show();
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
