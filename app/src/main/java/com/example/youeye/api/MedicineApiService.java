package com.example.youeye.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MedicineApiService {
    @GET("searchMedicine")
    Call<MedicineResponse> searchMedicine(@Query("name") String name);
}