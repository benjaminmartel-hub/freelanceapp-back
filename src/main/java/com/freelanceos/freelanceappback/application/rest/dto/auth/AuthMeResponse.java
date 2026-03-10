package com.freelanceos.freelanceappback.application.rest.dto.auth;

public class AuthMeResponse {
    private String username;
    private String provider;

    public AuthMeResponse() {
    }

    public AuthMeResponse(String username, String provider) {
        this.username = username;
        this.provider = provider;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }
}
