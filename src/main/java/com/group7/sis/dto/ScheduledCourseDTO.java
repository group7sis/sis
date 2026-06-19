package com.group7.sis.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.group7.sis.entity.academic.DayEntity.DayName;
import com.group7.sis.entity.academic.ScheduledCourseEntity.ScheduleStatus;
import com.group7.sis.validation.ValidateYear;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScheduledCourseDTO {
    private String id;
    private String code;

    @NotNull(message = "Schedule must be assigned to a course")
    private Long courseAssignmentId;

    private String courseTitle;
    private Long courseId;
    private Long programId;
    private Long curriculumId;
    private int yearLevel;
    private int semester;

    @NotNull(message = "Schedule must be assigned to a section")
    private Long sectionId;

    private String sectionName;
    
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

        @NotNull(message = "Day name is required")
        private DayName name;

        @NotNull(message = "Start time is required")
        @JsonFormat(pattern = "HH:mm")
        private LocalTime startTime;

        @NotNull(message = "End time is required")
        @JsonFormat(pattern = "HH:mm")
        private LocalTime endTime;

    }
}
