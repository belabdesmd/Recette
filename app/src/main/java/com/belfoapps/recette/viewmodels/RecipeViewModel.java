package com.belfoapps.recette.viewmodels;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.belfoapps.recette.base.AppDatabase;
import com.belfoapps.recette.models.SharedPreferencesHelper;
import com.belfoapps.recette.models.pojo.Recipe;
import com.belfoapps.recette.models.pojo.Shopping;
import com.belfoapps.recette.utils.GDPR;
import com.google.android.gms.ads.AdView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RecipeViewModel extends ViewModel {
    private static final String TAG = "RecipeViewModel";

    /***********************************************************************************************
     * *********************************** Declarations
     */
    private MutableLiveData<Recipe> recipeData;
    private MutableLiveData<Boolean> bookmarkedData;
    private final AppDatabase mDb;
    private final SharedPreferencesHelper mSharedPrefs;
    private final GDPR gdpr;
    private Recipe mRecipe;

    /***********************************************************************************************
     * *********************************** Constructor
     */
    @ViewModelInject
    public RecipeViewModel(AppDatabase mDb, SharedPreferencesHelper mSharedPrefs, GDPR gdpr) {
        this.mDb = mDb;
        this.mSharedPrefs = mSharedPrefs;
        this.gdpr = gdpr;
    }

    /***********************************************************************************************
     * *********************************** Methods
     */
    public void loadAd(AdView ad) {
        if (mSharedPrefs.isAdPersonalized()) {
            gdpr.showPersonalizedAdBanner(ad);
        } else {
            gdpr.showNonPersonalizedAdBanner(ad);
        }
    }

    public void loadRecipe(long recipeId) {
        new GetRecipe().execute(recipeId);
    }

    public void addShopping(Shopping shopping) {
        new UpdateShoppings("Add").execute(shopping);
    }

    public void removeShopping(Shopping shopping) {
        new UpdateShoppings("Delete").execute(shopping);
    }

    public boolean isSaved() {
        return mSharedPrefs.getRecipeIds().contains(mRecipe.getRecipeId());
    }

    public void unSaveRecipe() {
        ArrayList<Long> ids = mSharedPrefs.getRecipeIds();
        ids.remove(mRecipe.getRecipeId());
        mSharedPrefs.saveRecipeIds(ids);
        bookmarkedData.postValue(false);
    }

    public void saveRecipe() {
        ArrayList<Long> ids = mSharedPrefs.getRecipeIds();
        if (!ids.contains(mRecipe.getRecipeId())) {
            ids.add(mRecipe.getRecipeId());
            mSharedPrefs.saveRecipeIds(ids);
        }
        bookmarkedData.postValue(true);
    }

    //Getters
    public MutableLiveData<Recipe> getRecipeData() {
        if (recipeData == null)
            recipeData = new MutableLiveData<>();
        return recipeData;
    }

    public MutableLiveData<Boolean> getBookmarkedData() {
        Log.d(TAG, "getBookmarkedData");
        if (bookmarkedData == null)
            bookmarkedData = new MutableLiveData<>();
        return bookmarkedData;
    }

    public Recipe getRecipe() {
        return mRecipe;
    }

    //AsyncTasks
    @SuppressLint("StaticFieldLeak")
    public class GetRecipe extends AsyncTask<Long, Void, Recipe> {

        @Override
        protected Recipe doInBackground(Long... longs) {
            return mDb.contentDAO().getRecipe(longs[0]);
        }

        @Override
        protected void onPostExecute(Recipe recipe) {
            super.onPostExecute(recipe);
            mRecipe = recipe;
            recipeData.postValue(recipe);
        }
    }


    @SuppressLint("StaticFieldLeak")
    public class UpdateShoppings extends AsyncTask<Shopping, Void, Void> {

        private final String mode;

        public UpdateShoppings(String mode) {
            this.mode = mode;
        }

        @Override
        protected Void doInBackground(Shopping... shoppings) {
            switch (mode) {
                case "Add":
                    mDb.contentDAO().insertShopping(shoppings[0]);
                    break;
                case "Delete":
                    mDb.contentDAO().removeShopping(shoppings[0]);
                    break;
                case "Clear":
                    break;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            mSharedPrefs.setLastTimeUpdated(sdf.format(new Date()));
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mRecipe = null;
    }
}