package com.group7.sis.entity.users.student;

import com.group7.sis.entity.academic.ScheduledCourse;
import jakarta.persistence.Entity;

@Entity
public class EnrolledCourse {
    private ScheduledCourse sched_course;
    private double grade;
    private enum status {
        PASSED, FAILED, DROPPED
    }
}
