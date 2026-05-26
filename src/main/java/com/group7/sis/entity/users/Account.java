package com.group7.sis.entity.users;

import jakarta.persistence.Entity;

@Entity
public class Account {

    private enum Role {
        STUDENT, FACULTY
    }
    private enum Status {
        ACTIVE, INACTIVE
    }


    private String username;
    private String email;
    private String password;
    private Role role;
    private Status status;

    public Account(String username, String password, String email, Role role, Status status) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}