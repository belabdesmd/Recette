package com.belfoapps.recette.viewmodels;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.belfoapps.recette.R;
import com.belfoapps.recette.base.AppDatabase;
import com.belfoapps.recette.models.SharedPreferencesHelper;
import com.belfoapps.recette.models.pojo.Recipe;
import com.belfoapps.recette.utils.DataFetcher;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HomeViewModel extends ViewModel {
    private static final String TAG = "HomeViewModel";

    /***********************************************************************************************
     * *********************************** Declarations
     */
    private MutableLiveData<List<Recipe>> recipesData;
    private final AppDatabase mDb;
    private final SharedPreferencesHelper mSharedPrefs;
    private final DataFetcher fetcher;

    /***********************************************************************************************
     * *********************************** Constructor
     */
    @ViewModelInject
    public HomeViewModel(AppDatabase mDb, SharedPreferencesHelper mSharedPrefs, DataFetcher fetcher) {
        this.mDb = mDb;
        this.mSharedPrefs = mSharedPrefs;
        this.fetcher = fetcher;
    }

    /***********************************************************************************************
     * *********************************** Methods
     */
    public void getRecipes(Boolean fetched) {
        new GetRecipes().execute(fetched);
    }

    public boolean ableToFetchData(Context context) {
        Date last_fetch = new Date(mSharedPrefs.getTimestamp());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(last_fetch);
        calendar.add(Calendar.HOUR_OF_DAY, context.getResources().getInteger(R.integer.AVG_ADDING_TIME));
        return new Timestamp(System.currentTimeMillis()).after(new Timestamp(calendar.getTimeInMillis()));
    }

    public void refetchData(LifecycleOwner owner) {
        fetcher.getFetched().observe(owner, fetched -> {
            getRecipes(fetched);
            //Save Timestamp LOG
            mSharedPrefs.setTimestamp(System.currentTimeMillis());
            fetcher.destroyFetched();
        });
        fetcher.fetchData();
    }

    //Getters
    public MutableLiveData<List<Recipe>> getRecipesData() {
        if (recipesData == null)
            recipesData = new MutableLiveData<>();
        return recipesData;
    }

    //AsyncTasks
    @SuppressLint("StaticFieldLeak")
    private class GetRecipes extends AsyncTask<Boolean, Void, List<Recipe>> {

        @Override
        protected List<Recipe> doInBackground(Boolean... booleans) {
            if (booleans[0])
                return mDb.contentDAO().getLimitedRecipes();
            else return null;
        }

        @Override
        protected void onPostExecute(List<Recipe> recipes) {
            super.onPostExecute(recipes);
            recipesData.postValue(recipes);
        }
    }
}