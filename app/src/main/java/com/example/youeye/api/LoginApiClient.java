// File: com/example/youeye/api/LoginApiClient.java
package com.example.youeye.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class LoginApiClient {
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            // 로깅 인터셉터 설정 (디버깅 용도)
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // OkHttpClient 설정
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .build();

            // Retrofit 인스턴스 생성
            retrofit = new Retrofit.Builder()
                    .baseUrl("http://dyserver.asuscomm.com:13000/api/") // 실제 로그인 API의 base URL로 변경
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }
}
