package com.example.youeye.api;

public class LoginRequest {
    private String userID; // 서버의 필드명에 따라 변경
    private String pw;

    public LoginRequest(String userID, String pw) {
        this.userID = userID;
        this.pw = pw;
    }

    // Getters and Setters
    public String getUserID() { return userID; }
    public void setUserID(String userID) { this.userID = userID; }

    public String getPw() { return pw; }
    public void setPw(String pw) { this.pw = pw; }
}
