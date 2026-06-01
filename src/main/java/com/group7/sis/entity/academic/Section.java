package com.group7.sis.entity.academic;

import com.group7.sis.entity.users.StudentEntity;
import jakarta.persistence.Entity;                    
import java.util.List;                                

public class Section {
    public enum status {
        OPEN, CLOSED, WAITLIST
    }

    private String code;
    private String name;
    private ProgramEntity program;
    private int year_level;
    private String academic_year;
    private int semester;
    private int max_capacity;
    private String notes;
    private status status;
    private List<StudentEntity> students;
    private List<ScheduledCourseEntity> sched_courses;

    public Section(String code, String name, ProgramEntity program, int year_level, String academic_year, int semester, int max_capacity, String notes, status status, List<StudentEntity> students, List<ScheduledCourseEntity> sched_courses) {
        this.code = code;
        this.name = name;
        this.program = program;
        this.year_level = year_level;
        this.academic_year = academic_year;
        this.semester = semester;
        this.max_capacity = max_capacity;
        this.notes = notes;
        this.status = status;
        this.students = students;
        this.sched_courses = sched_courses;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProgramEntity getProgram() {
        return program;
    }

    public void setProgram(ProgramEntity program) {
        this.program = program;
    }

    public int getYear_level() {
        return year_level;
    }

    public void setYear_level(int year_level) {
        this.year_level = year_level;
    }

    public String getAcademic_year() {
        return academic_year;
    }

    public void setAcademic_year(String academic_year) {
        this.academic_year = academic_year;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public int getMax_capacity() {
        return max_capacity;
    }

    public void setMax_capacity(int max_capacity) {
        this.max_capacity = max_capacity;
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

    public List<StudentEntity> getStudents() {
        return students;
    }

    public void setStudents(List<StudentEntity> students) {
        this.students = students;
    }

    public List<ScheduledCourseEntity> getSched_courses() {
        return sched_courses;
    }

    public void setSched_courses(List<ScheduledCourseEntity> sched_courses) {
        this.sched_courses = sched_courses;
    }
}
