package com.matthewcannefax.menuplanner;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import javax.inject.Inject;

public class MainViewModelFactory implements ViewModelProvider.Factory {

    @Inject
    MainViewModelFactory() {
        super();
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MainViewModel();
    }
}
