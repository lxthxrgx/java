package com.example.pr5.DTO;

import java.util.List;

public class JwtResponse {
    private String accessToken;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    private List<String> roles;

    public JwtResponse() {}

    public JwtResponse(String accessToken, String type, Long id, String username, String email, List<String> roles) {
        this.accessToken = accessToken;
        this.type = type;
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getType() {
        return type;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

}
