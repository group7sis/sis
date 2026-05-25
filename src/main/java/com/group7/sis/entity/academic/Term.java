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
}
