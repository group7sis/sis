package com.group7.sis.entity.academic;

import jakarta.persistence.Entity;

import java.util.List;

@Entity
public class Curriculum {
    public enum Status {
        ACTIVE,
        INACTIVE
    }

    private String code;
    private String effective_year;
    private List<Year> years;
    private Status status;

    public Curriculum(String code, String effective_year, List<Year> years, Status status) {
        this.code = code;
        this.effective_year = effective_year;
        this.years = years;
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEffective_year() {
        return effective_year;
    }

    public void setEffective_year(String effective_year) {
        this.effective_year = effective_year;
    }

    public List<Year> getYears() {
        return years;
    }

    public void setYears(List<Year> years) {
        this.years = years;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
    
}
