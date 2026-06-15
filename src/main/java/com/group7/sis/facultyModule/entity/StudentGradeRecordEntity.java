package com.group7.sis.facultyModule.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.group7.sis.entity.users.student.EnrolledCourseEntity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="student_grade_records")
public class StudentGradeRecordEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "enrolled_course_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private EnrolledCourseEntity enrolledCourse;

    @Column
    @Min(0)
    @Max(100)
    private Integer assignments;
    
    @Column
    @Min(0)
    @Max(100)
    private Integer quizzes;

    @Column
    @Min(0)
    @Max(100)
    private Integer activities;

    @Column
    @Min(0)
    @Max(100)
    private Integer finalExam;

    @Column
    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private Double calculatedGrade;

    @Column
    @DecimalMax("5.00")
    @DecimalMin("1.00")
    private Double finalGrade;

    private boolean finalized;
}
