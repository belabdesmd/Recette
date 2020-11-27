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
import com.belfoapps.recette.utils.GDPR;
import com.google.android.gms.ads.AdView;

import java.util.List;

public class RecipesViewModel extends ViewModel {
    private static final String TAG = "RecipesViewModel";

    /***********************************************************************************************
     * *********************************** Declarations
     */
    private MutableLiveData<List<Recipe>> recipesData;
    private final AppDatabase mDb;
    private final GDPR gdpr;
    private final SharedPreferencesHelper mSharedPrefs;
    private List<Recipe> mRecipes;

    /***********************************************************************************************
     * *********************************** Constructor
     */
    @ViewModelInject
    public RecipesViewModel(AppDatabase mDb, SharedPreferencesHelper mSharedPrefs, GDPR gdpr) {
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

    public void loadRecipes(long categoryId) {
        new GetRecipes().execute(categoryId);
    }

    //Getters
    public MutableLiveData<List<Recipe>> getRecipesData() {
        if (recipesData == null)
            recipesData = new MutableLiveData<>();
        return recipesData;
    }

    public List<Recipe> getRecipes(){
        return mRecipes;
    }

    //AsyncTasks
    @SuppressLint("StaticFieldLeak")
    private class GetRecipes extends AsyncTask<Long, Void, List<Recipe>>{

        @Override
        protected List<Recipe> doInBackground(Long... ids) {
            if (ids[0] == -1)
                return mDb.contentDAO().getRecipes();
            else return mDb.contentDAO().getRecipesByCategory(ids[0]);
        }

        @Override
        protected void onPostExecute(List<Recipe> recipes) {
            super.onPostExecute(recipes);
            mRecipes = recipes;
            recipesData.postValue(recipes);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mRecipes = null;
    }
}