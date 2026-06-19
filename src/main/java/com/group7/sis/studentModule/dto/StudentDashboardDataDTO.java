package com.group7.sis.studentModule.dto;

import java.util.ArrayList;
import java.util.List;

import com.group7.sis.dto.ScheduledCourseDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentDashboardDataDTO {
    private String currentDay;
    private String currentDayOfTheWeek;
    private String currentMonth;
    private String currentYear;
    
    private String name;
    private Double gwa;
    private String gradeCompletionStatus;
    private String programCode;
    private String sectionName;
    private Integer totalEnrolledCourses;
    private Integer totalUnits;

    private List<ScheduledCourseDTO> schedulesToday = new ArrayList<>();
}
