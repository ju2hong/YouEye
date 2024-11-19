// File: com/example/youeye/api/LoginRequest.java
package com.example.youeye.api;

public class LoginRequest {
    private String userID;
    private String pw;
    private String android_id;

    public LoginRequest(String userID, String pw, String android_id) {
        this.userID = userID;
        this.pw = pw;
        this.android_id = android_id;
    }

    // Getters and Setters
    public String getUserID() { return userID; }
    public void setUserID(String userID) { this.userID = userID; }

    public String getPw() { return pw; }
    public void setPw(String pw) { this.pw = pw; }

    public String getAndroid_id() { return android_id; }
    public void setAndroid_id(String android_id) { this.android_id = android_id; }
}
