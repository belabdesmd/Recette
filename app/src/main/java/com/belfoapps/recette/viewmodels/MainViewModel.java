package com.belfoapps.recette.viewmodels;

import android.content.Context;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.belfoapps.recette.R;
import com.belfoapps.recette.models.SharedPreferencesHelper;
import com.belfoapps.recette.utils.DataFetcher;
import com.belfoapps.recette.utils.GDPR;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.InterstitialAd;

public class MainViewModel extends ViewModel {
    private static final String TAG = "MainViewModel";

    /***********************************************************************************************
     * *********************************** Declarations
     */
    private final SharedPreferencesHelper mSharedPrefs;
    private final DataFetcher fetcher;
    private final GDPR gdpr;
    private MutableLiveData<Boolean> dataLoaded;
    private InterstitialAd mInterstitialAd;

    /***********************************************************************************************
     * *********************************** Constructor
     */
    @ViewModelInject
    public MainViewModel(SharedPreferencesHelper mSharedPrefs, DataFetcher fetcher, GDPR gdpr) {
        this.mSharedPrefs = mSharedPrefs;
        this.fetcher = fetcher;
        this.gdpr = gdpr;
    }

    /***********************************************************************************************
     * *********************************** Methods
     */
    public void checkGDPRConsent(boolean isEnabled) {
        if (isEnabled) {
            gdpr.checkForConsent();
        }
    }

    public void initInterstitialAd(Context context) {
        mInterstitialAd = new InterstitialAd(context);
        mInterstitialAd.setAdUnitId(context.getResources().getString(R.string.INTERSTITIAL_AD_ID));

        if (context.getResources().getBoolean(R.bool.INTERSTITIAL_AD_Enabled)) {
            if (mSharedPrefs.isAdPersonalized())
                gdpr.loadPersonalizedInterstitialAd(mInterstitialAd);
            else gdpr.loadNonPersonalizedInterstitialAd(mInterstitialAd);

            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    // Code to be executed when an ad finishes loading.
                }

                @Override
                public void onAdFailedToLoad(int errorCode) {
                    // Code to be executed when an ad request fails.
                }

                @Override
                public void onAdOpened() {
                    // Code to be executed when the ad is displayed.
                }

                @Override
                public void onAdClicked() {
                    // Code to be executed when the user clicks on an ad.
                }

                @Override
                public void onAdLeftApplication() {
                    // Code to be executed when the user has left the app.
                }

                @Override
                public void onAdClosed() {
                }
            });
        }
    }

    public boolean ableToShowInterstitial(Context context) {
        if (mSharedPrefs.getInterstitialAdCount()
                < context.getResources().getInteger(R.integer.INTERSTITIAL_AD_COUNT)) {
            mSharedPrefs.setInterstitialAdCount(mSharedPrefs.getInterstitialAdCount() + 1);
            return false;
        } else {
            mSharedPrefs.setInterstitialAdCount(0);
            return true;
        }
    }

    public void showInterstitialAd() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

    public void loadData(LifecycleOwner owner) {
        fetcher.getFetched().observe(owner, fetched -> {
            dataLoaded.setValue(fetched);
            //Save Timestamp LOG
            mSharedPrefs.setTimestamp(System.currentTimeMillis());
            fetcher.destroyFetched();
        });
        fetcher.fetchData();
    }

    public MutableLiveData<Boolean> getDataLoaded() {
        if (dataLoaded == null)
            dataLoaded = new MutableLiveData<>();
        return dataLoaded;
    }
}