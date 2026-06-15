package com.group7.sis.facultyModule.dtos;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentGradeRecordDTO {
    private Long id;
    private String studentName;
    private String courseName;

    @Min(0)
    @Max(100)
    private Integer assignments;

    @Min(0)
    @Max(100)
    private Integer quizzes;

    @Min(0)
    @Max(100)
    private Integer activities;

    @Min(0)
    @Max(100)
    private Integer finalExam;

    private Double calculatedGrade;
    private Double finalGrade;
    private boolean finalized;
}
