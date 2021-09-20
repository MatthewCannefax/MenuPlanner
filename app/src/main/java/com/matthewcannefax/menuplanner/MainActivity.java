package com.matthewcannefax.menuplanner;

import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.matthewcannefax.menuplanner.databinding.ActivityMainBinding;

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
        binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.activity_main, null, false);
        binding.setLifecycleOwner(this);

        loadNavigationGraph();
    }

    private void loadNavigationGraph() {
        //TODO add navigation graph
//        navHostFragment = NavHostFragment.create();
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.content, navHostFragment)
//                .setPrimaryNavigationFragment(navHostFragment)
//                .commit();
    }
}
