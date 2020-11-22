package com.belfoapps.recette.viewmodels;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.belfoapps.recette.base.AppDatabase;
import com.belfoapps.recette.models.SharedPreferencesHelper;
import com.belfoapps.recette.models.pojo.Recipe;

import java.util.ArrayList;
import java.util.List;

public class SavedViewModel extends ViewModel {
    private static final String TAG = "SavedViewModel";

    /***********************************************************************************************
     * *********************************** Declarations
     */
    private MutableLiveData<List<Recipe>> recipesData;
    private final AppDatabase mDb;
    private final SharedPreferencesHelper mSharedPrefs;

    /***********************************************************************************************
     * *********************************** Constructor
     */
    @ViewModelInject
    public SavedViewModel(AppDatabase mDb, SharedPreferencesHelper mSharedPrefs) {
        this.mDb = mDb;
        this.mSharedPrefs = mSharedPrefs;
    }

    /***********************************************************************************************
     * *********************************** Methods
     */
    public void getRecipes() {
        new GetRecipes().execute();
    }

    //Getters
    public MutableLiveData<List<Recipe>> getRecipesData() {
        if (recipesData == null)
            recipesData = new MutableLiveData<>();
        return recipesData;
    }

    //AsyncTasks
    @SuppressLint("StaticFieldLeak")
    private class GetRecipes extends AsyncTask<Void, Void, List<Recipe>> {

        @Override
        protected List<Recipe> doInBackground(Void... voids) {
            List<Recipe> recipes = new ArrayList<>();
            for (Long id :
                    mSharedPrefs.getRecipeIds()) {
                recipes.add(mDb.contentDAO().getRecipe(id));
            }
            return recipes;
        }

        @Override
        protected void onPostExecute(List<Recipe> recipes) {
            super.onPostExecute(recipes);
            recipesData.postValue(recipes);
        }
    }
}