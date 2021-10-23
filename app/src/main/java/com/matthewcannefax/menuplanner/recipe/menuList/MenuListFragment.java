package com.matthewcannefax.menuplanner.recipe.menuList;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.matthewcannefax.menuplanner.MainActivity;
import com.matthewcannefax.menuplanner.MainViewModel;
import com.matthewcannefax.menuplanner.MenuApplication;
import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.databinding.FragmentMenuListBinding;
import com.matthewcannefax.menuplanner.recipe.Recipe;
import com.matthewcannefax.menuplanner.recipe.RecipeCategory;
import com.matthewcannefax.menuplanner.utils.AnimationUtils;
import com.matthewcannefax.menuplanner.utils.FilterHelper;
import com.matthewcannefax.menuplanner.utils.ImageHelper;
import com.matthewcannefax.menuplanner.utils.PermissionsHelper;
import com.matthewcannefax.menuplanner.utils.notifications.NotificationHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MenuListFragment extends Fragment {

    private MainViewModel viewModel;
    private FragmentMenuListBinding binding;
    private MenuListRecyclerAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        ((MenuApplication) requireActivity().getApplicationContext()).getMenuApplicationComponent().inject(this);
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

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

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Optional.ofNullable(((MainActivity) requireActivity()).getSupportActionBar()).ifPresent(ActionBar::show);
        setMenuListViewAdapter();
        setFilterBTNListener();
        binding.fab.setOnClickListener(v -> addRecipeToMenu());

        //check that the required permissions are allowed
        PermissionsHelper.checkPermissions(requireActivity(), requireContext());

        //if the menu list is not null notify the adapter of changes, in case there are any
        setCatAdapter();
    }

    private void setCatAdapter() {
        if (viewModel.getMenu() != null) {
            adapter.notifyDataSetChanged();

            //setup the arrayAdapter for catSpinner
            @SuppressWarnings("Convert2Diamond") ArrayAdapter<RecipeCategory> catSpinnerAdapter = new ArrayAdapter<RecipeCategory>(requireContext(), R.layout.category_spinner_item, FilterHelper.getMenuCategoriesUsed(requireContext()));
            catSpinnerAdapter.setDropDownViewResource(R.layout.category_spinner_item);
            binding.catSpinner.setAdapter(catSpinnerAdapter);
        }
    }

    private void setFilterBTNListener() {
        binding.filterBTN.setOnClickListener(view -> {
            final RecipeCategory selectedCat = (RecipeCategory) binding.catSpinner.getSelectedItem();
            if (selectedCat != RecipeCategory.ALL) {
                final List<Recipe> filteredRecipes = viewModel.getMenu().stream()
                        .filter(recipe -> recipe.getCategory().equals(selectedCat)).collect(Collectors.toList());
                adapter.submitList(filteredRecipes);
            } else {
                adapter.submitList(viewModel.getMenu());
            }
        });
    }

    private void addRecipeToMenu() {
        List<Recipe> allRecipes = viewModel.getCookbook();
        if (allRecipes != null && allRecipes.size() != 0) {
            Navigation.findNavController(requireView()).navigate(R.id.cookbook_fragment, null, AnimationUtils.getFragmentTransitionAnimation());
        } else {
            Snackbar.make(requireContext(), requireView(), getString(R.string.no_recipes_in_cookbook), Snackbar.LENGTH_LONG).show();
        }
    }

    //this method sets up the menu list adapter
    private void setMenuListViewAdapter() {
        //set up the menu list adapter only if the menu list exists
        if (viewModel.getMenu() != null) {
            adapter = new MenuListRecyclerAdapter(this::recipeClickListener, this::removeFromMenuClickListener);
            binding.menuRecyclerView.setAdapter(adapter);
            binding.menuRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        } else {
            Snackbar.make(requireContext(), requireView(), getString(R.string.no_menu_items), Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.setCurrentMenu(viewModel.getCurrentMenu());
        setMenuListViewAdapter();
        if (viewModel.getMenu() != null && viewModel.getMenu().size() != 0) {
            if (binding.catSpinner.getSelectedItemPosition() != 0) {
                final RecipeCategory selectedCat = (RecipeCategory) binding.catSpinner.getSelectedItem();
                List<Recipe> filteredRecipes = viewModel.getMenu().stream()
                        .filter(recipe -> recipe.getCategory().equals(selectedCat)).collect(Collectors.toList());
                adapter.submitList(filteredRecipes);
            } else {
                adapter.submitList(viewModel.getMenu());
            }
            binding.menuRecyclerView.setAdapter(adapter);
            binding.menuRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        }
    }

    private void recipeClickListener(final Recipe recipe) {
        viewModel.setSelectedRecipe(recipe);
        Navigation.findNavController(requireView()).navigate(R.id.view_recipe_fragment, null, AnimationUtils.getFragmentTransitionAnimation());
    }

    private void removeFromMenuClickListener(final Integer position) {
        final Recipe recipe = adapter.getCurrentList().get(position);
        final AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.remove_from_menu))
                .setMessage(String.format(getString(R.string.are_you_sure_remove_format), recipe.toString()))
                .setNegativeButton(getString(R.string.cancel), null)
                .setPositiveButton(getString(R.string.ok), (dialogInterface, i) -> {
                    Snackbar.make(requireView(), String.format(getString(R.string.format_recipe_removed), recipe.toString()), Snackbar.LENGTH_LONG).show();
                    viewModel.removeRecipeFromMenu(recipe.getRecipeID());
                    adapter.submitList(viewModel.getMenu());
                    final ArrayAdapter<RecipeCategory> rcAdapter = new ArrayAdapter<>(requireContext(), R.layout.category_spinner_item, FilterHelper.getMenuCategoriesUsed(requireContext()));
                    binding.catSpinner.setAdapter(rcAdapter);
                });
        builder.show();
    }
}
