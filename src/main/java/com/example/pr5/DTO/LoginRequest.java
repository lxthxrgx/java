package com.example.pr5.DTO;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;

      public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}