package com.group7.sis.dto;

import lombok.Getter;
import lombok.Setter;
import com.group7.sis.entity.academic.SectionEntity.Status;

import jakarta.validation.constraints.*;

@Getter
@Setter
public class SectionDTO {

    @NotEmpty(message = "Section code is required")
    private String code;

    @NotEmpty(message = "Section name is required")
    private String name;

    @NotNull(message = "Section must be assigned to a program")
    private Long programId;

    @NotNull(message = "Please specify year level")
    @Positive(message = "Year level must be positive")
    private int yearLevel;

    @NotBlank(message = "Academic year is required")
    @Pattern(
        regexp = "^\\d{4}-\\d{4}$",
        message = "Format must be YYYY-YYYY"
    )
    private String academicYear;

    @NotNull(message = "Please specify semester assignment")
    @Positive(message = "Semester must be positive")
    private int semester;

    @NotNull(message = "Max capacity is required")
    private int maxCapacity;

    private String notes;

    @NotNull(message = "Section status must be defined")
    private Status status;
}
