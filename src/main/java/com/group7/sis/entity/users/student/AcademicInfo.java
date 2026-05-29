package com.group7.sis.entity.users.student;

import com.group7.sis.entity.academic.Section;
import com.group7.sis.entity.academic.ProgramEntity;
import jakarta.persistence.Entity;
import java.util.List;
import java.time.LocalDate;

public class AcademicInfo {
    public enum ScholasticStatus {
        REGULAR,
        IRREGULAR
    }
    public enum StudentStatus {
        ACTIVE,
        INACTIVE
    }
    
    private ProgramEntity enrolled_program;
    private List<EnrolledCourse> enrolled_courses;
    private int year_level; // this is already in section so i could remove this
    private LocalDate enrollment_date;
    private Section section;
    private ScholasticStatus scholastic_status;
    private StudentStatus student_status;

    public AcademicInfo(ProgramEntity enrolled_program, List<EnrolledCourse> enrolled_courses, int year_level, LocalDate enrollment_date, Section section, ScholasticStatus scholastic_status, StudentStatus student_status) {
        this.enrolled_program = enrolled_program;
        this.enrolled_courses = enrolled_courses;
        this.year_level = year_level;
        this.enrollment_date = enrollment_date;
        this.section = section;
        this.scholastic_status = scholastic_status;
        this.student_status = student_status;
    }

    public ProgramEntity getEnrolled_program() {
        return enrolled_program;
    }

    public void setEnrolled_program(ProgramEntity enrolled_program) {
        this.enrolled_program = enrolled_program;
    }

    public List<EnrolledCourse> getEnrolled_courses() {
        return enrolled_courses;
    }

    //not sure about this. maybe have a method that adds an enrolled course to the list instead of setting the whole list
    public void setEnrolled_courses(List<EnrolledCourse> enrolled_courses) {
        this.enrolled_courses = enrolled_courses;
    }

    public int getYear_level() {
        return year_level;
    }

    public void setYear_level(int year_level) {
        this.year_level = year_level;
    }

    public LocalDate getEnrollment_date() {
        return enrollment_date;
    }

    public void setEnrollment_date(LocalDate enrollment_date) {
        this.enrollment_date = enrollment_date;
    }

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public ScholasticStatus getScholastic_status() {
        return scholastic_status;
    }

    public void setScholastic_status(ScholasticStatus scholastic_status) {
        this.scholastic_status = scholastic_status;
    }

    public StudentStatus getStudent_status() {
        return student_status;
    }

    public void setStudent_status(StudentStatus student_status) {
        this.student_status = student_status;
    }

}
