package com.group7.sis.entity.academic;

import java.time.LocalTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name ="days")
public class DayEntity {

    public enum DayName{
        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sched_course_id")
    private ScheduledCourseEntity scheduledCourse;

    @Column
    private DayName name;

    @Column
    private LocalTime startTime;

    @Column
    private LocalTime endTime;
}
