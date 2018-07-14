package com.matthewcannefax.menuplanner.utils;

import android.content.Context;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.matthewcannefax.menuplanner.R;

public class AdHelper {

    public static void SetupBannerAd(Context context, AdView mAdView){
        MobileAds.initialize(context, context.getString(R.string.admob_app_id));

        AdRequest adRequest = new AdRequest.Builder().build();

        mAdView.loadAd(adRequest);
    }

}
