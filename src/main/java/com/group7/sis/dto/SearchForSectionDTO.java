package com.group7.sis.dto;

import com.group7.sis.entity.users.student.AcademicInfoEntity.EnrollmentStatus;
import com.group7.sis.validation.ValidateYear;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchForSectionDTO {
    @NotNull(message = "Enrollment status must be defined")
    private EnrollmentStatus enrollmentStatus;

    @NotBlank(message = "Academic year is required")
    @Pattern(
        regexp = "^\\d{4}-\\d{4}$",
        message = "Format must be YYYY-YYYY"
    )
    @ValidateYear
    private String academicYear;

    @NotNull(message = "Semester assignment is required")
    @Positive(message = "Semester must be positive")
    private int semester;
}
