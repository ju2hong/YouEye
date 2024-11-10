package com.example.youeye.api;

public class CheckIdResponse {
    private boolean exists;
    private String message;

    // Getter와 Setter
    public boolean isExists() {
        return exists;
    }

    public void setExists(boolean exists) {
        this.exists = exists;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}