package com.group7.sis.entity.academic;

import jakarta.persistence.Entity;

@Entity
public class Term {
    private String acadamic_year;
    private int semester;

    public Term(String academic_year, int semester){
        this.acadamic_year = academic_year;
        this.semester = semester;
    }

    public String getAcadamic_year() {
        return acadamic_year;
    }

    public void setAcadamic_year(String acadamic_year) {
        this.acadamic_year = acadamic_year;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    
}
