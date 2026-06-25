package com.group7.sis.studentModule.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SchedulesSubmissionDTO {

    List<Long> currentSchedulesIds = new ArrayList<>();
    List<Long> failedCourseSchedulesIds = new ArrayList<>();
    List<Long> missedCourseSchedulesIds = new ArrayList<>();
}
