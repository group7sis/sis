package com.group7.sis.dto.statsDtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SectionStatsDto {
    private long totalSections;
    private long totalActiveSections;
    private long totalInactiveSections;
    private long totalEnrolledStudentsPerSection;
}
