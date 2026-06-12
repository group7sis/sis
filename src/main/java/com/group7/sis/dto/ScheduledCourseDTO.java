package com.group7.sis.dto;

import com.group7.sis.entity.academic.ScheduledCourseEntity.ScheduleStatus;
import com.group7.sis.validation.ValidateYear;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScheduledCourseDTO {

    private String code;

    @NotNull(message = "Schedule must be assigned to a course")
    private Long courseId;

    @NotNull(message = "Schedule must be assigned to a section")
    private Long sectionId;

    @NotBlank(message = "Academic year is required")
    @Pattern(
        regexp = "^\\d{4}-\\d{4}$",
        message = "Format must be YYYY-YYYY"
    )
    @ValidateYear
    private String academicYear;

    @Valid
    @NotEmpty(message = "Schedule must contain at least one day")
    private List<DayDTO> days;

    @NotNull(message = "A faculty member must be assigned")
    private Long assignedFacultyId;

    @NotEmpty(message = "Room is required")
    private String room;

    private String notes;

    @NotNull (message = "Schedule status must be defined")
    private ScheduleStatus status;

    @Getter
    @Setter
    public static class DayDTO{
        private Long id;

        @NotEmpty(message = "Day name is required")
        private String name;

        @NotEmpty(message = "Start time is required")
        private String startTime;

        @NotEmpty(message = "End time is required")
        private String endTime;
        
        private String breakTime;
    }
}
