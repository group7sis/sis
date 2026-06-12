package com.group7.sis.dto;

import com.group7.sis.entity.academic.TermEntity.TermStatus;
import com.group7.sis.validation.ValidateYear;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TermDTO {

    @NotBlank(message = "Academic year is required")
    @Pattern(
        regexp = "^\\d{4}-\\d{4}$",
        message = "Format must be YYYY-YYYY"
    )
    @ValidateYear
    private String academicYear;

    @NotNull(message = "Please specify semester of current academic year")
    @Positive(message = "Semester must be positive")
    private int semester;

    @NotNull(message = "Term status must be defined")
    private TermStatus status;
}
