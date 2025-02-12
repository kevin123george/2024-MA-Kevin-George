package com.cattleDB.FrontEndService.dtos;

public class LoginResponse {
    private String token;

    private long expiresIn;

    // Setter method that returns the current instance for method chaining
    public LoginResponse setToken(String token) {
        this.token = token;
        return this;  // Return the current instance for chaining
    }

    // Setter method that returns the current instance for method chaining
    public LoginResponse setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
        return this;  // Return the current instance for chaining
    }
}
