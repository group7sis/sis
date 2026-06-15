package com.group7.sis.facultyModule.dtos.statsDtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacultyDashboardStatsDTO {
    private long totalSchedules;
    private long totalStudents;
    private long totalSections;
    private long totalCoursesHandled;
}
