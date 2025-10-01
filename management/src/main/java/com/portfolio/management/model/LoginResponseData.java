package com.portfolio.management.model;

public class LoginResponseData {
    private String token;
    private String userId;
    private String phoneNumber;

    public LoginResponseData(String token, String userId, String phoneNumber) {
        this.token = token;
        this.userId = userId;
        this.phoneNumber = phoneNumber;
    }

    public String getToken() {
        return token;
    }

    public String getUserId() {
        return userId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}