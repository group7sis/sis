package com.group7.sis.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

public class Admin {

    @Id
    @GeneratedValue
    private int id;

    private String username;
    private String password;

    public Admin (String username, String password){
        this.username = username;
        this.password = password;
    }

    public int getId() {
        return id;
    }
    
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
