package com.group7.sis.dto;

import lombok.Getter;
import lombok.Setter;
import com.group7.sis.entity.academic.ProgramEntity.ProgramStatus;
import com.group7.sis.validation.ValidateYear;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import com.group7.sis.entity.academic.CurriculumEntity.CurriculumStatus;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ProgramDTO {

    @NotBlank(message = "Program title is required")
    private String title;

    @NotBlank(message = "Program code is required")
    private String code;

    @NotNull(message = "Program status must be defined")
    private ProgramStatus status;

    private Long activeCurriculumId;

    @Valid
    @NotEmpty(message = "Program must contain at least one curriculum")
    private List<CurriculumDTO> curriculums = new ArrayList<>();

    @Getter
    @Setter
    public static class CurriculumDTO{

        private Long id;

        @NotBlank(message = "Curriculum code is required")
        private String code; //must be system generated

        @NotBlank(message = "Effective year is required")
        @Pattern(
            regexp = "^\\d{4}-\\d{4}$",
            message = "Format must be YYYY-YYYY"
        )
        @ValidateYear
        private String effectiveYear;

        private int durationYear; //set in service by system

        // private Long facultyHeadId;

        @NotNull(message = "Curriculum status must be defined")
        private CurriculumStatus status;
        
        @Valid
        @NotEmpty(message = "Curriculum must contain at least one year")
        private List<YearDTO> years = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class YearDTO {

        private Long id;

        @NotNull(message = "Please specify the year level")
        @Positive(message = "Year level must be positive")
        private int level;

        @NotNull(message = "Total semester is required")
        @Positive(message = "Total semester must be positive")
        private int totalSemester;
    }
}
