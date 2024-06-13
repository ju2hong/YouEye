package com.example.youeye.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MedicineApiService {
    @GET("getSafeStadDrugInq")
    Call<ResponseBody> searchMedicine(
            @Query("serviceKey") String apiKey,
            @Query("pageNo") int pageNo,
            @Query("numOfRows") int numOfRows,
            @Query("type") String type,
            @Query("PRDLST_NM") String productName
    );
}
