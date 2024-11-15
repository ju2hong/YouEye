package com.example.youeye.api;

public class CheckIdRequest {
    private String userID;

    public CheckIdRequest(String userID) {
        this.userID = userID;
    }

    // Getter와 Setter
    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}