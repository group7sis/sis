package com.group7.sis.facultyModule.dtos.paged;

import java.util.ArrayList;
import java.util.List;

import com.group7.sis.dto.ScheduledCourseDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SchedulesByDayPageDTO {
    private List<ScheduledCourseDTO> monday = new ArrayList<>();
    private List<ScheduledCourseDTO> tuesday = new ArrayList<>();
    private List<ScheduledCourseDTO> wednesday = new ArrayList<>();
    private List<ScheduledCourseDTO> thursday = new ArrayList<>();
    private List<ScheduledCourseDTO> friday = new ArrayList<>();
    private List<ScheduledCourseDTO> saturday = new ArrayList<>();
    private List<ScheduledCourseDTO> sunday = new ArrayList<>();
}
