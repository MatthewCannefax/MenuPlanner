package com.matthewcannefax.menuplanner.recipe.menuList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.matthewcannefax.menuplanner.R;
import com.matthewcannefax.menuplanner.addEdit.EditRecipeActivity;
import com.matthewcannefax.menuplanner.databinding.FragmentMenuListBinding;
import com.matthewcannefax.menuplanner.recipe.Recipe;
import com.matthewcannefax.menuplanner.recipe.RecipeCategory;
import com.matthewcannefax.menuplanner.recipe.recipeList.RecipeListActivity;
import com.matthewcannefax.menuplanner.utils.FilterHelper;
import com.matthewcannefax.menuplanner.utils.PermissionsHelper;

import java.util.ArrayList;
import java.util.List;

public class MenuFragment extends Fragment {

    private FragmentMenuListBinding binding;
    private MenuListViewModel viewModel;
    private MenuListRecyclerAdapter adapter;
    private boolean isMenuLoading = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MenuListViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentMenuListBinding.bind(view);
        viewModel.setDataSource(requireContext());
        viewModel.loadMenu();

        adapter = new MenuListRecyclerAdapter(this::clickRecipe, this::longClickRecipe);
        binding.menuRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.menuRecyclerView.setAdapter(adapter);

        binding.catSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    viewModel.filterRecipes((RecipeCategory) binding.catSpinner.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        PermissionsHelper.checkPermissions(requireActivity(), requireContext());
        PermissionsHelper.setMenuFirstInstance(false);
        viewModel.getMenuList().observe(requireActivity(), recipes -> {
            new Handler(Looper.getMainLooper()).post(() -> {
                adapter.submitList(recipes);
                ((MenuListActivity) requireActivity()).isMenuLoading(false);
            });
        });
    }

    private void addRecipeToMenu() {
        if (!viewModel.isCookbookEmpty()) {
            Intent intent = new Intent(requireActivity(), RecipeListActivity.class);
            intent.putExtra("TITLE", requireContext().getString(R.string.add_to_menu));
            requireActivity().startActivity(intent);
        } else {
            Snackbar.make(requireActivity().findViewById(android.R.id.content), R.string.no_recipes_in_cookbook, Snackbar.LENGTH_LONG).show();
        }
    }

    private void clickRecipe(Recipe clickedRecipe) {
        Intent intent = new Intent(requireContext(), EditRecipeActivity.class);
        intent.putExtra(EditRecipeActivity.RECIPE_ID, clickedRecipe);
        startActivity(intent);
    }

    private boolean longClickRecipe(Recipe clickedRecipe) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext())
                .setTitle(R.string.remove_from_menu)
                .setMessage(String.format(getString(R.string.are_you_sure_remove_format), clickedRecipe.toString()))
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                    Snackbar.make(((View) binding.menuRecyclerView.getParent()), String.format(getString(R.string.format_recipe_removed), clickedRecipe.toString()), Snackbar.LENGTH_LONG).show();
                    viewModel.removeMenuItem(clickedRecipe.getRecipeID());
                });
        builder.show();
        return false;
    }
}
