package com.matthewcannefax.menuplanner;

import android.content.Intent;

import androidx.fragment.app.Fragment;

public abstract class BaseFragment extends Fragment {

    public abstract void handleActivityResult(final int requestCode, final int resultCode, final Intent data);
}
