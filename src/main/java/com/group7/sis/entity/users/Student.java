package com.group7.sis.entity.users;

import com.group7.sis.entity.users.student.AcademicInfo;
import jakarta.persistence.Entity;
import java.util.List;

@Entity
public class Student {
    private String student_number;
    private Account account;
    private Profile profile;
    private List<AcademicInfo> acad_info;
    private String year_admitted;

    public Student(String student_number, Account account, Profile profile, List<AcademicInfo> acad_info, String year_admitted) {
        this.student_number = student_number;
        this.account = account;
        this.profile = profile;
        this.acad_info = acad_info;
        this.year_admitted = year_admitted;
    }

    public String getStudent_number() {
        return student_number;
    }

    public void setStudent_number(String student_number) {
        this.student_number = student_number;
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

    public List<AcademicInfo> getAcad_info() {
        return acad_info;
    }

    //not sure about this. maybe have a method that adds an academic info to the list instead of setting the whole list
    public void setAcad_info(List<AcademicInfo> acad_info) {
        this.acad_info = acad_info;
    }

    public void addAcad_info(AcademicInfo academicInfo) {
        this.acad_info.add(academicInfo);
    }

    public String getYear_admitted() {
        return year_admitted;
    }

    public void setYear_admitted(String year_admitted) {
        this.year_admitted = year_admitted;
    }

    
}
