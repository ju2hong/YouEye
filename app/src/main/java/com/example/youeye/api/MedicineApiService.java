package com.example.youeye.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MedicineApiService {
    @GET("SafeStadDrugService/getSafeStadDrugInq")
    Call<MedicineResponse> searchMedicine(
            @Query("serviceKey") String apiKey,
            @Query("pageNo") int pageNo,
            @Query("numOfRows") int numOfRows,
            @Query("type") String type,
            @Query("PRDLST_NM") String productName
    );
}
