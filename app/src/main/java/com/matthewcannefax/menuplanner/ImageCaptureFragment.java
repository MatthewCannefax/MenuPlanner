package com.matthewcannefax.menuplanner;

import android.content.Intent;

public interface ImageCaptureFragment {
    void handleActivityResult(final int requestCode, final int resultCode, final Intent data);
}
