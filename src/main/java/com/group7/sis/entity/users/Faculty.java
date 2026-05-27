package com.group7.sis.entity.users;
import com.group7.sis.entity.academic.Course;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import java.util.List;

@Entity
public class Faculty {

    public enum status {
        ACTIVE,
        INACTIVE
    }
    private String faculty_number;
    private Account account;
    private Profile profile;
    private String department;
    private String employment_type;
    private String position;
    private LocalDate date_hired;
    private List<Course> qualified_courses;
    private status status;

    public Faculty(String faculty_number, Account account, Profile profile, String department, String employment_type, String position, LocalDate date_hired, List<Course> qualified_courses, status status) {
        this.faculty_number = faculty_number;
        this.account = account;
        this.profile = profile;
        this.department = department;
        this.employment_type = employment_type;
        this.position = position;
        this.date_hired = date_hired;
        this.qualified_courses = qualified_courses;
        this.status = status;
    }

    public String getFaculty_number() {
        return faculty_number;
    }

    public void setFaculty_number(String faculty_number) {
        this.faculty_number = faculty_number;
    }
    
    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getEmployment_type() {
        return employment_type;
    }

    public void setEmployment_type(String employment_type) {
        this.employment_type = employment_type;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public LocalDate getDate_hired() {
        return date_hired;
    }

    public void setDate_hired(LocalDate date_hired) {
        this.date_hired = date_hired;
    }

    public List<Course> getQualified_courses() {
        return qualified_courses;
    }

    public void setQualified_courses(List<Course> qualified_courses) {
        this.qualified_courses = qualified_courses;
    }

    public status getStatus() {
        return status;
    }

    public void setStatus(status status) {
        this.status = status;
    }

    
}
