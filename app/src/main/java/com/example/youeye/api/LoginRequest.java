package com.example.youeye.api;

public class LoginRequest {
    private String userID;
    private String pw;

    public LoginRequest(String userID, String pw) {
        this.userID = userID;
        this.pw = pw;
    }

    // Getters
    public String getUserID() { return userID; }
    public String getPw() { return pw; }

    // Setters (필요 시)
    public void setUserID(String userID) { this.userID = userID; }
    public void setPw(String pw) { this.pw = pw; }
}
