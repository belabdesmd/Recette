package com.belfoapps.recette.models;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

public interface GoogleSheetsEndpoints {

    String SPREADSHEET = "1tffYPV_K0CwBlz-ypRQF92MO-jojAK6pM-tiK8JdGW0";
    String API_KEY = "AIzaSyAEQ24M2WOBdKIDYgG5e2HoXM6A8a40DTI";

    @GET(SPREADSHEET + "/values/Recipes!A2:J10000?key=" + API_KEY)
    Call<ResponseBody> getRecipes();

    @GET(SPREADSHEET + "/values/Categories!A2:C1000?key="+ API_KEY)
    Call<ResponseBody> getCategories();

}
