package com.group7.sis.entity.academic;

import com.group7.sis.entity.users.Faculty;
import java.util.List;
import jakarta.persistence.Entity;

public class ScheduledCourse {
    public enum status {
        ACTIVE, INACTIVE
    }

    private ProgramEntity program;
    private CourseEntity course;
    private Section section; // Or String section idk yet
    private String academic_year;
    private List<Day> days;
    private Faculty assigned_faculty;
    private int year_level; // this is already in section so i could remove this
    private String notes;
    private status status;
    
    public ScheduledCourse(ProgramEntity program, CourseEntity course, Section section, String academic_year, List<Day> days, Faculty assigned_faculty, int year_level, String notes, status status) {
        this.program = program;
        this.course = course;
        this.section = section;
        this.academic_year = academic_year;
        this.days = days;
        this.assigned_faculty = assigned_faculty;
        this.year_level = year_level;
        this.notes = notes;
        this.status = status;
    }

    public ProgramEntity getProgram() {
        return program;
    }

    public void setProgram(ProgramEntity program) {
        this.program = program;
    }

    public CourseEntity getCourse() {
        return course;
    }

    public void setCourse(CourseEntity course) {
        this.course = course;
    }

    public String getAcademic_year() {
        return academic_year;
    }

    public void setAcademic_year(String academic_year) {
        this.academic_year = academic_year;
    }

    public List<Day> getDays() {
        return days;
    }

    public void setDays(List<Day> days) {
        this.days = days;
    }

    public Faculty getAssigned_faculty() {
        return assigned_faculty;
    }

    public void setAssigned_faculty(Faculty assigned_faculty) {
        this.assigned_faculty = assigned_faculty;
    }

    public int getYear_level() {
        return year_level;
    }

    public void setYear_level(int year_level) {
        this.year_level = year_level;
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

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }
}
