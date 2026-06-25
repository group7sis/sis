package com.group7.sis.studentModule.dto.paged;

import java.util.ArrayList;
import java.util.List;

import com.group7.sis.studentModule.dto.StudentScheduleDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SchedulesCategorizedDTO {
    List<StudentScheduleDTO> currentSchedules = new ArrayList<>();
    List<StudentScheduleDTO> failedCourseSchedules = new ArrayList<>();
    List<StudentScheduleDTO> missedCourseSchedules = new ArrayList<>();
    String message;
}
