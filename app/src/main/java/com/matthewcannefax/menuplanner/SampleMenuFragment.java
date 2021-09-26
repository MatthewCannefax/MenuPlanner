package com.matthewcannefax.menuplanner;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import javax.inject.Inject;

public class SampleMenuFragment extends Fragment {

    @Inject
    MainViewModelFactory viewModelFactory;

    private MainViewModel viewModel;

    //TODO setup layout and databinding

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
//        ((MenuApplication) requireActivity().getApplicationContext()).getMenuApplicationComponent().inject(this);
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this, viewModelFactory).get(MainViewModel.class);
        //TODO setup layout and binding
    }
}
