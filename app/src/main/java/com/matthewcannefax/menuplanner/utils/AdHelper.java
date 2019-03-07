package com.matthewcannefax.menuplanner.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.matthewcannefax.menuplanner.R;

public class AdHelper {

    public static void SetupBannerAd(Context context, AdView mAdView){
        MobileAds.initialize(context, context.getString(R.string.admob_app_id));

        AdRequest adRequest = new AdRequest.Builder().build();

        mAdView.loadAd(adRequest);
    }

    public static void showGroceryInterstitial(final Context context){
        MobileAds.initialize(context, context.getString(R.string.admob_app_id));

        final InterstitialAd interstitialAd = new InterstitialAd(context);
        interstitialAd.setAdUnitId(context.getString(R.string.admob_grocery_interstitial));
        interstitialAd.loadAd(new AdRequest.Builder().build());

        interstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                Toast.makeText(context, i, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                interstitialAd.show();
            }
        });
    }

    public static void showInterstitial(final Context context){
        MobileAds.initialize(context, context.getString(R.string.admob_app_id));

        final InterstitialAd interstitialAd = new InterstitialAd(context);
        interstitialAd.setAdUnitId(context.getString(R.string.admob_interstitial_id));
        interstitialAd.loadAd(new AdRequest.Builder().build());

        interstitialAd.setAdListener(new AdListener(){
            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                Toast.makeText(context, i, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                interstitialAd.show();
            }
        });


    }
}
