package com.matthewcannefax.menuplanner.grocery;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.matthewcannefax.menuplanner.MainViewModel;
import com.matthewcannefax.menuplanner.MenuApplication;
import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.databinding.FragmentGroceryListBinding;
import com.matthewcannefax.menuplanner.recipe.menuList.MenuListFragment;
import com.matthewcannefax.menuplanner.recipe.MeasurementType;
import com.matthewcannefax.menuplanner.recipe.Ingredient;
import com.matthewcannefax.menuplanner.recipe.Measurement;
import com.matthewcannefax.menuplanner.utils.navigation.NavDrawer;
import com.matthewcannefax.menuplanner.utils.NumberHelper;
import com.matthewcannefax.menuplanner.utils.ShareHelper;
import com.matthewcannefax.menuplanner.utils.database.DataSource;

import java.util.List;

//This activity displays a consolidated and sorted Grocery list based on the recipes that are added
//to the menu list
public class GroceryListFragment extends Fragment {

    private FragmentGroceryListBinding binding;
    private MainViewModel viewModel;
    private GroceryRecyclerAdapter recyclerAdapter;
    private static List<Ingredient> ingredients;
    private DataSource mDataSource;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        ((MenuApplication) requireActivity().getApplicationContext()).getMenuApplicationComponent().inject(this);
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        mDataSource = new DataSource();
        mDataSource.init(requireContext());
        mDataSource.open();
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
        ingredients = mDataSource.getAllGroceries();

        binding.fab.setOnClickListener(v -> addGroceryItem());

        //this method to setup the grocery list adapter
        setGroceryListAdapter();

        NavDrawer.setupNavDrawerMenuButton(((AppCompatActivity) requireActivity()).getSupportActionBar());

        //set up the nav drawer for this activity
        NavDrawer.setupNavDrawer(requireActivity(), requireContext(), binding.navList);
    }

    private void checkForNullGroceries() {
        if(mDataSource.getAllGroceries() == null){
            requireActivity().onBackPressed();
        }
    }

    private void addGroceryItem(){
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
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
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

                    //add the new grocery item to the database
                    mDataSource.createGroceryItem(newGroceryItem);

                    //notify the arrayadapter that the dataset has changed
//                    adapter = new GroceryItemAdapter(mContext, mDataSource.getAllGroceries());
//                    lv.setAdapter(adapter);

                    recyclerAdapter = new GroceryRecyclerAdapter(mDataSource.getAllGroceries(), clickGroceryItem);
                    binding.groceryRecyclerView.setAdapter(recyclerAdapter);
                    binding.groceryRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

                } else {
                    //Send the user a Toast to tell them that they need to enter both a name and amount in the edittexts
                    Toast.makeText(requireContext(), R.string.enter_name_amount, Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        mDataSource.open();
        ingredients = mDataSource.getAllGroceries();
        setGroceryListAdapter();

    }

    @Override
    public void onPause() {
        super.onPause();
        mDataSource.close();
    }

    private void setGroceryListAdapter(){
        if (ingredients != null) {
            recyclerAdapter = new GroceryRecyclerAdapter(ingredients, this::clickGroceryItem);
            binding.groceryRecyclerView.setAdapter(recyclerAdapter);
            binding.groceryRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        } else {
            Snackbar.make(requireContext(), requireView(), getString(R.string.no_grocery_list_found), Snackbar.LENGTH_LONG).show();
        }
    }

    private void clickGroceryItem(int ingredientID, GroceryViewChangeListener groceryViewChangeListener) {
        for (Ingredient ingredient : ingredients) {
            if (ingredient.getIngredientID() == ingredientID) {
                ingredient.setItemChecked(!ingredient.getItemChecked());
                mDataSource.setGroceryItemChecked(ingredient.getIngredientID(), ingredient.getItemChecked());

                groceryViewChangeListener.changeView(ingredient.getItemChecked());
                break;
            }
        }


    }

    //create the menu in the actionbar
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu){
//
//        //get the new menuinfater object
//        MenuInflater menuInflater = getMenuInflater();
//
//        //inflate the grocery list menu view
//        menuInflater.inflate(R.menu.grocery_list_menu, menu);
//
//        return true;
//    }

    //handle clicks on the actionbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        //if the remove selected items option is clicked
        switch (item.getItemId()) {
            case android.R.id.home:
                NavDrawer.navDrawerOptionsItem(binding.drawerLayout);
                return true;
            case R.id.removeSelectItems:
                int count = recyclerAdapter.getItemCount();

                //loop through the adapter
                for (int i = 0; i < recyclerAdapter.getItemCount(); i++) {
                    if (recyclerAdapter.getItem(i) instanceof GroceryItemRow) {
                        //get the ingredient item from the adapter item
                        Ingredient ingred = ((GroceryItemRow) recyclerAdapter.getItem(i)).getGroceryItem();

                        //if the item is checked and the the ingredient equals the item of the same position in the static grocery list
                        //the item will be removed
                        assert ingred != null;
                        if (ingred.getItemChecked()) {
                            //remove the item from the adapter
                            mDataSource.removeGroceryItem(ingred);
                        }
                    }
                }
                //display a Toast confirming to the user that the items have been removed
                //may want to switch to a dialog so the user can confirm deletion
                if (count != recyclerAdapter.getItemCount()) {
                    Snackbar.make(requireContext(), requireView(), getString(R.string.items_removed), Snackbar.LENGTH_LONG).show();
                }

                //reset the adapter
                ingredients = mDataSource.getAllGroceries();

                recyclerAdapter = new GroceryRecyclerAdapter(ingredients, this::clickGroceryItem);
                binding.groceryRecyclerView.setAdapter(recyclerAdapter);
                binding.groceryRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

                return true;
                
            case R.id.shareGroceryList:
                ShareHelper.sendGroceryList(requireContext());
                return true;
            case R.id.selectAllGroceries:
                for (Ingredient i :
                        mDataSource.getAllGroceries()) {
                    mDataSource.setGroceryItemChecked(i.getIngredientID(), true);
                }

                recyclerAdapter = new GroceryRecyclerAdapter(mDataSource.getAllGroceries(), this::clickGroceryItem);
                binding.groceryRecyclerView.setAdapter(recyclerAdapter);
                binding.groceryRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                return true;
            case R.id.help:
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Help");
                builder.setMessage(R.string.grocery_list_help);
                builder.setNeutralButton("OK", null);
                builder.show();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ShareHelper.activityResultImportCookbook(requireContext(), requireActivity(), requestCode, resultCode, data);
    }
}
