package com.group7.sis.dto;

import com.group7.sis.entity.academic.ScheduledCourseEntity.ScheduleStatus;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScheduledCourseDTO {
    private Long programId;
    private Long courseId;
    private Long sectionId;
    private int yearLevel;
    private String academicYear;
    private List<DayDTO> days;
    private Long assignedFacultyId;
    private String room;
    private String notes;
    private ScheduleStatus status;

    @Getter
    @Setter
    public static class DayDTO{
        private Long id;
        private String name;
        private String startTime;
        private String endTime;
        private String breakTime;
    }
}
