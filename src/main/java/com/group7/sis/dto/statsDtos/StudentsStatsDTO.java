package com.group7.sis.dto.statsDtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentsStatsDTO {
    private long totalStudents;
    private long totalActiveStudents;
    private long totalIrregularStudents;
    private long totalGraduatedStudents;
}
