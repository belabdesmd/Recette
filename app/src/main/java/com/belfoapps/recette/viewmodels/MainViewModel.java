package com.belfoapps.recette.viewmodels;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.belfoapps.recette.models.SharedPreferencesHelper;
import com.belfoapps.recette.utils.DataFetcher;

public class MainViewModel extends ViewModel {
    private static final String TAG = "MainViewModel";

    /***********************************************************************************************
     * *********************************** Declarations
     */
    private final SharedPreferencesHelper mSharedPrefs;
    private final DataFetcher fetcher;
    private MutableLiveData<Boolean> dataLoaded;

    /***********************************************************************************************
     * *********************************** Constructor
     */
    @ViewModelInject
    public MainViewModel(SharedPreferencesHelper mSharedPrefs, DataFetcher fetcher) {
        this.mSharedPrefs = mSharedPrefs;
        this.fetcher = fetcher;
    }

    /***********************************************************************************************
     * *********************************** Methods
     */
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