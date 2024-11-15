package com.example.youeye.api;

public class RegisterRequest {
    private String userID;
    private String pw;

    public RegisterRequest(String userID, String pw) {
        this.userID = userID;
        this.pw = pw;
    }

    // Getter and Setter methods
    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }
}