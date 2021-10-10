package com.matthewcannefax.menuplanner.addEdit;


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

import com.matthewcannefax.menuplanner.ImageCaptureFragment;
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
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class AddRecipeFragment extends Fragment implements ImageCaptureFragment {

    private Recipe newRecipe;
    private FragmentRecipeDetailBinding binding;
    private MainViewModel viewModel;
    private AddIngredientClickListener addIngredientClickListener;
    private DirectionsChangedListener directionsChangedListener;

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

        addIngredientClickListener = this::addIngredientListener;
        directionsChangedListener = this::directionsChangeListener;

        newRecipe = new Recipe();

        requireActivity().setTitle(R.string.add_recipe);

        List<RecipeCategory> recipeCats = new LinkedList<>(Arrays.asList(RecipeCategory.values()));
        recipeCats.remove(0);

        recipeCats.sort(Comparator.comparing(RecipeCategory::toString));

        //setup the Category Spinner
        ArrayAdapter<RecipeCategory> spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, recipeCats);
        binding.categorySpinner.setAdapter(spinnerAdapter);

        try {
            //set the default image in the recipeIMG imageView
            String imgPath = getString(R.string.no_img_selected);
            ImageHelper.setImageViewDrawable(imgPath, requireContext(), binding.recipeIMG);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        RecipeDetailListAdapter adapter = new RecipeDetailListAdapter(
                new RecipeDetailListRowBuilder(requireContext(), newRecipe).build(),
                newRecipe,
                addIngredientClickListener,
                directionsChangedListener);
        binding.ingredientDirectionRecyclerview.setAdapter(adapter);
        binding.ingredientDirectionRecyclerview.setLayoutManager(new LinearLayoutManager(requireContext()));

        ImageHelper.setImageViewClickListener(requireContext(), binding.recipeIMG, requireActivity());
    }

    //create the menu button in the actionbar (currently only contains the submit option)
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        //add the menu button to add recipes to the recipes list
//        MenuInflater menuInflater = getMenuInflater();
//
//        //using the menu layout created specifically for this activity
//        menuInflater.inflate(R.menu.add_recipe_menu, menu);
//        MenuItem shareItem = menu.findItem(R.id.shareRecipe);
//        shareItem.setVisible(false);
//        return true;
//    }

    //handle the clicks of the menu items
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        //if the submit button is clicked
//        //this is where the new recipe is all put together and then added to Recipe list and JSON files
//        if (item.getItemId() == R.id.menuSubmitBTN) {
//
//            if (newRecipe.getIngredientList() != null && newRecipe.getIngredientList().size() != 0) {
//                //get the name, directions and category from the controls
//                newRecipe.setName(binding.recipeName.getText().toString());
////                newRecipe.setDirections(directionsMultiLine.getText().toString());
//                newRecipe.setCategory((RecipeCategory) binding.categorySpinner.getSelectedItem());
//
//                if (newRecipe.getImagePath() == null || newRecipe.getImagePath().equals("")) {
//                    newRecipe.setImagePath(getString(R.string.no_img_selected));
//                }
//
//                viewModel.addRecipe(newRecipe);
//
//                requireActivity().onBackPressed();
//
//                return true;
//            } else {
//                String message = getString(R.string.at_least_one_ingredient);
//                Snackbar.make(requireContext(), requireView(), message, Snackbar.LENGTH_LONG).show();
//                return true;
//            }
//        } else if (item.getItemId() == android.R.id.home) {
//            NavDrawer.navDrawerOptionsItem(binding.drawerLayout);
//            return true;
//        } else if (item.getItemId() == R.id.help) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
//            builder.setTitle(R.string.help);
//            builder.setMessage(R.string.add_recipe_help);
//            builder.setNeutralButton(R.string.ok, null);
//            builder.show();
//            return true;
//        } else {
//            return false;
//        }
//
//    }

    @Override
    public void handleActivityResult(final int requestCode, final int resultCode, final Intent data) {
        newRecipe.setImagePath(ImageHelper.getPhotoTaken(requireContext(), requestCode, resultCode, data, binding.recipeIMG));
        ShareHelper.activityResultImportCookbook(requireContext(), requireActivity(), requestCode, resultCode, data);
    }

    private void addIngredientListener() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(requireContext());
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
                String s = charSequence.toString();

                Ingredient ingredient = viewModel.getIngredientByText(charSequence);

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
                //send a Toast prompting the user to make sure and fill in the alert dialog correctly
                Toast.makeText(requireContext(), R.string.enter_name_amount, Toast.LENGTH_SHORT).show();
            }
        });

        builder.show();
    }

    private void directionsChangeListener(String string) {
        if (string == null) {
            newRecipe.setDirections("");
        } else {
            newRecipe.setDirections(string);
        }
    }
}
