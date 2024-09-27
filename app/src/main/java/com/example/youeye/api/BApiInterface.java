package com.example.youeye.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface BApiInterface {

    @GET("{keyId}/{serviceId}/{dataType}/{startIdx}/{endIdx}")
    Call<BMedicineResponse> getAllMedicineData(
            @Path("keyId") String keyId,
            @Path("serviceId") String serviceId,
            @Path("dataType") String dataType,
            @Path("startIdx") String startIdx,
            @Path("endIdx") String endIdx
    );
}
