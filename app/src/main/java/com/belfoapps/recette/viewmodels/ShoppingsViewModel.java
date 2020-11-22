package com.belfoapps.recette.viewmodels;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.belfoapps.recette.base.AppDatabase;
import com.belfoapps.recette.models.SharedPreferencesHelper;
import com.belfoapps.recette.models.pojo.Shopping;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ShoppingsViewModel extends ViewModel {
    private static final String TAG = "ShoppingsViewModel";

    /***********************************************************************************************
     * *********************************** Declarations
     */
    private MutableLiveData<List<Shopping>> shoppingData;
    private MutableLiveData<Boolean> removedData;
    private final AppDatabase mDb;
    private final SharedPreferencesHelper mSharedPrefs;

    /***********************************************************************************************
     * *********************************** Constructor
     */
    @ViewModelInject
    public ShoppingsViewModel(AppDatabase mDb, SharedPreferencesHelper mSharedPrefs) {
        this.mDb = mDb;
        this.mSharedPrefs = mSharedPrefs;
    }

    /***********************************************************************************************
     * *********************************** Methods
     */
    public void getShoppings() {
        new GetShoppings("Get").execute();
    }

    public void clearShoppingList() {
        new GetShoppings("Clear").execute();
    }

    public void removeShopping(Shopping shopping) {
        new UpdateShoppings("Delete").execute(shopping);
    }

    public void addShopping(Shopping shopping) {
        new UpdateShoppings("Add").execute(shopping);
    }

    //Getters
    public MutableLiveData<List<Shopping>> getShoppingData() {
        if (shoppingData == null)
            shoppingData = new MutableLiveData<>();
        return shoppingData;
    }

    public MutableLiveData<Boolean> getRemovedData(){
        if (removedData == null)
            removedData = new MutableLiveData<>();
        return removedData;
    }

    public String getLastTimeUpdated() {
        return mSharedPrefs.getLastTimeUpdated();
    }

    //AsyncTasks
    @SuppressLint("StaticFieldLeak")
    public class GetShoppings extends AsyncTask<Void, Void, List<Shopping>> {

        private String mode;

        public GetShoppings(String mode) {
            this.mode = mode;
        }

        @Override
        protected List<Shopping> doInBackground(Void... voids) {
            return mDb.contentDAO().getShoppings();
        }

        @Override
        protected void onPostExecute(List<Shopping> shoppings) {
            super.onPostExecute(shoppings);
            if (mode.equals("Get"))
                shoppingData.postValue(shoppings);
            else if (mode.equals("Clear"))
                new ClearShoppings().execute(shoppings);
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class UpdateShoppings extends AsyncTask<Shopping, Void, Void> {

        private String mode;

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

    @SuppressLint("StaticFieldLeak")
    public class ClearShoppings extends AsyncTask<List<Shopping>, Void, Void> {

        @Override
        protected Void doInBackground(List<Shopping>... shoppings) {
            for (Shopping shopping : shoppings[0]) {
                mDb.contentDAO().removeShopping(shopping);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            removedData.postValue(true);
        }
    }
}