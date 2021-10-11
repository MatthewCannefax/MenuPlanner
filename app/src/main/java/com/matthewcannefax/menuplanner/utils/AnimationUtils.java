package com.matthewcannefax.menuplanner.utils;

import androidx.navigation.NavOptions;

public class AnimationUtils {
    public static NavOptions getFragmentTransitionAnimation() {
        return new NavOptions.Builder()
                .setEnterAnim(android.R.animator.fade_in)
                .setExitAnim(android.R.animator.fade_out)
                .build();
    }
}
