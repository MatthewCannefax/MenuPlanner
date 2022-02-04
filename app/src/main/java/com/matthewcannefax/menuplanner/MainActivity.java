package com.matthewcannefax.menuplanner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;
import com.matthewcannefax.menuplanner.databinding.ActivityMainBinding;
import com.matthewcannefax.menuplanner.recipe.Ingredient;
import com.matthewcannefax.menuplanner.recipe.Recipe;
import com.matthewcannefax.menuplanner.utils.JSONHelper;
import com.matthewcannefax.menuplanner.utils.ShareHelper;
import com.matthewcannefax.menuplanner.utils.database.RecipeTable;
import com.matthewcannefax.menuplanner.utils.navigation.NavHelper;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {

    @Inject
    MainViewModelFactory viewModelFactory;

    private MainViewModel viewModel;
    private ActivityMainBinding binding;
    private NavHostFragment navHostFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ((MenuApplication) getApplicationContext()).getMenuApplicationComponent().inject(this);
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this, viewModelFactory).get(MainViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setLifecycleOwner(this);
        viewModel.initializeDataSource(this);
        //TODO ROOM stuff - this might need to be done a different way
        viewModel.initRecipeDatabase(this);
        viewModel.setCurrentMenu(viewModel.getMenu());

        preloadRecipes();

        loadNavigationGraph();
        initializeNavDrawer();
    }

    private void initializeNavDrawer() {
        final ActionBar actionBar = Objects.requireNonNull(getSupportActionBar());
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);

        binding.drawerLayout.buttonClose.setOnClickListener(v -> ((MotionLayout) binding.getRoot()).transitionToStart());

        binding.drawerLayout.buttonMenu.setOnClickListener(v -> {
            navHostFragment.getNavController().navigate(R.id.menu_fragment);
            ((MotionLayout) binding.getRoot()).transitionToStart();
        });

        binding.drawerLayout.buttonCookbook.setOnClickListener(v -> {
            if (RecipeTable.isNotEmpty(this)) {
                navHostFragment.getNavController().navigate(R.id.cookbook_fragment);
            } else {
                Snackbar.make(findViewById(R.id.content), R.string.no_recipes_found, Snackbar.LENGTH_LONG).show();
            }
            ((MotionLayout) binding.getRoot()).transitionToStart();
        });

        binding.drawerLayout.buttonAddRecipe.setOnClickListener(v -> {
            navHostFragment.getNavController().navigate(R.id.add_recipe_fragment);
            ((MotionLayout) binding.getRoot()).transitionToStart();
        });

        binding.drawerLayout.buttonViewGroceryList.setOnClickListener(v -> {
            final List<Ingredient> groceries = viewModel.getAllGroceries();
            if (!groceries.isEmpty()) {
                navHostFragment.getNavController().navigate(R.id.grocery_list_fragment);
            } else {
                Snackbar.make(findViewById(android.R.id.content), R.string.no_grocery_list_found, Snackbar.LENGTH_LONG).show();
            }
            ((MotionLayout) binding.getRoot()).transitionToStart();
        });

        binding.drawerLayout.buttonNewGroceryList.setOnClickListener(v -> {
            NavHelper.newGroceryList(this, this,
                    view -> navHostFragment.getNavController().navigate(R.id.grocery_list_fragment));
            ((MotionLayout) binding.getRoot()).transitionToStart();
        });

        binding.drawerLayout.buttonImportCookbook.setOnClickListener(v -> {
            ShareHelper.importCookbook(this);
            ((MotionLayout) binding.getRoot()).transitionToStart();
        });

        binding.drawerLayout.buttonShareCookbook.setOnClickListener(v -> {
            final List<Recipe> recipes = viewModel.getCookbook();
            if (!recipes.isEmpty()) {
                ShareHelper.sendAllRecipes(this);
            } else {
                Snackbar.make(findViewById(android.R.id.content), R.string.no_recipes_found, Snackbar.LENGTH_LONG).show();
            }
            ((MotionLayout) binding.getRoot()).transitionToStart();
        });
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Optional.ofNullable(navHostFragment.getChildFragmentManager().getFragments().get(0))
                .ifPresent(fragment -> {
                    if (fragment instanceof ImageCaptureFragment) {
                        ((ImageCaptureFragment) fragment).handleActivityResult(requestCode, resultCode, data);
                    }
                });
        ShareHelper.activityResultImportCookbook(this, this, requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                final MotionLayout motionLayout = (MotionLayout) binding.getRoot();
                if (motionLayout.getCurrentState() == R.id.end) {
                    motionLayout.transitionToStart();
                } else {
                    motionLayout.transitionToEnd();
                }
                break;
            }
            default: {
                //do nothing
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void preloadRecipes() {
        final SharedPreferences sharedPref = getSharedPreferences(getString(R.string.is_preloaded), 0);
        boolean isPreloaded = sharedPref.getBoolean(getString(R.string.is_preloaded), false);

        if (!isPreloaded) {
            new Thread(() -> {
                viewModel.addRecipes(JSONHelper.preloadCookbookFromJSON(getApplicationContext()));
                SharedPreferences.Editor edit = sharedPref.edit();
                edit.putBoolean(getString(R.string.is_preloaded), true);
                edit.apply();
            }).start();
        }
    }

    private void loadNavigationGraph() {
        navHostFragment = NavHostFragment.create(R.navigation.main_navigation_graph);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_layout, navHostFragment)
                .setPrimaryNavigationFragment(navHostFragment)
                .commit();
    }
}
