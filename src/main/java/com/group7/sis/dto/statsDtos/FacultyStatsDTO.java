package com.group7.sis.dto.statsDtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacultyStatsDTO {
    private long totalFaculty;
    private long totalActiveFaculty;
    private long totalInactiveFaculty;
    private long totalSchedulesAssignedPerFaculty;
}
