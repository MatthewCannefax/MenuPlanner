package com.matthewcannefax.menuplanner.recipe.recipeList;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.matthewcannefax.menuplanner.MainViewModel;
import com.matthewcannefax.menuplanner.MenuApplication;
import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.databinding.FragmentCookbookBinding;
import com.matthewcannefax.menuplanner.recipe.Recipe;
import com.matthewcannefax.menuplanner.recipe.RecipeCategory;
import com.matthewcannefax.menuplanner.utils.AnimationUtils;

import java.util.List;

public class CookbookFragment extends Fragment {

    private List<Recipe> recipeList;
    private RecipeRecyclerAdapter recyclerAdapter;
    private MainViewModel viewModel;
    private FragmentCookbookBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        ((MenuApplication) requireActivity().getApplicationContext()).getMenuApplicationComponent().inject(this);
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        recipeList = viewModel.getCookbook();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(requireContext()), R.layout.fragment_cookbook, null, false);
        requireActivity().setTitle("Cookbook");//TODO need string resource

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayAdapter<RecipeCategory> catSpinnerAdapter = new ArrayAdapter<>(requireContext(), R.layout.category_spinner_item, viewModel.getCookbookCategories());
        catSpinnerAdapter.setDropDownViewResource(R.layout.category_spinner_item);
        binding.catSpinner.setAdapter(catSpinnerAdapter);
        setRecipeListAdapter();

        setFabListener();

        binding.filterBTN.setOnClickListener(v -> {
            final RecipeCategory selectedCategory = (RecipeCategory) binding.catSpinner.getSelectedItem();
            recyclerAdapter.submitList(viewModel.getRecipesByCategory(selectedCategory));});
    }

    private void setFabListener() {
        binding.fab.setOnClickListener(view -> {
            if (viewModel.getCurrentCookbook().stream().anyMatch(Recipe::isItemChecked)) {
                viewModel.getCurrentCookbook().stream().filter(Recipe::isItemChecked).forEach(recipe -> {
                    recipe.setItemChecked(false);
                    viewModel.addRecipeToMenu(recipe.getRecipeID());
                });
                requireActivity().onBackPressed();
            } else {
                Snackbar.make(requireContext(), requireView(), getString(R.string.no_recipes_selected), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void setRecipeListAdapter() {
        if (recipeList != null) {
            binding.recipeRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        } else {
            Snackbar.make(requireContext(), requireView(), getString(R.string.no_recipes_found), Snackbar.LENGTH_LONG).show();
        }
    }

    private void recipeClickListener(final Recipe recipe) {
        viewModel.setSelectedRecipe(recipe);

        Navigation.findNavController(requireView()).navigate(R.id.view_recipe_fragment, null, AnimationUtils.getFragmentTransitionAnimation());
    }

    private void checkClickListener(final Integer position) {
        final Recipe selectedRecipe = recyclerAdapter.getCurrentList().get(position);
        viewModel.getCurrentCookbook().stream().filter(recipe -> recipe.getRecipeID() == selectedRecipe.getRecipeID())
                .findFirst().ifPresent(recipe -> recipe.setItemChecked(!recipe.isItemChecked()));
    }


    @Override
    public void onResume() {
        super.onResume();
        if (binding.catSpinner.getSelectedItemPosition() != 0) {
            recipeList = viewModel.getRecipesByCategory((RecipeCategory) binding.catSpinner.getSelectedItem());
        } else {
            recipeList = viewModel.getCookbook();
        }
        if (viewModel.getCurrentCookbook() == null || viewModel.getCurrentCookbook().isEmpty()) {
            viewModel.setCurrentCookbook(viewModel.getCookbook());
        }
        recyclerAdapter = new RecipeRecyclerAdapter(this::checkClickListener, this::recipeClickListener);
        binding.recipeRecyclerView.setAdapter(recyclerAdapter);
        binding.recipeRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerAdapter.submitList(recipeList);
    }
}
