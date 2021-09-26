package com.matthewcannefax.menuplanner.recipe.menuList;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import com.matthewcannefax.menuplanner.MenuApplication;
import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.databinding.FragmentMenuListBinding;
import com.matthewcannefax.menuplanner.recipe.recipeList.RecipeListActivity;
import com.matthewcannefax.menuplanner.grocery.GroceryListActivity;
import com.matthewcannefax.menuplanner.recipe.RecipeCategory;
import com.matthewcannefax.menuplanner.recipe.Recipe;
import com.matthewcannefax.menuplanner.utils.FilterHelper;
import com.matthewcannefax.menuplanner.utils.navigation.NavDrawer;
import com.matthewcannefax.menuplanner.utils.navigation.NavHelper;
import com.matthewcannefax.menuplanner.utils.PermissionsHelper;
import com.matthewcannefax.menuplanner.utils.ShareHelper;
import com.matthewcannefax.menuplanner.utils.database.DataSource;
import com.matthewcannefax.menuplanner.utils.notifications.NotificationHelper;

import java.util.ArrayList;
import java.util.List;

//this activity is to display the selected MenuList
//it has contains buttons to add a recipe to the menu and generate a grocery list
public class MenuListFragment extends Fragment {

    public static final String RECIPE_ID_STRING = "selected_recipe";

    private FragmentMenuListBinding binding;

    private List<Recipe> menuList;
    private MenuListRecyclerAdapter adapter;
    DataSource mDataSource;
//    private boolean mTwoPane = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        ((MenuApplication) requireActivity().getApplicationContext()).getMenuApplicationComponent().inject(this);
        super.onCreate(savedInstanceState);
        mDataSource = new DataSource(requireContext());

        final SharedPreferences sharedPref = requireActivity().getSharedPreferences(getString(R.string.is_preloaded), 0);
        boolean isPreloaded = sharedPref.getBoolean(getString(R.string.is_preloaded), false);
        menuList = mDataSource.getAllMenuRecipes();

        PermissionsHelper.setMenuFirstInstance(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationHelper notificationHelper = new NotificationHelper(requireContext());
            notificationHelper.scheduleJob();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(requireContext()), R.layout.fragment_menu_list, null, false);
        requireActivity().setTitle(this.getString(R.string.menu_activity_name));

        setMenuListViewAdapter();

        final MenuListRecyclerAdapter allMenuAdapter = new MenuListRecyclerAdapter(getChildFragmentManager(), requireContext(), mDataSource.getAllMenuRecipes(), binding.catSpinner);

        setFilterBTNListener(requireContext(), binding.filterBTN, allMenuAdapter);

        binding.fab.setOnClickListener(view -> addRecipeToMenu());

        NavDrawer.setupNavDrawerMenuButton(((AppCompatActivity) requireActivity()).getSupportActionBar());

        NavDrawer.setupNavDrawer(requireActivity(), requireContext(), binding.navList);

        //check that the required permissions are allowed
        PermissionsHelper.checkPermissions(requireActivity(), requireContext());

        //checkPermissions(mContext, sharedPref, isPreloaded);

        //if the menu list is not null notify the adapter of changes, in case there are any
        setCatAdapter();

        return binding.getRoot();
    }

    private void setCatAdapter() {
        if (mDataSource.getAllMenuRecipes() != null) {
            adapter.notifyDataSetChanged();

            //setup the arrayAdapter for catSpinner
            @SuppressWarnings("Convert2Diamond") ArrayAdapter<RecipeCategory> catSpinnerAdapter = new ArrayAdapter<RecipeCategory>(requireContext(), R.layout.category_spinner_item, FilterHelper.getMenuCategoriesUsed(requireContext()));
            catSpinnerAdapter.setDropDownViewResource(R.layout.category_spinner_item);
            binding.catSpinner.setAdapter(catSpinnerAdapter);
        }
    }

    private void setFilterBTNListener(final Context mContext, Button filterBTN, final MenuListRecyclerAdapter allMenuAdapter) {
        filterBTN.setOnClickListener(view -> {
            RecipeCategory selectedCat = (RecipeCategory) binding.catSpinner.getSelectedItem();
            if (selectedCat != RecipeCategory.ALL) {
                List<Recipe> filteredRecipes = new ArrayList<>();

                for (Recipe r : mDataSource.getAllMenuRecipes()) {
                    if (r.getCategory() == selectedCat) {
                        filteredRecipes.add(r);
                    }
                }

                MenuListRecyclerAdapter filteredAdapter = new MenuListRecyclerAdapter(getChildFragmentManager(), mContext, filteredRecipes, binding.catSpinner);
                binding.menuRecyclerView.setAdapter(filteredAdapter);
                binding.menuRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            } else {
                MenuListRecyclerAdapter allAdapter = new MenuListRecyclerAdapter(getChildFragmentManager(), requireContext(), mDataSource.getAllMenuRecipes(), binding.catSpinner);
                binding.menuRecyclerView.setAdapter(allAdapter);
                binding.menuRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            }
        });
    }

    private void addRecipeToMenu() {
        List<Recipe> allRecipes = mDataSource.getAllRecipes();
        if (allRecipes != null && allRecipes.size() != 0) {
            //new intent to move to the RecipeListActivity
            Intent intent = new Intent(requireActivity(), RecipeListActivity.class);//TODO this will change
            intent.putExtra("TITLE", getString(R.string.add_to_menu));
            MenuListFragment.this.startActivity(intent);
        } else {
//            Toast.makeText(this, "No Recipes in the Cookbook", Toast.LENGTH_SHORT).show();
            Snackbar.make(requireContext(), requireView(), getString(R.string.no_recipes_in_cookbook), Snackbar.LENGTH_LONG).show();
        }
    }

    //this method sets up the menu list adapter
    private void setMenuListViewAdapter() {
        //set up the menu list adapter only if the menu list exists
        if (mDataSource.getAllMenuRecipes() != null) {
            adapter = new MenuListRecyclerAdapter(getChildFragmentManager(), requireContext(), mDataSource.getAllMenuRecipes(), binding.catSpinner);
            //set the adapter of the listview to the recipeItemAdapter
            //Might try to use a Recycler view instead, since it is typically smoother when scrolling
            binding.menuRecyclerView.setAdapter(adapter);
            binding.menuRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        }
        //if the list does not exist, send the user a toast saying that there are no menu items
        else {
//            Toast.makeText(this, "No Menu Items", Toast.LENGTH_SHORT).show();
            Snackbar.make(requireContext(), requireView(), getString(R.string.no_menu_items), Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        menuList = mDataSource.getAllMenuRecipes();
        setMenuListViewAdapter();

        if (mDataSource.getAllMenuRecipes() != null && mDataSource.getAllMenuRecipes().size() != 0) {
            if (binding.catSpinner.getSelectedItemPosition() != 0) {

                RecipeCategory selectedCat = (RecipeCategory) binding.catSpinner.getSelectedItem();
                List<Recipe> filteredRecipes = new ArrayList<>();

                for (Recipe r :
                        mDataSource.getAllMenuRecipes()) {
                    if (r.getCategory() == selectedCat) {
                        filteredRecipes.add(r);
                    }
                }

                adapter = new MenuListRecyclerAdapter(getChildFragmentManager(), requireContext(), filteredRecipes, binding.catSpinner);
            } else {
                adapter = new MenuListRecyclerAdapter(getChildFragmentManager(), requireContext(), mDataSource.getAllMenuRecipes(), binding.catSpinner);
            }

            binding.menuRecyclerView.setAdapter(adapter);
            binding.menuRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        }

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    //This is the overridden method to create the options menu in the actionbar
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        //add the menu button to add recipes
//        MenuInflater menuInflater = requireActivity().getMenuInflater();
//
//        //using the menu layout that is made specifically for this activity
//        menuInflater.inflate(R.menu.menu_activity_menu, menu);
//
//        return true;
//    }


    //this overridden method is to handle the actionbar clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //if the Add Recipe option is clicked
        switch (item.getItemId()) {
            case android.R.id.home:

                NavDrawer.navDrawerOptionsItem(binding.drawerLayout);
                return true;

            //if the Generate Grocery List option is clicked
            case R.id.generateGroceryListItem:
                NavHelper.newGroceryList(requireActivity(), requireContext());
                return true;
            case R.id.appendGroceryListItem:
                mDataSource.menuIngredientsToGroceryDB();
                Intent appendIntent = new Intent(requireActivity(), GroceryListActivity.class);//TODO this will change
                startActivity(appendIntent);
                return true;
            case R.id.help:
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle(R.string.help);
                builder.setMessage(R.string.menu_list_help);
                builder.setNeutralButton(R.string.ok, null);
                builder.show();
                return true;
            case R.id.removeAll:
                menuList = mDataSource.getAllMenuRecipes();
                if (menuList != null && menuList.size() != 0) {
                    AlertDialog.Builder removeBuilder = new AlertDialog.Builder(requireContext());
                    removeBuilder.setTitle(getString(R.string.are_you_sure));
                    removeBuilder.setMessage(getString(R.string.remove_all_from_menu));
                    removeBuilder.setNegativeButton(getString(R.string.no), null);
                    removeBuilder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mDataSource.removeAllMenuItems();
                            menuList = null;
                            binding.menuRecyclerView.setAdapter(null);
                            setCatAdapter();
                            setFilterBTNListener(requireContext(), binding.filterBTN, null);
                            Snackbar.make(requireContext(), requireView(), getString(R.string.recipes_removed), Snackbar.LENGTH_LONG).show();
                        }
                    });
                    removeBuilder.show();
                } else {
                    Snackbar.make(requireContext(), requireView(), getString(R.string.no_menu_items), Snackbar.LENGTH_LONG).show();
                }
                return true;
            //default; this will allow the back button to work correctly
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