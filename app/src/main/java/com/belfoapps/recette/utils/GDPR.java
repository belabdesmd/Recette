package com.belfoapps.recette.utils;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.belfoapps.recette.R;
import com.belfoapps.recette.models.SharedPreferencesHelper;
import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.net.MalformedURLException;
import java.net.URL;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ActivityContext;

public class GDPR {
    private static final String TAG = "GDPR";

    private ConsentForm form;
    private final Context mContext;
    private final SharedPreferencesHelper sharedPreferencesHelper;

    @Inject
    public GDPR(SharedPreferencesHelper sharedPreferencesHelper, @ActivityContext Context mContext) {
        this.mContext = mContext;
        this.sharedPreferencesHelper = sharedPreferencesHelper;
    }

    /**
     * GDPR CODE
     */
    public void checkForConsent() {
        Log.d(TAG, "checkForConsent: Checking For Consent");

        ConsentInformation consentInformation = ConsentInformation.getInstance(mContext);
        String[] publisherIds = {mContext.getResources().getString(R.string.PUBLISHER_ID)};

        consentInformation.requestConsentInfoUpdate(publisherIds, new ConsentInfoUpdateListener() {
            @Override
            public void onConsentInfoUpdated(ConsentStatus consentStatus) {
                Log.d(TAG, "onConsentInfoUpdated: Successful");
                // User's consent status successfully updated.
                switch (consentStatus) {
                    case PERSONALIZED:
                        Log.d(TAG, "Showing Personalized ads");
                        sharedPreferencesHelper.setAdPersonalized(true);
                        break;
                    case NON_PERSONALIZED:
                        Log.d(TAG, "Showing Non-Personalized ads");
                        sharedPreferencesHelper.setAdPersonalized(false);
                        break;
                    case UNKNOWN:
                        Log.d(TAG, "Requesting Consent");
                        if (ConsentInformation.getInstance(mContext)
                                .isRequestLocationInEeaOrUnknown()) {
                            requestConsent();
                        } else {
                            sharedPreferencesHelper.setAdPersonalized(true);
                        }
                        break;
                    default:
                        Log.d(TAG, "onConsentInfoUpdated: Nothing");
                        break;
                }
            }

            @Override
            public void onFailedToUpdateConsentInfo(String errorDescription) {
                Log.d(TAG, "onFailedToUpdateConsentInfo: Failed to update");
                // User's consent status failed to update.
            }
        });
    }

    private void requestConsent() {
        Log.d(TAG, "requestConsent: Requesting Consent");
        URL privacyUrl = null;
        try {
            privacyUrl = new URL(mContext.getResources().getString(R.string.PRIVACY_URL));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            // Handle error_image.
        }
        form = new ConsentForm.Builder(mContext, privacyUrl)
                .withListener(new ConsentFormListener() {
                    @Override
                    public void onConsentFormLoaded() {
                        // Consent form loaded successfully.
                        Log.d(TAG, "Requesting Consent: onConsentFormLoaded");
                        showForm();
                    }

                    @Override
                    public void onConsentFormOpened() {
                        // Consent form was displayed.
                        Log.d(TAG, "Requesting Consent: onConsentFormOpened");
                    }

                    @Override
                    public void onConsentFormClosed(
                            ConsentStatus consentStatus, Boolean userPrefersAdFree) {
                        Log.d(TAG, "Requesting Consent: onConsentFormClosed");
                        if (userPrefersAdFree) {
                            // Buy or Subscribe
                            Log.d(TAG, "Requesting Consent: User prefers AdFree");
                        } else {
                            Log.d(TAG, "Requesting Consent: Requesting consent again");
                            switch (consentStatus) {
                                case PERSONALIZED:
                                    sharedPreferencesHelper.setAdPersonalized(true);
                                    break;
                                case NON_PERSONALIZED:
                                case UNKNOWN:
                                    sharedPreferencesHelper.setAdPersonalized(false);
                                    break;
                            }
                        }
                        // Consent form was closed.
                    }

                    @Override
                    public void onConsentFormError(String errorDescription) {
                        Log.d(TAG, "Requesting Consent: onConsentFormError. Error - " + errorDescription);
                        // Consent form error_image.
                    }
                })
                .withPersonalizedAdsOption()
                .withNonPersonalizedAdsOption()
                .build();
        form.load();
    }

    public void showPersonalizedAdBanner(AdView ad) {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        ad.loadAd(adRequest);
    }

    public void showNonPersonalizedAdBanner(AdView ad) {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addNetworkExtrasBundle(AdMobAdapter.class, getNonPersonalizedAdsBundle())
                .build();
        ad.loadAd(adRequest);
    }

    public void loadPersonalizedInterstitialAd(InterstitialAd mInterstitialAd) {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mInterstitialAd.loadAd(adRequest);
    }

    public void loadNonPersonalizedInterstitialAd(InterstitialAd mInterstitialAd) {
        AdRequest request = new AdRequest.Builder()
                .addNetworkExtrasBundle(AdMobAdapter.class, getNonPersonalizedAdsBundle())
                .build();
        mInterstitialAd.loadAd(request);
    }

    private Bundle getNonPersonalizedAdsBundle() {
        Bundle extras = new Bundle();
        extras.putString("npa", "1");
        return extras;
    }

    private void showForm() {
        if (form == null) {
            Log.d(TAG, "Consent form is null");
        }
        if (form != null) {
            Log.d(TAG, "Showing consent form");
            form.show();
        } else {
            Log.d(TAG, "Not Showing consent form");
        }
    }

}
