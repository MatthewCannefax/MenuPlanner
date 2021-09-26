package com.matthewcannefax.menuplanner;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.matthewcannefax.menuplanner.databinding.ActivityMainBinding;
import com.matthewcannefax.menuplanner.utils.JSONHelper;
import com.matthewcannefax.menuplanner.utils.database.DataSource;

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

        preloadRecipes();

        loadNavigationGraph();
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
