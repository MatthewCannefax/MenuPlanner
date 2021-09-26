package com.matthewcannefax.menuplanner;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;
import com.matthewcannefax.menuplanner.databinding.ActivityMainBinding;
import com.matthewcannefax.menuplanner.recipe.Ingredient;
import com.matthewcannefax.menuplanner.recipe.Recipe;
import com.matthewcannefax.menuplanner.utils.ActivityNavEnum;
import com.matthewcannefax.menuplanner.utils.JSONHelper;
import com.matthewcannefax.menuplanner.utils.ShareHelper;
import com.matthewcannefax.menuplanner.utils.database.DataSource;
import com.matthewcannefax.menuplanner.utils.database.RecipeTable;
import com.matthewcannefax.menuplanner.utils.navigation.NavDrawer;
import com.matthewcannefax.menuplanner.utils.navigation.NavHelper;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {

    @Inject
    MainViewModelFactory viewModelFactory;

    private MainViewModel viewModel;
    private ActivityMainBinding binding;
    private NavHostFragment navHostFragment;
    private DataSource dataSource;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ((MenuApplication) getApplicationContext()).getMenuApplicationComponent().inject(this);
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this, viewModelFactory).get(MainViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setLifecycleOwner(this);
        dataSource = new DataSource(this);
        dataSource.open();

        NavDrawer.setupNavDrawerMenuButton(getSupportActionBar());
        NavDrawer.setupNavDrawer(this, this, binding.navList);

        preloadRecipes();

        loadNavigationGraph();
        initializeNavDrawer();
    }

    @Override
    protected void onDestroy() {
        dataSource.close();
        super.onDestroy();
    }

    private void initializeNavDrawer() {
        final ActionBar actionBar = Objects.requireNonNull(getSupportActionBar());
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);

        binding.navList.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ActivityNavEnum.values()));
        binding.navList.setOnItemClickListener((adapterView, view, i, l) -> {
            switch (ActivityNavEnum.getActivityEnum(i)) {
                case MENU_LIST_ACTIVITY: {
                    navHostFragment.getNavController().navigate(R.id.menu_fragment);
                    break;
                }
                case RECIPE_LIST_ACTIVITY: {
                    if (RecipeTable.isNotEmpty(this)) {
                        navHostFragment.getNavController().navigate(R.id.cookbook_fragment);
                    } else {
                        Snackbar.make(findViewById(R.id.content), R.string.no_recipes_found, Snackbar.LENGTH_LONG).show();
                    }
                    break;
                }
                case VIEW_GROCERY_LIST: {
                    final List<Ingredient> groceries = dataSource.getAllGroceries();
                    if (!groceries.isEmpty()) {
                        navHostFragment.getNavController().navigate(R.id.grocery_list_fragment);
                    } else {
                        Snackbar.make(findViewById(android.R.id.content), R.string.no_grocery_list_found, Snackbar.LENGTH_LONG).show();
                    }
                    break;
                }
                case NEW_GROCERY_LIST: {
                    NavHelper.newGroceryList(this, this,
                            v -> navHostFragment.getNavController().navigate(R.id.grocery_list_fragment));
                    break;
                }
                case IMPORT_COOKBOOK: {
                    ShareHelper.importCookbook(this);
                    break;
                }
                case SHARE_COOKBOOK: {
                    final List<Recipe> recipes = dataSource.getAllRecipes();
                    if (!recipes.isEmpty()) {
                        ShareHelper.sendAllRecipes(this);
                    } else {
                        Snackbar.make(findViewById(android.R.id.content), R.string.no_recipes_found, Snackbar.LENGTH_LONG).show();
                    }
                    break;
                }
                default: {
                    //do nothing, should never happen
                }
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    binding.drawerLayout.openDrawer(GravityCompat.START);
                }
            }
            default: {
                //do nothing
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void preloadRecipes() {
        final SharedPreferences sharedPref = getSharedPreferences(getString(R.string.is_preloaded), 0);
        boolean isPreloaded = sharedPref.getBoolean(getString(R.string.is_preloaded), false);

        if (!isPreloaded) {
            new Thread(() -> {
                DataSource mDataSource = new DataSource(getApplicationContext());
                mDataSource.open();

                mDataSource.importRecipesToDB(JSONHelper.preloadCookbookFromJSON(getApplicationContext()));

                mDataSource.close();

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
