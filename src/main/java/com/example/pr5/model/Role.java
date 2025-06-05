package com.example.pr5.model;

import jakarta.persistence.*;

@Entity
public class Role {

    @Id
    private String name; // ROLE_USER, ROLE_ADMIN

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
