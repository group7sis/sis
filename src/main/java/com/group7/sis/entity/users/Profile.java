package com.group7.sis.entity.users;

import jakarta.persistence.Entity;

@Entity
public class Profile {
    private String first_name;
    private String middle_name;
    private String last_name;
    private String suffix;
    private String gender;
    private String date_of_birth;
    private String civil_status;
    private String nationality;
    private String mobile_number;
}
