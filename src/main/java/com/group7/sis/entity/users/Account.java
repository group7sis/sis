package com.group7.sis.entity.users;

import jakarta.persistence.Entity;

@Entity
public class Account {
    private String username;
    private String email;
    private String password;
    private enum role {
        STUDENT, FACULTY
    }
    private enum status {
        ACTIVE, INACTIVE
    }
}
