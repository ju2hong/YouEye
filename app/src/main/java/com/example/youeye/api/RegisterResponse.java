package com.example.youeye.api;

public class RegisterResponse {
    private String message;

    public RegisterResponse(String message) {
        this.message = message;
    }

    // Getter and Setter methods
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}