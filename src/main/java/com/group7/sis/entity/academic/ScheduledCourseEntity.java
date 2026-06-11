package com.group7.sis.entity.academic;

import com.group7.sis.entity.users.FacultyEntity;
import java.util.List;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "scheduled_courses")
public class ScheduledCourseEntity {
    public enum ScheduleStatus {
        ACTIVE, INACTIVE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private CourseEntity course;

    @ManyToOne
    @JoinColumn(name = "section_id")
    private SectionEntity section;
    
    @Column
    private String academicYear;
    
    @OneToMany(mappedBy = "scheduledCourse", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DayEntity> days;

    @ManyToOne
    @JoinColumn(name = "faculty_id")
    private FacultyEntity assignedFaculty;

    @Column
    private String room;

    @Column
    private String notes;

    @Enumerated(EnumType.STRING)
    private ScheduleStatus status;
    
}
