package com.group7.sis.facultyModule.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.group7.sis.entity.users.FacultyEntity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name ="faculty_grade_configurations")
public class GradeConfigurationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "faculty_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private FacultyEntity faculty;

    @Column
    private Integer assignmentWeight;

    @Column
    private Integer quizWeight;

    @Column
    private Integer activityWeight;

    @Column
    private Integer finalExamWeight;
}
