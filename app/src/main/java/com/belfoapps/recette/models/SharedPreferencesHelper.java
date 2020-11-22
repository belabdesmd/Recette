package com.belfoapps.recette.models;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class SharedPreferencesHelper {
    private static final String LAST_UPDATE = "Last Update";
    private static final String TIMESTAMP = "Timestamp";
    private static final String PERSONALIZED_ADS = "AD";
    private static final String INT_ADS_COUNT = "Interstitial Ad Count";
    private static final String SAVED_RECIPES = "Saved Recipes";

    /************************************* Declarations *******************************************/
    private final SharedPreferences sharedPref;
    private final Gson gson;
    private final SharedPreferences.Editor editor;

    /************************************* Constructor ********************************************/
    @SuppressLint("CommitPrefEdits")
    public SharedPreferencesHelper(SharedPreferences sharedPref) {
        this.sharedPref = sharedPref;
        this.gson = new Gson();

        editor = sharedPref.edit();
    }

    /************************************* Methods ***********************************************/
    //Save Recipes
    public void saveRecipeIds(ArrayList<Long> ids) {
        editor.putString(SAVED_RECIPES, gson.toJson(ids)).apply();
    }

    public ArrayList<Long> getRecipeIds() {
        Type listType = new TypeToken<ArrayList<Long>>() {
        }.getType();
        return gson.fromJson(sharedPref.getString(SAVED_RECIPES, "[]"), listType);
    }

    //Shopping Last Time Updated
    public void setLastTimeUpdated(String date) {
        editor.putString(LAST_UPDATE, date).apply();
    }

    public String getLastTimeUpdated() {
        return sharedPref.getString(LAST_UPDATE, "XX/XX/XXXX");
    }

    //Save Log
    public void setTimestamp(Long timestamp) {
        editor.putLong(TIMESTAMP, timestamp).apply();
    }

    public Long getTimestamp() {
        return sharedPref.getLong(TIMESTAMP, System.currentTimeMillis());
    }

    //Ads
    public void setAdPersonalized(boolean isPersonalized) {
        editor.putBoolean(PERSONALIZED_ADS, isPersonalized).apply();
    }

    public boolean isAdPersonalized() {
        return sharedPref.getBoolean(PERSONALIZED_ADS, false);
    }

    //Interstitial Ads count
    public void setInterstitialAdCount(int count) {
        editor.putInt(INT_ADS_COUNT, count).apply();
    }

    public int getInterstitialAdCount() {
        return sharedPref.getInt(INT_ADS_COUNT, 0);
    }
}
