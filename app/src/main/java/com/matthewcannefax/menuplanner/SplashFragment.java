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
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
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

        ((MotionLayout) binding.getRoot()).addTransitionListener(new MotionLayout.TransitionListener() {
            @Override
            public void onTransitionStarted(MotionLayout motionLayout, int startId, int endId) {

            }

            @Override
            public void onTransitionChange(MotionLayout motionLayout, int startId, int endId, float progress) {

            }

            @Override
            public void onTransitionCompleted(MotionLayout motionLayout, int currentId) {
                Navigation.findNavController(requireView()).navigate(R.id.menu_fragment, null, new NavOptions.Builder()
                        .setEnterAnim(android.R.animator.fade_in)
                        .setPopUpTo(R.id.splash_fragment, true)
                        .setLaunchSingleTop(true)
                        .build());
            }

            @Override
            public void onTransitionTrigger(MotionLayout motionLayout, int triggerId, boolean positive, float progress) {

            }
        });

        new Handler(Looper.getMainLooper()).postDelayed(() -> ((MotionLayout) binding.getRoot()).transitionToEnd(), NAVIGATION_DELAY);
    }
}
