package com.group7.sis.dto;

import java.util.List;
import com.group7.sis.entity.academic.CourseEntity.Status;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CourseDTO {
    private String code;
    private String title;
    private int units;
    private String lectureHours;
    private String labHours;
    private String notes;
    private Status status;
    private List<Long> prerequisiteIds;
}