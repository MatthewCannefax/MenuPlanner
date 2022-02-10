package com.matthewcannefax.menuplanner.recipe.menuList;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

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
import com.matthewcannefax.menuplanner.utils.PermissionsHelper;

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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(requireContext()), R.layout.fragment_menu_list, null, false);
        requireActivity().setTitle(this.getString(R.string.menu_activity_name));
        adapter = new MenuListRecyclerAdapter(this::recipeClickListener, this::removeFromMenuClickListener);
        binding.menuRecyclerView.setAdapter(adapter);
        binding.menuRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Optional.ofNullable(((MainActivity) requireActivity()).getSupportActionBar()).ifPresent(ActionBar::show);
        setFilterBTNListener();
        binding.fab.setOnClickListener(v -> addRecipeToMenu());

        //check that the required permissions are allowed
        PermissionsHelper.checkPermissions(requireActivity(), requireContext());

        loadMenuItems();
    }

    private void setCategoryAdapter() {
        if (viewModel.getCurrentMenu() != null) {
            ArrayAdapter<RecipeCategory> catSpinnerAdapter = new ArrayAdapter<>(requireContext(), R.layout.category_spinner_item, FilterHelper.getMenuCategoriesUsed(requireContext()));
            catSpinnerAdapter.setDropDownViewResource(R.layout.category_spinner_item);
            binding.catSpinner.setAdapter(catSpinnerAdapter);
        }
    }

    private void setFilterBTNListener() {
        binding.filterBTN.setOnClickListener(view -> {
            final RecipeCategory selectedCat = (RecipeCategory) binding.catSpinner.getSelectedItem();
            if (selectedCat != RecipeCategory.ALL) {
                final List<Recipe> filteredRecipes = viewModel.getCurrentMenu().stream()
                        .filter(recipe -> recipe.getCategory().equals(selectedCat)).collect(Collectors.toList());
                adapter.submitList(filteredRecipes);
            } else {
                adapter.submitList(viewModel.getCurrentMenu());
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

    private void loadMenuItems() {
        if (viewModel.getCurrentMenu() == null || viewModel.getCurrentMenu().size() <= 0) {
            //TODO setup somekind of observable here
            viewModel.setCurrentMenu(viewModel.getMenuFromDB());
        }
        if (viewModel.getCurrentMenu() != null && viewModel.getCurrentMenu().size() != 0) {
            setCategoryAdapter();
            if (binding.catSpinner.getSelectedItemPosition() != 0) {
                final RecipeCategory selectedCat = (RecipeCategory) binding.catSpinner.getSelectedItem();
                List<Recipe> filteredRecipes = viewModel.getCurrentMenu().stream()
                        .filter(recipe -> recipe.getCategory().equals(selectedCat)).collect(Collectors.toList());
                adapter.submitList(filteredRecipes);
            } else {
                adapter.submitList(viewModel.getCurrentMenu());
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
                    adapter.submitList(viewModel.getCurrentMenu());
                    final ArrayAdapter<RecipeCategory> rcAdapter = new ArrayAdapter<>(requireContext(), R.layout.category_spinner_item, FilterHelper.getMenuCategoriesUsed(requireContext()));
                    binding.catSpinner.setAdapter(rcAdapter);
                });
        builder.show();
    }
}
