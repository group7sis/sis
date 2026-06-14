package com.group7.sis.dto.statsDtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SchedulesStatsDTO {
    private long totalSchedules;
    private long totalActiveSchedules;
    private long totalInactiveSchedules;
    private double weeklyHours;
    private double dailyHours;
}
