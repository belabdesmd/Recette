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
import com.belfoapps.recette.models.pojo.Category;
import com.belfoapps.recette.utils.DataFetcher;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

public class CategoriesViewModel extends ViewModel {
    private static final String TAG = "CategoriesViewModel";

    /***********************************************************************************************
     * *********************************** Declarations
     */
    private MutableLiveData<List<Category>> categoriesData;
    private final AppDatabase mDb;
    private final SharedPreferencesHelper mSharedPrefs;
    private final DataFetcher fetcher;

    /***********************************************************************************************
     * *********************************** Constructor
     */
    @ViewModelInject
    public CategoriesViewModel(AppDatabase mDb, SharedPreferencesHelper mSharedPrefs, DataFetcher fetcher) {
        this.mDb = mDb;
        this.mSharedPrefs = mSharedPrefs;
        this.fetcher = fetcher;
    }

    /***********************************************************************************************
     * *********************************** Methods
     */
    //Getters
    public MutableLiveData<List<Category>> getCategoriesData() {
        if (categoriesData == null)
            categoriesData = new MutableLiveData<>();
        return categoriesData;
    }

    public void getCategories(Boolean fetched) {
        new GetCategories().execute(fetched);
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
            getCategories(fetched);
            //Save Timestamp LOG
            mSharedPrefs.setTimestamp(System.currentTimeMillis());
            fetcher.destroyFetched();
        });
        fetcher.fetchData();
    }

    //AsyncTasks
    @SuppressLint("StaticFieldLeak")
    private class GetCategories extends AsyncTask<Boolean, Void, List<Category>> {

        @Override
        protected List<Category> doInBackground(Boolean... booleans) {
            if (booleans[0])
                return mDb.contentDAO().getCategories();
            else return null;
        }

        @Override
        protected void onPostExecute(List<Category> categories) {
            super.onPostExecute(categories);
            categoriesData.postValue(categories);
        }
    }
}