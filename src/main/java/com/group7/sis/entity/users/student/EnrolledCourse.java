package com.group7.sis.entity.users.student;

import com.group7.sis.entity.academic.ScheduledCourseEntity;
import jakarta.persistence.Entity;

public class EnrolledCourse {
    public enum Status {
        PASSED, FAILED, DROPPED
    }

    private ScheduledCourseEntity sched_course;
    private double grade;
    private Status status;

    public EnrolledCourse(ScheduledCourseEntity sched_course, double grade, Status status) {
        this.sched_course = sched_course;
        this.grade = grade;
        this.status = status;
    }

    public ScheduledCourseEntity getSched_course() {
        return sched_course;
    }

    //must have dis
    public void setSched_course(ScheduledCourseEntity sched_course) {
        this.sched_course = sched_course;
    }

    public double getGrade() {
        return grade;
    }

    public void setGrade(double grade) {
        this.grade = grade;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    
}
