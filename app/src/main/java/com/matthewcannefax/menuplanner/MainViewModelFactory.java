package com.matthewcannefax.menuplanner;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.matthewcannefax.menuplanner.utils.database.DataSource;

import javax.inject.Inject;

public class MainViewModelFactory implements ViewModelProvider.Factory {

    private final DataSource dataSource;

    @Inject
    MainViewModelFactory(final DataSource dataSource) {
        super();
        this.dataSource = dataSource;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MainViewModel(dataSource);
    }
}
