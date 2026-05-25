package com.group7.sis.entity.users;
import com.group7.sis.entity.academic.Course;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import java.util.List;

@Entity
public class Faculty {
    private Account account;
    private Profile profile;
    private String department;
    private String employment_type;
    private String position;
    private LocalDate date_hired;
    private List<Course> qualified_courses;
    private enum status {
        ACTIVE,
        INACTIVE
    }
}
