package com.matthewcannefax.menuplanner;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SplashFragment extends Fragment {

    private static final int NAVIGATION_DELAY = 5000;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO navigate to menu fragment
//        new Handler(Looper.getMainLooper()).postDelayed(() -> , NAVIGATION_DELAY);
    }
}
