package com.matthewcannefax.menuplanner;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.matthewcannefax.menuplanner.databinding.FragmentSplashBinding;

import java.util.Optional;

public class SplashFragment extends Fragment {

    private static final int NAVIGATION_DELAY = 1000;

    private FragmentSplashBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        ((MenuApplication) requireActivity().getApplicationContext()).getMenuApplicationComponent().inject(this);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(requireContext()), R.layout.fragment_splash, null, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Optional.ofNullable(((MainActivity) requireActivity()).getSupportActionBar()).ifPresent(ActionBar::hide);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Navigation.findNavController(requireView()).navigate(R.id.menu_fragment);
        }, NAVIGATION_DELAY);
    }
}
