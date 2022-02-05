package com.matthewcannefax.menuplanner.grocery;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.matthewcannefax.menuplanner.MainViewModel;
import com.matthewcannefax.menuplanner.MenuApplication;
import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.databinding.FragmentGroceryListBinding;
import com.matthewcannefax.menuplanner.recipe.Ingredient;
import com.matthewcannefax.menuplanner.recipe.Measurement;
import com.matthewcannefax.menuplanner.recipe.MeasurementType;
import com.matthewcannefax.menuplanner.utils.NumberHelper;
import com.matthewcannefax.menuplanner.utils.ShareHelper;

import java.util.List;

//This activity displays a consolidated and sorted Grocery list based on the recipes that are added
//to the menu list
public class GroceryListFragment extends Fragment {

    private static List<Ingredient> ingredients;
    private FragmentGroceryListBinding binding;
    private MainViewModel viewModel;
    private GroceryRecyclerAdapter recyclerAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        ((MenuApplication) requireActivity().getApplicationContext()).getMenuApplicationComponent().inject(this);
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(requireContext()), R.layout.fragment_grocery_list, null, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        checkForNullGroceries();

        //set the title in the actionbar
        requireActivity().setTitle(R.string.grocery_list);
        ingredients = viewModel.getAllGroceries();

        binding.fab.setOnClickListener(v -> addGroceryItem());

        //this method to setup the grocery list adapter
        setGroceryListAdapter();
        initializeTopBar();
    }

    private void initializeTopBar() {
        binding.deleteButton.setOnClickListener(view -> {
            recyclerAdapter.getCurrentList()
                    .stream()
                    .filter(groceryRow -> groceryRow instanceof GroceryItemRow &&
                            ((GroceryItemRow) groceryRow).getGroceryItem().isItemChecked())
                    .forEach(groceryRow -> viewModel.removeGroceryItem(((GroceryItemRow) groceryRow).getGroceryItem()));
            Snackbar.make(requireContext(), requireView(), getString(R.string.items_removed), Snackbar.LENGTH_LONG).show();
            ingredients = viewModel.getAllGroceries();
            enableShareButton();
            recyclerAdapter.submitList(GroceryRowBuilder.buildGroceryRows(ingredients));
            binding.deleteButton.setEnabled(false);
        });
        binding.shareButton.setOnClickListener(view -> ShareHelper.sendGroceryList(requireContext()));
        enableShareButton();
        enableDeleteButton();
    }

    private boolean areGroceriesChecked() {
        return recyclerAdapter.getCurrentList().stream()
                .anyMatch(groceryRow -> groceryRow instanceof GroceryItemRow &&
                        ((GroceryItemRow) groceryRow).getGroceryItem().isItemChecked());
    }

    private void checkForNullGroceries() {
        if (viewModel.getAllGroceries() == null) {
            requireActivity().onBackPressed();
        }
    }

    private void addGroceryItem() {
        //create an alertdialog to input this information
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(R.string.add_item);

        //inflate the add_ingredient_item layout
        @SuppressLint("InflateParams") View newItemView = getLayoutInflater().inflate(R.layout.add_ingredient_item, null);

        //controls inside the view
        final EditText etAmount = newItemView.findViewById(R.id.amountText);
        final Spinner spMeasure = newItemView.findViewById(R.id.amountSpinner);
        final EditText etName = newItemView.findViewById(R.id.ingredientName);
        final Spinner spCat = newItemView.findViewById(R.id.categorySpinner);

        //setup the default array adapters for the category and measurementtype spinners
        ArrayAdapter<MeasurementType> measureAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, MeasurementType.getEnum());
        ArrayAdapter<GroceryCategory> ingredCatAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, GroceryCategory.getEnum());

        //set the spinner adpaters
        spMeasure.setAdapter(measureAdapter);
        spCat.setAdapter(ingredCatAdapter);

        //set the newItemView as the view for the alertdialog
        builder.setView(newItemView);

        builder.setNegativeButton("Cancel", null);
        final GroceryClickListener clickGroceryItem = this::clickGroceryItem;
        builder.setPositiveButton("OK", (dialogInterface, i) -> {
            //check that there are values for the name and the amount
            //also using a custom tryParse method to check that the value for the amount is indeed a double
            if (!etName.getText().toString().equals("") && !etAmount.getText().toString().equals("") && NumberHelper.tryParseDouble(etAmount.getText().toString())) {
                //add the new Ingredient to the ingredientList
                Ingredient newGroceryItem = new Ingredient(
                        etName.getText().toString(),
                        (GroceryCategory) spCat.getSelectedItem(),
                        new Measurement(
                                Double.parseDouble(etAmount.getText().toString()),
                                (MeasurementType) spMeasure.getSelectedItem()
                        )
                );
                viewModel.addGroceryItem(newGroceryItem);
                ingredients = viewModel.getAllGroceries();
                enableShareButton();
                recyclerAdapter.submitList(GroceryRowBuilder.buildGroceryRows(ingredients));

            } else {
                //Send the user a Toast to tell them that they need to enter both a name and amount in the edittexts
                Toast.makeText(requireContext(), R.string.enter_name_amount, Toast.LENGTH_SHORT).show();
            }
        });

        builder.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        ingredients = viewModel.getAllGroceries();
        setGroceryListAdapter();
    }

    private void setGroceryListAdapter() {
        if (ingredients != null) {
            recyclerAdapter = new GroceryRecyclerAdapter(this::clickGroceryItem);
            binding.groceryRecyclerView.setAdapter(recyclerAdapter);
            binding.groceryRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
            recyclerAdapter.submitList(GroceryRowBuilder.buildGroceryRows(ingredients));
        } else {
            Snackbar.make(requireContext(), requireView(), getString(R.string.no_grocery_list_found), Snackbar.LENGTH_LONG).show();
        }
    }

    private void clickGroceryItem(final int ingredientID, final GroceryViewChangeListener groceryViewChangeListener) {
        ingredients.stream().filter(ingredient -> ingredient.getIngredientID() == ingredientID)
                .findFirst().ifPresent(ingredient -> {
            ingredient.setItemChecked(!ingredient.isItemChecked());
            viewModel.setGroceryItemChecked(ingredient.getIngredientID(), ingredient.isItemChecked());
            groceryViewChangeListener.changeView(ingredient.isItemChecked());
        });
        enableDeleteButton();
    }

    private void enableDeleteButton() {
        final boolean enable = areGroceriesChecked();
        binding.deleteButton.setEnabled(enable);
        binding.deleteButton.setClickable(enable);
    }

    private void enableShareButton() {
        binding.shareButton.setEnabled(!ingredients.isEmpty());
        binding.shareButton.setClickable(!ingredients.isEmpty());
    }
}
