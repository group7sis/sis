package com.group7.sis.entity.academic;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "course_assignments")
public class CourseAssignmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private CourseEntity course;

    @ManyToOne
    @JoinColumn(name = "curriculum_id")
    private CurriculumEntity curriculum;
    
    @Column
    private int yearLevel;
    
    @Column
    private int semester;
}
