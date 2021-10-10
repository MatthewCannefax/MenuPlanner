package com.matthewcannefax.menuplanner.addEdit;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.matthewcannefax.menuplanner.MainViewModel;
import com.matthewcannefax.menuplanner.MenuApplication;
import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.databinding.FragmentRecipeDetailBinding;
import com.matthewcannefax.menuplanner.grocery.GroceryCategory;
import com.matthewcannefax.menuplanner.recipe.Ingredient;
import com.matthewcannefax.menuplanner.recipe.Measurement;
import com.matthewcannefax.menuplanner.recipe.MeasurementType;
import com.matthewcannefax.menuplanner.recipe.Recipe;
import com.matthewcannefax.menuplanner.recipe.RecipeCategory;
import com.matthewcannefax.menuplanner.utils.ImageHelper;
import com.matthewcannefax.menuplanner.utils.NumberHelper;
import com.matthewcannefax.menuplanner.utils.ShareHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

//this class is for editing already existing recipes

public class ViewRecipeFragment extends Fragment {

    public static final String RECIPE_ID = "item_id";
    private MainViewModel viewModel;
    private FragmentRecipeDetailBinding binding;
    private boolean areDirectionsChanged = false;
    private String newDirections;

    //an object for the unedited recipe
    private Recipe oldRecipe;

    //an object for any changes made to the oldRecipe
    private Recipe newRecipe;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        ((MenuApplication) requireActivity().getApplicationContext()).getMenuApplicationComponent().inject(this);
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(requireContext()), R.layout.fragment_recipe_detail, null, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        oldRecipe = viewModel.getSelectedRecipe();
        newRecipe = oldRecipe;

        binding.recipeName.setText(oldRecipe.getName());

        //make sure RecipeCategory.ALL is not an option in the categories
        List<RecipeCategory> recipeCats = new LinkedList<>(Arrays.asList(RecipeCategory.values()));
        recipeCats.remove(0);

        Collections.sort(recipeCats, Comparator.comparing(RecipeCategory::toString));

        //setup the spinner
        ArrayAdapter<RecipeCategory> spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, recipeCats);
        binding.categorySpinner.setAdapter(spinnerAdapter);
        binding.categorySpinner.setSelection(spinnerAdapter.getPosition(oldRecipe.getCategory()));

        //setup the image if it is present
        if (oldRecipe.getImagePath() != null && !oldRecipe.getImagePath().equals("")) {
            ImageHelper.setImageViewDrawable(oldRecipe.getImagePath(), requireContext(), binding.recipeIMG);
        } else {
            ImageHelper.setImageViewDrawable("", requireContext(), binding.recipeIMG);
        }

        ImageHelper.setImageViewClickListener(requireContext(), binding.recipeIMG, requireActivity());

        RecipeDetailListAdapter adapter = new RecipeDetailListAdapter(new RecipeDetailListRowBuilder(requireContext(), oldRecipe).build(), oldRecipe, this::clickAddIngredientButton, this::setAreDirectionsChanged);
        binding.ingredientDirectionRecyclerview.setAdapter(adapter);
        binding.ingredientDirectionRecyclerview.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    //create the menu button in the actionbar (currently only contains the submit option)
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu){
//        //add the menu button to add recipes to the recipes list
//        MenuInflater menuInflater = getMenuInflater();
//
//        //using the menu layout created specifically for this activity
//        menuInflater.inflate(R.menu.add_recipe_menu, menu);
//
//        //currently getting the only item in the menu for this menu item object
//        MenuItem editSubmitBTN = menu.getItem(0);
//
//        //setting the text to "Edit" by default, it will be change on each click
//        editSubmitBTN.setTitle(getString(R.string.submit));
//
//        return true;
//    }

//    //handle the clicks of the menu items
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item){
//        //if the submit button is clicked and the recipe is editable
//        if(item.getItemId() == R.id.menuSubmitBTN) {
//
//            //check if there are ingredients in the ingredient list
//            if (oldRecipe.getIngredientList() != null && oldRecipe.getIngredientList().size() != 0) {
//                //Alert dialog to ask the user if they are sure they want to save the recipe
//                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
//                builder.setTitle(R.string.save_recipe_question);
//                builder.setMessage(getString(R.string.are_you_sure_change) + oldRecipe.getName() + getString(R.string.question_mark));
//                builder.setNegativeButton(R.string.cancel, null);
//                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        //new recipe object created by the user
//                        newRecipe = new Recipe();
//                        newRecipe = oldRecipe;
//                        newRecipe.setName(binding.recipeName.getText().toString());
////                        newRecipe.setDirections(directionsMultiLine.getText().toString());
//                        newRecipe.setCategory((RecipeCategory) binding.categorySpinner.getSelectedItem());
//
//                        //get the ingredients of the new recipe from the old recipe object
//                        newRecipe.setIngredientList(oldRecipe.getIngredientList());
//
//                        newRecipe.setImagePath(oldRecipe.getImagePath());
//
//                        if (areDirectionsChanged) {
//                            newRecipe.setDirections(newDirections);
//                        }
//                        viewModel.updateRecipe(newRecipe);
//                    }
//                });
//
//                builder.show();
//
//                return true;
//            } else {
//                String message = getString(R.string.at_least_one_ingredient);
//                Snackbar.make(requireContext(), requireView(), message, Snackbar.LENGTH_LONG).show();
//                return true;
//            }
//        }
//        else if(item.getItemId() == android.R.id.home) {
//            NavDrawer.navDrawerOptionsItem(binding.drawerLayout);
//            return true;
//        }else if(item.getItemId() == R.id.shareRecipe){
//            ShareHelper.sendSingleRecipe(requireContext(), oldRecipe.getRecipeID());
//            return true;
//        }
//        else if(item.getItemId() == R.id.help){
//            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
//            builder.setTitle("Help");
//            builder.setMessage(R.string.edit_recipe_help);
//            builder.setNeutralButton(R.string.ok, null);
//            builder.show();
//            return true;
//        }
//        else{
//            return false;
//        }
//
//    }

    //Override the onActivityResult to catch the image chosen or taken to set as the image for the edited recipe
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //get the path of the new image and set to the newRecipe object
        newRecipe.setImagePath(ImageHelper.getPhotoTaken(requireContext(), requestCode, resultCode, data, binding.recipeIMG));
        ShareHelper.activityResultImportCookbook(requireContext(), requireActivity(), requestCode, resultCode, data);
    }

    private void clickAddIngredientButton() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(getString(R.string.add_ingredient_dialog_title));

        View editIngredientView = LayoutInflater.from(requireContext())
                .inflate(R.layout.add_ingredient_item, requireActivity().findViewById(android.R.id.content), false);

        //controls inside the view
        final EditText etAmount = editIngredientView.findViewById(R.id.amountText);
        final Spinner spMeasure = editIngredientView.findViewById(R.id.amountSpinner);
        final EditText etName = editIngredientView.findViewById(R.id.ingredientName);
        final Spinner spCat = editIngredientView.findViewById(R.id.categorySpinner);

        ArrayAdapter<MeasurementType> measureAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, MeasurementType.getEnum());
        final ArrayAdapter<GroceryCategory> ingredCatAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, GroceryCategory.getEnumIngredients());

        //set the spinner adpaters
        spMeasure.setAdapter(measureAdapter);
        spCat.setAdapter(ingredCatAdapter);

        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                final Ingredient ingredient = viewModel.getIngredientByText(charSequence);
                if (ingredient.getCategory() != null && ingredient.getMeasurement().getType() != null) {
                    spCat.setSelection(GroceryCategory.getCatPosition(ingredient.getCategory()));
                    spMeasure.setSelection(MeasurementType.getOrdinal(ingredient.getMeasurement().getType()));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        builder.setView(editIngredientView);

        //setup the buttons for the alertdialog
        builder.setNegativeButton(R.string.cancel, null);
        final AddIngredientClickListener addIngredientClickListener = this::clickAddIngredientButton;
        final DirectionsChangedListener directionsChangedListener = this::setAreDirectionsChanged;
        builder.setPositiveButton(R.string.ok, (dialogInterface, i) -> {
            //check if the all the inputs are filled in correctly
            if (!etName.getText().toString().equals("") && !etAmount.getText().toString().equals("") && NumberHelper.tryParseDouble(etAmount.getText().toString())) {
                //check if the ingredient list exists for this recipe
                if (newRecipe.getIngredientList() != null) {
                    //add the new Ingredient to the ingredientList
                    Ingredient newIngredient = new Ingredient();
                    newIngredient.setName(etName.getText().toString());
                    newIngredient.setCategory((GroceryCategory) spCat.getSelectedItem());
                    newIngredient.setMeasurement(new Measurement(
                            Double.parseDouble(etAmount.getText().toString()),
                            (MeasurementType) spMeasure.getSelectedItem()
                    ));
                    newRecipe.getIngredientList().add(newIngredient);
                    RecipeDetailListAdapter recyclerAdapter1 = new RecipeDetailListAdapter(new RecipeDetailListRowBuilder(requireContext(), newRecipe).build(), newRecipe, addIngredientClickListener, directionsChangedListener);
                    binding.ingredientDirectionRecyclerview.setAdapter(recyclerAdapter1);
                    binding.ingredientDirectionRecyclerview.setLayoutManager(new LinearLayoutManager(requireContext()));
                }
                //if the ingredient list does not exist create the new Ingredient and a new Ingredient list
                //then add that to the recipe
                else {
                    //create the new ingredient
                    Ingredient ingredient = new Ingredient(
                            etName.getText().toString(),
                            (GroceryCategory) spCat.getSelectedItem(),
                            new Measurement(
                                    Double.parseDouble(etAmount.getText().toString()),
                                    (MeasurementType) spMeasure.getSelectedItem()
                            ));
                    //create the new ingredient list
                    List<Ingredient> newIngredredients = new ArrayList<>();

                    //add the new ingredient to the the new list
                    newIngredredients.add(ingredient);

                    //set the new list as the list for the new recipe
                    newRecipe.setIngredientList(newIngredredients);

                    RecipeDetailListAdapter recyclerAdapter1 = new RecipeDetailListAdapter(new RecipeDetailListRowBuilder(requireContext(), newRecipe).build(), newRecipe, addIngredientClickListener, directionsChangedListener);
                    binding.ingredientDirectionRecyclerview.setAdapter(recyclerAdapter1);
                    binding.ingredientDirectionRecyclerview.setLayoutManager(new LinearLayoutManager(requireContext()));
                }
            } else {
                Toast.makeText(requireContext(), R.string.enter_name_amount, Toast.LENGTH_SHORT).show();
            }
        });

        builder.show();
    }

    private void setAreDirectionsChanged(String string) {
        if (oldRecipe.getDirections() == null) {
            if (!string.equals("")) {
                areDirectionsChanged = true;
                newDirections = string;
            }
        } else {
            newDirections = string;
            areDirectionsChanged = (!oldRecipe.getDirections().equals(string));
        }
    }
}
