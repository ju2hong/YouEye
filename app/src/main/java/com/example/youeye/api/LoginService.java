// File: com/example/youeye/api/LoginService.java
package com.example.youeye.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LoginService {
    @POST("auth/login") // 실제 엔드포인트 경로로 변경
    Call<LoginResponse> login(@Body LoginRequest request);
}
