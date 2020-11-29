package com.belfoapps.recette.utils;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.belfoapps.recette.base.AppDatabase;
import com.belfoapps.recette.models.GoogleSheetsEndpoints;
import com.belfoapps.recette.models.pojo.Category;
import com.belfoapps.recette.models.pojo.Recipe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DataFetcher {
    private static final String TAG = "DataFetcher";

    private MutableLiveData<Boolean> fetched;
    private final GoogleSheetsEndpoints endpoints;
    private final AppDatabase mDb;

    @Inject
    public DataFetcher(GoogleSheetsEndpoints endpoints, AppDatabase mDb) {
        this.endpoints = endpoints;
        this.mDb = mDb;
    }

    public MutableLiveData<Boolean> getFetched() {
        if (fetched == null)
            fetched = new MutableLiveData<>();
        return fetched;
    }

    public void destroyFetched() {
        fetched = null;
    }

    public void fetchData() {
        //Fetch For Categories
        endpoints.getCategories().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                try {
                    //Transfer Response from json to a list of categories
                    JSONObject json = new JSONObject(response.body().string());
                    JSONArray rows = json.getJSONArray("values");
                    ArrayList<Category> categories = new ArrayList<>();
                    for (int i = 0; i < rows.length(); i++) {
                        JSONArray single_row = rows.getJSONArray(i);
                        categories.add(new Category(Long.parseLong(single_row.get(0).toString()), single_row.get(1).toString(),
                                single_row.get(2).toString()));
                    }

                    //Save Categories to a local database
                    new InsertCategories().execute(categories);

                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                    fetched.postValue(false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                fetched.postValue(false);
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    public class InsertRecipes extends AsyncTask<ArrayList<Recipe>, Void, Void> {

        @SafeVarargs
        @Override
        protected final Void doInBackground(ArrayList<Recipe>... arrayLists) {
            mDb.contentDAO().insertRecipes(arrayLists[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            fetched.postValue(true);
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class InsertCategories extends AsyncTask<ArrayList<Category>, Void, Void> {

        @SafeVarargs
        @Override
        protected final Void doInBackground(ArrayList<Category>... arrayLists) {
            mDb.contentDAO().insertCategory(arrayLists[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //Fetch For Recipes
            endpoints.getRecipes().enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call,
                                       @NonNull Response<ResponseBody> response) {
                    try {
                        JSONObject json = new JSONObject(response.body().string());
                        JSONArray rows = json.getJSONArray("values");

                        int curr = -1;
                        ArrayList<Recipe> recipes = new ArrayList<>();
                        for (int i = 0; i < rows.length(); i++) {
                            JSONArray single_row = rows.getJSONArray(i);
                            recipes.add(new Recipe(Long.parseLong(single_row.get(0).toString()),
                                    single_row.get(1).toString(),
                                    single_row.get(2).toString(),
                                    Long.parseLong(single_row.get(3).toString()),
                                    single_row.get(4).toString(),
                                    Integer.parseInt(single_row.get(5).toString()),
                                    Integer.parseInt(single_row.get(6).toString()),
                                    new ArrayList<>(Arrays.asList(single_row.get(7).toString().split("\\n"))),
                                    new ArrayList<>(Arrays.asList(single_row.get(8).toString().split("\\n")))));
                        }

                        //Save Recipes to a local database
                        new InsertRecipes().execute(recipes);

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        fetched.postValue(false);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    fetched.postValue(false);
                }
            });
        }
    }
}
