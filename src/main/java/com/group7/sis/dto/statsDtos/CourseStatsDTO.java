package com.group7.sis.dto.statsDtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseStatsDTO {
    private long totalCourses;
    private long totalActiveCourses;
    private long totalInactiveCourses;
}
