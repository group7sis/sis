package com.group7.sis.dto.pagedResponse;

import java.util.ArrayList;
import java.util.List;

import com.group7.sis.dto.ScheduledCourseDTO;
import com.group7.sis.dto.statsDtos.SchedulesStatsDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SchedulePageDTO {
    List<ScheduledCourseDTO> schedules = new ArrayList<>();
    SchedulesStatsDTO stats;
}
