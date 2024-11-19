// File: com/example/youeye/api/RegisterRequest.java
package com.example.youeye.api;

public class RegisterRequest {
    private String userID;
    private String pw;
    private String android_id;

    public RegisterRequest(String userID, String pw, String android_id) {
        this.userID = userID;
        this.pw = pw;
        this.android_id = android_id;
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

    public String getAndroid_id() {
        return android_id;
    }

    public void setAndroid_id(String android_id) {
        this.android_id = android_id;
    }
}
