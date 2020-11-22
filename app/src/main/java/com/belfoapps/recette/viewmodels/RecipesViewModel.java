package com.belfoapps.recette.viewmodels;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.belfoapps.recette.base.AppDatabase;
import com.belfoapps.recette.models.pojo.Recipe;

import java.util.List;

public class RecipesViewModel extends ViewModel {
    private static final String TAG = "RecipesViewModel";

    /***********************************************************************************************
     * *********************************** Declarations
     */
    private MutableLiveData<List<Recipe>> recipesData;
    private final AppDatabase mDb;

    /***********************************************************************************************
     * *********************************** Constructor
     */
    @ViewModelInject
    public RecipesViewModel(AppDatabase mDb) {
        this.mDb = mDb;
    }

    /***********************************************************************************************
     * *********************************** Methods
     */
    public void getRecipes(long categoryId) {
        new GetRecipes().execute(categoryId);
    }

    //Getters
    public MutableLiveData<List<Recipe>> getRecipesData() {
        if (recipesData == null)
            recipesData = new MutableLiveData<>();
        return recipesData;
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
            recipesData.postValue(recipes);
        }
    }
}