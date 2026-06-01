package com.group7.sis.entity.academic;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name ="days")
public class DayEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sched_course_id")
    private ScheduledCourseEntity scheduledCourse;

    @Column
    private String name;

    @Column
    private String startTime;

    @Column
    private String endTime;

    @Column
    private String breakTime;
}
