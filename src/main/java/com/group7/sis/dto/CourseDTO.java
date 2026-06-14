package com.group7.sis.dto;

import java.util.List;
import com.group7.sis.entity.academic.CourseEntity.Status;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

@Getter
@Setter
public class CourseDTO {

    private Long id;
    
    private String code;

    @NotBlank(message = "Course title is required")
    private String title;

    @NotNull(message = "Department label is required")
    private Long departmentId;

    @NotNull(message = "Units are required")
    @Positive(message = "Units must be positive numbers")
    private int units;

    @NotNull(message = "Lecture hours are required")
    @Positive (message = "Lecture hours must be positive")
    private int lectureHours;

    @NotNull(message = "Lab hours cannot be null. Put 0 if none")
    @PositiveOrZero(message = "Lab hours must be positive or zero")
    private int labHours;

    private String notes;

    @NotNull (message = "Course status must be defined")
    private Status status;
    
    private List<Long> prerequisiteIds;

    @Valid
    @NotEmpty(message = "Course must be assigned to at least one program curriculum")
    private List<CourseAssignmentDTO> courseAssignments;

    @Getter
    @Setter
    public static class CourseAssignmentDTO{
        private Long id;
        private Long programId;

        @NotNull(message = "Course must be assigned to a program curriculum")
        private Long curriculumId;

        @NotNull(message = "Year assignment is required")
        @Positive(message = "Year level must be positive")
        private int yearLevel;

        @NotNull(message = "Semester assignment is required")
        @Positive(message = "Semester must be positive")
        private int semester;
    }
}