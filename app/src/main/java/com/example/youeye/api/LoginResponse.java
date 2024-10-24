package com.example.youeye.api;

public class LoginResponse {
    private String token; // 서버의 응답에 따라 필드명 변경

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
