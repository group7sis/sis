package com.group7.sis.entity.users.student;

import com.group7.sis.entity.academic.ScheduledCourseEntity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class EnrolledCourseEntity {
    public enum Status {
        PASSED, FAILED, INCOMPLETE, ONGOING, DROPPED
    }

    @ManyToOne
    @JoinColumn(name = "acad_info_id")
    private AcademicInfoEntity academicInfo;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sched_course_id")
    private ScheduledCourseEntity schedule;

    @Column
    @DecimalMax("5.00")
    @DecimalMin("1.00")
    private Double grade;

    @Enumerated(EnumType.STRING)
    private Status status;
}
