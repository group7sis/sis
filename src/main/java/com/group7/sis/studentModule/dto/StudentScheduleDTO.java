package com.group7.sis.studentModule.dto;

import java.util.ArrayList;
import java.util.List;

import com.group7.sis.dto.ScheduledCourseDTO.DayDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentScheduleDTO {
    private String courseCode;
    private String courseTitle;
    private String facultyName;
    private String sectionName;
    private int lectureHours;
    private int labHours;
    private int units;

    private List<DayDTO> days = new ArrayList<>();
    
}
