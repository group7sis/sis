package com.group7.sis.entity.users.student;

import com.group7.sis.entity.academic.ScheduledCourseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class EnrolledCourseEntity {
    public enum Status {
        PASSED, FAILED, DROPPED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sched_course_id")
    private ScheduledCourseEntity schedule;

    @Column
    private double grade;

    @Enumerated(EnumType.STRING)
    private Status status;
}
