package com.group7.sis.facultyModule.dtos.paged;

import java.util.ArrayList;
import java.util.List;

import com.group7.sis.dto.ScheduledCourseDTO;
import com.group7.sis.facultyModule.dtos.statsDtos.FacultyDashboardStatsDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacultyDashboardPageDTO {
    List<ScheduledCourseDTO> schedules = new ArrayList<>();
    FacultyDashboardStatsDTO stats;
    String currentMonth;
    String currentDay;
    String currentYear;
    String currentDayOfTheWeek;
    String facultyName;
}
