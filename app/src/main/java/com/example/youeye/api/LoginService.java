package com.example.youeye.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LoginService {
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);
    @POST("auth/register")
    Call<RegisterResponse> register(@Body RegisterRequest request);
    @POST("auth/checkid")
    Call<CheckIdResponse> checkid(@Body CheckIdRequest request);
}
