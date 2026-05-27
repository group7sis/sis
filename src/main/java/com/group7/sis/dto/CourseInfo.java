package com.group7.sis.dto;

import com.group7.sis.entity.academic.Course;

public class CourseInfo {
    Course  course;
    private String program;
    private String curriculum_code;
    private int year_level; 
    private int semester;

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }
    
    public String getCurriculum_code() {
        return curriculum_code;
    }
    
    public void setCurriculum_code(String curriculum_code) {
        this.curriculum_code = curriculum_code;
    }

    public Course getCourse() {
        return course;
    }
    public void setCourse(Course course) {
        this.course = course;
    }
    public int getYear_level() {
        return year_level;
    }
    public void setYear_level(int year_level) {
        this.year_level = year_level;
    }
    public int getSemester() {
        return semester;
    }
    public void setSemester(int semester) {
        this.semester = semester;
    }
}
