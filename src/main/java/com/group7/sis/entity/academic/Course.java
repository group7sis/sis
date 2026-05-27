package com.group7.sis.entity.academic;

import jakarta.persistence.Entity;
import java.util.List;

@Entity
public class Course {

    public enum status {
        ACTIVE, INACTIVE
    }

    private String code;
    private String title;
    private List<Course> prequisites;
    private int units;
    private String lecture_hours;
    private String lab_hours;
    private String notes;
    private status status;

    public Course(String code, String title, List<Course> prequisites, int units, String lecture_hours, String lab_hours, String notes, status status) {
        this.code = code;
        this.title = title;
        this.prequisites = prequisites;
        this.units = units;
        this.lecture_hours = lecture_hours;
        this.lab_hours = lab_hours;
        this.notes = notes;
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Course> getPrequisites() {
        return prequisites;
    }

    public void setPrequisites(List<Course> prequisites) {
        this.prequisites = prequisites;
    }

    public int getUnits() {
        return units;
    }

    public void setUnits(int units) {
        this.units = units;
    }

    public String getLecture_hours() {
        return lecture_hours;
    }

    public void setLecture_hours(String lecture_hours) {
        this.lecture_hours = lecture_hours;
    }

    public String getLab_hours() {
        return lab_hours;
    }

    public void setLab_hours(String lab_hours) {
        this.lab_hours = lab_hours;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public status getStatus() {
        return status;
    }

    public void setStatus(status status) {
        this.status = status;
    }

    
}
